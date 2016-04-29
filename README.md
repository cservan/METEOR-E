# METEOR-E

An enhanced version of the METEOR mectric (http://www.cs.cmu.edu/~alavie/METEOR/) with DBnary and Word Embeddings.


## Language supported

DBnary:
 + English
 + French
 + Spanish
 + Russian
 + German

You can add all other language supported by DBnary (http://kaiko.getalp.org/about-dbnary/

## How to use Embeddings data

First, learn Word Embeddings using using word embeddings toolkit like multivec (https://github.com/eske/multivec) and save the model in text format.
then, you can activate the module called "embeddings" (with the switch "m") and specify the embeddings model with the switch "emb". The decision threshold for deciding whether an alignment is possible is set to 0.8, but you can set another one with the switch "embTh". 

Here is an example:

	java -jar meteor-1.5-DBnary-Embeddings test reference -l fr -m 'exact stem paraphrase embeddings' -emb model.txt -embTh 0.85


## TreeTagger & TT4J

We propose to use TreeTagger jointly with this version of METEOR to extract lemmas and POS tags. The script called "get_treetagger.bash" will download, extract and install TreeTagger from http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/ </br>
When you use TreeTagger, please cite:

    Helmut Schmid, "Probabilistic Part-of-Speech Tagging Using Decision Trees". 
    Proceedings of International Conference on New Methods in Language Processing, Manchester, UK, 1994.



## References

When using this toolkit with Word Embeddings please cite:

 + Word2Vec vs DBnary ou comment (ré)concilier représentations distribuées et réseaux lexico-sémantiques ? Le cas de l’évaluation en traduction automatique – Christophe Servan, Zied Elloumi, Hervé Blanchon and Laurent Besacier, TALN 2016, Paris, France, Juin 2016
 
with DBnary, please cite:
 
 + METEOR for Multiple Target Languages using DBnary – Zied Elloumi, Hervé Blanchon, Gilles Sérasset and Laurent Besacier, MT Summit 2015, Miami, Florida, USA, Nov 2015.
