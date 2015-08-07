(ns scheduler.core
  (:require [clojure.core.async :refer [put! close! reduce chan]]
            [mesomatic.scheduler :as sched]
            [mesomatic.types :as mtypes]
            [mesomatic.async.scheduler :as async]
            )
  (:gen-class))

(def driver (atom nil))

(defmulti handle-message (fn [_ message] (:type message)))

(defmethod handle-message :registered
  [response _]
  (println "Registered:" (:driver response))
  (let [new-driver (:driver response)]
    (println "New driver " new-driver)
    (swap! driver (fn [_] new-driver))
    (println driver)))

(defmethod handle-message :resource-offers
  [message {:keys [offers]}]
  (println "Offers Driver" driver)
  (println "Offers: " offers)
  (let [offer (first offers)
        new-offer (mtypes/->Offer (:id offer) (:framework-id offer) (:slave-id offer) (:hostname offer) (:resources offer) (:attributes offer) (:executor-ids offer))
        slave-id (:slave-id offers)
        cpu-resource (mtypes/->Resource "cpus" :value-scalar 1 [] #{} "*")
        mem-resource (mtypes/->Resource "mem" :value-scalar 128 [] #{} "*")
        task-id (mtypes/->TaskID 1)
        port-mapping (mtypes/->PortMapping 80 8080 "http")
        docker-info (mtypes/->DockerInfo "ubuntu" :docker-network-bridge port-mapping false [])
        volume (mtypes/->Volume "/cicaida" "/cicaida" :volume-rw)
        container-info (mtypes/->ContainerInfo :container-type-docker  [volume] "cicaida1" docker-info)
        task-info (mtypes/->TaskInfo "cicaida-chaingun" task-id slave-id [cpu-resource mem-resource] "docker" "echo hai" container-info [] [] [] [])]
    (sched/launch-tasks! (deref driver) new-offer [task-info])))

(defmethod handle-message :default
  [state message]
  (println "wtf:" message))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [ch (chan)
        sched (async/scheduler ch)
        framework {:name "cicada-chaingun-jd"}
        driver (sched/scheduler-driver sched framework "10.100.18.170:5050")]
    (sched/start! driver)
    (reduce handle-message {:driver driver} ch)
    (while true
      (Thread/sleep 1000))))