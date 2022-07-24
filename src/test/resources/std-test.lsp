



(println "Aló world!"
         "this is a first contact with"
         "this beautiful scripting language")

(println """Aló world!
         this is a first contact with
         this beautiful scripting language""")




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

(assert/eq true  (equals? null null))
(assert/eq false (equals? 1 null))
(assert/eq false (equals? null 1))
(assert/eq true  (equals? 1 1))

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






(assert/eq ["<a>" "<b>" "<c>"] (mapcar (fn[x](concat "<" x ">")) [:a :b :c]))
(assert/eq [false true false] (mapcar is-null? [:a null :c]))
(assert/eq [false true false] (mapcar not [true false true]))
(assert/eq [] (mapcar not []))






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



(assert/eq null (reduce (fn [a e] (concat a "-" e)) []))
(assert/eq "a-b-c" (reduce (fn [a e] (concat a "-" e)) [:a :b :c]))



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



(multi
    (def l1 [])
    (def l2 (list/append l1 "a"))
    (assert/eq [] l1)
    (assert/eq [:a] l2)
)


(assert/eq [] (list/reverse []))
(assert/eq [3 2 1] (list/reverse [1 2 3]))


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







(assert/eq 120 (math/fact 5))
(assert/eq 120 (math/fact 5.1))
(assert/eq 3628800 (math/fact 10))



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

(assert/eq 3 (dec 4))
(assert/eq 5 (inc 4))



(assert/eq 5 (math/abs -5))
(assert/eq 5 (math/abs 5))



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



(assert/eq [2 3 5 7] (primos 10))
(assert/eq
    [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97]
    (primos 100))


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
            (c/remove-if (fn [dog] (equals? (dog :getName) :din)))
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



(multi

    (defn is-even? [x]
        (= (% x 2) 0))

    (assert/eq
        true
        (any? is-even? [1 2 3 4 5]))
    (assert/eq
        true
        (any? is-even? [1 3 3 4 5]))
    (assert/eq
        false
        (any? is-even? [1 3 3 7 5]))
    (assert/eq
        false
        (any? is-even? []))


    (assert/eq
        false
        (every? is-even? [1 2 3 4 5]))
    (assert/eq
        false
        (every? is-even? [1 3 3 4 5]))
    (assert/eq
        true
        (every? is-even? [2 4 6 8 10]))
    (assert/eq
        true
        (every? is-even? []))
)




true

