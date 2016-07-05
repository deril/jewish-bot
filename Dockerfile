FROM clojure
RUN mkdir -p /usr/src/jewish-bot
WORKDIR /usr/src/jewish-bot
COPY project.clj /usr/src/jewish-bot/
RUN lein deps
COPY . /usr/src/jewish-bot
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" bot-standalone.jar
CMD ["java", "-jar", "bot-standalone.jar"]
