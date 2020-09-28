FROM java:8
VOLUME /tmp
ADD perssion-0.0.1-SNAPSHOT.jar /perssion.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/perssion.jar"]