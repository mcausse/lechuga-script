"""
    std.lsp - the standard library
    written by mhoms @ 2021

    multi          def            set            fn             defn
    and            or             if             while          for-each
    new            call-static    field-static   curry          throw
    try-catch      is-null?       +              -              *
    /              %              to-byte        to-short       to-int
    to-long        to-float       to-double
"""

(defn println [...xs]
    (defn print-one-arg [x]
        ((field-static :java.lang.System :out) :print x)
        x)
    (def r (for-each xs print-one-arg))
    ((field-static :java.lang.System :out) :println)
    r)

(defn print [s]
    ((field-static :java.lang.System :out) :print s))

(defn printf [s ...args]
    ((field-static :java.lang.System :out) :printf s (args :toArray))
    s)

(defn not [a]
    (if a
        false
        true))

(defn equals? [a b]
    (if (is-null? a)
        (is-null? b)
        (a :equals b)))

(defn to-string [x]
    (if (is-null? x)
        "null"
        (x :toString)))






(defn mapcar [f l]
    (def r [])
    (for e l
        (r :add (f e)))
    r)


(defn list/head [l]
    (if (l :isEmpty)
        (throw (new :org.homs.lechugascript.util.ValidationError
            "list/head: the list is empty"))
        (l :get 0)))

(defn list/tail [l]
    (if (l :isEmpty)
        (throw (new :org.homs.lechugascript.util.ValidationError
                    "list/tail: the list is empty"))
        (new :java.util.ArrayList (l :subList 1 (l :size)))))

(defn reduce [initial-value f l]
    (def acc initial-value)
    (for e l
        (set acc
            (f acc e)))
    acc)



; (defn concat [...xs]
;     (defn concat-two-args [a b]
;         ((to-string a) :concat (to-string b)))
;     (def r "")
;     (for-each
;         (x =>
;             (set r
;                 (concat-two-args r x)))
;         xs)
;     r)

(defn concat [...xs]
    (reduce
        ""
        (a b => ((to-string a) :concat (to-string b)))
        xs))






(defn str/join [sep ss]
    (call-static :java.lang.String :join sep ss))



(defn composite [...fs]
    (fn [value]
        (def r value)
        (for-each fs
            (f => (set r (f r))))
        r))




(defn list/append [l a]
    (def r [])
    (r :addAll l)
    (r :add a)
    r)

(defn list/cons [e l]
    (def r [])
    (r :add e)
    (r :addAll l)
    r)




(defn < [a b]
	(call-static :org.homs.lechugascript.util.ComparableUtils :lt a b))

(defn <= [a b]
	(call-static :org.homs.lechugascript.util.ComparableUtils :le a b))

(defn > [a b]
	(call-static :org.homs.lechugascript.util.ComparableUtils :gt a b))

(defn >= [a b]
	(call-static :org.homs.lechugascript.util.ComparableUtils :ge a b))

(defn = [a b]
	(call-static :org.homs.lechugascript.util.ComparableUtils :eq a b))

(defn <> [a b]
	(call-static :org.homs.lechugascript.util.ComparableUtils :ne a b))






(defn math/abs [x] (call-static :java.lang.Math :abs x))



(defn range [min max step?]
    (if (is-null? step)
        (set step 1))
    (def r [])
    (def n min)
    (while (< n max)
        (r :add n)
        (set n (+ n step)))
    r)

(defn seq [to step?]
    (range 0 to step))

(defn remove-if [f l]
    (def r [])
    (for-each l
        (e =>
            (if (not (f e))
                (r :add e))))
    r)

(defn remove-if-not [f l]
    (def r [])
    (for-each l
        (e =>
            (if (f e)
                (r :add e))))
    r)



(def c/mapcar          (curry mapcar))
(def c/reduce          (curry reduce))
(def c/remove-if       (curry remove-if))
(def c/remove-if-not   (curry remove-if-not))


(defn math/sum [...xs]
    (reduce 0 + xs))

(defn math/mul [...xs]
    (reduce 1 * xs))




(defn any? [f l]
    (def r false)
    (def i 0)
    (while
        (and
            (not r)
            (< i (l :size)))

        (if (f (l :get i))
            (set r true))

        (set i (+ i 1)))
    r)

(defn every? [f l]
    (def r true)
    (def i 0)
    (while
        (and
            r
            (< i (l :size)))

        (if (not (f (l :get i)))
            (set r false))

        (set i (+ i 1)))
    r)



(defn c/for-each [f]
    (fn [l]
        (for-each l f)))





(defn is-of-class? [className object]
    (def class (call-static :org.homs.lechugascript.util.ReflectUtils :classOf className))
    (class :isAssignableFrom (object :getClass)))



(defn assert/fail [msg]
    (throw (new :org.homs.lechugascript.util.AssertionError msg))
)

(defn assert/eq [expected obtained]
    (if (not (equals? expected obtained))
        (assert/fail (concat
            "obtained "
             ((obtained :getClass) :getName)
             "<"
             (to-string obtained)
             ">; expected "
             ((expected :getClass) :getName)
             "<"
             (to-string expected)
             ">"
        ))))

;;        (multi
;;            (def message
;;                (concat
;;                    "assert/eq: expected <"
;;                    (to-string expected)
;;                    ">, but obtained <"
;;                    (to-string obtained)
;;                    ">"))
;;            (throw (new :org.homs.lechugascript.util.AssertionError message))))


(defn assert/ne [expected obtained]
    (if (equals? expected obtained)
        (multi
            (def message
                (concat
                    "assert/eq: expected <"
                    (to-string expected)
                    ">, but obtained <"
                    (to-string obtained)
                    ">"))
            (throw (new :org.homs.lechugascript.util.AssertionError message)))))



true



