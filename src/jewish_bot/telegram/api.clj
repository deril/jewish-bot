(ns jewish-bot.telegram.api
  (:require [clj-http.client :as http]))

(def base-url "https://api.telegram.org/bot")

(defn get-updates
  "Receive updates from Bot via long-polling endpoint"
  [token {:keys [limit offset timeout]}]
  (let [url (str base-url token "/getUpdates")
        query {:timeout (or timeout 1)
               :offset  (or offset 0)
               :limit   (or limit 100)}
        resp (http/get url {:as :json :query-params query})]
    (-> resp :body :result)))

; (defn set-webhook
;   "Register WebHook to receive updates from chats"
;   [token webhook-url]
;   (let [url   (str base-url @token "/setWebhook")
;         query {:url webhook-url}]
;     (http/get url {:as :json :query-params query})))

(defn send-text
  "Sends message to the chat"
  ([token chat-id text] (send-text token chat-id {} text))
  ([token chat-id options text]
   (let [url (str base-url token "/sendMessage")
         query (into {:chat_id chat-id :text text} options)
         resp (http/get url {:as :json :query-params query})]
   (-> resp :body))))
