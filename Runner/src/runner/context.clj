(ns runner.context
  (:require [clojure.core.async :refer [go thread <!!]]))

(defmacro load-test [params & body]
  (let [defaults {:threads 10 :runs 10 :delay 0}
        params (merge defaults params)
        run (fn [{:keys [threads runs delay]} f]
              (let [results (doall 
                             (for [t (range threads)]
                               (thread
                                 (doall 
                                  (for [r (range runs)]
                                    (:request-time (f t r)))))))]
                (go (doseq [result results] (println (<!! result))))))]
    `(~run ~params (fn [~'thread-number ~'run-number] ~@body))))