#!/bin/bash
# Prints a usage message and exits
usage() {
   echo "Usage: $0 -t <Test_Corpus> -r <Ref_Corpus> -l <Language> -d <syn_path> -f <format_Text_or_SGLM>" 
    exit
}
# Read arguments
while test $# -gt 0
do
        if [ $1 = "-t" ]; then Test=$2;
        elif [ $1 = "-r" ]; then Ref=$2;
        elif [ $1 = "-l" ]; then Lang=$2;
        elif [ $1 = "-f" ]; then format=$2;
	elif [ $1 = "-d" ]; then SynPath=$2;
        fi
        shift
done

if [ -z $Test ] || [ -z $Ref ] || [ -z $synPath ]  [ -z $Lang ]  ; then usage; fi

# TreeTagger path 
TreeTagger="TreeTagger/cmd"       

# Meteor options 
options=" -norm "

# input conteins
if [ ! -d "./input" ]; then 
	mkdir input
else
	rm input/*
fi

if [ $format = "sgml" ]; then 
	#Concat Test - REF
	cat $Test $Ref > input/test.ref
 #SGML to plain  
    echo "---- Preprocessing of REF and TEST ----" 
    while read line; do   echo  -e $line |perl -pe 's/(^<[^\>]+>)|(<\/[^\>]+>)//g'| sed '/^$/d' | tr "A-Z" "a-z" | perl -pe 's/[\.\,\"\(\)\]\[\«\»\?\!\:\;\&]//gi' ; done  < input/test.ref > input/test.ref.lower
	 
		
		if [ $Lang = "fr" ]; then
			echo "---- run TreeTagger for FR ----" 
			$TreeTagger/tree-tagger-french input/test.ref.lower > input/test.ref.lower.ttg
		elif [ $Lang = "ru" ]; then
			echo "---- run TreeTagger for RU ----" 
			$TreeTagger/tree-tagger-russian input/test.ref.lower > input/test.ref.lower.ttg
		elif [ $Lang = "de" ]; then 
			echo "---- run TreeTagger for Ge ----" 
	    		$TreeTagger/tree-tagger-german input/test.ref.lower > input/test.ref.lower.ttg
		elif [ $Lang = "en" ]; then
			echo "---- run TreeTagger for EN ----" 
			$TreeTagger/tree-tagger-english input/test.ref.lower > input/test.ref.lower.ttg
		elif [ $Lang = "es" ]; then    
			echo "---- run TreeTagger for SP ----" 
			$TreeTagger/tree-tagger-spanish input/test.ref.lower > input/test.ref.lower.ttg
	fi
	#processing of TTG output    
	sort -u input/test.ref.lower.ttg > input/test.ref.lower.ttg.sorted
	# Run Meteor-Dbnary 
	echo "---- run Meteor DBnary ---- "
	java -cp meteor-1.5-Dbnary.jar  Meteor $Test $Ref $options -l $Lang -d $SynPath -sgml 
else
	# Plain Format 
	cat $Test $Ref |tr "A-Z" "a-z" > input/test.ref.lower
	if [ $Lang = "fr" ]; then
		$TreeTagger/tree-tagger-french input/test.ref.lower > input/test.ref.lower.ttg
	elif [ $Lang = "ru" ]; then
		$TreeTagger/tree-tagger-russian input/test.ref.lower > input/test.ref.lower.ttg
	elif [ $Lang = "de" ]; then
		$TreeTagger/tree-tagger-german input/test.ref.lower > input/test.ref.lower.ttg
	elif [ $Lang = "en" ]; then
		$TreeTagger/tree-tagger-english input/test.ref.lower > input/test.ref.lower.ttg
	elif [ $Lang = "es" ]; then
		$TreeTagger/tree-tagger-spanish input/test.ref.lower > input/test.ref.lower.ttg
	fi
#processing of TTG output  
sort -u input/test.ref.lower.ttg > input/test.ref.lower.ttg.sorted

#Run Meteor-Dbnary 
java -cp meteor-1.5-Dbnary.jar  Meteor $Test $Ref $options  -l $Lang -d $SynPath
fi
