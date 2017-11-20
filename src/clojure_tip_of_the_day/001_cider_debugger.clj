(ns clojure-tip-of-the-day.001-cider-debugger)

;;; Debug simple function

(defn pythagoras [x y]
  (let [x2 (* x x)
        y2 (* y y)]
    (Math/sqrt (+ x2 y2))))

;; expect 5 as a result
(pythagoras 3 4)















































;;; Debug more complex function


(def threshold 120)

(defn small-bug [n]
  (range 1 (inc n)))

(defn sum-up [n]
  (if (<= threshold (->> (small-bug n)
                        (map inc)
                        (reduce +)))
    (println "OK")
    (println "ERROR")))

;; expect "OK" in console  since sum of 2,3,...,16 is 135
(sum-up 15)

