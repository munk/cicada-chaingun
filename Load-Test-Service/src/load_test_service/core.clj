(ns load-test-service.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [clojure.java.io :as io]
            [liberator.dev :refer [wrap-trace]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [load-test-service.db :as db])
  (:gen-class))

(defn create-load-test-result [params]
  (str (:_id (db/insert params))))

(defn get-load-test-result [id]
  (db/find-by-id id))

(defresource load-test-result-entry []
             :available-media-types ["application/edn"]
             :allowed-methods [:post]
             :post! (fn [ctx]
                      {::id (create-load-test-result (get-in ctx [:request :body-params]))})
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
      wrap-params
      (wrap-trace :header :ui)
      (wrap-restful-format)))
