(ns stardate-test
  (:refer-clojure :exclude [second short])
  (:require [clojure.test :refer :all]
            [stardate :refer :all]))

(import '(java.time Instant ZonedDateTime ZoneId))

(def pt (ZoneId/of "US/Pacific"))

(defn approx [ x y ]
  (is (< (Math/abs (- x y)) 0.000001)))

(def zdt-fixture (ZonedDateTime/of 2014 9 30 17 17 27 0 pt))
(def i-fixture (.toInstant zdt-fixture))
(def stardate-fixture 2014.747978420491791)
(def epoch-milli-fixture 1412122647468)

(deftest ofZonedDateTime-test
  (testing "ofZonedDateTime 30 Sep 2014 17:17:27 -0700"
    (approx (stardate/ofZonedDateTime zdt-fixture) stardate-fixture)
  )
)

(deftest short-test
  (testing "short"
    (is (= "2014.748" (stardate/short stardate-fixture)))
  )
)

(deftest ofInstant-test
  (testing "ofInstant 30 Sep 2014 17:17:27 -0700"
    (approx (stardate/ofInstant i-fixture) stardate-fixture)
  )
)

(deftest ofISO8601-test
  (testing "ofISO8601 2014-09-30T17:17:27-07:00"
    (approx (stardate/ofISO8601 "2014-09-30T17:17:27-07:00")
            stardate-fixture)
  )
)

(deftest ofRFC2822-test
  (testing "ofRFC2822 Tue, 30 Sep 2014 17:17:27 -0700"
    (approx (stardate/ofRFC2822 "Tue, 30 Sep 2014 17:17:27 -0700")
            stardate-fixture)
  )
)

(deftest ofGitFormat-test
  (testing "ofGitFormat Tue Sep 30 17:17:27 2014 -0700"
    (approx (stardate/ofGitFormat "Tue Sep 30 17:17:27 2014 -0700")
            stardate-fixture)
  )
)

(deftest of-on-short-git-format
  (testing "of Thu Jul 2 04:36:36 2015 +0000"
    (approx (stardate/of "Thu Jul 2 04:36:36 2015 +0000")
            2015.499156392694)))

(deftest of-test
  (testing "of ZonedDateTime 30 Sep 2014 17:17:27 -0700"
    (approx (stardate/of zdt-fixture) stardate-fixture)
  )
  (testing "of Instant 30 Sep 2014 17:17:27 -0700"
    (approx (stardate/of i-fixture) stardate-fixture)
  )
)

(deftest now-test
  (testing "now"
    (approx (stardate/now) (stardate/ofZonedDateTime (ZonedDateTime/now)))))

(deftest toInstant-test
  (testing "toInstant 2014.747978420491791"
    (is (= epoch-milli-fixture
      (.toEpochMilli (stardate/toInstant stardate-fixture))))
  )
)

(deftest toZonedDateTime-test
  (testing "toZonedDateTime 2014.747978420491791 pt"
    (is (= epoch-milli-fixture
      (.. (stardate/toZonedDateTime stardate-fixture "US/Pacific")
        toInstant toEpochMilli)))
  )
)
