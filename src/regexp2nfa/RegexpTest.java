package regexp2nfa;

import io.IOHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nfa.NFA;
import nfa.NFA2DFA;
import dfa.DFA;

public class RegexpTest {

	public static void main(String[] args) {		
		String letter="a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z";
		String digit="0|1|2|3|4|5|6|7|8|9";
		Regexp regexp_id = new Regexp("@"+letter+"$@"+letter+"|"+digit+"$*");
		Regexp regexp_num=new Regexp("@"+digit+"$@"+digit+"$*");
		Regexp regexp_while=new Regexp("while");
		Regexp regexp_if=new Regexp("if");
		Regexp regexp_else=new Regexp("else");
		Regexp regexp_main=new Regexp("main");
		Regexp regexp_void=new Regexp("void");
		Regexp regexp_int=new Regexp("int");
		Regexp regexp_equal=new Regexp("=");
		Regexp regexp_larger=new Regexp(">");
		Regexp regexp_less=new Regexp("<");
		Regexp regexp_left1=new Regexp("[");
		Regexp regexp_right1=new Regexp("]");
		Regexp regexp_left3=new Regexp("{");
		Regexp regexp_right3=new Regexp("}");
		Regexp regexp_left2=new Regexp("(");
		Regexp regexp_right2=new Regexp(")");
		Regexp regexp_feng=new Regexp(";");
		Regexp regexp_for=new Regexp("for");
		Regexp regexp_plus=new Regexp("+");
		Regexp regexp_subscribe=new Regexp("-");
		Regexp regexp_pp=new Regexp("++");
		Regexp regexp_pe=new Regexp("+=");
		
	
		NFA nfa_id = (NFA)regexp_id.toNFA();
		NFA nfa_digit=(NFA)regexp_num.toNFA();
		NFA nfa_while=(NFA)regexp_while.toNFA();
		NFA nfa_if=(NFA)regexp_if.toNFA();
		
		NFA nfa_else=(NFA)regexp_else.toNFA();
		NFA nfa_equal=(NFA)regexp_equal.toNFA();
		NFA nfa_larger=(NFA)regexp_larger.toNFA();
		NFA nfa_less=(NFA)regexp_less.toNFA();
		NFA nfa_void=(NFA)regexp_void.toNFA();
		NFA nfa_main=(NFA)regexp_main.toNFA();
		NFA nfa_int=(NFA)regexp_int.toNFA();
		NFA nfa_left1=(NFA)regexp_left1.toNFA();
		NFA nfa_right1=(NFA)regexp_right1.toNFA();
		NFA nfa_left3=(NFA)regexp_left3.toNFA();
		NFA nfa_right3=(NFA)regexp_right3.toNFA();
		NFA nfa_left2=(NFA)regexp_left2.toNFA();
		NFA nfa_right2=(NFA)regexp_right2.toNFA();
		NFA nfa_feng=(NFA)regexp_feng.toNFA();
		NFA nfa_for=(NFA)regexp_for.toNFA();
		NFA nfa_plus=(NFA)regexp_plus.toNFA();
		NFA nfa_sub=(NFA)regexp_subscribe.toNFA();
		NFA nfa_pp=(NFA)regexp_pp.toNFA();
		NFA nfa_pe=(NFA)regexp_pe.toNFA();
		
		
		NFA2DFA nfa2dfa=new NFA2DFA();
		String[] words = new String[]{
				"i"
		};
		String[] word_digit=new String[]{
				"1","3"
		};
		String[] word_key=new String[]{
				"e","l","s","e"
		};
		String[] word_less=new String[]{
				"else"
		};
		
		NFA result=nfa_id.getBigNFA(nfa_digit).getBigNFA(nfa_while).getBigNFA(nfa_else)
				.getBigNFA(nfa_if).getBigNFA(nfa_equal).getBigNFA(nfa_larger).getBigNFA(nfa_less)
				.getBigNFA(nfa_void).getBigNFA(nfa_main).getBigNFA(nfa_int).getBigNFA(nfa_left1).getBigNFA(nfa_left3)
				.getBigNFA(nfa_right1).getBigNFA(nfa_right3).getBigNFA(nfa_feng).getBigNFA(nfa_for)
				.getBigNFA(nfa_plus).getBigNFA(nfa_sub).getBigNFA(nfa_pe).getBigNFA(nfa_pp)
				.getBigNFA(nfa_left2).getBigNFA(nfa_right2);
		DFA dfa_result=nfa2dfa.toDFA(result);
		Map<String,Integer> map=new HashMap<String, Integer>();
		for(int i=0;i<nfa_id.getAcceptingStates().size();i++){
			map.put(nfa_id.getAcceptingStates().get(i).getId(), 1);
		}
		for(int i=0;i<nfa_digit.getAcceptingStates().size();i++){
			map.put(nfa_digit.getAcceptingStates().get(i).getId(), 2);
		}
		map.put(nfa_while.getAcceptingStates().get(0).getId(), 3);
		map.put(nfa_if.getAcceptingStates().get(0).getId(), 4);
		map.put(nfa_else.getAcceptingStates().get(0).getId(), 5);
		map.put(nfa_for.getAcceptingStates().get(0).getId(), 6);
		map.put(nfa_void.getAcceptingStates().get(0).getId(), 7);
		map.put(nfa_main.getAcceptingStates().get(0).getId(), 8);
		map.put(nfa_int.getAcceptingStates().get(0).getId(), 9);
		
		map.put(nfa_left1.getAcceptingStates().get(0).getId(), 10);
		map.put(nfa_right1.getAcceptingStates().get(0).getId(), 11);
		map.put(nfa_left3.getAcceptingStates().get(0).getId(), 12);
		map.put(nfa_right3.getAcceptingStates().get(0).getId(), 13);
		map.put(nfa_left2.getAcceptingStates().get(0).getId(), 14);
		map.put(nfa_right2.getAcceptingStates().get(0).getId(), 15);
		map.put(nfa_feng.getAcceptingStates().get(0).getId(), 22);
		map.put(nfa_equal.getAcceptingStates().get(0).getId(), 16);
		map.put(nfa_plus.getAcceptingStates().get(0).getId(), 17);
		map.put(nfa_sub.getAcceptingStates().get(0).getId(), 18);
		map.put(nfa_pp.getAcceptingStates().get(0).getId(), 20);
		map.put(nfa_pe.getAcceptingStates().get(0).getId(), 21);
		map.put(nfa_less.getAcceptingStates().get(0).getId(), 19);
		dfa_result.getClass();
		
		char[] s=IOHelper.readFileByChars("test.txt");
		
		run(result, s, map);
		
		
	}
	
