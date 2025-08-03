#!/bin/bash

echo "⏳ Đang chờ MySQL sẵn sàng..."

until mysql -h mysql -u root -p1234 -e "SELECT 1" > /dev/null 2>&1; do
  echo "❗ MySQL chưa sẵn sàng, thử lại sau 2s..."
  sleep 2
done

echo "✅ MySQL đã sẵn sàng, chạy app!"

exec java -jar app.jar
