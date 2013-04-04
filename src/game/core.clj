(ns game.core
  (:gen-class)
  (:use [game.client :only [start-connection]])
  (:use [game.server :only [start-server]]))

(def server-info {:name "localhost" :port 3333})

(defn -main [& args]
  (print "Server or Client (s/c)? ")
  (flush)
  (let [choice (if (empty? args)
                 (read-line)
                 (first args))]
    (cond 
      (= choice "s") (do
                       (start-server server-info)
                       (println "Server started succesfully"))
      (= choice "c") (do 
                       (start-connection server-info)
                       (println "Connection established"))
      :else (recur []))))
