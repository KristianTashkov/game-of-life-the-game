(ns game.logic.client_commands
  (:use [game.state.client_state]
    [game.gui.main :only [redisplay the-frame]]))

(defn command-client-world-update
  [{:keys [world]}]
  (dosync
    (ref-set client-board (set world))
    (redisplay the-frame)))

(defn command-client-playing-changed
  [{:keys [state]}]
  (reset! client-playing state))
