package edu.cmu.meteor.aligner;

import java.io.*;
import java.util.*;

import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.TextImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.WordImpl;
import org.getalp.lexsema.similarity.measures.lesk.IndexedLeskSimilarity;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.LargeDocumentDisambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.method.RandomDisambiguator;
import org.getalp.lexsema.wsd.method.SimpleFirstSenseDisambiguator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
import edu.cmu.meteor.util.Constants;
import edu.cmu.meteor.util.Normalizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class WSDMatcher 
{
    private static StanfordCoreNLP stanford = null;
    
    private static DictionaryLRLoader dictionary = null;
    
    private static Disambiguator disambiguator = null;
    
    private static boolean verbose = true;
    
    private static boolean reallyVerbose = false;
    
    private static int currentIndex;
    
    private static class InputStruct
    {
        List<List<String>> input = new ArrayList<>();
        List<List<String>> disambiguation = new ArrayList<>();
    }
    
    private static InputStruct test = new InputStruct();
    
    private static InputStruct reference = new InputStruct();
    
    private static class DisambiguationCache
    {
        private static String cacheDirPath = "resources/wsd/cache";

        private static String cacheIndexFilePath = cacheDirPath + "/index.txt";

        private static Map<String, Integer> index = new HashMap<>();
        
        private static Map<Integer, List<List<String>>> cache = new HashMap<>();
        
        public DisambiguationCache()
        {
        	index = loadIndex();
        	if (verbose) System.err.println(index.size() + " texts in cache");
        	cache = loadCache(index);
        }
        
        private Map<String, Integer> loadIndex()
        {
        	Map<String, Integer> ret = new HashMap<>();
            try 
            {
                Scanner sc = new Scanner(new File(cacheIndexFilePath));
                while (sc.hasNext())
                {
                	String hash = sc.next();
                	String indexStr = sc.next();
                	Integer index = Integer.valueOf(indexStr);
                	ret.put(hash, index);
                }
                sc.close();
            }
            catch (FileNotFoundException e) 
            {
                System.err.println("Warning : WSD cache index file not found");
            }
            catch (Exception e)
            {
                System.err.println("Error : Error while parsing WSD cache index file");
                e.printStackTrace();
            }
            return ret;
        }

        private Map<Integer, List<List<String>>> loadCache(Map<String, Integer> index)
        {
        	Map<Integer, List<List<String>>> ret = new HashMap<>();
        	for (Integer key : index.values())
        	{
        		List<List<String>> value = loadSingleCache(key);
	            ret.put(key, value);
        	}
        	return ret;
        }

        private List<List<String>> loadSingleCache(Integer key)
        {
    		List<List<String>> ret = new ArrayList<>();
            try 
            {
                Scanner sc = new Scanner(new File(cacheDirPath + "/" + key));
                while (sc.hasNextLine())
                {
                    List<String> words = new ArrayList<>();
                    for (String word : sc.nextLine().split(" "))
                    {
                        words.add(word);
                    }
                    ret.add(words);
                }
                sc.close();
            }
            catch (FileNotFoundException e) 
            {
                System.err.println("Warning : WSD cache file " + key + " not found");
            }
            catch (Exception e)
            {
                System.err.println("Error : Error while parsing WSD cache file " + key);
                e.printStackTrace();
            }
            return ret;
        }
        
        public List<List<String>> getCacheValue(List<String> textLines)
        {
            String concat = concatenation(textLines);
            String hash = "" + concat.hashCode();
            if (!index.containsKey(hash)) return null;
            Integer hashIndex = index.get(hash);
            if (!cache.containsKey(hashIndex)) return null;
            return cache.get(hashIndex);
        }
        
        public void putCacheValue(List<String> key, List<List<String>> value)
        {
        	String concat = concatenation(key);
        	String hash = "" + concat.hashCode();
        	int indexx = index.size();
            try
            {
            	FileWriter fw = new FileWriter(cacheDirPath + "/" + indexx, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                for (List<String> line : value)
                {
                	for (String word : line)
                	{
                		out.print(word + " ");
                	}
                	out.println();
                }
                out.close();
            	
            	fw = new FileWriter(cacheIndexFilePath, true);
                bw = new BufferedWriter(fw);
                out = new PrintWriter(bw);
                out.println(hash + " " + index.size());
                out.close();
                
                index.put(hash, indexx);
                cache.put(indexx, value);
            } 
            catch (Exception e) 
            {
            	throw new RuntimeException(e);
            }
        }
        
        private String concatenation(List<String> lines)
        {
            String concat = "";
            for (String line : lines) concat += line;
            return concat;
        }
    }
    
    private static DisambiguationCache cache = null;

    public static void initWithFullInput(List<String> testLines, List<String> refLines, boolean normalize, boolean lowercase, boolean keepPunctuation)
    {
    	if (cache == null) cache = new DisambiguationCache();
        if (verbose) System.err.println("Initializing test input");
        test = initInputStructure(testLines, normalize, lowercase, keepPunctuation);
        if (verbose) System.err.println("Initializing reference input");
        reference = initInputStructure(refLines, normalize, lowercase, keepPunctuation);
        currentIndex = 0;
    }
    
    private static InputStruct initInputStructure(List<String> originalLines, boolean normalize, boolean lowercase, boolean keepPunctuation)
    {
        InputStruct ret = new InputStruct();
        List<String> lines = originalLines;
        if (verbose) System.err.println("Input has " + lines.size() + " lines");
        if (normalize) lines = normalize(lines, keepPunctuation);
        if (lowercase) lines = lowercase(lines);
        loadStanford();
        loadDictionary();
        loadDisambiguator();
        ret.input = tokenize(lines);
        ret.disambiguation = cache.getCacheValue(lines);
        if (ret.disambiguation != null)
        { 
        	if (verbose) System.err.println("Input disambiguation found in cache");
        	return ret;
        }
        if (verbose) System.err.println("Input disambiguation not found in cache");
        Text text = rawToText(lines);
        if (verbose) System.err.println("Parsed " + text.numberOfSentences() + " sentences");
        if (verbose) System.err.println("Loading senses... ");
        dictionary.loadSenses(text);
        if (verbose) System.err.println("Disambiguating... ");
        PrintStream out = System.out;
        System.setOut(System.err);
        Configuration config = disambiguator.disambiguate(text);
        System.setOut(out);
        ret.disambiguation = alignDisambiguation(text, config, ret.input);
        if (reallyVerbose) System.err.println("Final disambiguation :");
        if (reallyVerbose) printDisambiguation(ret.input, ret.disambiguation, text, config);
        cache.putCacheValue(lines, ret.disambiguation);
        return ret;
    }
    
    private static List<String> normalize(List<String> lines, boolean keepPunctuation)
    {
        List<String> normalizedLines = new ArrayList<>();
        for (String line : lines)
        {
            normalizedLines.add(Normalizer.normalizeLine(line, Constants.LANG_EN, keepPunctuation));
        }
        return normalizedLines;
    }
    
    private static List<String> lowercase(List<String> lines)
    {
        List<String> lowercasedLines = new ArrayList<>();
        for (String line : lines)
        {
            lowercasedLines.add(line.toLowerCase());
        }
        return lowercasedLines;
    }
    
    private static List<List<String>> tokenize(List<String> lines)
    {
        List<List<String>> tokenized = new ArrayList<>();
        for (String line : lines)
        {
            tokenized.add(tokenize(line));
        }
        return tokenized;
    }

    private static ArrayList<String> tokenize(String line) 
    {
        ArrayList<String> tokens = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(line);
        while (tok.hasMoreTokens())
        {
            tokens.add(tok.nextToken());
        }
        return tokens;
    }
    
    private static List<List<String>> alignDisambiguation(Text currentText, Configuration currentDisambiguation, List<List<String>> realText)
    {
        Sentence[] currentSentences = currentText.sentences().toArray(new Sentence[currentText.sentences().size()]);
        List<List<String>> realDisambiguation = new ArrayList<>();
        for (int j = 0 ; j < realText.size() ; j++)
        {
            Sentence currentSentence = currentSentences[j];
            int i = 0;
            List<String> list = new ArrayList<>();
            for (int k = 0 ; k < realText.get(j).size() ; k++)
            {
                int iBkp = i;
                boolean found = false;
                while (!found && i < currentSentence.size())
                {
                    if (realText.get(j).get(k).contains(currentSentence.getWord(i).getSurfaceForm()) ||
                        currentSentence.getWord(i).getSurfaceForm().contains(realText.get(j).get(k)))
                    {
                        found = true;
                    }
                    else
                    {
                        i++;
                    }
                }
                if (!found)
                {
                    list.add("0");
                    i = iBkp;
                }
                else
                {
                    int assignment = currentDisambiguation.getAssignment(currentText.indexOfWord(currentSentence.getWord(i)));
                    String senseID = (assignment >= 0) ? currentText.getSenses(currentText.indexOfWord(currentSentence.getWord(i))).get(assignment).getId() : "0";
                    list.add(senseID);
                    i++;
                }
            }
            realDisambiguation.add(list);
        }
        return realDisambiguation;
    }
    
    private static void printDisambiguation(List<List<String>> text, List<List<String>> disambiguation, Text wsdText, Configuration wsdDisambiguation)
    {
        Sentence[] wsdSentences = wsdText.sentences().toArray(new Sentence[wsdText.sentences().size()]);
        for (int j = 0 ; j < text.size() ; j++)
        {
            System.err.print("[SMT] Line " + (j+1) + " : ");
            for (int k = 0 ; k < text.get(j).size() ; k++)
            {
                System.err.print(text.get(j).get(k) + " [" + disambiguation.get(j).get(k) + "] ");
            }
            System.err.println();

            System.err.print("[WSD] Line " + (j+1) + " : ");
            for (int k = 0 ; k < wsdSentences[j].size() ; k++)
            {
                Word word = wsdSentences[j].getWord(k);
                String wordStr = word.getSurfaceForm();
                int wordIndex = wsdText.indexOfWord(word);
                int assignment = wsdDisambiguation.getAssignment(wordIndex);
                String senseStr = (assignment >= 0) ? wsdText.getSenses(wordIndex).get(assignment).getId() : "0";
                System.err.print(wordStr + " [" + senseStr + "] ");
            }
            System.err.println();
        }
    }
    
    public static void incrementIndex()
    {
        currentIndex++;
    }
    
    public static void match(int stage, Alignment a, Stage s) 
    {
        int index = currentIndex++;
        if (verbose) System.err.println("WSD scoring line " + (index + 1));
        if (reference.disambiguation.size() <= index)
        {
            System.err.println("Error : reference.disambiguation.size");
        }
        if (test.disambiguation.size() <= index)
        {
            System.err.println("Error : test.disambiguation.size");
        }
        for (int j = 0; j < a.words2.size(); j++)
        {
            if (reference.disambiguation.get(index).size() <= j)
            {
                System.err.println("Error : reference.disambiguation.get(" + index + ").size");
            }

            String senseKeyRef = reference.disambiguation.get(index).get(j);
            if (senseKeyRef.equals("0")) continue;
            for (int i = 0; i < a.words1.size(); i++) 
            {
                if (test.disambiguation.get(index).size() <= i)
                {
                    System.err.println("Error : test.disambiguation.get(" + index + ").size");
                    for (int k = 0 ; k < test.disambiguation.get(index).size() ; k++)
                    {
                        System.err.println("test.input.get(index)[" + k + "]" + "\"" + test.input.get(index).get(k) + "\"");
                        System.err.println("test.disambiguation.get(index)[" + k + "]" + "\"" + test.disambiguation.get(index).get(k) + "\"");
                    }
                    for (int k = 0 ; k < a.words1.size() ; k++)
                    {
                        System.err.println("a.words1[" + k + "]" + "\"" + a.words1.get(k) + "\"");
                    }
                }
                    
                String senseKeyTest = test.disambiguation.get(index).get(i);
                if (senseKeyTest.equals("0")) continue;
                if (s.words1[i] == s.words2[j]) continue;
                if (senseKeyRef.equals(senseKeyTest))
                {
                    Match m = new Match();
                    m.module = stage;
                    m.prob = 1;
                    m.start = j;
                    m.length = 1;
                    m.matchStart = i;
                    m.matchLength = 1;
                    s.matches.get(j).add(m);
                    s.line1Coverage[i]++;
                    s.line2Coverage[j]++;
                }
            }
        }
    }

    private static void loadStanford()
    {
        if (stanford != null) return;
        PrintStream out = System.out;
        if (verbose) System.setOut(System.err);
        else System.setOut(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        props.put("tokenize.options", "untokenizable=allKeep");
        stanford = new StanfordCoreNLP(props);
        System.setOut(out);
    }

    private static void loadDictionary()
    {
        if (dictionary != null) return;
        try
        {
            dictionary = new DictionaryLRLoader(new FileInputStream("resources/wsd/dictionary.xml"), true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void loadDisambiguator()
    {
        if (disambiguator != null) return;
        ConfigurationScorer scorer = new ConfigurationScorerWithCache(new IndexedLeskSimilarity());
        int iterations = 100000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;
        disambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, false); 
        disambiguator = new LargeDocumentDisambiguator(disambiguator, 300, verbose);
        //disambiguator = new RandomDisambiguator();
        //disambiguator = new SimpleFirstSenseDisambiguator();
    }

    private static Text rawToText(List<String> rawraw)
    {
        PrintStream out = System.out;
        PrintStream err = System.err;
        System.setOut(err);
        if (!verbose) System.setErr(new PrintStream(new OutputStream(){public void write(int arg0) throws IOException{}}));
        Text txt = new TextImpl();
        for (int i = 0 ; i < rawraw.size() ; i++)
        {
            String raw = rawraw.get(i);
            Sentence sentenceRAWRAWR = new SentenceImpl("");
            Annotation document = new Annotation(raw);
            stanford.annotate(document);
            List<CoreMap> sentences = document.get(SentencesAnnotation.class);
            for(CoreMap sentence: sentences) 
            {
                for (CoreLabel token: sentence.get(TokensAnnotation.class))
                {
                    String lemma = token.getString(LemmaAnnotation.class);
                    String surfaceForm = token.originalText();
                    String pos = token.getString(PartOfSpeechAnnotation.class);
                    Word word = new WordImpl("", lemma, surfaceForm, pos);
                    sentenceRAWRAWR.addWord(word);
                }
            }
            txt.addSentence(sentenceRAWRAWR);
        }
        System.setOut(out);
        System.setErr(err);
        return txt;
    }
}
