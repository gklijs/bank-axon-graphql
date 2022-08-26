#!/usr/bin/env bash
  mvn -f core-api clean install &&
  mvn -f legacy-api clean install &&
  mvn -f initializer clean package &&
  mvn -f command-handler clean package &&
  mvn -f graphql-endpoint clean package &&
  mvn -f projector clean package &&
  mvn -f kafka-event-emitter clean package &&
  mvn -f kafka-legacy-consumer clean package &&
  mvn -f kafka-legacy-producer clean package &&
  docker-compose -f docker-backend.yml -f docker-initializer.yml -f docker-frontend.yml build --no-cache
