(ns scheduler.core
  (:require [clojure.core.async :as async :refer [alts!! timeout put! close! chan]]
            [mesomatic.scheduler :as sched]
            [mesomatic.types :as mtypes]
            [mesomatic.async.scheduler :as masync])
  (:gen-class))

(def task-channel (chan 10))

(defmulti handle-message (fn [_ message] (:type message)))

(defmethod handle-message :registered
  [driver _]
  (println "Registered:" (:driver driver)))

(defmethod handle-message :resource-offers
  [state {:keys [offers]}]
  (loop []
    (let [[task _] (alts!! [task-channel (timeout 50)])]
      (if task
        (do
          (println "submitting task:" task)
          (recur))
        (println "no tasks"))))
  (println "Offers: " offers)
  (let [task-id 1
        container-info []]))

(defmethod handle-message :default
  [state message]
  (println "wtf:" message))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [ch (chan)
        sched (masync/scheduler ch)
        framework {:name "cicada-chaingun"}
        driver (sched/scheduler-driver sched framework "10.100.17.164:5050")]
    (sched/start! driver)
    (async/reduce handle-message {:driver driver} ch)
    (while true
      (Thread/sleep 1000))))
