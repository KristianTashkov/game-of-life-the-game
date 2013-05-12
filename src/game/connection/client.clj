(ns game.connection.client
  (:use [game.connection.commands])
  (:use [game.connection.communication :only [read-message write-message open-message-pump *connection*]])
  (:use [clojure.java.io :only [reader writer]])
  (:import (java.net Socket)))

(declare conn-handler)

(def client-commands-map {:world-update command-client-world-update})

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (reader (.getInputStream socket))
        out (writer (.getOutputStream socket))
        conn (ref {:in in :out out :socket socket :alive true})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))

(defn conn-handler [conn]
  (binding [*connection* conn]
    (open-message-pump client-commands-map)))


(defn start-connection
  [server-info]
  (binding [*connection* (connect server-info)]
    (println "Available commands: \"play\",\"pause\" and \"change\"")
    (while (:alive @*connection*)
      (let [choice (read-line)]
        (case choice
          "exit" (do
                   (println "Shutting down...")
                   (shutdown-agents)
                   (write-message {:type :exit})
                   (.close (:socket @*connection*))
                   (dosync (alter *connection* assoc :alive false)))
          "play" (write-message {:type :play-pause, :state true})
          "pause" (write-message {:type :play-pause, :state false})
          "change" (write-message {:type :change-cell, :cell (read-line) :state (read-line)})
          (println "Wrong command!"))))))
