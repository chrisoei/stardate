(ns stardate.core)

(import '(java.time Instant ZonedDateTime ZoneId))

(def utc (ZoneId/of "UTC"))

(defn get-start-of-year [y]
  (.toEpochSecond (ZonedDateTime/of y 1 1 0 0 0 0 utc)))

(defn of
  ([#^ZonedDateTime zdt] ; if called with ZoneDateTime argument
    (let [
        y (.getYear (.withZoneSameInstant zdt utc))
        t0 (get-start-of-year y)
        t1 (get-start-of-year (inc y))
        t (.toEpochSecond zdt)
      ]
      (+ y (/ (double (- t t0)) (- t1 t0)))
    ))
  ([] ; if no arguments supplied, use now
    (of (ZonedDateTime/now))
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
    (Instant/ofEpochMilli (long (* 1000 t)))
  )
)

(defn toZonedDateTime
  ([#^Double sd #^String z]
    (.atZone (toInstant sd) (ZoneId/of z))
  )
)
