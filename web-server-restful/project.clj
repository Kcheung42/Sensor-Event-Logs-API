(defproject web-server-restful "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [compojure "1.6.1"]
                 [expectations "2.2.0-rc3"]
                 [org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.7.0"]
                 [ring/ring-jetty-adapter "1.7.0"]
                 [ring/ring-json "0.5.0-beta1"]
                 ]

  :ring {:handler web-server-restful.handler/app
         :nrepl {:start? true
                 :port 9998}}
  :profiles {:dev
             {:dependencies [[javax.servlet/servlet-api "2.5"]
                             [ring-mock "0.1.5"]]}}

  :main web-server-restful.handler

  :plugins [
            [lein-ring "0.12.4"]
            [com.jakemccrary/lein-test-refresh "0.23.0"]
            ])
