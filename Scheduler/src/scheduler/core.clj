(ns scheduler.core
  (:require [clojure.core.async :refer [put! close! reduce chan]]
            [mesomatic.scheduler :as sched]
            [mesomatic.types :as mtypes]
            [mesomatic.async.scheduler :as async]
            )
  (:gen-class))

(def driver (atom ()))

(defmulti handle-message (fn [_ message] (:type message)))

(defmethod handle-message :registered
  [response _]
  (println "Registered:" (:driver response))
  (swap! driver (:driver response)))

(defmethod handle-message :resource-offers
  [driver {:keys [offers]}]
  (println "Offers Driver" driver)
  (println "Offers: " offers)
  (let [offer (first offers)
        slave-id (:slave-id offers)
        cpu-resource (mtypes/->Resource "cpus" :value-scalar 1 [] nil nil)
        mem-resource (mtypes/->Resource "mem" :value-scalar 128 [] nil nil)
        task-id (mtypes/->TaskID 1)
        port-mapping (mtypes/->PortMapping 80 8080 "http")
        docker-info (mtypes/->DockerInfo "ubuntu" :docker-network-bridge port-mapping false [])
        volume (mtypes/->Volume "/cicaida" "/cicaida" :volume-rw)
        container-info (mtypes/->ContainerInfo :container-type-docker  [volume] "cicaida1" docker-info)
        task-info (mtypes/->TaskInfo "cicaida-chaingun" task-id [cpu-resource mem-resource] nil nil container-info nil nil nil nil nil)]
    (sched/launch-tasks! (:driver driver) (:id offer) [task-info] nil)))

(defmethod handle-message :default
  [state message]
  (println "wtf:" message))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [ch (chan)
        sched (async/scheduler ch)
        framework {:name "cicada-chaingun-jd"}
        driver (sched/scheduler-driver sched framework "dev-sandbox-mesos-master1.nyc.dev.yodle.com:5050")]
    (sched/start! driver)
    (reduce handle-message {:driver driver} ch)
    (while true
      (Thread/sleep 1000))))