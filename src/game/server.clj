(ns game.server
  (:use [clojure.java.io :only [reader writer]]
        [server.socket :only [create-server]]))

(def standart-out *out*)

(defn logger-write
  [message]
  (binding [*out* standart-out]
    (println message)
    (flush)))

(defn read-message
  []
  (let [message (try (let [msg (read-line)]
                       (read-line)
                       msg)
                  (catch Exception e (logger-write "Socket faulted state.")))]
    (if-not (nil? message)
      (clojure.string/trim-newline message)
      message)))

(defn- client-callback [in out]
  (binding [*in* (reader in)
            *out* (writer out)
            *err* (writer System/err)]
    (loop [input (read-message)]
      (when-not (nil? input)
        (logger-write input)
        (if (= input "joke")
          (println "Haha thats funny!")
          (println (str "Why did you say " input " to me?!")))
        (flush)
        (when-not (nil? *in*)
          (recur (read-message)))))
    (logger-write "Client exiting...")))

(defn start-server [server-info] (create-server (Integer. (:port server-info)) client-callback))