	public static void run(NFA result,char[] s,Map<String, Integer> map){
		ArrayList<String[]> string_token=getSingleString(s);
		ArrayList<String> token=new ArrayList<>();
		for(int i=0;i<string_token.size();i++){
			String tmp=result.recognize(string_token.get(i));
			int id=map.get(tmp);
			
			String content=getString(string_token.get(i));
			token.add(getToken(id, content));
		}
		IOHelper.writeIntoFile("output.txt", token);
	}
	
	public static String getString(String[] s){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<s.length;i++){
			sb.append(s[i]);
		}
		return sb.toString();
	}
	
	public static String getToken(int id,String content){
		StringBuffer sb=new StringBuffer();
		if (id==1) {
			sb.append("<标识符,"+content+",1>");
		}
		else if(id==2){
			sb.append("<数字,"+content+",2>");
		}
		else if (id>2&&id<10) {
			sb.append("<关键字,"+content+","+id+">");
		}
		else {
			sb.append("<符号,"+content+","+id+">");
		}
		return sb.toString();
		
	}
	
	public static ArrayList<String[]> getSingleString(char[] s){
		ArrayList<String[]> result=new ArrayList<>();
		
		int length=s.length;
		int index=0;
		int start=0;
		int end=0;
		while(true){
			if (index==length||s[index]=='\0') {
				break;
			}
			else {
				char c=s[index];
				if(isCharacter(c)||isDigit(c)){
					index++;
					end++;
				}
				//index=18
				else if(c==' '||c=='\n'||c=='\t'){
					if (start!=end) {
						result.add(processChartoString(s, start, end));
					}
					index++;
					start=index;
					end=index;
				}
				else {
					if (start!=end) {
						result.add(processChartoString(s, start, end));
					}
					if (isOperation(c)&&isOperation(s[index+1])) {
						result.add(processChartoString(s, index, index+2));
						index+=2;
						end=index;
						start=index;
					}
					else {
						result.add(processChartoString(s, index, index+1));
						index++;
						end=index;
						start=index;
					}
					
				}
			}
		}
		return result;
	}
	
	public static String[] processChartoString(char[] s,int start,int end){
		String[] result=new String[end-start];
		for(int i=start;i<end;i++){
			result[i-start]=s[i]+"";
		}
		return result;
	}
	
	public static boolean isCharacter(char c){
		if((c>='a'&&c<='z')||(c>='A'&&c<='Z')){
			return true;
		}
		return false;
	}
	
	public static boolean isDigit(char c){
		if (c>='0'&&c<='9') {
			return true;
		}
		return false;
	}
	
	public static boolean isOperation(char c){
		if (c=='+'||c=='-'||c=='*'||c=='/'||c=='=') {
			return true;
		}
		return false;
	}
	
	

}
