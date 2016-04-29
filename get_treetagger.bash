mkdir -p treetagger
pushd treetagger
wget http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/tree-tagger-linux-3.2.tar.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/tagger-scripts.tar.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/english-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/french-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/german-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/russian-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/spanish-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/english-chunker-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/french-chunker-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/german-chunker-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/spanish-chunker-par-linux-3.2-utf8.bin.gz \
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/install-tagger.sh

bash ./install-tagger.sh

popd
