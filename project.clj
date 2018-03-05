(defproject clojure-tip-of-the-day "0.1.0-SNAPSHOT"
  :description "Examples for screencast 'Clojure tip of the day'"
  :url "https://github.com/curiousprogrammer-net/clojure-tip-of-the-day"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta3"]
                 [clj-http "3.7.0"]]
  :main ^:skip-aot clojure-tip-of-the-day.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
