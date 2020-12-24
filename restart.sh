#!/usr/bin/env bash

./clean.sh &&
  docker-compose -f docker-cluster.yml up -d &&
  sleep 10 &&
  docker-compose -f docker-bank.yml up -d
