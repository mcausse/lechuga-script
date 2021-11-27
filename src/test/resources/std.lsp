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

(println "Aló world!"
         "this is a first contact with"
         "this beautiful scripting language")

(println """Aló world!
         this is a first contact with
         this beautiful scripting language""")


(defn not [a]
    (if a
        false
        true))

(defn eq? [a b]
    (if (is-null? a)
        (is-null? b)
        (a :equals [b])))

(defn to-string [x]
    (if (is-null? x)
        "null"
        (x :toString)))

(defn concat [...xs]
    (defn concat-two-args [a b]
        (a :concat [(to-string b)]))
    (def r "")
    (for-each
        (fn [x]
            (set r
                (concat-two-args r x)))
        xs)
    r)

(defn assert/fail [msg]
    (throw (new :org.homs.lispo.util.AssertionError [msg]))
)

(defn assert/eq [expected obtained]
    (if (not (eq? expected obtained))
        (multi
            (def message
                (concat
                    "assert/eq: expected <"
                    (to-string expected)
                    ">, but obtained <"
                    (to-string obtained)
                    ">"))
            (throw (new :org.homs.lispo.util.AssertionError [message])))))

(defn assert/ne [expected obtained]
    (if (eq? expected obtained)
        (multi
            (def message
                (concat
                    "assert/eq: expected <"
                    (to-string expected)
                    ">, but obtained <"
                    (to-string obtained)
                    ">"))
            (throw (new :org.homs.lispo.util.AssertionError [message])))))


(assert/eq 1 1)
(assert/ne 1 2)
(assert/eq "jou" "jou")
(assert/ne "jou" "juas")
(assert/eq true true)
(assert/ne true false)
(assert/ne true "jou")
(assert/ne true 42)
(assert/eq [1 2 3] [1 2 3])
(assert/ne [1 2 3] [1 2 3 4])
(assert/eq {[1 :one]} {[1 :one]})
(assert/ne {[1 :one]} {[1 :two]})

(assert/eq 3 (println 1 2 3))

(try-catch
    (assert/fail "an exception should be thrown.")
    :org.homs.lispo.util.AssertionError
    (fn [e at]
        (assert/eq "an exception should be thrown." (e :getMessage))))



(assert/eq true (not false))
(assert/eq false (not true))

(assert/eq true  (eq? null null))
(assert/eq false (eq? 1 null))
(assert/eq false (eq? null 1))
(assert/eq true  (eq? 1 1))

(assert/eq "null"       (to-string null))
(assert/eq "true"       (to-string true))
(assert/eq "jou"        (to-string "jou"))
(assert/eq "42"         (to-string 42))
(assert/eq "3.14159"    (to-string 3.14159))
(assert/eq "[1, 2, 3]"  (to-string [1 2 3]))
(assert/eq "{1=one}"    (to-string {[1 :one]}))

(assert/eq "" (concat))
(assert/eq "null" (concat null))
(assert/eq "a" (concat :a))
(assert/eq "abcd" (concat :a :b :c :d))
(assert/eq "123nulltrue[1, 2, 3]" (concat 1 2 3 null true [1 2 3]))








(defn mapcar [f l]
    (def r [])
    (for-each
        (fn [e]
            (r :add [(f e)]))
        l)
    r)

(assert/eq ["<a>" "<b>" "<c>"] (mapcar (fn[x](concat "<" x ">")) [:a :b :c]))
(assert/eq [false true false] (mapcar is-null? [:a null :c]))
(assert/eq [false true false] (mapcar not [true false true]))
(assert/eq [] (mapcar not []))



(defn list/empty? [l]
    (l :isEmpty))

(assert/eq true (list/empty? []))
(assert/eq false (list/empty? [1]))

(defn list/size [l]
    (l :size))

(assert/eq 0 (list/size []))
(assert/eq 3 (list/size [1 2 3]))


(defn list/head [l]
    (if (list/empty? l)
        (throw (new :org.homs.lispo.util.ValidationError [
            "list/head: the list is empty"]))
        (l :get [0])))

(assert/eq 1 (list/head [1 2 3]))
(assert/eq 2 (list/head [2 3]))
(assert/eq 3 (list/head [3]))
(try-catch
    (multi
        (list/head [])
        (assert/fail "an exception should be thrown")
    )
    :org.homs.lispo.util.ValidationError
    (fn[e]
        ;; (e :printStackTrace)
        (assert/eq "list/head: the list is empty" (e :getMessage))
    )
)

