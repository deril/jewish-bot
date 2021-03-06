(ns jewish-bot.core
  (:require [jewish-bot.telegram.handlers :refer :all]
            [jewish-bot.telegram.polling :as p]
            [jewish-bot.telegram.api :as api]

            [environ.core :refer [env]]

            [jewish-bot.exchange :as exchange  :refer [exchange]]
            [jewish-bot.urban-dictionary :as urban-dictionary :refer [ud]]
            [jewish-bot.duckduckgo :as duckduckgo :refer [go]]
            [jewish-bot.dice :as dice :only [roll]])
  (:import [org.apache.commons.daemon Daemon DaemonContext])
  (:gen-class
    :implements [org.apache.commons.daemon.Daemon]))

(def state (atom {}))
(def token (env :telegram-token))

(defhandler bot-api
  (command "help" {{id :id} :chat}
           (api/send-text token id "Help!"))
  (command "start" {user :user} (println "User" user "joined"))
  (command "ex"
           {attributes :text {chat-id :id} :chat}
           (exchange/exchange token chat-id attributes))
  (command "ud"
           {attributes :text {chat-id :id} :chat}
           (urban-dictionary/ud token chat-id attributes))
  (command "go"
           {attributes :text {chat-id :id} :chat}
           (duckduckgo/go token chat-id attributes))
  (command "dice"
           {attributes :text {username :username} :from {chat-id :id} :chat}
           (dice/roll token chat-id attributes username)))

(def channel (p/start token bot-api))

;; Demonize
(defn init
  [args]
  (swap! state assoc :running true))

(defn start
  []
  (while (:running @state)
    channel))

(defn stop
  []
  (swap! state assoc :running false)
  (p/stop channel))

(defn -init [this ^DaemonContext context]
  (init (.getArguments context)))

(defn -start [this]
  (future (start)))

(defn -stop [this]
  (stop))

(defn -main [& args]
  (init args)
  (start))
