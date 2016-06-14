package nfa;

import org.junit.Test;

import dfa.DFA;
import fa.*;

public class NFATest {

/*	@Test
	public void test() {
		NFA nfa = new NFA("nfa_1.txt");
		List<String> alphabet = nfa.getAlphabet();
		System.out.println("Alphabet: ");
		for(int i=0; i<alphabet.size(); i++) {
			System.out.print(alphabet.get(i) + "\t");
		}
		System.out.println();
		
		List<Integer>[][] trainsitionMat = nfa.getStateTransitionMat();
		System.out.println("Transition matrix:");
		for(int i=0; i<trainsitionMat.length; i++) {
			for(int j=0; j<trainsitionMat[i].length; j++) {
				System.out.print("(");
				for(int stateID : trainsitionMat[i][j]) {
					System.out.print(stateID + " ");
				}			
				System.out.print(")\t");
			}
			System.out.println();
		}
	}
*/

	//@Test
	public void testRecognize() {
		FA nfa = new NFA("nfa_1.txt");
		String[] wordsToRecognize = {
			"b", "a", "a", "a", "a", "!"
		};
//		if(nfa.recognize(wordsToRecognize)) {
//			System.out.println("Recognize!");
//		} else {
//			System.out.println("Don't Recognize!");
//		}
	}
	
/*	//@Test
	public void test_closure() {
		NFA nfa = new NFA("nfa_2.txt");
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set<String> closure = nfa_closure(new HashSet(nfa.startState));
		System.out.println("closure of start state:");
		for(String id: closure) {
			System.out.print(id + "\t");
		}
	}
*/	
/*	//@Test
	public void testArcTransform() {
		NFA nfa = new NFA("nfa_2.txt");
		List<FAState> stateListI = Arrays.asList(
			nfa.getStateById("X"), nfa.getStateById("1"), nfa.getStateById("2")	
		);
		Set<String> arcTransform = nfa.arcTransform(stateListI, "b");
		System.out.println("ArcTransform of stateListI:");
		for(String id: arcTransform) {
			System.out.print(id + "\t");
		}
	}
*/	
	@Test
	public void testNFA2DFA() {
		String[] wordsForSample_nfa_2 = {"b", "a", "a"}; 
				//{"b", "b"}; 
		//{"b", "b", "b", "a"}; 
		//{"b", "a", "a", "a", "b"};
	
	//	String[] wordsForSample_nfa_3 = //{"b", "a", "a", "!"}; 
	//		{"b", "a", "a", "a", "!"};
		NFA nfa = new NFA("nfa_2.txt");
		NFA2DFA nfa2dfa = new NFA2DFA();
		DFA dfa = nfa2dfa.toDFA(nfa);		
//		if(dfa.recognize(wordsForSample_nfa_2)) {
//			System.out.println("Recognize");
//		} else {
//			System.out.println("Not recognize");
//		}
	}

	//@Test
	public void testNFAConcatenation() {
		NFA nfa_1 = new NFA("nfa_for_concatenation_1.txt");
		NFA nfa_2 = new NFA("nfa_for_concatenation_2.txt");
		FA nfa = nfa_1.concatenation(nfa_2);
//		if(nfa.recognize(new String[]{"a", "b"})) {
//			System.out.println("Recoginze!");
//		} else {
//			System.out.println("Not recoginze!");
//		}
	}
	
	//@Test
	public void testNFAClosure() {
		NFA nfa = new NFA("nfa_for_closure.txt");
		nfa.closure();
//		if(nfa.recognize(new String[]{"a", "b", "a", "b", "a", "b"})) {
//			System.out.println("Recoginze!");
//		} else {
//			System.out.println("Not recoginze!");
//		}
	}
	
	//@Test
	public void testUnion() {
		NFA nfa_1 = new NFA("nfa_for_union_1.txt");
		NFA nfa_2 = new NFA("nfa_for_union_2.txt");
		FA nfa = nfa_1.union(nfa_2);
//		if(nfa.recognize(new String[]{"b"})) {
//			System.out.println("Recoginze!");
//		} else {
//			System.out.println("Not recoginze!");
//		}
	}
	
}
