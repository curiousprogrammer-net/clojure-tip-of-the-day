(ns clojure-tip-of-the-day.004-threading-macros)

;;;; Clojure's treading macros showcase.
;;;;
;;;; `->`, `->>`, `as->`,
;;;; `some->`, `some->>`, `cond->`, `cond->>`
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



;;; 4. `some->`
(defn add-bonus [salary bonus-percents]
  (* salary
     (+ 1 (/ bonus-percents 100))))

;; our function works OK on a person with salary
(-> person
    :work
    :salary
    (add-bonus 20))

;; person withotu salary => NPE!
(-> unemployed
    :work
    :salary
    (add-bonus 20))

;; let's fix NPE with `some->`
(some-> unemployed
        :work
        :salary
        (add-bonus 20))

(some-> person
        :work
        :salary
        (add-bonus 20))


;;; 5. use `some->>`
;;; if we have a functions that expect
;;; the argument in the last position
(defn assign-to-position [position work]
  (assoc work :position position))

(->> person
     :work
     (assign-to-position :director))

;; doesn't fail but we end up with a strange result
;; (map containing only the `:position` key)
(->> unemployed
     :work
     (assign-to-position :director))

;; we prefer to get nil if person has no work at all!
(some->> unemployed
         :work
         (assign-to-position :director))



;;; 6. `cond->`
;;; Thread a value through ALL expressions with passing test
;;; The CIDER debugger can be useful for investigation
(defn elixir-of-life? [person]
  (let [children (:children person)]
    (or (not children)
        (< (count children) 2))))

(defn parent? [p]
  (some? (:children p)))


;; elixir has no effect, but salary is increased
(cond-> person
  (elixir-of-life? person) (update-in [:age] dec)
  (parent? person) (update-in [:work :salary] #(* % 2))
  (> (:age person) 30) (assoc :tag :old))

;; no work & salary, but elixir takes effect
(cond-> unemployed
  (elixir-of-life? unemployed) (update-in [:age] dec)
  (parent? unemployed) (update-in [:work :salary] #(* % 2))
  (> (:age unemployed) 30) (assoc :tag :old))



;;; 7. `cond->>` is like `cond->`
;;; but inserts threaded value as the last argument

(defn make-brave [brave-tag person]
  (assoc person :tag brave-tag))

(cond->> (:children person)
  (> (-> person :work :salary) 49000) (cons {:name "Orphan" :age 15})
  (parent? person) (filter #(< 3 (:age %))))

