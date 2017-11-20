(ns clojure-tip-of-the-day.002-cider-enlighten-mode)


;;; Simple function
(defn pythagoras [x y]
  (let [x2 (* x x)
        y2 (* y y)]
    (Math/sqrt (+ x2 y2))))

(pythagoras 3 4)

(pythagoras 6 8)



;;; Function calling another function
(def threshold 120)

(defn small-bug [n]
  (range 1 (inc n)))

(defn sum-up [n]
  (if (<= threshold (->> (small-bug n)
                        (map inc)
                        (reduce +)))
    (println "OK")
    (println "ERROR")))

(sum-up 15)


(defn sum-up2 [n]
  (let [sn (small-bug n)
        inc-sn (map inc sn)
        result (reduce + inc-sn)]
  (if (<= threshold result)
    (println "OK")
    (println "ERROR"))))

(sum-up2 15)
