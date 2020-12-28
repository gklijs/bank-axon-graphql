#!/usr/bin/env bash

docker-compose -f docker-axon-server.yml -f docker-backend.yml -f docker-initializer.yml -f docker-frontend.yml down
