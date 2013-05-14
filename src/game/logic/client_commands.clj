(ns game.logic.client_commands
  (:use [game.state.client_state]))

(defn command-client-world-update
  [{:keys [world]}]
  (dosync
    (ref-set client-board world)
    (println world)))
