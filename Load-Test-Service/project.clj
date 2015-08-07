(defproject load-test-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/yodle/cicada-chaingun.git"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}
  :plugins [[lein-ring "0.9.6"]]
  :ring { :handler load-test-service.core/handler}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [liberator "0.13"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [com.novemberain/monger "3.0.0"]
                 [ring-middleware-format "0.5.0"]
                 [http-kit "2.1.16"]]
  :main ^:skip-aot load-test-service.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
