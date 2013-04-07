(ns game.connection.communication)

(def ^:dynamic *connection* (ref {:alive false}))

(defn write-message [msg]
  (binding [*out* (:out @*connection*)]
    (when msg
      (let [trimmed-msg (clojure.string/trim-newline msg)]
        (when msg
          (println msg))))))

(defn read-message
  []
  (binding [*in* (:in @*connection*)]
    (try
      (let [message (read-line)]
        (when message
          (clojure.string/trim-newline message)))
      (catch Exception e (do
                           (dosync (alter *connection* assoc :alive false))
                           (println "Socket faulted state."))))))

(defn open-message-pump
  [commands]
  (while (:alive @*connection*)
    (let [msg (read-message)]
      (when msg
        (let [command ((keyword msg) commands)]
          (when command
            (command)))))))
