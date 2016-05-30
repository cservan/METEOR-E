/*
 * Carnegie Mellon University
 * Copyright (c) 2004, 2010
 * 
 * This software is distributed under the terms of the GNU Lesser General
 * Public License.  See the included COPYING and COPYING.LESSER files.
 * 
 */

package edu.cmu.meteor.aligner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import static java.util.Arrays.asList;

public class SynonymMatcher {

	public static void match(String language, int stage, Alignment a, Stage s,
			SynonymDictionary synonyms) {
 
                // Map words to sets of synonym set numbers
                
		Hashtable<Integer, HashSet<Integer>> string1Syn = new Hashtable<Integer, HashSet<Integer>>();
		Hashtable<Integer, HashSet<Integer>> string2Syn = new Hashtable<Integer, HashSet<Integer>>();

//		System.err.println(a.words1.toArray(new String[a.words1.size()]));
//		System.err.println(a.words2.toArray(new String[a.words2.size()]));
		if (synonyms.ttw != null )
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
			String out1=synonyms.ttw.tag(tab1);
//			System.err.println("OUT1 " + out1);
			
			String[] tab2=new String[a.words2.size()];			
			for (int i=0; i< a.words2.size(); i++)
			{
				tab2[i]=a.words2.get(i);
			}						
			String out2=synonyms.ttw.tag(tab2);
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
						a.POS1.add(_tabout1[j]);
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
						a.POS2.add(_tabout2[j]);
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
			System.exit(0);
		}
//		System.exit(0);
		// Line 1
		if (a.lemma1.size() != a.words1.size())
		{
			System.err.println(a.words1.size());
			System.err.println(a.lemma1.size());
		}
		if (a.lemma2.size() != a.words2.size())
		{
			System.err.println(a.words2.size());
			System.err.println(a.lemma2.size());
		}
		for (int i = 0; i < a.words1.size(); i++) {
			HashSet<Integer> set = new HashSet<Integer>(synonyms
					.getSynSets(language, a.words1.get(i)));
			set.addAll(synonyms.getSynSets(language,a.lemma1.get(i)));
			string1Syn.put(i, set);
		}

		// Line 2
		for (int i = 0; i < a.words2.size(); i++) {
			HashSet<Integer> set = new HashSet<Integer>(synonyms
					.getSynSets(language, a.words2.get(i)));
			set.addAll(synonyms.getSynSets(language,a.lemma2.get(i)));
			string2Syn.put(i, set);
		}

/*		// Line 1
		for (int i = 0; i < a.words1.size(); i++) {
			HashSet<Integer> set = new HashSet<Integer>(synonyms
					.getSynSets(language, a.words1.get(i)));
			set.addAll(synonyms.getStemSynSets(language,a.words1.get(i)));
			string1Syn.put(i, set);
		}

		// Line 2
		for (int i = 0; i < a.words2.size(); i++) {
			HashSet<Integer> set = new HashSet<Integer>(synonyms
					.getSynSets(language, a.words2.get(i)));
			set.addAll(synonyms.getStemSynSets(language,a.words2.get(i)));
			string2Syn.put(i, set);
		}
*/
		for (int j = 0; j < a.words2.size(); j++) {

			for (int i = 0; i < a.words1.size(); i++) {

				Iterator<Integer> sets1 = string1Syn.get(i).iterator();
				HashSet<Integer> sets2 = string2Syn.get(j);

				boolean syn = false;
				double weight = 0;
				while (sets1.hasNext()) {
					if (sets2.contains(sets1.next())) {
						syn = true;
						weight = 1;
						break;
					}
				}

				// Match if DIFFERENT words with SAME synset
				if (syn && s.words1[i] != s.words2[j]) {

					Match m = new Match();
					m.module = stage;
					m.prob = weight;
					m.start = j;
					m.length = 1;
					m.matchStart = i;
					m.matchLength = 1;

					// Add this match to the list of matches and mark coverage
					s.matches.get(j).add(m);
					s.line1Coverage[i]++;
					s.line2Coverage[j]++;
				}
			}
		}
	}
}