(defn list/tail [l]
    (if (list/empty? l)
        (throw (new :org.homs.lispo.util.ValidationError [
                    "list/tail: the list is empty"]))
        (l :subList [1 (list/size l)])))

(assert/eq [2 3] (list/tail [1 2 3]))
(assert/eq [3] (list/tail [2 3]))
(assert/eq [] (list/tail [3]))
(try-catch
    (multi
        (list/tail [])
        (assert/fail "an exception should be thrown"))
    :org.homs.lispo.util.ValidationError
    (fn [e]
        (assert/eq "list/tail: the list is empty" (e :getMessage)))
)
(assert/eq [3] (list/tail (list/tail [1 2 3])))
(assert/eq 3 (list/head (list/tail (list/tail [1 2 3]))))
(assert/eq [] (list/tail (list/tail (list/tail [1 2 3]))))

(defn reduce [f l]
    (if (list/empty? l)
        null
        (multi
            (def acc (list/head l))
            (def ll (list/tail l))
            (while (not (list/empty? ll))
                (set acc (f acc (list/head ll)))
                (set ll (list/tail ll)))
            acc)))

(assert/eq null (reduce (fn [a e] (concat a "-" e)) []))
(assert/eq "a-b-c" (reduce (fn [a e] (concat a "-" e)) [:a :b :c]))

(defn str/join [sep ss]
    (if (list/empty? ss)
        ""
        (reduce (fn [a e] (concat a sep e)) ss)))

(assert/eq "" (str/join "-" []))
(assert/eq "a-b-c" (str/join "-" [:a :b :c]))




(try-catch
    ;(throw (new :org.homs.lispo.util.AssertionError [:fallaste]))
    (throw (new :java.lang.RuntimeException [:fallaste]))
    :java.lang.RuntimeException
    (fn [e] (println e))
)

(multi
    (defn tri [a b c] (concat a b c))
    (assert/eq "123" (tri "1" "2" "3"))

    (def c/tri (curry tri))
    (assert/eq "123" ((c/tri "1" "2") "3"))

    (def c/c/tri (curry c/tri))
    (assert/eq "123" (((c/c/tri "1") "2") "3"))
)


(multi
    (defn twice [a] (concat a a))
    (defn twice2 [a] (twice (twice a) (twice a)))
    (assert/eq "oo" (twice :o))
    (assert/eq "oooo" (twice2 :o))
)



(defn composite [...fs]
    (fn [value]
        (def r value)
        (for-each
            (fn [f] (set r (f r)))
            fs)
        r))

;;
;; COMPOSITE
;;
(multi

    (defn str/wrap-with [prefix postfix s]
        (concat prefix s postfix))
    (assert/eq "<a>" (str/wrap-with "<" ">" :a))

    (def c/str/wrap-with (curry str/wrap-with))
    (assert/eq "<a>" ((c/str/wrap-with "<" ">") :a))

    (defn str/e3encoder [s]
        (s :replace ["e" "3"])
    )
    (assert/eq "3ncod3r" (str/e3encoder "encoder"))

    (def c/str/e3encoder (curry str/e3encoder))
    (assert/eq "3ncod3r" ((c/str/e3encoder) "encoder"))

    (defn str/join [separator ss]
        (def str-joiner (new :java.util.StringJoiner [separator]))
        (for-each
            (fn[s] (str-joiner :add [(to-string s)]))
            ss)
        (str-joiner :toString)
    )
    (assert/eq "a/b/c" (str/join "/" [:a :b :c]))
    (assert/eq "1/2/3" (str/join "/" [1 2 3]))
    (assert/eq "null/true/false" (str/join "/" [null true false]))

    (def c/str/join (curry str/join))
    (assert/eq "a/b/c" ((c/str/join "/") [:a :b :c]))

    ;;
    ;; PoC de "composite"
    ;;
    (multi
        (def value [:one :two :three])
        (set value ((c/str/join "-") value))
        (set value ((c/str/e3encoder) value))
        (set value ((c/str/wrap-with "(" ")") value))
        (assert/eq "(on3-two-thr33)" value)
    )

    (multi
        (def xxx
            (composite
                (c/str/join "-")))
        (assert/eq "" (xxx []))
        (assert/eq "a" (xxx [:a]))
        (assert/eq "one-two-three" (xxx [:one :two :three]))
    )
    (multi
        (def xxx
            (composite
                (c/str/join "-")
                (c/str/e3encoder)))
        (assert/eq "" (xxx []))
        (assert/eq "a" (xxx [:a]))
        (assert/eq "on3-two-thr33" (xxx [:one :two :three]))
    )
    (multi
        (def xxx
            (composite
                (c/str/join "-")
                (c/str/e3encoder)
                (c/str/wrap-with "(" ")")))
        (assert/eq "()" (xxx []))
        (assert/eq "(a)" (xxx [:a]))
        (assert/eq "(on3-two-thr33)" (xxx [:one :two :three]))
    )
    (multi
        (assert/eq "(on3-two-thr33)"
            (
                (composite
                    (c/str/join "-")
                    (c/str/e3encoder)
                    (c/str/wrap-with "(" ")")
                )
                [:one :two :three]
            )
        )
    )
)



