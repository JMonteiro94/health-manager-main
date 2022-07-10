FROM adoptopenjdk/openjdk:8-jdk-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    SLEEP=5 \
    MY_HOME=/home/hmm

RUN useradd -ms /bin/sh hmm
WORKDIR /home/hmm
ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown hmm:hmm entrypoint.sh
USER hmm

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8081
ADD ./target/*.jar app.jar