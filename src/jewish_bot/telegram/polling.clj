(ns jewish-bot.telegram.polling
  "Declares long-polling routines to communicate with Telegram Bot API"
  (:require [clojure.core.async :refer [>!! <! go go-loop
                                        chan close! thread alts!!]]
            [jewish-bot.telegram.api :as api]))

(defn new-offset
  "Returns an offset for telegram updates"
  [updates default]
  (if (seq updates)
    (-> updates last :update_id inc)
    default))

(defn create-producer
  "Passed channel should be always empty.
  Close it to stop long-polling.
  Returns channel with updates from Telegram"
  [running token]
  (let [updates (chan)]
    (thread
      (loop [offset 0]
        (let [data (go (api/get-updates token {:offset offset}))
              [result _] (alts!! [running data])]
          (if-not result
            (close! updates)
            (do (doseq [upd result] (>!! updates upd))
                (recur (new-offset result offset)))))))
    updates))

(defn create-consumer
  "Creates a consumer from given handler function
  and channel with updates.

  Starts infinite loop inside go-routine
  that will pull messages from channel.

  Will be stopped when channel is closed."
  [updates handler]
  (go-loop []
    (when-let [data (<! updates)]
      (handler data)
      (recur))))

(defn start
  "Starts long-polling process"
  [token handler]
  (let [running (chan)
        updates (create-producer running token)]
    (create-consumer updates handler)
    running))

(defn stop
  "Stops everything"
  [running]
  (close! running))