;;
;; RECURSIVE REVERSE
;;
(defn list/append [l a]
    (def r [])
    (r :addAll [l])
    (r :add [a])
    r)

(multi
    (def l1 [])
    (def l2 (list/append l1 "a"))
    (assert/eq [] l1)
    (assert/eq [:a] l2)
)


(defn list/reverse [l]
    (if (list/empty? l)
        []
        (list/append (list/reverse (list/tail l)) (list/head l))))

(assert/eq [] (list/reverse []))
(assert/eq [3 2 1] (list/reverse [1 2 3]))


(defn list/cons [e l]
    (def r [])
    (r :add [e])
    (r :addAll [l])
    r)

(multi
    (assert/eq [[]] (list/cons [] []))
    (assert/eq [1] (list/cons 1 []))
    (assert/eq [1 2] (list/cons 1 [2]))
    (assert/eq [[1] 2] (list/cons [1] [2]))
)




(assert/eq 0 (+))
(assert/eq 6 (+ 1 2 3))
(assert/eq 0 (-))
(assert/eq -4 (- 1 2 3))
(assert/eq 0 (*))
(assert/eq 6 (* 1 2 3))
(assert/eq 0 (/))
(assert/eq 0 (/ 1 2 3))
(assert/eq 0.16666666666666666 (/ 1.0 2.0 3.0))
(assert/eq 0 (%))
(assert/eq 1 (% 5 2))
(assert/eq 0 (% 4 2))


(assert/eq 15.2
    (to-double
        (+
            (to-byte 1)
            (to-short 2)
            (to-int 3)
            (to-float 4.0)
            (to-double 5.2))))

(assert/eq -13.2
    (to-double
        (-
            (to-byte 1)
            (to-short 2)
            (to-int 3)
            (to-float 4.0)
            (to-double 5.2))))

(assert/eq 120.0
    (to-double
        (*
            (to-byte 1)
            (to-short 2)
            (to-int 3)
            (to-float 4.0)
            (to-double 5.0))))

(assert/eq 0.008333333333333333
    (to-double
        (/
            (to-byte 1)
            (to-short 2)
            (to-int 3)
            (to-float 4.0)
            (to-double 5.0))))

(assert/eq 1.0
    (to-double
        (%
            (to-byte 1)
            (to-short 2)
            (to-int 3)
            (to-float 4.0)
            (to-double 5.0))))

(assert/eq 3 (to-int 3.14159))
(assert/eq 3.0 (to-double 3))








(defn math/fact [x]
    (set x (to-int x))
	(if (eq? x 1)
		1
		(* x (math/fact (- x 1)))))

(assert/eq 120 (math/fact 5))
(assert/eq 120 (math/fact 5.1))
(assert/eq 3628800 (math/fact 10))




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


(assert/eq true (< 2 3))
(assert/eq true (<= 2 3))
(assert/eq false (> 2 3))
(assert/eq false (>= 2 3))
(assert/eq false (= 2 3))
(assert/eq true (<> 2 3))
(assert/eq false (= 2 1))
(assert/eq true (= 2 2))
(assert/eq 0 (% 2 2))
(assert/eq 1 (% 3 2))


(defn dec [x]  (- x 1))
(defn inc [x]  (+ x 1))

(assert/eq 3 (dec 4))
(assert/eq 5 (inc 4))




(defn math/abs [x] (call-static :java.lang.Math :abs [x]))

(assert/eq 5 (math/abs -5))
(assert/eq 5 (math/abs 5))



(defn seq [min max step?]
    (if (is-null? step)
        (set step 1))
    (def r [])
    (def n min)
    (while (< n max)
        (r :add [n])
        (set n (+ n step)))
    r)

(multi
    (assert/eq [] (seq 2 0))
    (assert/eq [] (seq 2 2))
    (assert/eq [2] (seq 2 3))
    (assert/eq [2 3] (seq 2 4))
    (assert/eq [2 3 4] (seq 2 5))

    (assert/eq [0 2 4 6 8] (seq 0 10 2))
    (assert/eq [0 3 6 9] (seq 0 10 3))
    (assert/eq [0] (seq 0 10 100))
)



