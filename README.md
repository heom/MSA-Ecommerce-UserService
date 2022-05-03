# MSA Ecommerce
- [MSA Ecommerce] User Service Instance
![user](https://user-images.githubusercontent.com/42602972/165477509-8579620c-baa4-4dec-a1aa-d5b893cce311.png)

## 프로젝트 개발 구성
- Java 8
- Spring Boot(+Maven) 2.6.4
- Spring Data JPA 2.6.4
- Spring Security 2.6.4
- Spring Actuator 2.6.4
- JWT jwtt 0.9.1
- Junit 5
- Spring Cloud 2021.0.1
  - Eureka-client 3.1.1
  - Config 3.1.1
  - Bus-amqp(RabbitMq) 3.1.1
  - Openfeign 3.1.1 
  - Resilience4j 2.1.1
  - Sleuth 3.1.1
  - Zipkin 2.2.3 
- H2(Embedded) 1.4.200
- Prometheus

## 프로젝트 서버 구성
- IP : localhost
- PORT : 8888
- RabbitMq : localhost:5672

## 추가 정리
- **로드밸런싱 테스트를 위한 여러개의 서버 구동방법**
  - Intellij Run
  - Terminal
    1. $ mvnw spring-boot:run
    2. $ mvnw clean compile package  
       $ java -jar ./target/user-service-0.0.1-SNAPSHOT.jar
