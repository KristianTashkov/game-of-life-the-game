(ns game.logic.main
  (:use [game.logic.basic_implementation]))

(defn new-world
  []
  (empty-board))

(defn next-generation
  [world]
  (step world))

(defn living-cells
  [world]
  (seq world))
