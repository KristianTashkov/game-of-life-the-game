(ns game.core
  (:gen-class)
  (:use [game.connection.client :only [start-connection connect]])
  (:use [game.connection.server :only [start-server]]))

(def server-info {:name "localhost" :port 3333})

(defn -main [& args]
  (print "Server or Client (s/c)? ")
  (flush)
  (let [choice (if (empty? args)
                 (read-line)
                 (first args))]
    (cond
      (= choice "s") (start-server server-info)
      (= choice "c") (start-connection server-info)
      :else (recur []))))
