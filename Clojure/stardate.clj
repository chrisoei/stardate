(import '(java.time ZonedDateTime ZoneId))

(defn stardate 
  ([zdt] ; if called with ZoneDateTime argument
    (let [
        utc (ZoneId/of "UTC")
        get-start-of-year
          #(.toEpochSecond (ZonedDateTime/of %1 1 1 0 0 0 0 utc))
        y (.getYear (.withZoneSameInstant zdt utc))
        t0 (get-start-of-year y)
        t1 (get-start-of-year (inc y))
        t (.toEpochSecond zdt)
      ]
      (+ y (/ (double (- t t0)) (- t1 t0)))
    ))
  ([] ; if no arguments supplied, use now
    (stardate (ZonedDateTime/now))
  )
)
