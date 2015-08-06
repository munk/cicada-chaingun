(ns runner.reporting)

(def results (atom []))

(defn log-result [r]
  (swap! results conj r))

(defn get-results []
  (deref results))

