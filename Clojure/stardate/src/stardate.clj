(ns stardate (:refer-clojure :exclude [second]))

(import '(java.time Instant ZonedDateTime ZoneId))

(def utc (ZoneId/of "UTC"))

(defn- get-start-of-year [y]
  (.. (ZonedDateTime/of y 1 1 0 0 0 0 utc) toInstant toEpochMilli))

; Constants are based on 365.2425 days/Gregorian year

(def millisecond (/ 1 31556952000.0))
(def second (/ 1 31556952.0)) ; potential conflict with clojure.core/second
(def minute (/ 1 525949.2))
(def hour (/ 1 8765.82))
(def day (/ 1 365.2425))
(def week (/ 7.0 365.2425))
(def fortnight (/ 14.0 365.2425))
(def month (/ 1 12.0))

(defn ofInstant
  ([#^Instant i]
    (let [
        zdt (ZonedDateTime/ofInstant i utc)
        y (.getYear zdt)
        t0 (get-start-of-year y)
        t1 (get-start-of-year (inc y))
        t (.toEpochMilli i)
      ]
      (+ y (/ (double (- t t0)) (- t1 t0)))
   ))
  ([] ; if no arguments supplied, use now
    (ofInstant (Instant/now))
  )
)

(defn now []
  (ofInstant (Instant/now))
)

(defn ofZonedDateTime
   ([#^ZonedDateTime zdt]
    (let [
        y (.getYear (.withZoneSameInstant zdt utc))
        t0 (get-start-of-year y)
        t1 (get-start-of-year (inc y))
        t (.. zdt toInstant toEpochMilli)
      ]
      (+ y (/ (double (- t t0)) (- t1 t0)))
    ))
  ([] ; if no arguments supplied, use now
    (ofZonedDateTime (ZonedDateTime/now))
  )
)

(defn toInstant
  [#^Double sd]
  (let [
      y (int sd)
      t0 (get-start-of-year y)
      t1 (get-start-of-year (inc y))
      t (+ t0 (* (- sd y) (- t1 t0)))
    ]
    (Instant/ofEpochMilli (long t))
  )
)

(defn toZonedDateTime
  ([#^Double sd #^String z]
    (.atZone (toInstant sd) (ZoneId/of z))
  )
)
