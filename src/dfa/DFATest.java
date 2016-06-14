package dfa;

import nfa.NFA;
import nfa.NFA2DFA;

import org.junit.Test;

import fa.FA;

public class DFATest {
	//@Test
/*	public void testDataGetter() {
		DFA dfa = new DFA("dfa_1.txt");
		List<String> alphabet = dfa.getAlphabet();
		System.out.println("Alphabet: ");
		for(int i=0; i<alphabet.size(); i++) {
			System.out.print(alphabet.get(i) + "\t");
		}
		System.out.println();
		
		int[][] trainsitionMat = dfa.getStateTransitionMat();
		System.out.println("Transition matrix:");
		for(int i=0; i<trainsitionMat.length; i++) {
			for(int j=0; j<trainsitionMat[i].length; j++) {
				System.out.print(trainsitionMat[i][j] + "\t");
			}
			System.out.println();
		}
	}
*/
	//@Test
	public void testDRecognize() {
		FA dfa = new DFA("dfa_1.txt");
		String[] words = {"b", "a", "a", "a", "a", "!"};
//		if(dfa.recognize(words)) {
//			System.out.println("Recognize");
//		} else {
//			System.out.println("Not recognize");
//		}
	}
	
	@Test
	public void testDFASimplify() {
		String[] wordsForSample_nfa_2 = {
				"b", "a", "b", "b", "b", "b", "a", "a", "b", "b"
		}; 	
		NFA nfa = new NFA("nfa_2.txt");
		NFA2DFA nfa2dfa = new NFA2DFA();
		DFA dfa = nfa2dfa.toDFA(nfa);		
		dfa.simplify();			
//		if(dfa.recognize(wordsForSample_nfa_2)) {
//			System.out.println("Recognize");
//		} else {
//			System.out.println("Not recognize");
//		}
	}
	
}
