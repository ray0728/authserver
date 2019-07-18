![travis_ci](https://www.travis-ci.org/ray0728/authserver.svg?branch=master)
# authserver
## 说明
基于Spring OAuth2的权限授权服务器，提供基于authorization code的权限验证方案。
* 服务会将生成的token信息，根据tokenkey.jks([可替换](#tokenkey]))进行加密并生成JWT Token。
* JWT Token的生命周期交由Redis服务维护。
* 对authorization code流程中RedirectURL有**特殊处理**（仅认可来源于[Gatewayserver][1]的请求。
* 服务本身**不维护**用户信息相关的存储，用户相关信息依赖[AccountServer][2]。

### tokenkey
该文件可通过JDK相关工具生成，并将生成时所使用的密钥保存在配置文件当中
```java
tokenkey.jks.private.key = 123456abcdefg
```
或通过参数方式传入
```java
-Dtokenkey.jks.private.key=123456abcdefg
```

## 运行方式：  
application.properties中并**不包含**完整配置信息，所以**不支持**直接运行  
* java 方式

```java
java
-Djava.security.egd=file:/dev/./urandom                  \
-Dspring.cloud.config.uri=$CONFIGSERVER_URI              \
-Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI \
-Dspring.redis.host=$REDIS_URI                           \
-Dspring.profiles.active=$PROFILE                        \
-jar target.jar
```
* docker 方式  
建议用docker-compose方式运行

```docker
authserver:
    image: ray0728/authserv:1.0
    ports:
      - "10004:10004"
    environment:
      REDIS_PORT: "6379"
      ZIPKIN_PORT: "9411"
      CONFIGSERVER_PORT: "10002"
      EUREKASERVER_PORT: "10001"
      RESOURCE_PORT: "10005"
      CONFIGSERVER_URI: 你的配置服务器地址
      EUREKASERVER_URI: EUREKA地址
      KAFKA_URI: KAFKA IP:PORT
      REDIS_URI: REDIS服务IP
      ZIPKIN_URI: ZIPKIN_URI地址
      PROFILE: "dev"
```  
关于Docker  
编译完成的Docker位于[Dockerhub][3]请结合Release中的[Tag标签][4]获取对应的Docker

[1]:https://github.com/ray0728/gatewayserver
[2]:https://github.com/ray0728/accountserver
[3]:https://hub.docker.com/r/ray0728/configserv/tags
[4]:https://github.com/ray0728/configserver/tags
