(ns game.connection.server
  (:use [game.connection.commands]
    [game.connection.communication :only [read-message write-message open-message-pump *connection*]]
    [game.logic.main]
    [game.state.server_state]
    [clojure.java.io :only [reader writer]]
    [server.socket :only [create-server]]))

(def server-commands-map {:exit command-server-exit
                          :play-pause command-server-play-pause
                          :change-cell command-server-change-cell})

(defn- client-callback [in out]
  (binding [*connection* (ref {:in (reader in) :out (writer out) :alive true :id (swap! current-id inc)})]
    (println (str "Client #" (:id @*connection*) " connected."))
    (dosync
      (alter clients assoc (:id @*connection*) *connection*))
    (open-message-pump server-commands-map)
    (dosync
      (alter clients dissoc *connection*))
    (println (str "Client #" (:id @*connection*) " disconnected."))))

(defn start-server
  [server-info]
  (create-server (Integer. (:port server-info)) client-callback)
  (println "Server started!")
  (while true
    (when @server-playing
      (dosync
        (alter server-board next-generation)
        (update-board-clients))
      (Thread/sleep 1000))))
