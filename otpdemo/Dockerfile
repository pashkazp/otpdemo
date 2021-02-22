# to make one-time-password.jar
# $ mvn clean package
#
# to build docker
# $ docker build -t otp-demo:0.0.1 .
#
# to run docker
# $ docker run --name otp-demo -d -p 8080:8080 -t otp-demo:0.0.1
# 
FROM adoptopenjdk/openjdk13:alpine-jre
WORKDIR /opt/app
ARG JAR_FILE=target/one-time-password.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]