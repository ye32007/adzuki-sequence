FROM centos
RUN yum -y install java
RUN echo "Asia/Shanghai" > /etc/timezone
VOLUME /tmp
VOLUME /usr/app/logs
WORKDIR /usr/app
ADD ./adzuki-sequence-web/target/adzuki-sequence-web.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]