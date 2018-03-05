(ns clojure-tip-of-the-day.007-clj-refactor

  (:require [clj-http.client :as client]))


;;;; Clojure Tip of the Day episode 7: clj-refactor
;;;; See the list of available refactorings: https://github.com/clojure-emacs/clj-refactor.el/wiki
;;;; Check PurelyFunctional's "clj-refactor Reference Sheet": https://purelyfunctional.tv/clojure-resource-center/ 


;;; dependency, requires/imports - e.g. clj-http (Support https://clojuriststogether.org/!)
;;; `, r a p` (cljr-add-project-dependency)
;;; or `, r h d` (cljr-hotload-dependency) + manual
;;; `, r a r` (cljr-add-require-to-ns) and/or `, r a i` (cljr-add-import-to-ns)

;; download http://example.com/
;; https://github.com/dakrone/clj-http
;; With basic auth: https://github.com/dakrone/clj-http#basic-auth
(defn example-get []
  (-> (client/get
       "https://example.com")
      :body))

(subs (example-get) 960 983)


;;; "extract function"
;;; Similarly, you can extract "constant" and "def"

;; `, r e f` (cljr-extract-function): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-extract-function
;; just-odds:
(defn- just-odds
  []
  (->> (range 10)
     (map inc)
     (filter odd?)))



;; `, r f e` (cljr-create-fn-from-example): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-create-fn-from-example
;; this just create a stub without body
(defn- compute-secrets [a]
  (reduce + a))

(let [a (range 10)]
  (compute-secrets a))


;;; `let` refactorings

;; `, r i l` (cljr-introduce-let): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-introduce-let
;; `, r e l` (cljr-expand-let): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-expand-let
;; `, r m l` (cljr-move-to-let): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-move-to-let
(let [odds (filter odd? (range 10))
      even-again (map inc odds)]
  (apply + even-again))

(let [odds (filter odd? (range 10))
      even-again (map inc odds)]
  (apply + even-again))

;;; convert to collection type: https://github.com/clojure-emacs/clj-refactor.el/wiki/clojure-convert-to-collection-type

;; `, r c [` (clojure-convert-collection-to-vector)
(reduce + [1 2 3 4 5 6 ])

;;; threading

;; `, r t l` (cljr-thread-last-all): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-thread-last-all
;; `, r u w` (cljr-unwind): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-unwind-thread
;; `, r u a` (cljr-unwind-all): https://github.com/clojure-emacs/clj-refactor.el/wiki/cljr-unwind-all
(apply + (filter odd? (map inc (range 10))))
