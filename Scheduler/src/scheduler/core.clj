(ns scheduler.core
  (:require [clojure.core.async :refer [put! close! reduce chan]]
            [mesomatic.scheduler :as sched]
            )
  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [sched (sched/scheduler
                (registered        [this driver framework-id master-info]
                                   (println "Registered"))
                (reregistered      [this driver master-info]
                                   (println "reregistered"))
                (disconnected      [this driver]
                                   (println "disconnected"))
                (resource-offers   [this driver offers]
                                   (println "resource offers"))
                (offer-rescinded   [this driver offer-id]
                                   (println "offer recinded"))
                (status-update     [this driver status]
                                   (println "status update"))
                (framework-message [this driver executor-id slave-id data]
                                   (println "framework msg"))
                (slave-lost        [this driver slave-id]
                                   (println "slave lost"))
                (executor-lost     [this driver executor-id slave-id status]
                                   (println "executor lost"))
                (error             [this driver message]
                                   (println "error")))
        framework {:name "cicada-chaingun"}
        driver (sched/scheduler-driver sched framework "10.100.17.164:5050")]
    (sched/start! driver)
    (while true
      (Thread/sleep 1000))))