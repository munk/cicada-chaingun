(ns load-test-service.stats
  (:require [incanter.stats :refer [mean median sd]]))

(defn stats [samples] ;; samples should be a sequence of numbers
  {:min (apply min samples)
   :max (apply max samples)
   :mean (mean samples)
   :median (median samples)
   :std-dev (sd samples)})
