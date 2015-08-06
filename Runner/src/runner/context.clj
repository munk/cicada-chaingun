(ns runner.context
  (:require [clojure.core.async :refer [go thread <!!]]
            [clj-http.client :as http]
            [runner.reporting :refer [log-result]]))

(def ^:dynamic *thread-number*)
(def ^:dynamic *run-number*)

(defn- run-load-test
  [f {:keys [threads runs] :or {threads 10 runs 10}}]
  (let [results (doall
                 (for [t (range threads)]
                   (thread
                     (doall
                      (for [r (range runs)]
                        (binding [*thread-number* t
                                  *run-number* r]
                          (f)))))))]
    (go (doseq [result results] (<!! result)))))

(defmacro load-test [params & body]
  `(~run-load-test (fn [] ~@body) ~params))

(letfn [(wrap [f]
          (fn [& args]
            (-> (apply f args)
                (select-keys [:request-time :status])
                (merge {:url (first args)
                        :thread *thread-number*
                        :run *run-number*})
                log-result)))]
  (def GET (wrap http/get))
  (def PUT (wrap http/put))
  (def POST (wrap http/post))
  (def DELETE (wrap http/delete)))

