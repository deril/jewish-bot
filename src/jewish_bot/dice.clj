(ns jewish-bot.dice
  (:require [jewish-bot.telegram.api :as api :only [send-text]]))

(defn roll-dice
  "Roll x number of y sided dice"
  [x y]
  (loop [cnt x total 0]
    (if (zero? cnt)
      total
      (recur (- cnt 1) (+ total (+ (rand-int y) 1))))))

(defn build-message
  [name dice]
  (str
   (format "%s: %c " name (int 127922))
   dice))

(defn roll
  "Roll a dice"
  [token id params username]
  (api/send-text token id (build-message username (roll-dice 1 6))))
