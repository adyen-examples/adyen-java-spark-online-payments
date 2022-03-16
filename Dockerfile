FROM --platform=linux/amd64 amazoncorretto:11-alpine-jdk
COPY build/libs/adyen-java-online-payments-fat-0.1.jar adyen-java-online-payments-fat-0.1.jar
COPY src/main/resources/templates src/main/resources/templates
ENTRYPOINT ["java","-jar","/adyen-java-online-payments-fat-0.1.jar"]
