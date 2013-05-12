(ns game.connection.commands
  (:use [game.state.server_state]
    [game.state.client_state]
    [game.logic.main]
    [game.connection.communication :only [write-message read-message *connection*]]
    [cheshire.core :only (generate-string parse-string)]))

(defn update-board-clients
  []
  (let [living (living-cells @server-board)]
    (println living)
    (doseq [client @clients]
      (binding [*connection* (val client)]
        (write-message {:type :world-update, :world living})))))

;; Client commands

(defn command-client-world-update
  [{:keys [world]}]
  (dosync
    (ref-set client-board world)
    (println world)))

;; Server commands

(defn command-server-change-cell
  [{:keys [cell state] :as args}]
  (dosync
    (alter server-board (if (parse-string state) conj disj) (parse-string cell))
    (update-board-clients)))

(defn command-server-exit
  [args]
  (dosync
    (alter *connection* assoc :alive false)))

(defn command-server-play-pause
  [{:keys [state] :as args}]
  (dosync
    (ref-set server-playing state))
  (println (if state "Playing.." "Paused")))
