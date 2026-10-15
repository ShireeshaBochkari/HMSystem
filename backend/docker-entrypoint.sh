#!/bin/sh
# Convert Render's postgres://user:pass@host:port/db into JDBC settings.
if [ -n "$DATABASE_URL" ] && [ -z "$DB_URL" ]; then
  rest="${DATABASE_URL#*://}"
  creds="${rest%%@*}"; hostpart="${rest#*@}"
  export DB_USER="${creds%%:*}"; export DB_PASS="${creds#*:}"
  case "$hostpart" in *:*) ;; *) hostpart="${hostpart%%/*}:5432/${hostpart#*/}";; esac
  export DB_URL="jdbc:postgresql://${hostpart}"
fi
exec java -XX:MaxRAMPercentage=75 -jar /app/hostel.jar
