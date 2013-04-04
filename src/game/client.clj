(ns game.client
  (:use [clojure.java.io :only [reader writer]])
  (:import (java.net Socket)))

(declare conn-handler)

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (reader (.getInputStream socket))
        out (writer (.getOutputStream socket))
        conn (ref {:in in :out out :socket socket})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))

(defn write [conn msg]
  (binding [*out* (:out @conn)]
    (println (str msg "\r"))
    (flush)))

(defn conn-handler [conn]
  (println "Type something to chat with him or \"exit\" to quit (if you tell him a joke he will respond!)")
  (print "Say something to him: ")
  (flush)
  (future (while (nil? (:exit @conn))
            (binding [*in* (:in @conn)]
              (let [msg (read-line)]
                (println msg)
                (flush)))))
  (while (nil? (:exit @conn))
    (let [choice (read-line)]
      (case choice
        "exit" (do
                 (println "shutting down...")
                 (flush)
                 (.close (:socket @conn))
                 (shutdown-agents)
                 (dosync (alter conn #(assoc % :exit true))))
        (write conn choice)))))

(defn start-connection [server-info] (connect server-info))
