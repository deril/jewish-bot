(ns jewish-bot.duckduckgo
  (:require [clojure.string :as s :only [blank? split join trim]]
            [clj-http.client :as http :only [get]]
            [jewish-bot.telegram.api :as api :only [send-text]]))

(def base-url "http://api.duckduckgo.com")

(defn get-answer [term]
  (let [no-res "No Results"]
    (if (s/blank? term)
      no-res
      (let [query {:q term :format "json"}
            res (http/get base-url {:as :json :query-params query})]
        (let [type (-> res :body :Type)]
          (case type
            ("D" "C") (-> res :body :AbstractURL)
            "N" no-res
            "A" (-> res :body :AbstractText)
            "E" (-> res :body :Redirect)
            no-res))))))


(defn go
  "Gets instant answer from DuckDuckGo"
  [token id params]
  (let [term (s/join " " (rest (s/split params #"\s")))]
    (api/send-text token id (get-answer term))))

