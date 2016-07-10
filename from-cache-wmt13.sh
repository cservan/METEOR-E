#/usr/bin/env bash

if [ $# -lt 1 ]; then
    echo "Usage: $0 <tmp file>"
    exit 1
fi

cd wmt13-metrics-task
python2 ./system-level-correlations.py --metrics $1 --human human_scores --directions cs-en de-en | grep tuning | awk '{print $4}'

