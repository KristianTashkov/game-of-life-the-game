(ns game.connection.server
  (:use [game.connection.commands]
        [game.connection.communication :only [read-message write-message open-message-pump]]
        [clojure.java.io :only [reader writer]]
        [server.socket :only [create-server]]))

(def server-commands-map {:message command-server-message
                          :exit command-server-exit})

(defn- client-callback [in out]
  (println "Client connected.")
  (let [conn (ref {:in (reader in) :out (writer out)})]
    (open-message-pump conn server-commands-map)
    (while (nil? (:exit @conn)))
    (println "Client disconnected.")))

(defn start-server 
  [server-info] 
  (create-server (Integer. (:port server-info)) client-callback)
  (comment "Server code here"))
