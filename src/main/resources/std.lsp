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
        ((field-static :java.lang.System :out) :print [x])
        x)
    (def r (for-each print-one-arg xs))
    ((field-static :java.lang.System :out) :println)
    r)

(defn not [a]
    (if a
        false
        true))

(defn equals? [a b]
    (if (is-null? a)
        (is-null? b)
        (a :equals [b])))

(defn to-string [x]
    (if (is-null? x)
        "null"
        (x :toString)))

(defn concat [...xs]
    (defn concat-two-args [a b]
        ((to-string a) :concat [(to-string b)]))
    (def r "")
    (for-each
        (fn [x]
            (set r
                (concat-two-args r x)))
        xs)
    r)





(defn mapcar [f l]
    (def r [])
    (for-each
        (fn [e]
            (r :add [(f e)]))
        l)
    r)


(defn list/head [l]
    (if (l :isEmpty)
        (throw (new :org.homs.lispo.util.ValidationError [
            "list/head: the list is empty"]))
        (l :get [0])))

(defn list/tail [l]
    (if (l :isEmpty)
        (throw (new :org.homs.lispo.util.ValidationError [
                    "list/tail: the list is empty"]))
        (l :subList [1 (l :size)])))

(defn reduce [f l]
    (if (l :isEmpty)
        null
        (multi
            (def acc (list/head l))
            (def ll (list/tail l))
            (while (not (ll :isEmpty))
                (set acc (f acc (list/head ll)))
                (set ll (list/tail ll)))
            acc)))


(defn str/join [sep ss]
    (if (ss :isEmpty)
        ""
        (reduce (fn [a e] (concat a sep e)) ss)))





(defn composite [...fs]
    (fn [value]
        (def r value)
        (for-each
            (fn [f] (set r (f r)))
            fs)
        r))


(defn list/append [l a]
    (def r [])
    (r :addAll [l])
    (r :add [a])
    r)




;;
;; RECURSIVE REVERSE
;;
(defn list/reverse [l]
    (if (l :isEmpty)
        []
        (list/append (list/reverse (list/tail l)) (list/head l))))



(defn list/cons [e l]
    (def r [])
    (r :add [e])
    (r :addAll [l])
    r)









(defn math/fact [x]
    (set x (to-int x))
	(if (equals? x 1)
		1
		(* x (math/fact (- x 1)))))




(defn < [a b]
	(call-static :org.homs.lispo.util.ComparableUtils :lt [a b]))

(defn <= [a b]
	(call-static :org.homs.lispo.util.ComparableUtils :le [a b]))

(defn > [a b]
	(call-static :org.homs.lispo.util.ComparableUtils :gt [a b]))

(defn >= [a b]
	(call-static :org.homs.lispo.util.ComparableUtils :ge [a b]))

(defn = [a b]
	(call-static :org.homs.lispo.util.ComparableUtils :eq [a b]))

(defn <> [a b]
	(call-static :org.homs.lispo.util.ComparableUtils :ne [a b]))


(defn dec [x]  (- x 1))
(defn inc [x]  (+ x 1))




(defn math/abs [x] (call-static :java.lang.Math :abs [x]))



(defn seq [min max step?]
    (if (is-null? step)
        (set step 1))
    (def r [])
    (def n min)
    (while (< n max)
        (r :add [n])
        (set n (+ n step)))
    r)



(defn remove-if [f l]
    (def r [])
    (for-each
        (fn [e]
            (if (not (f e))
                (r :add [e])))
        l)
    r)

(defn remove-if-not [f l]
    (def r [])
    (for-each
        (fn [e]
            (if (f e)
                (r :add [e])))
        l)
    r)


(defn primos [n]
    (def r (seq 2 n))
    (for-each
        (fn [x]
            (set r
                (remove-if
                    (fn [xx]
                        (and (> xx x) (= 0 (% xx x))))
                    r)))
        (seq 2 n))
    r)



(def c/mapcar          (curry mapcar))
(def c/reduce          (curry reduce))
(def c/remove-if       (curry remove-if))
(def c/remove-if-not   (curry remove-if-not))


(defn math/sum [...xs]
    (reduce + xs))

(defn math/mul [...xs]
    (reduce * xs))




(defn any? [f l]
    (def r false)
    (def i 0)
    (while
        (and
            (not r)
            (< i (l :size)))

        (if (f (l :get [i]))
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

        (if (not (f (l :get [i])))
            (set r false))

        (set i (+ i 1)))
    r)






true