;;; XXX posar location, home
;;;
;;; (assert/fail "aaaa")


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


(multi
    (defn is-parell? [x]
        (= (% x 2) 0))

    (assert/eq [] (remove-if is-parell? []))
    (assert/eq [] (remove-if-not is-parell? []))

    (assert/eq [1 3] (remove-if is-parell? [1 2 3 4]))
    (assert/eq [2 4] (remove-if-not is-parell? [1 2 3 4]))

    (assert/eq [1 3 5 7] (remove-if is-parell? [1 2 3 4 5 6 7 8]))
    (assert/eq [2 4 6 8] (remove-if-not is-parell? [1 2 3 4 5 6 7 8]))
)


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

(assert/eq [2 3 5 7] (primos 10))
(assert/eq
    [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97]
    (primos 100))


(def c/mapcar          (curry mapcar))
(def c/reduce          (curry reduce))
(def c/remove-if       (curry remove-if))
(def c/remove-if-not   (curry remove-if-not))

;;;
;;;
;;; COMPOSITION FESTIVAL
;;;
;;;
(multi

    (def k
        (composite
            (c/mapcar inc)
            (c/reduce concat)))

    (assert/eq null (k []))
    (assert/eq "23456" (k [1 2 3 4 5]))

    (def k
        (composite
            (c/mapcar dec)
            (c/reduce +)))

    (assert/eq null (k []))
    (assert/eq 10 (k [1 2 3 4 5]))
)


(defn math/sum [...xs]
    (reduce + xs))

(defn math/mul [...xs]
    (reduce * xs))

(assert/eq 6 (math/sum 1 2 3))
(assert/eq 6 (math/mul 1 2 3))



;;;
;;;
;;; COMPOSITION FESTIVAL
;;;
;;;
(multi

    (def dogs-list [
        (new :org.homs.lispo.InterpreterTest$Dog [100 :faria])
        (new :org.homs.lispo.InterpreterTest$Dog [101 :din])
        (new :org.homs.lispo.InterpreterTest$Dog [102 :negra])
        (new :org.homs.lispo.InterpreterTest$Dog [103 :blanca])
        (new :org.homs.lispo.InterpreterTest$Dog [104 :gossa])
    ])

    (def composition
        (composite
            (c/remove-if (fn [dog] (eq? (dog :getName) :din)))
            (c/mapcar (fn [dog] (dog :getName)))
            (c/reduce (fn [a e] (concat a "-" e)))))

    (assert/eq "faria-negra-blanca-gossa" (composition dogs-list))

    (def composition
        (defn is-odd? [x] (<> 0 (% x 2)))
        (composite
            (c/remove-if is-odd?)
            (c/mapcar math/abs)
            (c/reduce +)))

    (assert/eq 4 (composition [-2 -1 0 1 2]))

)

(multi

    (defn hora-to-mins [hora]
        (def sep-index (hora :indexOf [":"]))
        (def hores (to-int (hora :substring [0 sep-index])))
        (def mins (to-int (hora :substring [(inc sep-index) (hora :length)])))
        (+ (* hores 60) mins)
    )

    (assert/eq  12 (hora-to-mins "00:12"))
    (assert/eq  62 (hora-to-mins "01:02"))
    (assert/eq 123 (hora-to-mins "02:03"))

    (defn zeroer [n]
        (while (> 2 (n :length))
            (set n (concat :0 n)))
        n)

    (assert/eq "00" (zeroer ""))
    (assert/eq "01" (zeroer "1"))
    (assert/eq "12" (zeroer "12"))

    (defn mins-to-hora [mins]
        (concat
            (zeroer (to-string (/ mins 60)))
            ":"
            (zeroer (to-string (% mins 60)))))

    (assert/eq "00:12" (mins-to-hora 12))
    (assert/eq "01:02" (mins-to-hora 62))
    (assert/eq "02:03" (mins-to-hora 123))

    (for-each
        (fn [n]
            (assert/eq 123 (hora-to-mins (mins-to-hora 123))))
        (seq 1 1000 12))

)

;;;
;;; String interpolation
;;;
(multi
    (def script-name "Merluzo Script")
    (assert/eq
        "hello Merluzo Script!"
        """hello ${script-name}$!"""
    )

    (assert/eq
        "hello 5!"
        """hello ${(+ 2 3)}$!"""
    )
)

true
