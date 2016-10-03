# METEOR-E

An enhanced version of the METEOR metric V1.5 (http://www.cs.cmu.edu/~alavie/METEOR/) with DBnary and Word Embeddings.
Works with java 8. Other versions of java has not been tested yet.

## Language supported

DBnary:
 + English
 + French
 + Spanish
 + Russian
 + German

You can add all other language supported by DBnary (http://kaiko.getalp.org/about-dbnary/)

## How to use Embeddings data

First, learn Word Embeddings using using word embeddings toolkit like multivec (https://github.com/eske/multivec) and save the model in text format.
then, you can activate the module called "embeddings" (with the switch "m") and specify the embeddings model with the switch "emb". The decision threshold for deciding whether an alignment is possible is set to 0.8, but you can set another one with the switch "embTh". 

Here is an example:

	java -jar meteor-1.5-DBnary-Embeddings test reference -l fr -m 'exact embeddings paraphrase ' -emb model.fr -embTh 0.85

To reproduce experiements proposed in our work:

 + METEOR Baseline: the METEOR score is estimated using Exact, Stem, Synonym and Paraphrase modules for English as a target language and Exact, Stem and Paraphrase modules for other target languages:
 
 
		  java -jar meteor-1.5-DBnary-Embeddings-TT.jar hypothesis reference -l en -m 'exact stem synonym paraphrase' -norm
 
 
 + METEOR DBnary: similar to METEOR Baseline but Synonym module is available for any target language since it uses DBnary resource instead of Wordnet: 

 
		  java -jar meteor-1.5-DBnary-Embeddings-TT.jar hypothesis reference -l fr -m 'exact stem synonym paraphrase'  -d resources/synonym-DBnary/french-synonym  -norm

		  
 + METEOR Vector: the Stem and Synonym modules are replaced by the Vector module:

 
		 java -jar meteor-1.5-DBnary-Embeddings-TT.jar hypothesis reference -l fr -m 'exact embeddings paraphrase' -norm -emb resources/word-embeddings/embeddings.fr [-embTh 0.75] -w "1.0 0.8 0.6"

		 
 + METEOR Baseline + Vector: the METEOR Baseline configuration is augmented with the Vector module:


		java -jar meteor-1.5-DBnary-Embeddings-TT.jar hypothesis reference -l en -m 'exact stem synonym paraphrase embeddings' -norm -emb resources/word-embeddings/embeddings.en [-embTh 0.75] -w "1.0 0.4 0.8 0.6 0.8"
 
 
 + METEOR DBnary + Vector: the METEOR DBnary configuration is augmented with the Vector module:

 
		java -jar meteor-1.5-DBnary-Embeddings-TT.jar hypothesis reference -l fr -m 'exact stem synonym paraphrase embeddings' -norm -emb resources/word-embeddings/embeddings.fr [-embTh 0.75] -w "1.0 0.4 0.8 0.6 0.8" -d resources/synonym-DBnary/french-synonym



## TreeTagger & TT4J

We propose to use TreeTagger jointly with this version of METEOR to extract lemmas and POS tags. The script called "get_treetagger.bash" will download, extract and install TreeTagger from http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/ </br>
When you use TreeTagger, please cite:

 + Probabilistic Part-of-Speech Tagging Using Decision Trees – Helmut Schmid, Proceedings of International Conference on New Methods in Language Processing, Manchester, UK, 1994.


## Acknowledgement

This toolkit is part of the project KEHATH (https://kehath.imag.fr/) funded by the French National Research Agency.

## References

When using this toolkit with Word Embeddings please cite:

 + Word2Vec vs DBnary: Augmenting METEOR using Vector Representations or Lexical Resources? – Christophe Servan, Alexandre Bérard, Zied Elloumi, Hervé Blanchon and Laurent Besacier, COLING 2016, Osaka, Japan, Dec 2016
 
with DBnary, please cite:
 
 + METEOR for Multiple Target Languages using DBnary – Zied Elloumi, Hervé Blanchon, Gilles Sérasset and Laurent Besacier, MT Summit 2015, Miami, Florida, USA, Nov 2015.

## Links

 + MultiVec: https://github.com/eske/multivec
 + TERcpp-E (WER-E & TER-E) : https://github.com/cservan/tercpp-embeddings

