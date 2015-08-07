(ns scheduler.core
  (:require [clojure.core.async :as async :refer [alts!! timeout put! close! chan]]
            [mesomatic.scheduler :as sched]
            [mesomatic.types :as mtypes]
            [mesomatic.async.scheduler :as masync]
            [liberator.core :refer [resource defresource]]
            [compojure.core :refer [defroutes ANY]]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.params :refer [wrap-params]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

(def task-channel (chan 10))

(defmulti handle-message (fn [_ message] (:type message)))

(defmethod handle-message :registered
  [state _]
  (println "Registered:" state)
  state)

(defn foo [driver offers]
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
    (sched/launch-tasks! driver new-offer [task-info])))

(defmethod handle-message :resource-offers
  [{:keys [driver] :as state} {:keys [offers]}]
  (loop []
    (let [[task _] (alts!! [task-channel (timeout 50)])]
      (if task
        (do
          (println "submitting task:" task)
          (foo driver offers)
          (recur))
        (do
          (println "no tasks")
          (sched/decline-offer driver (-> offers first :id))))))
  (println "Offers: " offers)
   state)

(defmethod handle-message :default
  [state message]
  (println "wtf:" message)
   state)

(defresource submit-task []
             :available-media-types ["application/edn"]
             :allowed-methods [:post]
             :post! (fn [ctx]
                      (let [message (slurp (get-in ctx [:request :body]))]
                        (if message
                          (async/>!! task-channel message)))))

(defroutes app
  (ANY "/task" [] (submit-task)))

(def handler
  (-> app
      wrap-params
      (wrap-trace :header :ui)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [ch (chan)
        sched (masync/scheduler ch)
        framework {:name "cicada-chaingun"}
        driver (sched/scheduler-driver sched framework "10.100.18.65:5050")]
    (sched/start! driver)
    (async/reduce handle-message {:driver driver} ch)

    (let [port (Integer/parseInt (or (System/getenv "PORT") "3001"))]
      (run-server app {:port port})
      (println (str "Listening on port " port)))))

