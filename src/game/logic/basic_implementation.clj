(ns game.logic.basic_implementation)
;; This is the game of life implementation from the "Clojure Programming" book.

(defn empty-board [] #{})

(defn stepper
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(def step (stepper neighbours #{3} #{2 3}))

