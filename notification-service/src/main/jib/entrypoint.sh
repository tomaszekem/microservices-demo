#!/bin/sh

echo "Starting notification-service application"
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "com.tomaszekem.notificationservice.NotificationServiceApplication"  "$@"
