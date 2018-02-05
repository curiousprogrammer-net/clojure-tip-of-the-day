(ns clojure-tip-of-the-day.006-multimethod-reload)


;;; Let's start with simple multimethod definitions
(defmulti desire-to-be-great? :type)

(defmethod desire-to-be-great? :human [living-thing]
  "yes")

(defmethod desire-to-be-great? :default [living-thing]
  "no")

(desire-to-be-great? {:type :human
                      :age 32})

(desire-to-be-great? {:type :dog
                      :weight 10})


;;; At this point, we realized that we want to use `:kind`, not `:type`...
(defmulti desire-to-be-great? :kind)

(defmethod desire-to-be-great? :human [living-thing]
  "yes")

(defmethod desire-to-be-great? :default [living-thing]
  "no")

(desire-to-be-great? {:kind :human
                      :age 32})

(desire-to-be-great? {:kind :dog
                      :weight 10})




;; to resolve our issue we can use `ns-unmap`
(ns-unmap *ns* 'desire-to-be-great?)

;; or we can just override the definition with `def`
(def desire-to-be-great? nil)
