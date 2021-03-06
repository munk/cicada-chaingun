(ns runner.context
  (:require [clojure.core.async :refer [go thread <!!]]
            [clj-http.client :as http]
            [runner.reporting :as reporting]))

(def ^:dynamic *thread-number*)
(def ^:dynamic *run-number*)

(defn- run-load-test
  [f {:keys [threads runs] :or {threads 10 runs 10}}]
  (reporting/add-expected-runs (* threads runs))
  (let [results (doall
                 (for [t (range threads)]
                   (thread
                     (doall
                      (for [r (range runs)]
                        (binding [*thread-number* t
                                  *run-number* r]
                          (f)))))))]
    (go (doseq [result results] (<!! result)))))

(defn- split-params-and-body [args]
  (reduce (fn [[params body] part]
            (if (keyword? (first part))
              [(assoc params (first part) (second part)) body]
              [params (concat body part)]))
          [{} '()]
          (partition-all 2 args)))

(defmacro load-test [& args]
  (let [[params body] (split-params-and-body args)]
    `(~run-load-test (fn [] ~@body) ~params)))

(letfn [(wrap [f]
          (fn [& args]
            (-> (apply f args)
                (select-keys [:request-time :status])
                (merge {:url (first args)
                        :thread *thread-number*
                        :run *run-number*})
                (reporting/log-result))))]
  (def GET (wrap http/get))
  (def PUT (wrap http/put))
  (def POST (wrap http/post))
  (def DELETE (wrap http/delete)))

