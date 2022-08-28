
    ;;;
    ;;; now in monadic style
    ;;;


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




    (defn wrap-minutes [minutes]
        {[:minutes minutes]
         [:to-string (=> (minutes-to-timestamp minutes))]})

    (assert/eq
        67
        ((wrap-minutes 67) :get :minutes))
    (assert/eq
        "01:07"
        (((wrap-minutes 67) :get :to-string)))

    (defn wrap-timestamp [timestamp]
        {[:minutes (timestamp-to-minutes timestamp)]
         [:to-string (=> timestamp)]})

    (assert/eq
        67
        ((wrap-timestamp "01:07") :get :minutes))
    (assert/eq
        "01:07"
        (((wrap-timestamp "01:07") :get :to-string)))


    (defn compute-worked-time [entrada sortida descans]
        (wrap-minutes
            (-
                (sortida :get :minutes)
                (entrada :get :minutes)
                (descans :get :minutes))))

    (multi
        (def r (compute-worked-time
            (wrap-timestamp "08:00")
            (wrap-timestamp "17:15")
            (wrap-minutes 45)))

        (assert/eq
            510
            (r :get :minutes))
        (assert/eq
            "08:30"
            ((r :get :to-string)))
    )


    (defn compute-table [entrades sortides descans step]

        (def )

    )



true