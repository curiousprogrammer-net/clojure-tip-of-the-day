(ns clojure-tip-of-the-day.003-threading-macros-tracing)

(def spy #(do (println "DEBUG:" %) %))

(defn first-spy [coll]
  (-> coll
      vec
      (conj (rand-int 100) (rand-int 1000))
      spy
      butlast))

(first-spy (range 10))


(defn last-spy [coll]
  (->> coll
       (map (juxt identity inc))
       spy
       (filter (fn [[x y]] (odd? y)))
       spy
       (map #(apply + %))))

(last-spy (range 10))



(defn as-spy [coll]
  (as-> coll $
      (vec $)
      (spy $)
      (conj $ (rand-int 100) (rand-int 1000))
      (spy $)
      (map (juxt identity inc) $)
      (spy $)
      (filter (fn [[x y]] (odd? y)) $)
      (spy $)
      (map #(apply + %) $)))

(as-spy (range 10))

