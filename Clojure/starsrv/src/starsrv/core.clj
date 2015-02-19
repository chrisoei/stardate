(ns starsrv.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.request :as req]
            [clojure.data.json :as json]
            stardate)
  (:gen-class))

(defn convert [request]
  (let [ b (req/body-string request)
         j0 (json/read-str b)
         zone (get j0 "zone" "America/Los_Angeles")
         j (dissoc j0 "zone")
         k (first (keys j))
         v (first (vals j))]
    (try
         (cond (= k "git") { "stardate" (stardate/ofGitFormat v) }
               (= k "iso8601") { "stardate" (stardate/ofISO8601 v) }
               (= k "now") { "stardate" (stardate/now) }
               (= k "rfc2822") { "stardate" (stardate/ofRFC2822 v) }
               (= k "stardate") { "zonedDateTime"
                             (.toString (stardate/toZonedDateTime
                               (Double/parseDouble v)
                               zone))})
         (catch Exception e { "error" (.getMessage e) }))))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (convert request))})

(defn -main
  "Convert to/from stardates"
  [& args]
  (jetty/run-jetty handler {:port 7827}))
