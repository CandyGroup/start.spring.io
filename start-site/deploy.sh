#!/usr/bin/env bash
cp -r ../start-client/public/. src/main/resources/static
mvn clean package -DskipTests=true -Ddisable.checks=true
cp Dockerfile target/
cd target/ || exit
mkdir dependency && cd dependency || exit
jar -xf ../start-site-exec.jar
cd ..
docker build -t jy97799/start.jiangy.me:1.0 .
docker push jy97799/start.jiangy.me:1.0
