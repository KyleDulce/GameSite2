FROM openjdk:17
ARG DEFAULT_VERSION
ENV VERSION $DEFAULT_VERSION
ADD "/Server/build/libs/gamesite2-${VERSION}.jar" "gamesite2-${VERSION}.jar"
EXPOSE 8080
ENTRYPOINT java -jar gamesite2-${VERSION}.jar