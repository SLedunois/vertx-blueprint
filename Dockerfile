FROM maven:3.6.3-jdk-8-slim AS build
COPY . /usr/build/project
WORKDIR /usr/build/project
RUN mvn clean install package -DskipTests

FROM openjdk:8-jre-alpine
ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_NAME store-1.0.0-SNAPSHOT-fat.jar
EXPOSE 8000
EXPOSE 8001
RUN mkdir -p $VERTICLE_HOME
COPY --from=build /usr/build/project/target/$VERTICLE_NAME $VERTICLE_HOME
COPY --from=build /usr/build/project/cluster.xml $VERTICLE_HOME

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_NAME -cluster -Dvertx.hazelcast.config=$VERTICLE_HOME/cluster.xml"]
