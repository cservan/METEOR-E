#/usr/bin/env bash

if [ $# -lt 1 ]; then
    echo "Usage: $0 <tmp file>"
    exit 1
fi

cd wmt14-metrics-task
./compute-system-correlations --metrics $1 --human human-2014-05-16.scores --directions de-en fr-en ru-en --tablefmt plain --samples human-2014-05-16.folded/*.human.scores | grep tuning | awk '{print $5}'

