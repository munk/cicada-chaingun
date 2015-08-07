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
  [driver _]
  (println "Registered:" (:driver driver))
   driver)

(defmethod handle-message :resource-offers
  [{:keys [driver] :as state} {:keys [offers]}]
  (loop []
    (let [[task _] (alts!! [task-channel (timeout 50)])]
      (if task
        (do
          (println "submitting task:" task)
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
