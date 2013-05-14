(ns game.connection.server
  (:use [game.logic.server_commands]
    [game.connection.communication :only [read-message write-message open-message-pump]]
    [game.logic.main]
    [game.state.server_state]
    [clojure.java.io :only [reader writer]]
    [server.socket :only [create-server]]))

(def server-commands-map {:exit command-server-exit
                          :play-pause command-server-play-pause
                          :change-cell command-server-change-cell})

(defn- client-callback [in out]
  (let [connection (ref {:in (reader in) :out (writer out) :alive true :id (swap! current-id inc)})]
    (add-client connection)
    (write-message connection {:type :world-update, :world @server-board})
    (write-message connection {:type :playing-changed, :state @server-playing})
    (open-message-pump server-commands-map connection)
    (remove-client connection)))

(defn start-server
  [server-info]
  (create-server (Integer. (:port server-info)) client-callback)
  (println "Server started!")
  (reset! is-server true)
  (while true
    (when @server-playing
      (dosync
        (alter server-board next-generation)
        (update-board-clients))
      (Thread/sleep 200))))
