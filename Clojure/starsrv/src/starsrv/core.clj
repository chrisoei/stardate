(ns starsrv.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.request :as req]
            [clojure.data.json :as json]
            stardate)
  (:gen-class))

(defn convert [request]
  (def b (req/body-string request))
  (def j (json/read-str b :key-fn keyword))
  (def f (:f j))
  (def a (:a j))
  (cond (= f "git-to-stardate") (stardate/ofGitFormat a)
        (= f "iso8601-to-stardate") (stardate/ofISO8601 a)
        (= f "now-to-stardate") (stardate/now)
        (= f "rfc2822-to-stardate") (stardate/ofRFC2822 a)))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str {"result" (convert request)})})

(defn -main
  "Convert to/from stardates"
  [& args]
  (jetty/run-jetty handler {:port 7827}))
