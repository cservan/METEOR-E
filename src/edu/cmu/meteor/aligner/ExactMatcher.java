/*
 * Carnegie Mellon University
 * Copyright (c) 2004, 2010
 * 
 * This software is distributed under the terms of the GNU Lesser General
 * Public License.  See the included COPYING and COPYING.LESSER files.
 * 
 */

package edu.cmu.meteor.aligner;

public class ExactMatcher {

	public static void match(int stage, Alignment a, Stage s) {
/*                for (int i = 0; i < s.words1.length; i++) {
                if (i != 0 ) { System.out.print(" "); } else {System.out.print("TOK1:\t");}
                System.out.print(s.m_wordStrings1.get(i));
                }
                System.out.print("\n");
                for (int i = 0; i < s.words2.length; i++) {
                if (i != 0 ) { System.out.print(" "); } else {System.out.print("TOK2:\t");}
                System.out.print(s.m_wordStrings2.get(i));
                }
                System.out.print("\n");
*/
		// Simplest possible matcher: test all word keys for equality

		for (int j = 0; j < s.words2.length; j++) {

			for (int i = 0; i < s.words1.length; i++) {

				// Match
				if (s.words1[i] == s.words2[j]) {

					Match m = new Match();
					m.module = stage;
					m.prob = 1;
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
