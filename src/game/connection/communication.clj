(ns game.connection.communication
  (:use [game.state.server_state]
    [cheshire.core :only (generate-string parse-string)]))

(defn write-message [connection msg]
  (try
    (binding [*out* (:out @connection)]
      (when msg
        (println (generate-string msg))))
    (catch Exception e (do
                         (println connection)
                         (println msg)
                         (remove-client connection)
                         (println "Socket faulted state.")))))

(defn read-message
  [connection]
  (binding [*in* (:in @connection)]
    (try
      (when-let [message (read-line)]
        (-> message clojure.string/trim-newline (parse-string true)))
      (catch Exception e (do
                           (remove-client connection)
                           (println "Socket faulted state."))))))

(defn open-message-pump
  [commands connection]
  (while (:alive @connection)
    (when-let [msg (read-message connection)]
      (when-let [command ((keyword (:type msg)) commands)]
        (command (assoc msg :connection connection))))))
