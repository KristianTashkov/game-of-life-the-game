(ns game.state.client_state)

(def client-board (ref #{}))
(def client-playing (atom false))
(def server-connection :null)