(ns game.connection.client
  (:use [game.connection.commands])
  (:use [game.connection.communication :only [read-message write-message open-message-pump]])
  (:use [clojure.java.io :only [reader writer]])
  (:import (java.net Socket)))

(declare conn-handler)

(def client-commands-map {:message command-client-message})

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (reader (.getInputStream socket))
        out (writer (.getOutputStream socket))
        conn (ref {:in in :out out :socket socket})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))

(defn conn-handler [conn]
  (println "Available commands: \"say\",\"exit\"")
  (open-message-pump conn client-commands-map)
  (while (nil? (:exit @conn))
    (let [choice (read-line)]
      (case choice
        "exit" (do
                 (println "Shutting down...")
                 (shutdown-agents)
                 (send-command conn "exit" #())
                 (.close (:socket @conn))
                 (dosync (alter conn #(assoc % :exit true))))
        "say" (send-command conn "message" #(write-message conn (read-line)))
        (println "Wrong command!")))))

(defn start-connection 
  [server-info] 
  (let [conn (connect server-info)]
    (comment "Client code here (GUI)")))
