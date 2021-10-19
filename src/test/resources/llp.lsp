

(def dako-link-server
    (junnel {
        [:type :server]
        [:port 5555]
        [:connect false]
        [:location [3 3 1 0]]
        [text
            """
            MSH|^~\\&|||LISTestTool||20210201||OML^O21^OML_O21|MSGID-10000047||2.5|0
            PID|1|MN-10000000|||Lopez^Roger^John||19800101|M
            ORC|NW|CASE-100||||E
            OBR||CASE-100;A;1;1||5^TEST^STAIN|||||||||||||||CASE-100;A;1;1^1|CASE-100;A;1^1|CASE-100;A^A
            """
        ]
        [:on-request (fn [message]
            """
            MSH|^~\\&|Ventana Transmit|APLab|Ventana|APLab|20210129213546||ACK^O21|MCI-11774fdbe129|P|2.4
            MSA|AA|MSGID-10000047||||
            """
        )]
    }))

(dako-link-server :start)
(dako-link-server :send
            """
            MSH|^~\\&|Ventana Transmit|APLab|Ventana|APLab|20210129213546||ACK^O21|MCI-11774fdbe129|P|2.4
            MSA|AA|MSGID-10000047||||
            """)
(dako-link-server :stop)

true