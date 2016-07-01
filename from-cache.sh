#/usr/bin/env bash

if [ $# -lt 4 ]; then
    echo "Usage: ./from-cache.sh <modules> <weights> <params> <cachefile>"
    exit 1
fi

MODULES="$1"
WEIGHTS="$2"
PARAMS="$3"
CACHEFILE="$4"

while read LINE
do
    read -a tab <<< "$LINE"
    HYP="${tab[0]}"
    REF="${tab[1]}"
    LANG="${tab[2]}"
    SET="${tab[3]}"
    SYS="${tab[4]}"
    CACHELINE="${tab[-31]} ${tab[-30]} ${tab[-29]} ${tab[-28]} ${tab[-27]} ${tab[-26]} ${tab[-25]} ${tab[-24]} ${tab[-23]} ${tab[-22]} ${tab[-21]} ${tab[-20]} ${tab[-19]} ${tab[-18]} ${tab[-17]} ${tab[-16]} ${tab[-15]} ${tab[-14]} ${tab[-13]} ${tab[-12]} ${tab[-11]} ${tab[-10]} ${tab[-9]} ${tab[-8]} ${tab[-7]} ${tab[-6]} ${tab[-5]} ${tab[-4]} ${tab[-3]} ${tab[-2]} ${tab[-1]}"

    java -Xmx2G -cp lib/lexsema-wsd-core.jar:meteor-1.5-DBnary-Embeddings-TT.jar Meteor "$HYP" "$REF" -l en -embLemma monolingual_vectors.lem -norm -m "$MODULES" -w "$WEIGHTS" -p "$PARAMS" -from-cache -cache "$CACHELINE" # | $FMT $LANG $SET $SYS $NAME

done < "$CACHEFILE"

