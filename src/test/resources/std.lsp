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
        (x :toString [])))

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

(try-catch
    (assert/fail "an exception should be thrown.")

    :org.homs.lispo.util.AssertionError
    (fn [e]
        (assert/eq "an exception should be thrown." e.getMessage)
    )
)



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
(assert/eq "abcd" (concat :a :b :c :d))












(defn println [...xs]
    (defn println-one-arg [x]
        ((field-static :java.lang.System :out) :println [x]))
    (for-each println-one-arg xs))

(println "Aló world!"
         "this is a first contact with"
         "this beautiful scripting language")



(defn list/add [l e]
    (l :add [e])
    l)


(defn mapcar [f l]
    (def r [])
    (for-each
        (fn [e]
            (list/add r (f e)))
        l)
    r)

(println (mapcar (fn[x](concat "<" x ">")) [:a :b :c]))



(defn reduce [i f l]
    (def acc i)
    (for-each
        (fn [e]
            (set acc (f acc e)))
        l)
    acc)

;; TODO "-a-b-c" nein! setejar en l'acc inicial el primer valor de la collection
(println (reduce "" (fn[a e](concat a "-" e)) [:a :b :c]))


(try-catch
    ;(throw (new :org.homs.lispo.util.AssertionError [:fallaste]))
    (throw (new :java.lang.RuntimeException [:fallaste]))
    :java.lang.RuntimeException
    (fn [e] (println e))
)

true
