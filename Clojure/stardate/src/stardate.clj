(ns stardate (:refer-clojure :exclude [second short]))

(import '(java.time Instant ZonedDateTime ZoneId))
(import '(java.text SimpleDateFormat))
(import '(java.util Locale))

(def utc (ZoneId/of "UTC"))

(def ^:private get-start-of-year
  (memoize (fn [y]
    (.. (ZonedDateTime/of y 1 1 0 0 0 0 utc) toInstant toEpochMilli))))

; Constants are based on 365.2425 days/Gregorian year

(def millisecond (/ 1 31556952000.0))
(def second (/ 1 31556952.0)) ; potential conflict with clojure.core/second
(def minute (/ 1 525949.2))
(def hour (/ 1 8765.82))
(def day (/ 1 365.2425))
(def week (/ 7.0 365.2425))
(def fortnight (/ 14.0 365.2425))
(def month (/ 1 12.0))

(defn ofInstant [#^Instant i]
  (let [
      zdt (ZonedDateTime/ofInstant i utc)
      y (.getYear zdt)
      t0 (get-start-of-year y)
      t1 (get-start-of-year (inc y))
      t (.toEpochMilli i)
    ]
    (+ y (/ (double (- t t0)) (- t1 t0)))))

(defn now []
  (ofInstant (Instant/now)))

(defn ofZonedDateTime [#^ZonedDateTime zdt]
  (let [
      y (.getYear (.withZoneSameInstant zdt utc))
      t0 (get-start-of-year y)
      t1 (get-start-of-year (inc y))
      t (.. zdt toInstant toEpochMilli)
    ]
    (+ y (/ (double (- t t0)) (- t1 t0)))))

(defn toInstant
  [#^Double sd]
  (let [
      y (int sd)
      t0 (get-start-of-year y)
      t1 (get-start-of-year (inc y))
      t (+ t0 (* (- sd y) (- t1 t0)))
    ]
    (Instant/ofEpochMilli (long t))))

(defn toZonedDateTime
  ([#^Double sd #^String z]
    (.atZone (toInstant sd) (ZoneId/of z))))

(defmulti of
  "(of x) will convert a ZonedDateTime, Instant, Double,
  GregorianCalendar, or java.util.Date.
  (of y m d h mi s z) works with z set to America/Los_Angeles or Asia/Shanghai.
  (of y m d h mi s) assumes UTC time zone.
  (of y m d z) assumes the time is noon.
  (of y m d) assumes noon time and UTC time zone.
  (of \"2014-09-30T17:17:27-07:00\") works on SOME ISO8601 strings.
  (of \"Tue, 30 Sep 2014 17:17:27 -0700\") works on RFC2822.
  (of \"Tue Sep 30 17:17:27 2014 -0700\") works on git dates."
  (fn [x & r] (class x)))
(defmethod of ZonedDateTime [zdt] (ofZonedDateTime zdt))
(defmethod of Instant [i] (ofInstant i))
(defmethod of Double [sd] sd)
(defmethod of java.util.GregorianCalendar [gc] (of (.toZonedDateTime gc)))
(defmethod of java.util.Date [d] (of (.toInstant d)))
(defmethod of Long
  ([y m d h mi s z]
    (of (ZonedDateTime/of
          y m d h mi s 0 (ZoneId/of z))))
  ([y m d z]
     (of y m d 12 0 0 z))
  ([y m d]
     (of y m d "UTC")))

(defn canonical
  ([] (canonical (now)))
  ([x] (format "%.15f" (of x))))

(defn short
  ([] (short (now)))
  ([x] (format "%.3f" (of x))))

(defn ofISO8601
  "Convert ISO8601 string to stardate"
  [#^String x]
  (of (javax.xml.bind.DatatypeConverter/parseDateTime x)))

(let [rfc2822 (SimpleDateFormat.
               "EEE, dd MMM yyyy HH:mm:ss Z"
               Locale/ENGLISH)]
  (defn ofRFC2822 [#^String x]
    (of (.parse rfc2822 x))))

(let [git-format (SimpleDateFormat.
               "EEE MMM dd HH:mm:ss yyyy Z"
               Locale/ENGLISH)]
  (defn ofGitFormat [#^String x]
    (of (.parse git-format x))))

(defmulti ofString count)
(defmethod ofString 31 [x] (ofRFC2822 x))
(defmethod ofString 30 [x] (ofGitFormat x))
(defmethod ofString 29 [x] (ofGitFormat x))
(defmethod ofString 25 [x] (ofISO8601 x))
(defmethod ofString 24 [x] (ofISO8601 x))

(defmethod of String [x] (ofString x))
