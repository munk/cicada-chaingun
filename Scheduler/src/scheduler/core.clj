(ns scheduler.core
  (:require [clojure.core.async :refer [put! close! reduce chan]]
            [mesomatic.scheduler :as sched]
            [mesomatic.async.scheduler :as async]
            )
  (:gen-class))

(defmulti handle-mesos-message (fn [_ message] (:type message)))

(defmethod handle-mesos-message :default
  [state {:keys [offers]}]
  (println "hello")
  (assoc state :offers offers))

(defmethod handle-mesos-message :registered
  [& stuff]
  (println stuff))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [sched (sched/scheduler )
        framework {:name "cicada-chaingun"}
        driver (sched/scheduler-driver sched framework "dev-mesos-master5.nyc.dev.yodle.com:5050")]
    (println "hello2")
    (sched/start! driver)
    (println "hello3")
    (while true
      (reduce handle-mesos-message {:driver driver} ch)
      (Thread/sleep 5000))
    ))