(defproject event-simulator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.9.0"]
                 [expectations "2.2.0-rc3"]
                 [clojure.joda-time "0.7.0"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.1"]
                 ]
  :plugins [
            [com.jakemccrary/lein-test-refresh "0.23.0"]
            [lein-autoexpect "1.0"]
            ]
  :main event-simulator.core)
