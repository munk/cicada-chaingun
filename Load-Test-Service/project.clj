(defproject load-test-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.9.6"]]
  :ring { :handler load-test-service.core/handler}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [liberator "0.13"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]]
  :main ^:skip-aot load-test-service.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
