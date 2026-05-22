# 1단계: 빌드 스테이지
FROM amazoncorretto:21-alpine-jdk AS builder
WORKDIR /app

# Gradle 래퍼와 설정 파일만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# [핵심] 라이브러리 의존성만 먼저 다운로드 (코드 변경돼도 라이브러리 다운로드는 캐싱됨)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스코드 복사 및 빌드 (테스트 제외)
COPY src src
RUN ./gradlew build -x test --no-daemon

# 2단계: 실행 스테이지 (용량 최소화)
FROM amazoncorretto:21-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일만 복사 (plain.jar 제외하고 실제 실행용 jar만 타겟팅)
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
