FROM eclipse-temurin:11-jre-focal
EXPOSE 8089
# ENV LOGGING_FILE_NAME=/tmp/app.log

CMD ["java", "--add-modules", "java.se", "--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.nio=ALL-UNNAMED", "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.management/sun.management=ALL-UNNAMED", "--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED", "-jar", "/opt/app/japp.jar"]

RUN mkdir /opt/app
COPY ./target/hoomepoint-0.0.1-SNAPSHOT.jar /opt/app/japp.jar
