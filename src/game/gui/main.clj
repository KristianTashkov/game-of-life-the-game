(ns game.gui.main
  (use [seesaw core color graphics behave]
    [game.logic.main]
    [game.state.client_state]
    [game.connection.communication :only [write-message]]))

(def cell-size 20)
(def board-margin 10)
(declare the-frame)

(defn project [x]
  (+ board-margin (* x cell-size)))

(defn rect-from [[x y]]
  (letfn [(project [n] (+ board-margin (* n cell-size)))]
    (rect (project x) (project y) cell-size cell-size)))

(defn cell-from [point]
  (letfn [(project [n] (int (/ (- n board-margin) cell-size)))]
    [(-> point .x project)
     (-> point .y project)]))

(defn draw-world [c g]
  (dosync
    (doseq [cell (living-cells @client-board)]
      (draw g (rect-from cell) (style :background :green)))))

(defn make-panel []
  (border-panel
    :north (flow-panel :align :center
             :items [(button :text "Start/Stop" :class :play-pause)])
    :center (canvas :paint draw-world
              :class :world
              :background :black)
    :vgap 5
    :hgap 5
    :border 5))

(defn make-frame []
  (frame :title   "Game of life"
    :size    [600 :by 600]
    :content (make-panel)))


(defn redisplay [root]
  (config! (select root [:.world])   :paint draw-world))

(defn add-behaviors [root]
  (listen (select root [:.world]) :mouse-clicked (fn [e]
                                                   (let [cell (cell-from (.getPoint e))]
                                                     (write-message server-connection {:type :change-cell,
                                                                                       :cell cell
                                                                                       :state (not (alive? @client-board cell))}))))
  (listen (select root [:.play-pause])  :mouse-clicked (fn [e] (if @client-playing
                                                                 (write-message server-connection {:type :play-pause, :state false})
                                                                 (write-message server-connection {:type :play-pause, :state true})))))

(defonce the-frame (make-frame))

(defn start-game []
  (native!)
  (config! the-frame :content (make-panel))
  (show! the-frame)
  (add-behaviors the-frame)
  (redisplay the-frame))
