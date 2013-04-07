(ns game.connection.commands
  (:use [game.connection.communication :only [write-message read-message *connection*]]))

(defn send-command
  [type command-body]
  (write-message type)
  (command-body))

;; Client commands
(defn command-client-message
  []
  (let [msg (read-message)]
    (when msg
      (println msg))))

;; Server commands
(defn command-server-message
  []
  (let [msg (read-message)]
    (when msg
      (do
        (println msg)
        (if (= msg "joke")
          (send-command "message" #(write-message "Thats a funny joke!"))
          (send-command "message" #(write-message "Thats was so boring!")))))))

(defn command-server-exit
  []
  (dosync
    (alter *connection* assoc :alive false)))
