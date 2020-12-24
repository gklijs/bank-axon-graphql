#!/usr/bin/env bash
  mvn -f core-api clean install &&
  mvn -f command-handler clean package &&
  mvn -f graphql-endpoint clean package &&
  mvn -f projector clean package &&
  docker-compose -f docker-bank.yml build --no-cache
