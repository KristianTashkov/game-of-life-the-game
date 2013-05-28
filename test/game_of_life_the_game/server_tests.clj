(ns game-of-life-the-game.server_tests
  (:use clojure.test
    game.connection.communication
    game.connection.server
    game.connection.client
    [clojure.java.io :only [reader writer]]
    [game.state.server_state]
    [cheshire.core :only (generate-string parse-string)]))

(def test-server-info {:name "localhost" :port 3333})

(defn message-reader [message-queue connection]
  (while (:alive @connection)
    (when-let [msg (read-message connection)]
      (swap! message-queue conj msg))))

(defn await-server [connection message-queue]
  (write-message connection {:type :ping})
  (while (not (some #(= % {:type "pong"}) @message-queue)))
  (swap! message-queue (fn [coll] (remove #(= % {:type "pong"}) coll))))

(deftest server-test
  (testing "Server is adding and removing clients"
    (let [message-queue (atom [])
          server-future (future (start-server test-server-info))
          connection (connect test-server-info)
          client-future (future (message-reader message-queue connection))]
      (await-server connection message-queue)
      (is (= 1 (count @clients)))
      (write-message connection {:type :exit})
      (await-server connection message-queue)
      (is (= 0 (count @clients)))
      (future-cancel server-future)
      (future-cancel client-future)
      (shutdown-agents))))
