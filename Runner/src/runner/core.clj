(ns runner.core
  (:import [java.io StringWriter])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.core.async :refer [thread]]
            [liberator.core :refer [resource]]
            [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes ANY POST]]
            [runner.reporting :refer [get-results]])
  (:gen-class))

(defonce server (atom nil))

(defn run-load-test 
  "Black magic"
  [test]
  (binding [*ns* *ns*]
    (in-ns 'load-test-context)
    (refer-clojure)
    (use 'runner.context)
    (eval test)))

(defn handle-post-load-test [context]
  (-> context :request :body slurp edn/read-string run-load-test))

(defn handle-get-load-test-results [context]
  (let [w (StringWriter.)]
    (pprint (get-results) w)
    (.toString w)))

(defn stop-server! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defroutes app
  (ANY "/load-test" [] (resource :allowed-methods [:get :post]
                                 :available-media-types ["application/edn"]
                                 :post! handle-post-load-test
                                 :handle-ok handle-get-load-test-results))
  (POST "/shutdown" [] (resource :allowed-methods [:post]
                                 :available-media-types ["application/edn"]
                                 :post! (fn [_]
                                          (println "Shutting down...")
                                          (thread 
                                            (Thread/sleep 100)
                                            (stop-server!))))))

(defn start-server! []
  (when (nil? @server)
    (let [port (Integer/parseInt (or (System/getenv "PORT_LOCAL") "3000"))]
      (reset! server (run-server app {:port port :join? false}))
      (println "Load test runner listening on port" port))))

(defn -main [& args]
  (start-server!))

;; To facilitate interactive development
(defn reset-server! []
  (stop-server!)
  (start-server!))

