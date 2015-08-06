(ns runner.context
  (:require [clojure.core.async :refer [go thread <!!]]
            [clj-http.client :as http]
            [runner.reporting :refer [log-result]]))

(def ^:dynamic *thread-number*)
(def ^:dynamic *run-number*)

(defmacro load-test [params & body]
  (let [defaults {:threads 10 :runs 10 :delay 0}
        params (merge defaults params)
        run (fn [{:keys [threads runs delay]} f]
              (let [results (doall
                             (for [t (range threads)]
                               (thread
                                 (doall
                                  (for [r (range runs)]
                                    (binding [*thread-number* t
                                              *run-number* r]
                                      (:request-time (f))))))))]
                (go (doseq [result results] (<!! result)))))]
    `(~run ~params (fn [] ~@body))))

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

