(defproject jewish-bot "0.2.0-SNAPSHOT"
  :description "Telegram bot for Post-Apocalyptic B. community"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.macro "0.1.5"]
                 [org.clojure/core.async "0.2.374"]
                 [cheshire "5.6.3"]
                 [clj-http "2.1.0"]
                 [environ "1.0.3"]
                 [commons-daemon "1.0.15"]]
  :main jewish-bot.core
  :profiles {:uberjar {:aot :all}})
