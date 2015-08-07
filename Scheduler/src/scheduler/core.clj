(ns scheduler.core
  (:require [clojure.core.async :refer [put! close! reduce chan]]
            [mesomatic.scheduler :as sched]
            [mesomatic.types :as mtypes]
            [mesomatic.async.scheduler :as async]
            )
  (:gen-class))

(defmulti handle-message (fn [_ message] (:type message)))

(defmethod handle-message :registered
  [driver _]
  (println "Registered:" (:driver driver)))

(defmethod handle-message :resource-offers
  [state {:keys [offers]}]
  (println "Offers: " offers)
  (let [task-id (mtypes/TaskId. 1)
        container-info (mtypes/)
        ])
  )

(defmethod handle-message :default
  [state message]
  (println "wtf:" message))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [ch (chan)
        sched (async/scheduler ch)
        framework {:name "cicada-chaingun"}
        driver (sched/scheduler-driver sched framework "10.100.17.164:5050")]
    (sched/start! driver)
    (reduce handle-message {:driver driver} ch)
    (while true
      (Thread/sleep 1000))))