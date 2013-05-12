(ns game.connection.commands
  (:use [game.connection.communication :only [write-message read-message *connection*]]))

;; Client commands
(defn command-client-message
  [{:keys [msg]}]
  (when msg
    (println msg)))

;; Server commands
(defn command-server-message
  [{:keys [msg] :as args}]
  (when msg
    (do
      (println msg)
      (if (= msg "joke")
        (write-message {:type :message, :msg "Thats a funny joke!"})
        (write-message {:type :message, :msg "That was boring!"})))))

(defn command-server-exit
  [args]
  (dosync
    (alter *connection* assoc :alive false)))
