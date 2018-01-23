(ns clojure-tip-of-the-day.004-threading-macros)

;;;; Clojure's treading macros showcase.
;;;;
;;;; `->`, `->>`, `as->``some->`, `some->>`, `cond->`, `cond->>`
;;;; 
;;;; They help to convert nested fn calls into linear flow
;;;; of function calls. 
;;;;
;;;; See also excellent Clojure 'Threading Macros Guide':
;;;;   https://clojure.org/guides/threading_macros


;;; Let's start with data
(def person
  {:name "Arthur"
   :age 31
   :children [{:name "Lisa"
               :age 4}
              {:name "Martin"
               :age 2}]
   :work {:salary 500000}})

(def unemployed {:name "Jeremy"
                 :age 27})



;;; 1. `->` (thread-first)

;; we can use it with fns that expect the collection
;; to be passed as a first argument
(update (update
         (assoc-in person [:work :salary] 60000) :age inc)
        :children
        conj {:name "Barbara" :age 0})

(-> person
    (assoc-in [:work :salary] 60000)
    (update :age inc)
    (update :children conj {:name "Barbara"
                            :age 0}))

;; we can use it like `get-in`
(-> person :work :salary)

;; it's nil safe too
(-> person :wrk :salary)

;; we can use with Java methods
(-> person
    :name
    (.substring 0 3)
    .toLowerCase)



;;; 2. `->>` (thread-last)
;;; Let's say we want to do collection filter

;; watch thread-first macro fail
(-> person
    :children
    (filter #(> (:age %) 3)))

;; try thread-last
(->> person
    :children
    (filter #(> (:age %) 3))
    (map :name))



;;; 3. `as->` (thread-as) 
;;; What if we need combine functions
;;; that expects arguments at different positions?

(as-> person $
  (update $ :children conj {:name "Jon" :age 3})
  (:children $)
  (filter #(< (:age %) 4) $)
  (map :name $))


