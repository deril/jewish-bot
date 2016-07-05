(ns jewish-bot.urban-dictionary
  (:require [clojure.string :as s :only [blank? split join trim]]
            [clj-http.client :as http :only [get]]
            [jewish-bot.telegram.api :as api :only [send-text]]))

(def base-url "http://api.urbandictionary.com/v0/define")

(defn get-definition [term]
  (let [no-res "No Results"]
    (if (s/blank? term)
      no-res
      (let [query {:term term}
            res (http/get base-url {:as :json :query-params query})]
        (if (= (-> res :body :result_type) "exact")
          (-> res :body :list first :definition)
          "No Results")))))

(defn ud
  "Gets definition of the word from Urban Dictionary"
  [token id params]
  (let [term (s/join " " (rest (s/split params #"\s")))]
    (api/send-text token id (get-definition term))))

