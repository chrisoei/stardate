(ns stardate)

(import '(java.time Instant ZonedDateTime ZoneId))

(def utc (ZoneId/of "UTC"))

(defn- get-start-of-year [y]
  (.. (ZonedDateTime/of y 1 1 0 0 0 0 utc) toInstant toEpochMilli))

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
