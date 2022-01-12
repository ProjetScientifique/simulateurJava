FROM maven:3.5.4-jdk-8-alpine as maven
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn clean compile assembly:single

FROM openjdk:8u171-jre-alpine
WORKDIR /simulator
COPY --from=maven target/Simulator-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./Simulator.jar
CMD ["java", "-cp", "Simulator.jar", "launcher.Launcher"]