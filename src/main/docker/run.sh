#!/bin/sh
echo "********************************************************"
echo "Waiting for the configuration server to start on port $CONFIGSERVER_PORT"
echo "********************************************************"
while ! `nc -z config.iamray.cn $CONFIGSERVER_PORT`; do sleep 3; done
echo "*******  Configuration Server has started"

echo "********************************************************"
echo "Waiting for the eureka server to start on port $EUREKASERVER_PORT"
echo "********************************************************"
while ! `nc -z discovery.iamray.cn $EUREKASERVER_PORT`; do sleep 3; done
echo "******* Eureka Server has started"

echo "********************************************************"
echo "Waiting for the ZIPKIN server to start  on port $ZIPKIN_PORT"
echo "********************************************************"
while ! `nc -z zipkin $ZIPKIN_PORT`; do sleep 10; done
echo "******* ZIPKIN has started"

echo "********************************************************"
echo "Waiting for the REDIS server to start  on port $REDIS_PORT"
echo "********************************************************"
while ! `nc -z redis $REDIS_PORT`; do sleep 10; done
echo "******* REDIS has started"


echo "********************************************************"
echo "Starting the Auth Server"
echo "********************************************************"
java -Xmx200m -Xms200m                                     \
     -Djava.security.egd=file:/dev/./urandom                \
     -Dspring.cloud.config.uri=$CONFIGSERVER_URI            \
     -Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI \
     -Dspring.redis.host=$REDIS_URI                         \
     -Dspring.profiles.active=$PROFILE                      \
-jar /usr/local/server/@project.build.finalName@.jar