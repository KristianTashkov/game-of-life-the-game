(ns game.server
  (:use [clojure.java.io :only [reader writer]]
        [server.socket :only [create-server]]))

(def standart-out *out*)

(defn logger-write
  [message]
  (binding [*out* standart-out]
   (println message)
   (flush)))

(defn- client-callback [in out]
  (binding [*in* (reader in)
            *out* (writer out)
            *err* (writer System/err)]
      (loop [input (clojure.string/trim-newline (read-line))]
                (read-line)
                (logger-write input)
                (if (= input "joke")
                  (println "Haha thats funny!")
                  (println (str "Why did you say " input " to me?!")))
                (flush)
                (recur (clojure.string/trim-newline (read-line)))))
    (logger-write "Exiting..."))

(defn start-server [] (create-server (Integer. 3333) client-callback))
