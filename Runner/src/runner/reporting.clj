(ns runner.reporting)

(defonce report 
  (atom {:expected-runs 0
         :results []}))

(defn add-expected-runs [n]
  (swap! report update-in [:expected-runs] + n))

(defn log-result [r]
  (swap! report update-in [:results] conj r))

(defn get-report []
  (let [snapshot @report]
    (assoc snapshot :completed-runs 
           (count (:results snapshot)))))
