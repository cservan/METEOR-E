package edu.lig.multivec.util;
/*********************************
 * lib-distance: an open-source library to use the word2vec models.
 *
 * Copyright 2015, Christophe Servan, GETALP-LIG, University of Grenoble, France
 * Contact: christophe.servan@gmail.com
 *
 * The library lib-distance is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 2.1 of the licence, or
 * (at your option) any later version.
 *
 * This program and library are distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * **********************************/

import java.io.*;
import java.lang.reflect.Array;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.net.URL;


public class Lib_distance
{
	private int vocab_hash_size = 30000000;
	private int vocab_size, vector_size;
    private String[] vocab;
    private float[][] embeddings;
    private int[] vocab_hash = new int[vocab_hash_size];
    private HashMap<String, Integer> myHashMap;
    private HashMap<Integer, String> myReverseHashMap;
    public static final int MAXRETURN = 50;
    public String concatHexValues(String s1, String s2, String s3, String s4)
    {
    		String toReturn = "";
    		if (s1.length() < 2)
    		{
    			toReturn = toReturn+"0"+s1;  
    		}
    		else toReturn = toReturn+s1;
    		if (s2.length() < 2)
    		{
    			toReturn = toReturn+"0"+s2;  
    		}
    		else toReturn = toReturn+s2;
    		if (s3.length() < 2)
    		{
    			toReturn = toReturn+"0"+s3;  
    		}
    		else toReturn = toReturn+s3;
    		if (s4.length() < 2)
    		{
    			toReturn = toReturn+"0"+s4;  
    		}
    		else toReturn = toReturn+s4;
    		return toReturn;
    	
    }

