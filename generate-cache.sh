#/usr/bin/env bash

if [ $# -lt 2 ]; then
    echo "Usage: ./generate-cache.sh <modules> <data-dir>"
    echo "Modules are: exact stem synonym paraphrase embeddings wsd"
    exit 1
fi

MODULES="$1"
PLAIN="$2"

for SET in $(ls $PLAIN/system-outputs) ; do
    for LANG in de fr ru ; do
        for HYP in $PLAIN/system-outputs/$SET/$LANG-en/* ; do
            SYS=$(basename $HYP)
            SYS=$(echo $SYS | sed -re "s/$SET.//;s/.$LANG-en//")
            echo -n "$LANG-en $SYS "
            java -Xmx2G -cp lib/lexsema-wsd-core.jar:meteor-1.5-DBnary-Embeddings-TT.jar Meteor $HYP $PLAIN/references/$SET-ref.$LANG-en -l en -embLemma monolingual_vectors.lem -norm -m "$MODULES" -w '1 1 1 1 1 1' -p '1 1 1 1' -generate-cache 2> /dev/null
        done
    done
done

