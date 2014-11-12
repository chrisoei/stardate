(ns starsrv.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as params]
            [clojure.data.json :as json]
            stardate)
  (:gen-class))

(defn convert [params]
  (def f (.get params "f"))
  (def a (.get params "a"))
  (cond (= f "git-to-stardate") (stardate/ofGitFormat a)
        (= f "iso8601-to-stardate") (stardate/ofISO8601 a)
        (= f "now-to-stardate") (stardate/now)
        (= f "rfc2822-to-stardate") (stardate/ofRFC2822 a)))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str {"result" (convert (:params request))})})

(def app
  (-> handler params/wrap-params))

(defn -main
  "Convert to/from stardates"
  [& args]
  (jetty/run-jetty app {:port 7827}))
