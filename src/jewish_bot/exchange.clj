(ns jewish-bot.exchange
  (:require [clojure.string :as s :only [split replace]]
            [clj-http.client :as http :only [get]]
            [jewish-bot.telegram.api :as api :only [send-text]]))

(def base-url "https://query.yahooapis.com/v1/public/yql")

(defn build-query
  [pair]
  (str "select Rate from yahoo.finance.xchange where pair in (\"" pair "\")"))

(defn get-rate
  "Gets exchange rate from yahoo finance"
  [currencies]
  (let [pair (s/replace currencies #":" "")
        query {:q (build-query pair)
               :format "json"
               :env "store://datatables.org/alltableswithkeys"}
        resp (http/get base-url {:as :json :query-params query})]
    (if-let [results (-> resp :body :query :results)]
      (let [rate (read-string (-> results :rate :Rate))]
        (if (number? rate) rate)))))

(defn exchange
  "Exchange currencies. Allowed params /ex EUR:USD 15"
  [token id params]
  (let [[_ currencies value] (s/split params #"\s")]
    (if (and (not (nil? value)) (number? (read-string value)))
      (if-let [rate (get-rate currencies)]
        (let [amount (read-string value)]
          (api/send-text token id (format "%.2f %s \nPlus %.2f for your broker 😏"
                                          (* rate amount)
                                          (re-find #".{3}$" currencies)
                                          (* amount 0.01))))
        (api/send-text token id "Wrong currency pair"))
      (api/send-text token id "Wrong currency amount"))))
