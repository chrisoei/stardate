(defproject stardate "3.1.0"
  :description "A library that converts to/from fractional years. Requires Java 8+."
  :url "http://github.com/chrisoei/stardate"
  :scm {
    :name "git"
    :url "https://github.com/chrisoei/stardate.git"
    :dir "Clojure/stardate"
  }
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]])

(when (< (.compareTo (System/getProperty "java.version") "1.8") 0)
  (println "Warning: need Java 8"))
