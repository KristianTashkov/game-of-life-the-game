(ns game.connection.communication)

(defn write-message [conn msg]
  (binding [*out* (:out @conn)]
    (when msg
      (let [trimmed-msg (clojure.string/trim-newline msg)]
        (when msg
          (println msg))))))

(defn read-message
  [conn]
  (binding [*in* (:in @conn)]
    (try 
      (let [message (read-line)]
        (when message
          (clojure.string/trim-newline message)))
      (catch Exception e (do
                           (dosync (alter conn #(assoc % :exit true)))
                           (println "Socket faulted state."))))))

(defn open-message-pump
  [conn commands]
  (future (while (nil? (:exit @conn))
              (let [msg (read-message conn)]
                (when msg
                  (let [command ((keyword msg) commands)]
                    (when command
                      (command conn))))))))