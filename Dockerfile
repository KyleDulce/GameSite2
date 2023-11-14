FROM openjdk:17
ARG DEFAULT_VERSION
ENV VERSION $DEFAULT_VERSION
ADD "/Server/build/libs/gamesite-server-jar-with-dependencies.jar" "gamesite-server.jar"
EXPOSE 8080
ENTRYPOINT java -jar gamesite-server.jar