(ns load-test-service.db
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId))

(defonce conn (mg/connect {:host "dev-mongodb1.dev.yodle.com"}))

(defn insert [result]
  (let [db (mg/get-db conn "load-test")]
    (mc/insert-and-return db "results" result)))

(defn find-by-id [id]
  (let [db (mg/get-db conn "load-test")]
    (mc/find-map-by-id db "results" (ObjectId. id))))
