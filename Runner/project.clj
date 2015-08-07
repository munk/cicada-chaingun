(defproject runner "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [clj-http "2.0.0"]
                 [liberator "0.13"]
                 [compojure "1.4.0"]
                 [http-kit "2.1.18"]]
  :main ^:skip-aot runner.core
  :target-path "target/%s"
  :uberjar-name "cicada-chaingun-runner.jar"
  :profiles {:uberjar {:aot :all
                       :omit-source true}})
