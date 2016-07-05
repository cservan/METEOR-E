#/usr/bin/env bash

if [ $# -lt 1 ]; then
    echo "Usage: $0 <modules>"
    echo "Modules are: exact stem wsd synonym paraphrase embeddings"
    exit 1
fi

MODULES="$1"
PLAIN="wmt13-metrics-task/wmt13-baselines/wmt13-data/plain"

for SET in $(ls $PLAIN/system-outputs) ; do
    for LANG in cs de fr ru es ; do
        for HYP in $PLAIN/system-outputs/$SET/$LANG-en/* ; do
            SYS=$(basename $HYP)
            SYS=$(echo $SYS | sed -re "s/$SET.//;s/.$LANG-en//")
            REF=$PLAIN/references/$SET-ref.$LANG-en
            echo -n "$HYP $REF $LANG-en $SET $SYS "
            java -Xmx2G -cp lib/lexsema-wsd-core.jar:meteor-1.5-DBnary-Embeddings-TT.jar Meteor $HYP $REF -l en -embLemma monolingual_vectors.lem -norm -m "$MODULES" -w '1 1 1 1 1 1' -p '1 1 1 1' -generate-cache 2> /dev/null
        done
    done
done

