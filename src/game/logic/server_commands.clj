(ns game.logic.server_commands
  (:use [game.state.server_state]
    [game.logic.main]
    [game.connection.communication :only [write-message read-message]]
    [cheshire.core :only (generate-string parse-string)]))

(defn update-board-clients
  []
  (let [living (living-cells @server-board)]
    (println living)
    (doseq [client @clients]
      (let [connection (val client)]
        (write-message connection {:type :world-update, :world living})))))

(defn update-playing-clients
  []
  (doseq [client @clients]
    (let [connection (val client)]
      (write-message connection {:type :playing-changed, :state @server-playing}))))

(defn command-server-change-cell
  [{:keys [cell state] :as args}]
  (dosync
    (alter server-board (if state conj disj) cell)
    (update-board-clients)))

(defn command-server-exit
  [args])
  ;(dosync
    ;(alter connection assoc :alive false)))

(defn command-server-play-pause
  [{:keys [state] :as args}]
  (dosync
    (ref-set server-playing state))
  (update-playing-clients)
  (println (if state "Playing.." "Paused..")))