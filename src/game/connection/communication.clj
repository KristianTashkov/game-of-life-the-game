(ns game.connection.communication
  (:use [game.state.server_state]
    [cheshire.core :only (generate-string parse-string)]))

(def ^:dynamic *connection* (ref {:alive false}))

(defn write-message [msg]
  (try
    (binding [*out* (:out @*connection*)]
      (when msg
        (println (generate-string msg))))
    (catch Exception e (do
                         (remove-client *connection*)
                         (println "Socket faulted state.")))))

(defn read-message
  []
  (binding [*in* (:in @*connection*)]
    (try
      (when-let [message (read-line)]
        (-> message clojure.string/trim-newline (parse-string true)))
      (catch Exception e (do
                           (remove-client *connection*)
                           (println "Socket faulted state."))))))

(defn open-message-pump
  [commands]
  (while (:alive @*connection*)
    (when-let [msg (read-message)]
      (when-let [command ((keyword (:type msg)) commands)]
        (command msg)))))
