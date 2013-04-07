(ns game.connection.server
  (:use [game.connection.commands]
    [game.connection.communication :only [read-message write-message open-message-pump *connection*]]
    [clojure.java.io :only [reader writer]]
    [server.socket :only [create-server]]))

(def current-id (atom 0))
(def clients (ref {}))

(def server-commands-map {:message command-server-message
                          :exit command-server-exit})

(defn- client-callback [in out]
  (binding [*connection* (ref {:in (reader in) :out (writer out) :alive true :id (swap! current-id inc)})]
    (println (str "Client #" (:id @*connection*) " connected."))
    (dosync
      (alter clients assoc (:id @*connection*) *connection*))
    (open-message-pump server-commands-map)
    (dosync
      (alter clients dissoc @*connection*))
    (println (str "Client " (:id @*connection*) " disconnected."))))

(defn start-server
  [server-info]
  (create-server (Integer. (:port server-info)) client-callback)
  (println "Server started!"))
