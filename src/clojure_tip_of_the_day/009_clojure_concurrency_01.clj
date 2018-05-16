(ns clojure-tip-of-the-day.009-clojure-concurrency-01)

;;;; This is the second episode to the Clojure concurrency series.
;;;; It's a whirlwind tour of various Clojure concurrency/state management constructs.
;;;; It's super-fast and intentiaonlly superficial.
;;;; Most of the constructs will be discussed in a separate episode.
;;;;
;;;; Things discussed in this episode - max 30 seconds for each?
;;;; 1. Vars - indirection, reloadable code, root binding, dynamic vars
;;;; 2. Atoms - simple, most used, retried, atomic, uncoordinated
;;;; 3. Refs - coordinated, synchronous, original selling point but rarely used nowadays
;;;; 4. Agents - async, uncoordinated, safe for side effects (e.g. logging to file),
;;;;             can be combined with STM, executed only when ref transaction succeeds
;;;; 5. Futures - async thread execution, executes on agents' thread pool, silent exceptions, 
;;;; 6. Promises - similar to futures buy more complex and flexible, thread must be created by client
;;;; 7. Delays - delayed initialization, exceptions cached
;;;; 8. Unified Succession Model
;;;; 9. deref - blocking, with timeout (not available for Delays)
;;;; 10. MISC - locks, threads, thread pools & ExecutorService, fork-join, pmap, reducers, core.async
;;;;
;;;; ## Resources:
;;;;  * The Ultimate Guide to Clojure Concurrency: https://purelyfunctional.tv/guide/clojure-concurrency/
;;;;  * PurelyFunctional.tv concurrency lessons: https://purelyfunctional.tv/courses/concurrency/
;;;;  * Clojure Concurrency - Rich Hickey: https://www.youtube.com/watch?v=nDAfZK8m5_8


;;; 1. Vars - indirection, reloadable code,
;;;   root binding, dynamic vars
(def x 10)
x

(def x 11)
x

(alter-var-root #'x inc)

(def ^:dynamic xd 0)
xd

(binding [xd 12]
  (println xd))


;;; 2. Atoms - simple, most used, retried, atomic,
;;;   uncoordinated

(def users (atom {}))
users

@users

(swap! users assoc :john "John Doe")
@users


;;; 3. Refs - coordinated, synchronous,
;;;           original selling point but rarely used 
(def users-ref (ref {}))
@users-ref

(def accounts (ref {}))
@accounts

;; now we want to atomically updated both refs
;; or neither of them
(defn update-users [username fullname]
  (alter users-ref assoc username fullname))
(defn update-accounts [username balance]
  (alter accounts assoc username balance))
(dosync
 (update-users :john "Johnn Doe")
 (update-accounts :john 1000.01M))

@users-ref

@accounts

;; transaction will fail and be retried
;; if some ref is modified
(future (dosync
         (println "Updating")
         (update-users :jack "Jack Black")
         (update-accounts :jack 1000000.M)
         (Thread/sleep 3000)
         (println "Updated")))

@users-ref

@accounts


;;; 4. Agents - async, uncoordinated,
;;;   safe for side effects (e.g. logging to file),
;;;   can be combined with STM (executed at the end)


(def mail-box (agent []))
(future (dosync
         (println "Updating")
         (send-off mail-box #(do (println "sending email...")
                                 (conj % "my mail")))
         (update-users :jack "Jack Black")
         (update-accounts :jack 1000000.M)
         (Thread/sleep 3000)
         (println "Updated")))

@mail-box

;; errors has to be checked and "cleared" explicitly
(agent-errors mail-box)
(restart-agent mail-box [])


;;; 5. Futures - async thread execution,
;;;   executes on agents' thread pool, silent exceptions 
(defn now []
  (let [now-date (java.util.Date/from (java.time.Instant/now))]
    (subs (str now-date) 11 19)))

(defn my-action []
  (println "Running: " (now))
  (Thread/sleep 3000)
  (println "Finished." (now)))

(my-action)

(future (my-action))


;;; 6. Promises - similar to futures but more complex
;;;   and flexible, thread must be created by client
(def start-value (promise))
(def end-value (promise))

(doto (Thread. (fn []
                 (deliver start-value 999)
                 (println "long running computation...")
                 (Thread/sleep 3000)
                 (deliver end-value 1000)))
  .start)

(defn deref-promise [p p-name]
  (println "deref" p-name "start: " (now))
  @p
  (println "deref" p-name "end: " (now))
  (println))

(deref-promise start-value "start-value")
(deref-promise end-value "end-value")


;;; 7. Delays - delayed initialization, exceptions cached
(def ultimate (delay
               (println "Executing a very time consuming operation...")
               42))
@ultimate


;;; 8. Unified Succession Model
;;;   Some info here: https://stackoverflow.com/questions/39309911/what-is-the-unified-update-model/39310567#39310567

;; Vars
(alter-var-root #'x #(* 2 %))

;; Atoms
(swap! users assoc :juraj "Juraj Martinka")

;; Refs
(dosync (alter users-ref assoc :juraj "Juraj Martinka"))

;; Agents
(send mail-box #(do (println "sending email...")
                        (conj % "my mail")))


;;; 9. deref - blocking, with timeout (not for Delays)
(def promised-value (promise))

@promised-value

(deref promised-value 1000 :not-available)


;;; 10. MISC - locks, threads, thread pools & ExecutorService,
;;;   fork-join, pmap, reducers, core.async
