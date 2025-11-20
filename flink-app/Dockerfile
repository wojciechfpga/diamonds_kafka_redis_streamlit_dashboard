FROM eclipse-temurin:11-jdk-jammy

WORKDIR /opt/flink_job

# Instalacja narzędzi
RUN apt-get update && apt-get install -y curl unzip wget

# Instalacja sbt
RUN curl -L -o sbt.zip https://github.com/sbt/sbt/releases/download/v1.9.9/sbt-1.9.9.zip && \
    unzip sbt.zip -d /opt/sbt && \
    ln -s /opt/sbt/sbt/bin/sbt /usr/bin/sbt && \
    rm sbt.zip

# Pobranie Flinka (cache’owane dopóki wersja się nie zmieni)
RUN wget https://archive.apache.org/dist/flink/flink-1.15.3/flink-1.15.3-bin-scala_2.12.tgz && \
    tar -xzf flink-1.15.3-bin-scala_2.12.tgz -C /opt && \
    mv /opt/flink-1.15.3 /opt/flink && \
    rm flink-1.15.3-bin-scala_2.12.tgz

ENV PATH="/opt/flink/bin:$PATH"

# Dopiero teraz kopiujemy kod
COPY . .

COPY /schemas/diamond.avsc src/main/resources/diamond.avsc

RUN cp flink-conf.yaml /opt/flink/conf/flink-conf.yaml
RUN sbt compile && sbt assembly

#CMD ["java", "-jar", "target/scala-2.12/flink-app-assembly-0.1.jar"]
