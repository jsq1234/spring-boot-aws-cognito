#!/bin/bash

if [[ "$1" == "--build" ]] || [[ ! -d "./target" ]]; then
    ./mvnw clean package -DskipTests -T 8
    docker-compose up --build
else
    docker-compose up
fi

docker-compose down
