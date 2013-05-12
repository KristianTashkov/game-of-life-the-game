(ns game.state.server_state
  (:use [game.logic.main]))

(def is-server (atom false))
(def current-id (atom 0))
(def clients (ref {}))
(def server-board (ref (new-world)))
(def server-playing (ref false))

(defn add-client
  [connection]
  (println (str "Client #" (:id @connection) " connected."))
  (dosync
    (alter clients assoc (:id @connection) connection)))

(defn remove-client
  [connection]
  (dosync
    (alter connection assoc :alive false)
    (when @is-server
      (alter clients dissoc (:id @connection))))
  (when @is-server
    (println (str "Client #" (:id @connection) " disconnected."))))
