#!/bin/bash
export JAVA_HOME=$JAVA_HOME
export PATH=$PATH
export CLASS_PATH=$CLASS_PATH
export JWT_SECRET=$JWT_SECRET
export NAVER_ID=$NAVER_ID
export NAVER_SECRET=$NAVER_SECRET
export MySQL_Username=MySQL_Username
export MySQL_Password=$MySQL_Password

CURRENT_PID=$(sudo lsof -i :8080)
#8080 포트 프로세스 종료
if [ -n "$CURRENT_PID" ]; then sudo kill -9 $CURRENT_PID
fi

cd /home/ubuntu/app/build/libs
sudo nohup java -jar todo-0.0.1-SNAPSHOT.jar

#sudo docker build -t ap .
#sudo docker run -d -p 8080:8080 ap

#cd /home/ubuntu/app/build/libs
#sudo fuser -k 8080/tcp
#nohup java -jar todo-0.0.1-SNAPSHOT.jar