	public Lib_distance(String filename) throws IOException
	  {
		  
		  FileInputStream inputBinaryFile = new FileInputStream (filename);	  // Fichier binaire    
		  DataInputStream inputBinaryData = new DataInputStream (inputBinaryFile);    // Le même fichier via stream

		  System.out.println();
		  String infos = inputBinaryData.readLine().toString();
		  String[] vInfos = infos.split(" ");
		  vocab_size = Integer.parseInt(vInfos[0]);
		  vector_size = Integer.parseInt(vInfos[1]);
		  vocab= new String[(int)vocab_size];
		  //Initialize all the values of the hash array to -1
		  Arrays.fill(vocab_hash,-1);
		  String l_word="";
		  int wordCount=0;
		  myHashMap = new HashMap<String, Integer>(vocab_hash_size);
		  myReverseHashMap = new HashMap<Integer, String>(vocab_hash_size);
		  System.out.print("Vocabulary size: ");
		  System.out.println(vocab_size);
		  System.out.print("Vector size: ");
		  System.out.println(vector_size);
		  embeddings = new float[(int)vocab_size][(int)vector_size];
		  int colCount=0;
		  while (wordCount<vocab_size)
		  {
			  char[] l_c=Character.toChars((int)inputBinaryData.read());
			  l_word="";
			  while (l_c[0] != ' ')
			  {
				  l_word=l_word+String.copyValueOf(l_c);
				  l_c=Character.toChars((int)inputBinaryData.read());
			  }
			  colCount=0;
			  vocab[wordCount]=l_word;
			  myHashMap.put(new String(l_word), wordCount);
			  myReverseHashMap.put(wordCount,new String(l_word));
			  float l_length=0;
			  while(colCount < vector_size)
			  {
				  String hex3 = Integer.toHexString(inputBinaryData.read()).toUpperCase();
				  String hex2 = Integer.toHexString(inputBinaryData.read()).toUpperCase();
				  String hex1 = Integer.toHexString(inputBinaryData.read()).toUpperCase();
				  String hex0 = Integer.toHexString(inputBinaryData.read()).toUpperCase();
				  String hex=concatHexValues(hex0, hex1, hex2, hex3);
			  	  Long lI = Long.parseLong(hex, 16);
				  Float lF = Float.intBitsToFloat(lI.intValue());
				  embeddings[wordCount][colCount]=lF;
				  l_length=l_length+(lF*lF);
				  colCount++;
			  }
			  colCount = 0;
			  l_length=(float) Math.sqrt(l_length);
			  while(colCount < vector_size)
			  {
				  embeddings[wordCount][colCount]=embeddings[wordCount][colCount]/l_length;
				  colCount++;
			  }
			  inputBinaryData.read();
			  wordCount++;
			  if (wordCount % (vocab_size/100) == 0) System.out.print(".");
		  if (wordCount % (vocab_size/10) == 0) System.out.print("|");
		  }
		  System.out.println();
	  }
	// Read Text file with the concern encodage
	public Lib_distance(URL filename, String encodage) throws IOException
	  {
		  File modelFile = new File(filename.getFile());
		  BufferedReader modelReader = new BufferedReader(new InputStreamReader(new FileInputStream(modelFile), encodage));
		

		  System.out.println();
		  
		  String infos = modelReader.readLine();
		  String[] vInfos = infos.split(" ");
		  vocab_size = Integer.parseInt(vInfos[0]);
		  vector_size = Integer.parseInt(vInfos[1]);
		  vocab= new String[(int)vocab_size];
		  //Initialize all the values of the hash array to -1
		  Arrays.fill(vocab_hash,-1);
		  String l_word="";
		  int wordCount=0;
		  myHashMap = new HashMap<String, Integer>(vocab_hash_size);
		  myReverseHashMap = new HashMap<Integer, String>(vocab_hash_size);
		  System.out.print("Vocabulary size: ");
		  System.out.println(vocab_size);
		  System.out.print("Vector size: ");
		  System.out.println(vector_size);
		  embeddings = new float[(int)vocab_size][(int)vector_size];
		  int colCount=0;
		  while ((infos = modelReader.readLine()) != null)
		  {
			  vInfos = infos.split(" ");
			  l_word = vInfos[0]; 
			  colCount=0;
			  vocab[wordCount]=l_word;
			  myHashMap.put(new String(l_word), wordCount);
			  myReverseHashMap.put(wordCount,new String(l_word));
			  float l_length=0;
			  while(colCount < vector_size)
			  {
				  Float lF = Float.parseFloat(vInfos[colCount + 1]);
				  embeddings[wordCount][colCount]=lF;
				  l_length=l_length+(lF*lF);
				  colCount++;
			  }
			  colCount = 0;
			  l_length=(float) Math.sqrt(l_length);
			  while(colCount < vector_size)
			  {
				  embeddings[wordCount][colCount]=embeddings[wordCount][colCount]/l_length;
				  colCount++;
			  }
			  wordCount++;
			  if (wordCount % (vocab_size/100) == 0) System.out.print(".");
		  if (wordCount % (vocab_size/10) == 0) System.out.print("|");
		  }
		  modelReader.close();
		  System.out.println();
	  }
	public float[] getVector(String word)
	{
		float[] toReturn= new float[(int)vector_size];
		Arrays.fill(toReturn,0);
		if ( myHashMap.containsKey(word) )
		{
			int wordIndex = (int)myHashMap.get(word);
			toReturn = embeddings[wordIndex];
		}
		return toReturn;
	}
	public float getSimilarity(float[] v1, float[] v2)
	{
		  float sim = 0;
		  int colCount = 0;
		  while(colCount < vector_size)
		  {
			  sim = sim + v1[colCount]*v2[colCount];
			  colCount++;
		  }
		  return sim;		
	}
	public float getSimilarity(String w1, String w2)
	{
		  if ( !myHashMap.containsKey(w1) )
		  {
			  //System.err.println(w1 + " not founded!");
			  return 0;
		  }
		  if ( !myHashMap.containsKey(w2) )
		  {
			  //System.err.println(w2 + " not founded!");
			  return 0;
		  }
		  float[] v1 = getVector(w1);
		  float[] v2 = getVector(w2);
		  //float[] vfinal = new float[(int)vector_size];
		  return getSimilarity(v1, v2);		  
	}
	public float getDistance(String w1, String w2)
	{
		  return 1-getSimilarity(w1, w2);
	}
	public float getSimilarityProb(float[] v1, float[] v2)
	{
		  float sim = 0;
		  int colCount = 0;
		  while(colCount < vector_size)
		  {
			  sim = sim + v1[colCount]*v2[colCount];
			  colCount++;
		  }
		  return ((float)1+sim)/(float)2;		
	}
	public float getSimilarityProb(String w1, String w2)
	{
		  if ( !myHashMap.containsKey(w1) )
		  {
			  //System.err.println(w1 + " not founded!");
			  return 0;
		  }
		  if ( !myHashMap.containsKey(w2) )
		  {
			  //System.err.println(w2 + " not founded!");
			  return 0;
		  }
		  float[] v1 = getVector(w1);
		  float[] v2 = getVector(w2);
		  //float[] vfinal = new float[(int)vector_size];
		  return getSimilarityProb(v1, v2);		  
	}
	public float getDistanceProb(String w1, String w2)
	{
		  return 1-getSimilarityProb(w1, w2);
	}
	public Vector< Vector <String> > getClosest(String w1)
	{
		Vector< Vector <String> > toReturn = new Vector< Vector <String> > ();
		Vector <String> l_array = new Vector <String> ();
		l_array.add("");
		l_array.add("0.0");
		toReturn.add(l_array);
		  if ( !myHashMap.containsKey(w1) )
		  {
			  //System.err.println(w1 + " not founded!");
			  return toReturn;
		  }
		  float[] v1 = getVector(w1);
		  int w1_id = (int)myHashMap.get(w1);
		  int i,j;
		  float[] v2; 
		  for (i = 0 ; i < vocab_size ; i++)
		  {
			  if (i != w1_id)
			  {
				  v2 = embeddings[i];
				  l_array = new Vector <String> ();
				  l_array.add(new String(myReverseHashMap.get(i)));
				  float tmpScore = getSimilarity(v1, v2);
				  l_array.add(Float.toString(tmpScore));
				  j=0;
				  while (j < toReturn.size() && j < MAXRETURN)
				  {
					  Vector <String>  l_l_array = toReturn.get(j);
					  float l_tmpScore = Float.parseFloat(l_l_array.get(1));
					  if  (tmpScore > l_tmpScore)
					  {
						  toReturn.add(j,l_array);
						  break;
					  }
					  j++;
				  }
			  }
		  }
		  toReturn.remove(toReturn.lastElement());
		  return toReturn;
	}
//	public Vector< Vector <String> > getClosest(String w1)
//	{
//		Vector< Vector <String> > toReturn = new Vector< Vector <String> > ();
//		Vector <String> l_array = new Vector <String> ();
//		l_array.add("");
//		l_array.add("0.0");
//		toReturn.add(l_array);
//		  if ( !myHashMap.containsKey(w1) )
//		  {
//			  //System.err.println(w1 + " not founded!");
//			  return toReturn;
//		  }
//		  float[] v1 = getVector(w1);
//		  int w1_id = (int)myHashMap.get(w1);
//		  int i,j;
//		  float[] v2; 
//		  for (i = 0 ; i < vocab_size ; i++)
//		  {
//			  if (i != w1_id)
//			  {
//				  v2 = embeddings[i];
//				  l_array = new Vector <String> ();
//				  l_array.add(new String(myReverseHashMap.get(i)));
//				  float tmpScore = getSimilarityProb(v1, v2);
//				  l_array.add(Float.toString(tmpScore));
//				  j=0;
//				  while (j < toReturn.size() && j < MAXRETURN)
//				  {
//					  Vector <String>  l_l_array = toReturn.get(j);
//					  float l_tmpScore = Float.parseFloat(l_l_array.get(1));
//					  if  (tmpScore > l_tmpScore)
//					  {
//						  toReturn.add(j,l_array);
//						  break;
//					  }
//					  j++;
//				  }
//			  }
//		  }
//		  toReturn.remove(toReturn.lastElement());
//		  return toReturn;
//	}
	public Vector< Vector <String> > getClosest(String w1 , Vector <String> vs)
	{
		Vector< Vector <String> > toReturn = new Vector< Vector <String> > ();
		Vector <String> l_array = new Vector <String> ();
		l_array.add("");
		l_array.add("-1000.0");
		toReturn.add(l_array);
		  if ( !myHashMap.containsKey(w1) )
		  {
			  //System.err.println(w1 + " not founded!");
			  return toReturn;
		  }
		  float[] v1 = getVector(w1);
//		  int w1_id = (int)myHashMap.get(w1);
		  int i,j;
		  float[] v2; 
		  float tmpScore = 0;
		  for (i = 0 ; i < vs.size() ; i++)
		  {
			  l_array = new Vector <String> ();
			  if ( !myHashMap.containsKey(w1) )
			  {
				  l_array.add(new String(vs.get(i)));
				  l_array.add(new String("0.0"));
			  }
			  else
			  {
				  v2 = getVector(vs.get(i));
				  l_array.add(new String(vs.get(i)));
				  tmpScore = getSimilarity(v1, v2);
				  l_array.add(Float.toString(tmpScore));
				  
			  }
			  j=0;
			  while (j < toReturn.size() )
			  {
				  Vector <String>  l_l_array = toReturn.get(j);
				  float l_tmpScore = Float.parseFloat(l_l_array.get(1));
				  if  (tmpScore > l_tmpScore)
				  {
					  toReturn.add(j,l_array);
					  break;
				  }
				  j++;
			  }
		  }
		  toReturn.remove(toReturn.lastElement());
		  return toReturn;
	}
	public Vector< Vector <String> > getSimilarity(String w1 , Vector <String> vs)
	{
		Vector< Vector <String> > toReturn = new Vector< Vector <String> > ();
		Vector <String> l_array = new Vector <String> ();
		  if ( !myHashMap.containsKey(w1) )
		  {
			  //System.err.println(w1 + " not founded!");
			  return toReturn;
		  }
		  float[] v1 = getVector(w1);
//		  int w1_id = (int)myHashMap.get(w1);
		  int i,j;
		  float[] v2; 
		  float tmpScore=0;
		  for (i = 0 ; i < vs.size() ; i++)
		  {
			  l_array = new Vector <String> ();
			  if ( !myHashMap.containsKey(w1) )
			  {
				  l_array.add(new String(vs.get(i)));
				  l_array.add(new String("0.0"));
			  }
			  else
			  {
				  v2 = getVector(vs.get(i));
				  l_array.add(new String(vs.get(i)));
				  tmpScore = getSimilarity(v1, v2);
				  l_array.add(Float.toString(tmpScore));
			  }
		  }
		  toReturn.remove(toReturn.lastElement());
		  return toReturn;
	}	
	public Vector< Vector <String> > getSimilarityProb(String w1 , Vector <String> vs)
	{
		Vector< Vector <String> > toReturn = new Vector< Vector <String> > ();
		Vector <String> l_array = new Vector <String> ();
		  if ( !myHashMap.containsKey(w1) )
		  {
			  //System.err.println(w1 + " not founded!");
			  return toReturn;
		  }
		  float[] v1 = getVector(w1);
//		  int w1_id = (int)myHashMap.get(w1);
		  int i,j;
		  float[] v2; 
		  float tmpScore=0;
		  for (i = 0 ; i < vs.size() ; i++)
		  {
			  l_array = new Vector <String> ();
			  if ( !myHashMap.containsKey(w1) )
			  {
				  l_array.add(new String(vs.get(i)));
				  l_array.add(new String("0.0"));
			  }
			  else
			  {
				  v2 = getVector(vs.get(i));
				  l_array.add(new String(vs.get(i)));
				  tmpScore = getSimilarityProb(v1, v2);
				  l_array.add(Float.toString(tmpScore));
			  }
		  }
		  toReturn.remove(toReturn.lastElement());
		  return toReturn;
	}	
}