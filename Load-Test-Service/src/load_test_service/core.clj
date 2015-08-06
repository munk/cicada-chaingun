(ns load-test-service.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]])
  (:gen-class))

(defn create-load-test-result []
  (let [id (str (java.util.UUID/randomUUID))]
    {::id id }))

(defn get-load-test-result [id]
  {:id id})

(defresource load-test-result-entry []
             :available-media-types ["text/plain"]
             :allowed-methods [:post]
             :post! (fn [ctx] (create-load-test-result))
             :post-redirect? (fn [ctx] {:location (format "/result/%s" (::id ctx))}))

(defresource load-test-result-entry-resource [id]
             :available-media-types ["application/edn"]
             :allowed-methods [:get]
             :handle-ok (fn [ctx] (get-load-test-result id)))

(defroutes app
  (ANY "/result" [] (load-test-result-entry))
  (ANY "/result/:id" [id] (load-test-result-entry-resource id)))

(def handler
  (-> app
      wrap-params))
