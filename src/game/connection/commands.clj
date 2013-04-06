(ns game.connection.commands
   (:use [game.connection.communication :only [write-message read-message]]))

(defn send-command
  [conn type command-body]
  (write-message conn type)
  (command-body))

;; Client commands
(defn command-client-message
  [conn]
  (let [msg (read-message conn)]
    (when msg
      (println msg))))

;; Server commands
(defn command-server-message
  [conn]
  (let [msg (read-message conn)]
    (when msg
      (do
        (println msg)
        (if (= msg "joke")
          (send-command conn "message" #(write-message conn "Thats a funny joke!"))
          (send-command conn "message" #(write-message conn "Thats was so boring!")))))))

(defn command-server-exit
  [conn]
  (dosync 
    (alter conn #(assoc % :exit true))))