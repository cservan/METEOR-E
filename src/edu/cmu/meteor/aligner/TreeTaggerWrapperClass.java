package edu.cmu.meteor.aligner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static java.util.Arrays.asList;

import org.annolab.mytt4j.*;

/**
 * 
 * Wapper to Treetager using the wrapper TT4J
 * 
 */
public class TreeTaggerWrapperClass {
/*
	 TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
	 try {
	     tt.setModel("/treetagger/models/english.par:iso8859-1");
	     tt.setHandler(new TokenHandler<String>() {
	         public void token(String token, String pos, String lemma) {
	             System.out.println(token+"\t"+pos+"\t"+lemma);
	         }
	     });
	     tt.process(asList(new String[] {"This", "is", "a", "test", "."}));
	 }
	 finally {
	     tt.destroy();
	 }
	 */
	public Map<String, String> hashTTPOS2UPOS;
	private TreeTaggerWrapper<String> tt;

	public TreeTaggerWrapperClass(TreeTaggerWrapperClass tagger) {
		this.tt = tagger.tt;
	}

	public TreeTaggerWrapperClass(TreeTaggerWrapper<String> ttw) {
		this.tt = ttw;
	}
	
	public TreeTaggerWrapperClass(String lang, String libPath) {
	    System.setProperty("treetagger.home", "treetagger");
		tt = new TreeTaggerWrapper<String>();
		try {
			tt.setModel(libPath+"/"+lang+"-utf8.par:utf8");
		     tt.setHandler(new TokenHandler<String>() {
		         public void token(String token, String pos, String lemma) {
		             System.out.println(token+"\t"+pos+"\t"+lemma);
		         }
		     });		
		}
		catch (final IOException e) {
			System.err.println("File model: "+libPath+"/"+lang+"-utf8.par does not exist, please check your TreeTagger installation");
		}
        hashTTPOS2UPOS = new java.util.HashMap<String,String>();
        try { 
            // output of TTG
        	//System.err.println("Loading dictionnary: ./resources/TreeTagger2UniversalPOS/UnivTag."+lang);
        	InputStream inputFlux=new FileInputStream("./resources/TreeTagger2UniversalPOS/UnivTag."+lang); 
            InputStreamReader lecture=new InputStreamReader(inputFlux);
            BufferedReader buff=new BufferedReader(lecture);
            String line;
            //int l_cptWL=0;

           while ((line=buff.readLine())!=null){
        	   if ((line.split("\t").length) > 1)
        	   {
                 String POSTT = line.split("\t")[0];
                 String POSUT = line.split("\t")[1];
                 hashTTPOS2UPOS.put(POSTT,POSUT);
             //    l_cptWL++;
        	   }

             }
           //System.err.println(l_cptWL);
           buff.close();
         } catch (IOException ex) {
 			System.err.println("File ./resources/TreeTagger2UniversalPOS."+lang+"does not exist, please check your paths.");
         }
	}
	

	/**
	 * 
	 * THIS METHOD IS A SYNCHRONIZED WRAPPER FOR NON-THREADSAFE SNOWBALL
	 * STEMMERS.
	 */
	public synchronized String tag(String[] sentence) {
		String to_return="";
		if (tt == null)
		{
			System.err.println("TreeTaggerWrapper is null");
		}
			
		try {
			
			tt.process(asList(sentence));
			to_return=tt.getOutput();
		}
		catch (final Exception e) {
			System.err.println("File model not initialized");
		}
		return to_return;
	}
    public void hashCharge(String language){
        
    }
}
