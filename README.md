
# Lechuga Script

```lisp
;;;
;;; n-queens.lechuga - the N-Queens problem solver.
;;;
(def num-solutions 0)

;;
;; Prints a human-readable board to the console.
;;
(defn display-board [state]
    (for i (seq (state :size))
        (for j (seq (state :size))
            (print
                (if (= (state :get i) j)
                    "Q "
                    "· ")))
        (println)))

;;
;; This function returns `true` if and only if the position row/col in the
;; board is safe to place a new queen.
;;
(defn is-safe? [state row col]
    (def r true)
    (def i 0)
    (while (and r (< i row))
        (set r
            (and
                (<> col (state :get i))
                (<> (math/abs (- (state :get i) col))
                    (math/abs (- i row)))))
        (inc i))
    r)

;;
;; Recursive function to explore the solutions space. The `solution-found-handler`
;; argument is the handler that consumes the solutions found.
;;
(defn queen [solution-found-handler n state row]
    (if (= row n)
        (solution-found-handler state)
        (let {}
            (def col 0)
            (while (< col n)
                (if (is-safe? state row col)
                    (let {}
                        (state :set row col)
                        (queen solution-found-handler n state (+ row 1))))
                (inc col)))))

;;
;; This is a solutions-found consumer, that displays the board of the first solution
;; found, and then continues counting the solutions.
;;
(defn show-the-first-solution-found-handler [state]
    (if (= num-solutions 0)
        (display-board state))
    (inc num-solutions))

;;
;; The N-Queens solver entry point.
;;
(defn n-queens [n]
    (println "RUNNING THE " n "-QUEENS")
    (set num-solutions 0)
    (def state (seq n))
    (state :set 0 -1)
    (queen show-the-first-solution-found-handler n state 0)
    (println "FOUND " num-solutions " SOLUTIONS.")
    (println)
    num-solutions)

(assert/eq 4 (n-queens 6))
(assert/eq 40 (n-queens 7))
(assert/eq 92 (n-queens 8))
(assert/eq 352 (n-queens 9))
```

```
RUNNING THE 6-QUEENS
· Q · · · · 
· · · Q · · 
· · · · · Q 
Q · · · · · 
· · Q · · · 
· · · · Q · 
FOUND 4 SOLUTIONS.

RUNNING THE 7-QUEENS
Q · · · · · · 
· · Q · · · · 
· · · · Q · · 
· · · · · · Q 
· Q · · · · · 
· · · Q · · · 
· · · · · Q · 
FOUND 40 SOLUTIONS.

RUNNING THE 8-QUEENS
Q · · · · · · · 
· · · · Q · · · 
· · · · · · · Q 
· · · · · Q · · 
· · Q · · · · · 
· · · · · · Q · 
· Q · · · · · · 
· · · Q · · · · 
FOUND 92 SOLUTIONS.

RUNNING THE 9-QUEENS
Q · · · · · · · · 
· · Q · · · · · · 
· · · · · Q · · · 
· · · · · · · Q · 
· Q · · · · · · · 
· · · Q · · · · · 
· · · · · · · · Q 
· · · · · · Q · · 
· · · · Q · · · · 
FOUND 352 SOLUTIONS.

```


```
===================================
BUILTIN DEFINITIONS
===================================
%                *                +                -                /                and
call-static      curry            def              defn             field-static     fn
for              for-each         if               is-null?         multi            new
or               quote            set              throw            to-byte          to-double
to-float         to-int           to-long          to-short         try-catch        while


===================================
STD DEFINITIONS
===================================
<                <=               <>               =                >                >=
any?             assert/eq        assert/fail      assert/ne        c/mapcar         c/reduce
c/remove-if      c/remove-if-not  composite        concat           dec              equals?
every?           inc              list/append      list/cons        list/head        list/tail
mapcar           math/abs         math/mul         math/sum         not              println
reduce           remove-if        remove-if-not    seq              str/join         to-string
```
