#!/bin/bash

# Environment Variables for St. Peter's College Certi-Portal
# UPDATE THESE VALUES AS NEEDED

export DB_USERNAME="admin"
export DB_PASSWORD="admin"
export MAIL_PASSWORD="!r0n M@n1729" # Change this!
export MAIL_USERNAME="office.stpeters@gmail.com"
export RAZORPAY_KEY_ID="rzp_test_CHANGE_THIS"   # Change this!
export RAZORPAY_KEY_SECRET="rzp_secret_CHANGE_THIS" # Change this!

echo "Starting St. Peter's College Certi-Portal..."
echo "Database User: $DB_USERNAME"

# Run the application
./mvnw spring-boot:run -DskipTests
