
public class Trainer2 
{
	public static void main(String[] args)
	{
		// remplacer le "test.sh"
		String dataPath = "./wmt14-metrics-task/baselines/data/plain";
		String candidatePath = dataPath + "/system-outputs";
		String referencePath = dataPath + "/references";
		
		
		/*
		for SET in $(ls $PLAIN/system-outputs) ; do
		    for LANG in de fr ru ; do
		        echo "$LANG-en:"
		        for HYP in $PLAIN/system-outputs/$SET/$LANG-en/* ; do
		            SYS=$(basename $HYP)
		            SYS=$(echo $SYS | sed -re "s/$SET.//;s/.$LANG-en//")
		            echo " $SYS"
		            java -Xmx2G -cp lib/lexsema-wsd-core.jar:meteor-1.5-DBnary-Embeddings-TT.jar Meteor $HYP $PLAIN/references/$SET-ref.$LANG-en -l en -embLemma monolingual_vectors.lem -norm -m "$MODULES" -w "$WEIGHTS" -p '0.85 0.20 0.60 0.75 0.6' # | $FMT $LANG-en $SET $SYS $NAME
		        done
		    done
		done

		mv $(dirname $0)/$NAME.*.seg.scr $SUB/
		mv $(dirname $0)/$NAME.*.sys.scr $SUB/ 
		cat $SUB/$NAME.*.seg.scr > $SUB/$NAME.seg.score
		cat $SUB/$NAME.*.sys.scr > $SUB/$NAME.sys.score
		*/
	}
}
