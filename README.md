
# Lechuga Script

```lisp
;;;
;;; n-queens.lechuga - the N-Queens problem solver.
;;;
;;; This program is a N-Queens solver that displays the first encountered
;;; solution and then continues exploring to count the total of solutions.
;;;

;;
;; this is the counter of solutions found.
;;
(def num-solutions 0)

;;
;; Prints a human-readable board to the console.
;;
(defn display-board [state]
    (for i (seq 0 (state :size))
        (for j (seq 0 (state :size))
            (print
                (if (= (state :get i) j)
                    "Q "
                    "· ")))
        (println)))

;;
;; This function is called when a solution is found, increasing the counter
;; of solutions, and if is the first solution, prints the board.
;;
(defn solution-found-handler [state]
    (if (= num-solutions 0)
        (display-board state))
    (set num-solutions (+ num-solutions 1)))

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
        (set i (+ i 1)))
    r)

;;
;; Recursive function to explore the solutions space.
;;
(defn queen [n state row]
    (if (= row n)
        (solution-found-handler state)
        (let {}
            (def col 0)
            (while (< col n)
                (if (is-safe? state row col)
                    (let {}
                        (state :set row col)
                        (queen n state (+ row 1))))
                (set col (+ col 1))))))

;; 
;; The N-Queens solver entry point. 
;; 
(defn n-queens [n]
    (println "RUNNING THE " n "-QUEENS")
    (def state (seq 0 n))
    (state :set 0 -1)
    (queen n state 0)
    (println "FOUND " num-solutions " SOLUTIONS."))


(n-queens 8)
```

```
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
