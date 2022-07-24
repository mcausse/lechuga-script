

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
            (values-set :add [(m :get [key])]))
        (m :keySet))

    (assert/eq (new :java.util.ArrayList [(m :values)]) values-set)
)


true

