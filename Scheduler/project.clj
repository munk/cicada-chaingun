(defproject scheduler "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [edpaget/clj-mesos "0.22.1-SNAPSHOT"]]
  :main ^:skip-aot scheduler.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
