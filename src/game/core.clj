(ns game.core
  (:gen-class)
  (:use [game.client :only [start-connection]])
  (:use [game.server :only [start-server]]))

(defn -main [& args]
  (println "Server or Client (s/c)?")
  (flush)
  (let [choice (read-line)]
    (cond 
      (= choice "s") (do
                       (start-server)
                       (println "Server started succesfully"))
      (= choice "c") (do 
                       (start-connection)
                       (println "Connection established"))
      :else (recur args))))
