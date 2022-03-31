#!/usr/bin/env bash

docker-compose -f docker-initializer.yml up -d &&
sleep 10 &&
for i in 1 2 3 4 5 6 7 8 9 10; do
  failures=$(docker inspect -f '{{ .State.ExitCode }}' initializer)
  if [[ "$failures" == "0" ]]; then
    echo -e "Successful initialization in loop ${i}"
    exit 0
  fi
  sleep 5
done
exit 1