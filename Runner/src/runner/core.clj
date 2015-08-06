(ns runner.core
  (:require [clojure.core.async :refer [thread]]
            [clojure.edn :as edn]
            [clj-http.client :as http]))

(defn -main [filename]
  (binding [*ns* *ns*]
    (in-ns 'load-test-context)
    (refer-clojure)
    (use 'runner.context)
    (require '[clj-http.client :as http])
    (load-file filename)))
