/*
 * Carnegie Mellon University
 * Copyright (c) 2004, 2010
 * 
 * This software is distributed under the terms of the GNU Lesser General
 * Public License.  See the included COPYING and COPYING.LESSER files.
 * 
 */

package edu.cmu.meteor.aligner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import edu.cmu.meteor.util.Constants;
import edu.lig.multivec.util.Lib_distance;

public class Aligner {

	/* Configuration */

    /**
     *
     */
    

	public String language;

	private int moduleCount;
	private ArrayList<Integer> modules;
	private ArrayList<Double> moduleWeights;

	private int beamSize;

	private Stemmer stemmer;
	private TreeTaggerWrapperClass ttWrapperClass;
	private Lib_distance distance;
	private double embeddingsThreshold;
	private int embeddingsType;
	private SynonymDictionary synonyms;
	private ParaphraseTransducer paraphrase;
	private HashSet<String> functionWords;

	// Used for sorting partial alignments
	private Comparator<PartialAlignment> partialComparator;

	public Aligner(String language, ArrayList<Integer> modules) {
         
		this.beamSize = Constants.DEFAULT_BEAM_SIZE;
		this.partialComparator = Constants.PARTIAL_COMPARE_TOTAL;
		setupModules(language, modules, Constants.DEFAULT_WORD_DIR_URL,
				Constants.DEFAULT_SYN_DIR_URL,
				Constants.getDefaultParaFileURL(Constants
						.getLanguageID(Constants.normLanguageName(language))));
	}

	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights) {
            
		this.beamSize = Constants.DEFAULT_BEAM_SIZE;
		this.partialComparator = Constants.PARTIAL_COMPARE_TOTAL;
		setupModules(language, modules, Constants.DEFAULT_WORD_DIR_URL,
				Constants.DEFAULT_SYN_DIR_URL,
				Constants.getDefaultParaFileURL(Constants
						.getLanguageID(Constants.normLanguageName(language))));
		this.moduleWeights = moduleWeights;
	}

	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights, int beamSize) {
		this.beamSize = beamSize;
		this.partialComparator = Constants.PARTIAL_COMPARE_TOTAL;
		setupModules(language, modules, Constants.DEFAULT_WORD_DIR_URL,
				Constants.DEFAULT_SYN_DIR_URL,
				Constants.getDefaultParaFileURL(Constants
						.getLanguageID(Constants.normLanguageName(language))));
		this.moduleWeights = moduleWeights;
	}

	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights, int beamSize, URL wordFileURL) {
		this.beamSize = beamSize;
		this.partialComparator = Constants.PARTIAL_COMPARE_TOTAL;
		setupModules(language, modules, wordFileURL,
				Constants.DEFAULT_SYN_DIR_URL,
				Constants.getDefaultParaFileURL(Constants
						.getLanguageID(Constants.normLanguageName(language))));
		this.moduleWeights = moduleWeights;
	}

	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights, int beamSize, URL wordFileURL,
			URL synDirURL) {
		this.beamSize = beamSize;
		this.partialComparator = Constants.PARTIAL_COMPARE_TOTAL;
		setupModules(language, modules, wordFileURL, synDirURL,
				Constants.getDefaultParaFileURL(Constants
						.getLanguageID(Constants.normLanguageName(language))));
		this.moduleWeights = moduleWeights;
	}

	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights, int beamSize, URL wordFileURL,
			URL synDirURL, URL paraDirURL) {
		this.beamSize = beamSize;
		this.partialComparator = Constants.PARTIAL_COMPARE_TOTAL;
		setupModules(language, modules, wordFileURL, synDirURL, paraDirURL);
		this.moduleWeights = moduleWeights;
	}

	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights, int beamSize, URL wordFileURL,
			URL synDirURL, URL paraDirURL,
			Comparator<PartialAlignment> partialComparator) {
		this.beamSize = beamSize;
		this.partialComparator = partialComparator;
		setupModules(language, modules, wordFileURL, synDirURL, paraDirURL);
		this.moduleWeights = moduleWeights;
	}
	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights, int beamSize, URL wordFileURL,
			URL synDirURL, URL paraDirURL, URL embeddingsDirURL,
			Comparator<PartialAlignment> partialComparator) {
		this.beamSize = beamSize;
		this.partialComparator = partialComparator;
		setupModules(language, modules, wordFileURL, synDirURL, paraDirURL, embeddingsDirURL);
		this.moduleWeights = moduleWeights;
	}
