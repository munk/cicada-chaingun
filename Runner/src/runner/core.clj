(ns runner.core
  (:require [clojure.edn :as edn]
            [liberator.core :refer [resource]]
            [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes ANY]]))

(defonce server (atom nil))

(defn run-load-test [test]
  (binding [*ns* *ns*]
    (in-ns 'load-test-context)
    (refer-clojure)
    (use 'runner.context)
    (require '[clj-http.client :as http])
    (eval test)))

(defroutes app
  (ANY "/load-test" [] (resource :allowed-methods [:post]
                                 :available-media-types ["text/plain"]
                                 :post! (fn [context]
                                          (-> context :request :body slurp read-string run-load-test)))))

;; The following three functions are meant to facilitate interactive development
(defn stop-server! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server! []
  (let [port (Integer/parseInt (or (System/getenv "PORT_LOCAL") "3000"))]
    (reset! server (run-server app {:port port :join? false}))
    (println "Load test runner listening on port" port)))

(defn reset-server! []
  (stop-server!)
  (start-server!))

(defn -main [& args]
  (start-server!))
