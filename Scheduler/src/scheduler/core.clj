(ns scheduler.core
  (:require [clj-mesos.scheduler]
            [clojure.pprint])
  (:gen-class))

(def myscheduler
  (clj-mesos.scheduler/scheduler (registered [driver fid mi]
                                             (println "registered" fid mi))
                                 (resourceOffers [driver offers]
                                                 (clojure.pprint/pprint offers))))

(def overdriver
  (clj-mesos.scheduler/driver
    myscheduler {:user "" :name "testframework"} "dev-mesos-master5.nyc.dev.yodle.com:5050"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (clj-mesos.scheduler/start overdriver)
  (clj-mesos.scheduler/stop overdriver))