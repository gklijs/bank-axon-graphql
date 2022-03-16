#!/usr/bin/env bash

./clean.sh &&
  docker-compose -f docker-axon-server.yml up -d &&
  sleep 10 &&
  docker-compose -f docker-backend.yml up -d &&
  sleep 5 &&
  ./initialize.sh &&
  docker-compose -f docker-frontend.yml up -d
