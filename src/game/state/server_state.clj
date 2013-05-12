(ns game.state.server_state
  (:use [game.logic.main]))

(def current-id (atom 0))
(def clients (ref {}))
(def server-board (ref (new-world)))
(def server-playing (ref false))