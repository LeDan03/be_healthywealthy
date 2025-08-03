FROM openjdk:17-jdk-slim

# Cài client MySQL chính chủ
RUN apt-get update && apt-get install -y default-mysql-client

# Tạo thư mục làm việc
WORKDIR /app

# Copy file jar vào
COPY AnalyticsService/target/*.jar app.jar

# Copy script chờ MySQL
COPY wait-for-mysql.sh /app/wait-for-mysql.sh

# Cho phép thực thi script
RUN chmod +x /app/wait-for-mysql.sh

EXPOSE 8083

# Dùng script làm entrypoint
ENTRYPOINT ["/app/wait-for-mysql.sh"]