// 	public Aligner(String language, ArrayList<Integer> modules,
// 			ArrayList<Double> moduleWeights, int beamSize, URL embeddingsDirURL) {
// 		this.beamSize = beamSize;
// 		this.partialComparator = Constants.PARTIAL_COMPARE_TOTAL;
// 		setupModules(language, modules, wordFileURL, Constants.DEFAULT_SYN_DIR_URL, Constants.getDefaultParaFileURL(Constants
// 						.getLanguageID(Constants.normLanguageName(language))), embeddingsDirURL);
// 		this.moduleWeights = moduleWeights;
// 	}

	public Aligner(String language, ArrayList<Integer> modules,
			ArrayList<Double> moduleWeights, int beamSize, URL wordFileURL,
			URL embeddingsDirURL,
			Comparator<PartialAlignment> partialComparator) {
		this.beamSize = beamSize;
		this.partialComparator = partialComparator;
		setupModules(language, modules, wordFileURL, Constants.DEFAULT_SYN_DIR_URL, Constants.getDefaultParaFileURL(Constants
						.getLanguageID(Constants.normLanguageName(language))), embeddingsDirURL);
		this.moduleWeights = moduleWeights;
	}

// 	public Aligner(String language, ArrayList<Integer> modules,
// 			ArrayList<Double> moduleWeights, int beamSize, URL wordFileURL,
// 			URL synDirURL, URL embeddingsDirURL,
// 			Comparator<PartialAlignment> partialComparator) {
// 		this.beamSize = beamSize;
// 		this.partialComparator = partialComparator;
// 		setupModules(language, modules, wordFileURL, synDirURL, Constants.getDefaultParaFileURL(Constants
// 						.getLanguageID(Constants.normLanguageName(language))), embeddingsDirURL);
// 		this.moduleWeights = moduleWeights;
// 	}

	public Aligner(Aligner aligner) {
		this.beamSize = aligner.beamSize;
		this.moduleCount = aligner.moduleCount;
		this.language = aligner.language;
		this.modules = new ArrayList<Integer>(aligner.modules);
		this.moduleWeights = new ArrayList<Double>(aligner.moduleWeights);
		this.partialComparator = aligner.partialComparator;
		this.embeddingsThreshold = aligner.embeddingsThreshold; 
		this.embeddingsType = aligner.embeddingsType; 
		this.ttWrapperClass = new TreeTaggerWrapperClass(this.language, "treetagger/lib/");
		for (int module : this.modules) {
			if (module == Constants.MODULE_STEM) {
				// Each aligner needs its own stemmer
				this.stemmer = Constants.newStemmer(this.language);
			} else if (module == Constants.MODULE_SYNONYM) {
				// Dictionaries can be shared
				this.synonyms = aligner.synonyms;
			} else if (module == Constants.MODULE_PARAPHRASE) {
				// Dictionaries can be shared
				this.paraphrase = aligner.paraphrase;
			} else if (module == Constants.MODULE_EMBEDDINGS) {
				// Dictionaries can be shared
				this.distance = aligner.distance;
			}
		}
		this.functionWords = aligner.functionWords;
	}

	private void setupModules(String language, ArrayList<Integer> modules,
			URL wordFileURL, URL synDirURL, URL paraDirURL) {
		this.language = Constants.normLanguageName(language);
		this.moduleCount = modules.size();
		this.modules = modules;
		this.moduleWeights = new ArrayList<Double>();
		this.ttWrapperClass = new TreeTaggerWrapperClass(this.language, "treetagger/lib/");
		for (int i = 0; i < this.modules.size(); i++) {
			int module = this.modules.get(i);
			if (module == Constants.MODULE_EXACT) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_EXACT);
			} else if (module == Constants.MODULE_STEM) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_STEM);
				this.stemmer = Constants.newStemmer(this.language);
			} else if (module == Constants.MODULE_SYNONYM) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_SYNONYM);
				try {
					URL excFileURL = new URL(synDirURL.toString() + "/"
							+ this.language + ".exceptions");
					URL synFileURL = new URL(synDirURL.toString() + "/"
							+ this.language + ".synsets");
					URL relFileURL = new URL(synDirURL.toString() + "/"
							+ this.language + ".relations");
					
                                       
                                       this.synonyms = new SynonymDictionary( this.language, excFileURL,
							synFileURL, relFileURL, this.ttWrapperClass);
				} catch (IOException ex) {
					throw new RuntimeException(
							"Error: Synonym dictionary could not be loaded ("
									+ synDirURL.toString() + ")");
				}
			} else if (module == Constants.MODULE_PARAPHRASE) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_PARAPHRASE);
				this.paraphrase = new ParaphraseTransducer(paraDirURL);
			} else if (module == Constants.MODULE_WSD) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_WSD);
			}
		}
		this.functionWords = new HashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					wordFileURL.openStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				this.functionWords.add(line);
			}
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException("No function word list ("
					+ wordFileURL.toString() + ")");
		}
	}

	private void setupModules(String language, ArrayList<Integer> modules,
			URL wordFileURL, URL synDirURL, URL paraDirURL, URL embeddingsDirURL) {
		this.language = Constants.normLanguageName(language);
		this.moduleCount = modules.size();
		this.modules = modules;
		this.moduleWeights = new ArrayList<Double>();
		this.ttWrapperClass = new TreeTaggerWrapperClass(this.language, "treetagger/lib/");
		for (int i = 0; i < this.modules.size(); i++) {
			int module = this.modules.get(i);
			if (module == Constants.MODULE_EXACT) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_EXACT);
			} else if (module == Constants.MODULE_STEM) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_STEM);
				this.stemmer = Constants.newStemmer(this.language);
			} else if (module == Constants.MODULE_SYNONYM) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_SYNONYM);
				try {
					URL excFileURL = new URL(synDirURL.toString() + "/"
							+ this.language + ".exceptions");
					URL synFileURL = new URL(synDirURL.toString() + "/"
							+ this.language + ".synsets");
					URL relFileURL = new URL(synDirURL.toString() + "/"
							+ this.language + ".relations");
					
                                       
                                       this.synonyms = new SynonymDictionary( this.language, excFileURL,
							synFileURL, relFileURL, this.ttWrapperClass);
				} catch (IOException ex) {
					throw new RuntimeException(
							"Error: Synonym dictionary could not be loaded ("
									+ synDirURL.toString() + ")");
				}
			} else if (module == Constants.MODULE_PARAPHRASE) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_PARAPHRASE);
				this.paraphrase = new ParaphraseTransducer(paraDirURL);
			} else if (module == Constants.MODULE_EMBEDDINGS) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_EMBEDDINGS);
				try
				{
					this.distance = new Lib_distance(embeddingsDirURL,"UTF8");
				} catch (IOException ex) {
					throw new RuntimeException("No model file ("
							+ embeddingsDirURL.toString() + ")");
				}
				
				if (this.distance == null)
				{
				    System.err.println("Aligner: Error Embeddings are null");
				}
			} else if (module == Constants.MODULE_WSD) {
				this.moduleWeights.add(Constants.DEFAULT_WEIGHT_WSD);
			}
		}
		this.functionWords = new HashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					wordFileURL.openStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				this.functionWords.add(line);
			}
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException("No function word list ("
					+ wordFileURL.toString() + ")");
		}
	}

	public void updateModuleWeights(ArrayList<Double> moduleWeights) {
		this.moduleWeights = new ArrayList<Double>(moduleWeights);
	}

	public Alignment align(String line1, String line2) {
		Alignment a = new Alignment(line1, line2);
		align(a);
		return a;
	}

	public Alignment align(ArrayList<String> words1, ArrayList<String> words2) {
		Alignment a = new Alignment(words1, words2);
		align(a);
		return a;
	}

	private void align(Alignment a) {
		// Set the stage for matching
		Stage s = new Stage(a.words1, a.words2);
		
        PrintStream out = System.out;
        PrintStream err = System.err;
        System.setOut(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
        System.setErr(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
		
		if (ttWrapperClass != null )
		{
			a.POS1=new ArrayList<String>();
			a.POS2=new ArrayList<String>();
			a.lemma1=new ArrayList<String>();
			a.lemma2=new ArrayList<String>();
			String[] tab1=new String[a.words1.size()];
			for (int i=0; i< a.words1.size(); i++)
			{
				tab1[i]=a.words1.get(i);
			}
			String out1=ttWrapperClass.tag(tab1);
//			System.err.println("OUT1 " + out1);
			
			String[] tab2=new String[a.words2.size()];			
			for (int i=0; i< a.words2.size(); i++)
			{
				tab2[i]=a.words2.get(i);
			}						
			String out2=ttWrapperClass.tag(tab2);
//			System.err.println("OUT2 " + out2);
//			System.exit(0);
			String[] tabout1 = out1.split("\n");			
			for (int i=0; i< tabout1.length; i++)
			{
				String[] _tabout1 = tabout1[i].split("\t");
				for (int j=0; j< _tabout1.length; j++)
				{
//					System.err.println(_tabout1[j]);
					if ((j+1) % 2 == 0)
					{
//						System.err.print(j);
//						System.err.print("\t");
//						System.err.println("POS "+_tabout1[j]);
						a.POS1.add((String)ttWrapperClass.hashTTPOS2UPOS.get(_tabout1[j]));
					}
					if ((j+1) % 3 == 0)
					{
//						System.err.print(j);
//						System.err.print("\t");
//						System.err.println("Lemma "+_tabout1[j]);
						a.lemma1.add(_tabout1[j]);
					}					
				}
			}
			String[] tabout2 = out2.split("\n");			
			for (int i=0; i< tabout2.length; i++)
			{
				String[] _tabout2 = tabout2[i].split("\t");
				for (int j=0; j< _tabout2.length; j++)
				{
//					System.err.println(_tabout2[j]);
					if ((j+1) % 2 == 0)
					{
/*						System.err.print(j);
						System.err.print("\t");
						System.err.println("POS "+_tabout2[j]);*/
						a.POS2.add((String)ttWrapperClass.hashTTPOS2UPOS.get(_tabout2[j]));
					}
					if ((j+1) % 3 == 0)
					{
/*						System.err.print(j);
						System.err.print("\t");
						System.err.println("Lemma "+_tabout2[j]);*/
						a.lemma2.add(_tabout2[j]);
					}					
				}
			}
/*
			System.err.println("POS");
			for (int i=0; i< a.POS1.size(); i++)
			{
				System.err.println(a.POS1.get(i));
			}
			for (int i=0; i< a.POS2.size(); i++)
			{
				System.err.println(a.POS2.get(i));
			}
			System.err.println("LEMMAS");
			for (int i=0; i< a.lemma1.size(); i++)
			{
				System.err.println(a.lemma1.get(i));
			}
			for (int i=0; i< a.lemma2.size(); i++)
			{
				System.err.println(a.lemma2.get(i));
			}*/
		}
		else
		{
			System.err.println("TreeTagger output are empty, please check your installation!");
			System.exit(1);
		}
//		System.exit(0);		

		// Special case: if sentences are identical, only exact matches are
		// needed. This prevents beam search errors.
		int modsUsed = moduleCount;
		/* sauf que ça fait de la merde avec le WSD ^^
		if (a.words1.size() == a.words2.size()
				&& Arrays.equals(s.words1, s.words2)) {
			modsUsed = 1;
		}
		*/
		

		// For each module
		for (int modNum = 0; modNum < modsUsed; modNum++) {

			// Get the matcher for this module
			int matcher = modules.get(modNum);

			// Match with the appropriate module
			if (matcher == Constants.MODULE_EXACT) {
				// Exact just needs the alignment object
				ExactMatcher.match(modNum, a, s);
			} else if (matcher == Constants.MODULE_STEM) {
				// Stem also need the stemmer
				StemMatcher.match(modNum, a, s, stemmer);
			} else if (matcher == Constants.MODULE_SYNONYM) {
//				System.setOut(out);
//				System.setErr(err);
				// Synonym also need the synonym dictionary
				SynonymMatcher.match(this.language, modNum, a, s, synonyms);
//		        System.setOut(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
//		        System.setErr(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
			} else if (matcher == Constants.MODULE_PARAPHRASE) {
				// Paraphrase also need the paraphrase dictionary
				ParaphraseMatcher.match(modNum, a, s, paraphrase);
			} else if (matcher == Constants.MODULE_EMBEDDINGS) {
				// Embeddings need the embeddings file
				EmbeddingsMatcher.match(modNum, a, s, distance, embeddingsThreshold, embeddingsType);
			} else if (matcher == Constants.MODULE_WSD) {
//				System.setOut(out);
//				System.setErr(err);
				WSDMatcher.match(modNum, a, s);
//		        System.setOut(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
//		        System.setErr(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
			} else {
				throw new RuntimeException("Matcher not recognized: " + matcher);
			}
		}

		// All possible matches have been identified. Now search
		// for the highest scoring alignment.

		boolean[] line1UsedWords = new boolean[a.words1.size()];
		Arrays.fill(line1UsedWords, false);

		boolean[] line2UsedWords = new boolean[a.words2.size()];
		Arrays.fill(line2UsedWords, false);

		PartialAlignment initialPath = new PartialAlignment(
				new Match[a.words2.size()], line1UsedWords, line2UsedWords);
/*		for (int i = 0; i < s.matches.size(); i++) {
			System.err.print(i);
			System.err.print("|");
			System.err.println(s.matches.get(i).size());
			for (int j = 0; j < s.matches.get(i).size(); j++) {
				System.err.print(s.matches.get(i).get(j).matchStart);
				System.err.print("|");
				System.err.print(s.matches.get(i).get(j).matchLength);
				System.err.print("|");
				System.err.print(s.matches.get(i).get(j).prob);
				System.err.print("|");
				System.err.print(s.matches.get(i).get(j).module);
			System.err.print("\t");
			}
			System.err.println("");
		}*/
//		System.err.print(initialPath.matches1);
//		System.err.print(initialPath.matches2);
//		System.err.print(initialPath.allMatches1);
//		System.err.print(initialPath.allMatches2);
		// One-to-one, non-overlapping matches are definite
//		System.err.println("******************* Overlaping:");
		
		for (int i = 0; i < s.matches.size(); i++) {
			if (s.matches.get(i).size() == 1) {
				Match m = s.matches.get(i).get(0);
				boolean overlap = false;
//				System.err.println(i);
				for (int j = 0; j < m.length; j++)
					if (s.line2Coverage[i + j] != 1)
						overlap = true;
				for (int j = 0; j < m.matchLength; j++)
					if (s.line1Coverage[m.matchStart + j] != 1)
						overlap = true;
/*				System.err.println(overlap);
				System.err.print(m.matchStart);
				System.err.print("|");
				System.err.print(m.matchLength);
				System.err.print("|");
				System.err.print(m.prob);
				System.err.print("|");
				System.err.print(m.module);
				System.err.println("");
				System.err.println("**");*/
				if (!overlap) {
					initialPath.matches[i] = m;
					for (int j = 0; j < m.length; j++)
						initialPath.line2UsedWords[i + j] = true;
					for (int j = 0; j < m.matchLength; j++)
						initialPath.line1UsedWords[m.matchStart + j] = true;
				}
			}
		}
//		System.err.println("");
//		for (int i = 0; i < initialPath.line1UsedWords.length; i++) {
//			System.err.print(initialPath.line1UsedWords[i]);
//			System.err.print("\t");
//		}
//		System.err.println("");
//		
//		for (int i = 0; i < initialPath.line2UsedWords.length; i++) {
//			System.err.print(initialPath.line2UsedWords[i]);
//			System.err.print("\t");
//		}
//		System.err.println("");
//		
		// Resolve best alignment using remaining matches
		PartialAlignment best = resolve(s, initialPath);

		// Match totals
		int[] contentMatches1 = new int[moduleCount];
		int[] contentMatches2 = new int[moduleCount];
		Arrays.fill(contentMatches1, 0);
		Arrays.fill(contentMatches2, 0);

		int[] functionMatches1 = new int[moduleCount];
		int[] functionMatches2 = new int[moduleCount];
		Arrays.fill(functionMatches1, 0);
		Arrays.fill(functionMatches2, 0);

		// Populate these while summing to avoid rehashing
		boolean[] isFunctionWord1 = new boolean[a.words1.size()];
		boolean[] isFunctionWord2 = new boolean[a.words2.size()];

		// Check for function words
		for (int i = 0; i < a.words1.size(); i++)
			if (functionWords.contains(a.words1.get(i).toLowerCase())) {
				isFunctionWord1[i] = true;
				a.line1FunctionWords.add(i);
			}
		for (int i = 0; i < a.words2.size(); i++)
			if (functionWords.contains(a.words2.get(i).toLowerCase())) {
				isFunctionWord2[i] = true;
				a.line2FunctionWords.add(i);
			}

		// Sum matches by module, word type
		for (int i = 0; i < best.matches.length; i++) {
			Match m = best.matches[i];
			if (m != null) {
				for (int j = 0; j < m.matchLength; j++) {
					if (isFunctionWord1[m.matchStart + j])
						functionMatches1[m.module]++;
					else
						contentMatches1[m.module]++;
				}
				for (int j = 0; j < m.length; j++) {
					if (isFunctionWord2[m.start + j])
						functionMatches2[m.module]++;
					else
						contentMatches2[m.module]++;
				}
			}
		}
		for (int i = 0; i < moduleCount; i++) {
			a.moduleContentMatches1.add(contentMatches1[i]);
			a.moduleContentMatches2.add(contentMatches2[i]);
			a.moduleFunctionMatches1.add(functionMatches1[i]);
			a.moduleFunctionMatches2.add(functionMatches2[i]);
		}

		// Copy best partial to final alignment
		a.matches = Arrays.copyOf(best.matches, best.matches.length);

		// Total matches and chunks
		int[] cc = getCountAndChunks(a.matches);
		a.line1Matches = cc[0];
		a.line2Matches = cc[1];
		a.numChunks = cc[2];

		double avgMatches = ((double) (a.line1Matches + a.line2Matches)) / 2;
		a.avgChunkLength = (a.numChunks > 0) ? avgMatches / a.numChunks : 0;
		
		
		System.setOut(out);
		System.setErr(err);
		
	}

	// Beam search for best alignment
	private PartialAlignment resolve(Stage s, PartialAlignment start) {

		// Current search path queue
		ArrayList<PartialAlignment> paths = null;
		// Next search path queue
		ArrayList<PartialAlignment> nextPaths = new ArrayList<PartialAlignment>();
		nextPaths.add(start);
		// Proceed left to right
		for (int current = 0; current <= s.matches.size(); current++) {
			// Advance
			paths = nextPaths;
			nextPaths = new ArrayList<PartialAlignment>();
//			System.err.println(current);
			// Sort possible paths
			Collections.sort(paths, partialComparator);

			// Try as many paths as beam allows
			for (int rank = 0; rank < beamSize && rank < paths.size(); rank++) {

				PartialAlignment path = paths.get(rank);

				// Case: Path is complete
				if (current == s.matches.size()) {
					// Close last chunk
					if (path.lastMatchEnd != -1)
						path.chunks++;
					nextPaths.add(path);
					continue;
				}
//				for (int j = 0; j < s.matches.get(current).size(); j++) {
//					System.err.print(s.matches.get(current).get(j).matchStart);
//					System.err.print("|");
//					System.err.print(s.matches.get(current).get(j).matchLength);
//					System.err.print("|");
//					System.err.print(s.matches.get(current).get(j).prob);
//					System.err.print("|");
//					System.err.print(s.matches.get(current).get(j).module);
//				System.err.print("\t");
//				}
//				System.err.println("");			
				
				// Case: Current index word is in use
				if (path.line2UsedWords[current] == true) {
					// If this is still part of a match
					if (current < path.idx) {
						// Continue
						nextPaths.add(path);
					}
					// If fixed match
					else if (path.matches[path.idx] != null) {
						Match m = path.matches[path.idx];
						// Add both match sizes times module weight
						path.matchCount++;
						path.matches1 += m.matchLength
								* moduleWeights.get(m.module);
						path.matches2 += m.length * moduleWeights.get(m.module);
						path.allMatches1 += m.matchLength;
						path.allMatches2 += m.length;
						// Not continuous in line1
						if (path.lastMatchEnd != -1
								&& m.matchStart != path.lastMatchEnd) {
							path.chunks++;
						}
						// Advance to end of match + 1
						path.idx = m.start + m.length;
						path.lastMatchEnd = m.matchStart + m.matchLength;
						// Add distance
						path.distance += Math.abs(m.start - m.matchStart);
						// Continue
						nextPaths.add(path);
					}
					continue;
				}

				// Case: Multiple possible matches
				// For each match starting at index start
				ArrayList<Match> matches = s.matches.get(current);
				for (int i = 0; i < matches.size(); i++) {
					Match m = matches.get(i);

					// Check to see if words are unused
					if (path.isUsed(m))
						continue;
//					System.err.println("Continue 3");

					// New path
					PartialAlignment newPath = new PartialAlignment(path);

					// Select m for this start index
					newPath.setUsed(m, true);
					newPath.matches[current] = m;

					// Calculate new stats
					newPath.matchCount++;
					newPath.matches1 += m.matchLength
							* moduleWeights.get(m.module);
					newPath.matches2 += m.length * moduleWeights.get(m.module);
					newPath.allMatches1 += m.matchLength;
					newPath.allMatches2 += m.length;
					if (newPath.lastMatchEnd != -1
							&& m.matchStart != newPath.lastMatchEnd) {
						newPath.chunks++;
					}
					newPath.idx = m.start + m.length;
					newPath.lastMatchEnd = m.matchStart + m.matchLength;
					path.distance += Math.abs(m.start - m.matchStart);

					// Add to queue
					nextPaths.add(newPath);
				}
				// Try skipping this index
				if (path.lastMatchEnd != -1) {
					path.chunks++;
					path.lastMatchEnd = -1;
				}
				path.idx++;
				nextPaths.add(path);
			}
			if (nextPaths.size() == 0) {
				System.err
						.println("Warning: unexpected conditions - skipping matches until possible to continue");
				nextPaths.add(paths.get(0));
			}
		}
		// Return top best path
		Collections.sort(nextPaths, partialComparator);
//		for (int i=0; i< nextPaths.size(); i++){
//			System.err.println(nextPaths.get(i).matches.length);
//			System.err.println("**");
////			for (int i=0; i< nextPaths.size(); i++){
////			for (int j=0; j< nextPaths.get(i).matches.length; j++){
////				Match m=nextPaths.get(i).matches[j];
////				System.err.print(m.matchStart);
////				System.err.print("|");
////				System.err.print(m.matchLength);
////				System.err.print("|");
////				System.err.print(m.prob);
////				System.err.print("|");
////				System.err.print(m.module);
////				System.err.println("");
////				System.err.println("**");
////			}
//			
//		}
		return nextPaths.get(0);
	}

	// Count matches and chunks, return int[] { matches1, matches2, chunks }
	private int[] getCountAndChunks(Match[] matches) {
		// Chunks
		int matches1 = 0;
		int matches2 = 0;
		int chunks = 0;
		int idx = 0;
		int lastMatchEnd = -1;
		while (idx < matches.length) {
			Match m = matches[idx];
			// Gap in line2
			if (m == null) {
				// End of chunk
				if (lastMatchEnd != -1) {
					chunks++;
					lastMatchEnd = -1;
				}
				// Advance in line2
				idx++;
			} else {
				// Add both match sizes
				matches1 += m.matchLength;
				matches2 += m.length;
				// Not continuous in line1
				if (lastMatchEnd != -1 && m.matchStart != lastMatchEnd) {
					chunks++;
				}
				// Advance to end of match + 1
				idx = m.start + m.length;
				lastMatchEnd = m.matchStart + m.matchLength;
			}
		}
		// End current open chunk if exists
		if (lastMatchEnd != -1)
			chunks++;
		int[] cc = { matches1, matches2, chunks };
		return cc;
	}
	public void setEmbeddingsThreshold(double threshold) {
		this.embeddingsThreshold = threshold;
	}
	public void setEmbeddingsType(int embType) {
		this.embeddingsType = embType;
	}
	public int getEmbeddingsType() {
		return this.embeddingsType;
	}
}
