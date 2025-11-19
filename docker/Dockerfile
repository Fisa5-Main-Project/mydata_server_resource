# eclipse-temurin 17 버전 (alpine 기반) 사용
FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar knowwhohow-resource.jar
ENTRYPOINT ["java", "-jar", "knowwhohow-resource.jar"]
LABEL authors="khj"