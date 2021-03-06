(ns stardate-test
  (:refer-clojure)
  (:require [clojure.test :refer :all]
            [stardate :as sd]))

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
    (approx (sd/ofZonedDateTime zdt-fixture) stardate-fixture)))

(deftest short-test
  (testing "short"
    (is (= "2014.748" (sd/short stardate-fixture)))))

(deftest ofInstant-test
  (testing "ofInstant 30 Sep 2014 17:17:27 -0700"
    (approx (sd/ofInstant i-fixture) stardate-fixture)))

(deftest ofISO8601-test
  (testing "ofISO8601 2014-09-30T17:17:27-07:00"
    (approx (sd/ofISO8601 "2014-09-30T17:17:27-07:00")
            stardate-fixture)))

(deftest ofRFC2822-test
  (testing "ofRFC2822 Tue, 30 Sep 2014 17:17:27 -0700"
    (approx (sd/ofRFC2822 "Tue, 30 Sep 2014 17:17:27 -0700")
            stardate-fixture)))

(deftest ofGitFormat-test
  (testing "ofGitFormat Tue Sep 30 17:17:27 2014 -0700"
    (approx (sd/ofGitFormat "Tue Sep 30 17:17:27 2014 -0700")
            stardate-fixture)))

(deftest of-on-short-git-format
  (testing "of Thu Jul 2 04:36:36 2015 +0000"
    (approx (sd/of "Thu Jul 2 04:36:36 2015 +0000")
            2015.499156392694)))

(deftest of-on-possibly-short-rfc2822-format
  (testing "of Thu, 02 Jul 2015 04:51:24 +0000"
    (approx (sd/of "Thu, 02 Jul 2015 04:51:24 +0000")
            2015.4991845509894))
  (testing "of Thu,  2 Jul 2015 04:53:46 +0000"
    (approx (sd/of "Thu,  2 Jul 2015 04:53:46 +0000")
                         2015.49918905378)))

; The standard Java libraries will not parse
; all forms of ISO-8601 correctly.
(deftest of-on-unparseable-iso-8601-format
  (testing "of 2015-07-02T05:02:28+0000"
    (is (thrown? IllegalArgumentException
        (sd/of "2015-07-02T05:02:28+0000")))))

(deftest of-test
  (testing "of ZonedDateTime 30 Sep 2014 17:17:27 -0700"
    (approx (sd/of zdt-fixture) stardate-fixture))
  (testing "of Instant 30 Sep 2014 17:17:27 -0700"
    (approx (sd/of i-fixture) stardate-fixture)))

(deftest of-list-test-full
  (testing "of 2014 9 30 17 17 27 America/Los_Angeles"
    (approx (sd/of 2014 9 30 17 17 27 "America/Los_Angeles")
            stardate-fixture)))

(deftest of-list-test-date-and-zone-only
  (testing "of 2014 9 30 America/Los_Angeles"
    (approx (sd/of 2014 9 30 "America/Los_Angeles")
            2014.7473744292238)))

(deftest of-list-test-date-only
  (testing "of 2014 9 30"
    (approx (sd/of 2014 9 30)
            2014.7465753424658)))

(deftest now-test
  (testing "now"
    (approx (sd/now) (sd/ofZonedDateTime (ZonedDateTime/now)))))

(deftest toInstant-test
  (testing "toInstant 2014.747978420491791"
    (is (= epoch-milli-fixture
      (.toEpochMilli (sd/toInstant stardate-fixture))))))

(deftest toZonedDateTime-test
  (testing "toZonedDateTime 2014.747978420491791 pt"
    (is (= epoch-milli-fixture
      (.. (sd/toZonedDateTime stardate-fixture "US/Pacific")
        toInstant toEpochMilli)))))
