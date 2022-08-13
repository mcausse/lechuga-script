

(multi

    ;;
    ;;
    ;;
    (defn add [a b] (+ a b))
    (assert/eq 5 (add 2 3))

    ;;
    ;;
    ;;
    (defn c/add [a]
        (fn [b]
            (+ a b)))
    (assert/eq 5 ((c/add 2) 3))

    ;;
    ;;
    ;;
    (def c/add
        (curry add))
    (assert/eq 5 ((c/add 2) 3))
)


(multi

    (def m {[:one 1][:two 2][:three 3]})
    (def values-set [])
    (for-each
        (fn [key]
            (values-set :add (m :get key)))
        (m :keySet))

    (assert/eq (new :java.util.ArrayList (m :values)) values-set)
)




(multi

    ;;
    ;; Computes the number of minutes (int) from
    ;; an String timestamp in the "HH:MM" format.
    ;;
    (defn timestamp-to-minutes [timestamp]
        (def colon-pos (timestamp :indexOf ":"))
        (def hours (to-int (timestamp :substring 0 colon-pos)))
        (def mins  (to-int (timestamp :substring (inc colon-pos))))
        (+ (* hours 60) mins))

    (assert/eq   0 (timestamp-to-minutes "00:00"))
    (assert/eq  67 (timestamp-to-minutes "01:07"))
    (assert/eq 120 (timestamp-to-minutes "01:60"))
    (assert/eq 120 (timestamp-to-minutes "02:00"))

    ;;
    ;; Computes the HH:MM timestamp based on
    ;; the number of minutes (int).
    ;;
    (defn minutes-to-timestamp [minutes]
        (def hours (/ minutes 60))
        (def mins  (% minutes 60))

        (defn pad-with-zeros [value to-length]
            (set value (to-string value))
            (while (< (value :length) to-length)
                (set value ("0" :concat value)))
            value)

        (str/join ":"
            [(pad-with-zeros hours 2)
             (pad-with-zeros mins  2)]))

    (assert/eq "00:00" (minutes-to-timestamp   0))
    (assert/eq "01:07" (minutes-to-timestamp  67))
    (assert/eq "02:00" (minutes-to-timestamp 120))
    (assert/eq "02:09" (minutes-to-timestamp 129))




    (def files-entradas-mins
        (seq
            (timestamp-to-minutes "08:00")
            (timestamp-to-minutes "10:10")
            (timestamp-to-minutes "00:10")))
    (def columnes-sortidas-mins
        (seq
            (timestamp-to-minutes "16:00")
            (timestamp-to-minutes "19:10")
            (timestamp-to-minutes "00:10")))
    (def descans-mins (timestamp-to-minutes "00:45"))

    ;(table files-entradas-mins columnes-sortidas-mins descans-mins)
)


(multi

    ;;
    ;; c/add is the currified form of +
    ;;
    (defn c/add [a]
        (fn [b]
            (+ a b)))

    ;;
    ;; c/mul is the currified form of *
    ;;
    (defn c/mul [a]
        (fn [b]
            (* a b)))

    (assert/eq
        26
        ((composite
            (c/add 3)
            (c/mul 2)) 10))
)

(multi

    ;;
    ;; c/add is the currified form of +
    ;;
    (defn c/add [a]
        (fn [b]
            (+ a b)))

    (def c/add
        (a => (b => (+ a b))))

    ;;
    ;; c/mul is the currified form of *
    ;;
    (defn c/mul [a]
        (b =>
            (* a b)))

    (assert/eq
        26
        ((composite
            (c/add 3)
            (c/mul 2)) 10))
)


(multi

    (defn sqr [x]
        (* x x))

    (assert/eq 1 (sqr 1))
    (assert/eq 4 (sqr 2))
    (assert/eq 9 (sqr 3))
    (assert/eq 16 (sqr 4))

    (defn isqrt [n]
        (set n (to-int n))
        (def i 1)
        (while (<= (* i i) n)
            (set i (+ i 1)))
        (- i 1))

    (assert/eq 0 (isqrt  0))
    (assert/eq 0 (isqrt  0.5))
    (assert/eq 1 (isqrt  1))
    (assert/eq 1 (isqrt  2))
    (assert/eq 1 (isqrt  3))
    (assert/eq 2 (isqrt  4))
    (assert/eq 2 (isqrt  5))
    (assert/eq 2 (isqrt  6))
    (assert/eq 2 (isqrt  7))
    (assert/eq 2 (isqrt  8))
    (assert/eq 3 (isqrt  9))
    (assert/eq 3 (isqrt 10))
    (assert/eq 3 (isqrt 11))
    (assert/eq 3 (isqrt 12))
    (assert/eq 3 (isqrt 13))
    (assert/eq 3 (isqrt 14))
    (assert/eq 3 (isqrt 15))
    (assert/eq 4 (isqrt 16))
    (assert/eq 4 (isqrt 17))


    ;; =============================
    ;; k(i) = i * i
    ;; k(i+1) = (i+1) * (i+1)
    ;; -----------------------------
    ;; k(i+1) = i*i + i + i + 1
    ;; k(i+1) = i*i + 2*i + 1
    ;; k(i+1) = k(i) + 2*i + 1
    ;; =============================
    ;; k(i+1) = k(i) + i << 1 + 1
    ;; =============================

    (defn isqrt [n]
        (set n (to-int n))
        (def i 1)
        (def k 1)
        (while (<= k n)
            (set k (+ k (* i 2) 1))
            (set i (+ i 1)))
        (- i 1))

    (assert/eq 0 (isqrt  0))
    (assert/eq 0 (isqrt  0.5))
    (assert/eq 1 (isqrt  1))
    (assert/eq 1 (isqrt  2))
    (assert/eq 1 (isqrt  3))
    (assert/eq 2 (isqrt  4))
    (assert/eq 2 (isqrt  5))
    (assert/eq 2 (isqrt  6))
    (assert/eq 2 (isqrt  7))
    (assert/eq 2 (isqrt  8))
    (assert/eq 3 (isqrt  9))
    (assert/eq 3 (isqrt 10))
    (assert/eq 3 (isqrt 11))
    (assert/eq 3 (isqrt 12))
    (assert/eq 3 (isqrt 13))
    (assert/eq 3 (isqrt 14))
    (assert/eq 3 (isqrt 15))
    (assert/eq 4 (isqrt 16))
    (assert/eq 4 (isqrt 17))
)



true

