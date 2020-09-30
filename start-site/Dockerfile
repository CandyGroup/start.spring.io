FROM openjdk:8-jre-alpine
ENV LANG=zh_CN.UTF-8 \
    LC_ALL=zh_CN.UTF-8 \
    TZ=Asia/Shanghai
EXPOSE 8080
COPY ./dependency/BOOT-INF/lib /app/lib
COPY ./dependency/META-INF /app/META-INF
COPY ./dependency/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","io.spring.start.site.StartApplication"]
