package edu.cmu.meteor.aligner;

import java.io.IOException;
import static java.util.Arrays.asList;

import org.annolab.tt4j.*;

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
}
