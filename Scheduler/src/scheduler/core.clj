(ns scheduler.core
  (:require [clj-mesos.scheduler]
            [clojure.pprint])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def myscheduler
  (clj-mesos.scheduler/scheduler (registered [driver fid mi]
                                             (println "registered" fid mi))
                                 (resourceOffers [driver offers]
                                                 (clojure.pprint/pprint offers))))