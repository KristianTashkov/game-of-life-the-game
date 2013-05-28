(ns game.connection.client
  (:use [game.state.client_state])
  (:use [game.gui.main])
  (:use [game.logic.client_commands])
  (:use [game.connection.communication :only [read-message write-message open-message-pump]])
  (:use [clojure.java.io :only [reader writer]])
  (:import (java.net Socket)))

(declare conn-handler)

(def client-commands-map {:world-update command-client-world-update
                          :playing-changed command-client-playing-changed})

(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (reader (.getInputStream socket))
        out (writer (.getOutputStream socket))
        conn (ref {:in in :out out :socket socket :alive true})]
    (doto (Thread. #(conn-handler conn)) (.start))
    conn))

(defn conn-handler [conn]
  (open-message-pump client-commands-map conn))


(defn start-connection
  [server-info]
  (let [connection (connect server-info)]
    (alter-var-root (var server-connection) (fn [x] connection))
    (start-game)
    (println "Available commands: \"play\",\"pause\" and \"change\"")
    (while (:alive @connection)
      (let [choice (read-line)]
        (case choice
          "exit" (do
                   (println "Shutting down...")
                   (shutdown-agents)
                   (write-message connection {:type :exit})
                   (.close (:socket @connection))
                   (dosync (alter connection assoc :alive false)))
          "play" (write-message connection {:type :play-pause, :state true})
          "pause" (write-message connection {:type :play-pause, :state false})
          "change" (write-message connection {:type :change-cell, :cell (read-line) :state (read-line)})
          (println "Wrong command!"))))))
