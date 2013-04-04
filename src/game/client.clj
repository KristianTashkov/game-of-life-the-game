(ns game.client
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader)))

(def server {:name "localhost" :port 3333})

(declare conn-handler)

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:in in :out out})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))

(defn write [conn msg]
  (doto (:out @conn)
    (.println (str msg "\r"))
    (.flush)))

(defn conn-handler [conn]
  (println "Type something to chat with him or \"exit\" to quit (if you tell him a joke he will respond!)")
  (print "Say something to him: ")
  (flush)
  (future (while (nil? (:exit @conn))
            (let [msg (.readLine (:in @conn))]
              (println msg)
              (flush))))
  (while (nil? (:exit @conn))
      (let [choice (read-line)]
          (case choice 
            "exit" (do
                     (println "shutting down")
                     (flush)
                     (shutdown-agents)
                     (dosync (alter conn #(assoc % :exit true))))
            (write conn choice)))))

(defn start-connection [] (connect server))