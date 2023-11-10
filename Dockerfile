#FROM openjdk:17-slim
#WORKDIR /usr/src/app
#
#ARG JAR_PATH=./build/libs
#COPY ${JAR_PATH}/todo-0.0.1-SNAPSHOT.jar ${JAR_PATH}/todo-0.0.1-SNAPSHOT.jar
#
#CMD ["java","-jar","./build/libs/todo-0.0.1-SNAPSHOT.jar"]

# 기본 이미지로 openjdk 17 slim 이미지 사용
FROM openjdk:17-slim

# 컨테이너 내부의 작업 디렉터리 설정
WORKDIR /usr/src/app

# 컨테이너 내부에 JAR 파일이 복사될 경로 정의
ARG JAR_PATH=./build/libs

# 호스트에서 컨테이너 내의 지정된 경로로 JAR 파일 복사
COPY ${JAR_PATH}/todo-0.0.1-SNAPSHOT.jar ${JAR_PATH}/todo-0.0.1-SNAPSHOT.jar

# OpenJDK 17 설치 (이미 설치되어 있지 않은 경우에만 설치)
RUN apt-get update && apt-get install -y openjdk-17-jdk

# JWT_SECRET을 위한 환경 변수 설정
ENV JWT_SECRET=$JWT_SECRET

# 애플리케이션을 실행하는 명령어 정의
CMD ["/usr/local/openjdk-17/bin/java", "-jar", "./build/libs/todo-0.0.1-SNAPSHOT.jar"]
