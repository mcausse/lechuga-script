(defn println [...xs]
    (defn println-one-arg [x]
        ((field-static :java.lang.System :out) :println [x])
        x)
    (for-each println-one-arg xs))

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
        (throw (new :org.homs.lispo.util.AssertionError [
            (concat
                "assert/eq: expected <"
                (to-string expected)
                ">, but obtained <"
                (to-string obtained)
                ">")
        ]))))

(defn assert/ne [expected obtained]
    (if (eq? expected obtained)
        (throw (new :org.homs.lispo.util.AssertionError [
            (concat
                "assert/ne: expected <"
                (to-string expected)
                ">, but obtained an equal instance"
            )
        ]))))


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
    (fn [e]
        (assert/eq "an exception should be thrown." e.getMessage)))



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
    (def acc (list/head l))
    (def ll (list/tail l))
    (while (not (list/empty? ll))
        (set acc (f acc (list/head ll)))
        (set ll (list/tail ll)))
    acc)

(assert/eq "a-b-c" (reduce (fn[a e](concat a "-" e)) [:a :b :c]))

(try-catch
    ;(throw (new :org.homs.lispo.util.AssertionError [:fallaste]))
    (throw (new :java.lang.RuntimeException [:fallaste]))
    :java.lang.RuntimeException
    (fn [e] (println e))
)

;;; TODO que es treballi amb BigInteger/BigDecimal !!!! aritmetica gratis

;;; TODO posar location, home
;;; (assert/fail "aaaa")


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
        (assert/eq "one-two-three" (xxx [:one :two :three]))
    )
    (multi
        (def xxx
            (composite
                (c/str/join "-")
                (c/str/e3encoder)))
        (assert/eq "on3-two-thr33" (xxx [:one :two :three]))
    )
    (multi
        (def xxx
            (composite
                (c/str/join "-")
                (c/str/e3encoder)
                (c/str/wrap-with "(" ")")))
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


;;;     (defn jou [a]
;;;         (jou (println (concat "." a)))
;;;     )
;;;     (jou "123")

;; TODO tenim algun test que verifiqui recursivitat, rollo factorial ???
;;
;; RECURSIVE REVERSE
;;
(defn list/append [l a]
    (def r [])
    (r :addAll [l])
    (r :add [a])
    r
)
(multi
    (def l1 [])
    (def l2 (list/append l1 "a"))
    (assert/eq [] l1)
    (assert/eq [:a] l2)
)


(defn list/reverse2 [l]
    (println l (list/empty? l))
    (if (list/empty? l)
        []
        (list/append (list/reverse2 (list/tail l)) (list/head l))))

(assert/eq [3 2 1] (list/reverse2 [1 2 3]))






true
