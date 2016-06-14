package regexp2nfa;

import java.util.*;

import fa.FA;
import nfa.NFA;

public class Regexp {
	private String regexp;	//正则表达式

	public Regexp(String regexp) {
		super();
		this.regexp = regexp;
	}
	
	/**
	 * 将正则表达式转化为等价的NFA对象
	 * @return 与该正则表达式等价的NFA对象
	 */
	public FA toNFA() {
		//在正则表达式中添加省略掉的毗邻运算符(.)
		String newRegexp = addRemovedConcatenationOP();
		System.out.println(newRegexp);
		//将添加毗邻运算符后的正则表达式转化为后缀表达式
		String postfix = infixToPostfix(newRegexp);
		System.out.println(postfix);
		//将用后缀表达式表示的正则表达式转化为等价的NFA对象
		return evaluateExpression(postfix);
	}
	
	/**
	 * 在传入的正则表达式中添加省略掉的毗邻运算符(.)
	 * @return 添加上毗邻运算符后的正则表达式(.)
	 */
	private String addRemovedConcatenationOP() {
		StringBuffer sb = new StringBuffer();
		sb.append(regexp.charAt(0));
		for(int i=1; i<this.regexp.length(); i++) {
			//添加毗邻运算符(.)
			if(isCharacter(i) && (regexp.charAt(i-1) != '@' && regexp.charAt(i-1) != '|' ) 
			 || (regexp.charAt(i) == '@' && isCharacter(i-1))) {
				sb.append('.');
			}
			sb.append(regexp.charAt(i));
		}
		return sb.toString();
	}

	/**
	 * 判断正则表达式regexp中当前下标的字符是不是字母
	 * @param i 下标
	 * @return 若为字母，则返回true；否则，返回false
	 */
	private boolean isCharacter(int i) {
		return regexp.charAt(i) >= 'a' && regexp.charAt(i) <= 'z' 
			|| regexp.charAt(i) >= 'A' && regexp.charAt(i) <= 'Z';
	}

	/**
	 * 将前缀表达式转化为后缀表达式
	 * @param expression 前缀表达式
	 * @return 转化得到的后缀表达式
	 */
	private String infixToPostfix(String expression) {
		StringBuffer postfix = new StringBuffer();
		//存储操作符的栈
		Stack<Character> operatorStack = new Stack<Character>();
		// 将操作符与操作数分开
	    StringTokenizer tokens =
	    	new StringTokenizer(expression, "@$*|.", true);
	    // 阶段1: 扫描符号串
	    while(tokens.hasMoreTokens()) {
	    	String token = tokens.nextToken().trim();
	    	if(token.length() == 0) {  //空格
	    		continue;
	    	} else if(token.charAt(0) == '|') {
	    		// Process all * , . in the top of the operator stack
	    		while(!operatorStack.isEmpty() 
	    			&& (operatorStack.peek() == '*' || operatorStack.peek() == '.')) {
	    			postfix.append(operatorStack.pop() + " ");
	    		}
	    		// Push the | operator into the operator stack
	    		operatorStack.push(token.charAt(0));
	    		
	    	} else if(token.charAt(0) == '.') {
	    		// Process all . in the top of the operator stack
	    		while (!operatorStack.isEmpty() && operatorStack.peek().equals('.')) {
					postfix.append(operatorStack.pop() + " ");
		        }
	    		// Push the . operator into the operator stack
		        operatorStack.push(token.charAt(0));
		        
	    	} else if(token.charAt(0) == '*') {
	    		postfix.append(token.charAt(0) + " ");    		
	    	} else if(token.charAt(0) == '@') {
	    		operatorStack.push(new Character('@')); // Push '(' to stack
	    	} else if(token.charAt(0) == '$') { 
	    		// Process all the operators in the stack until seeing '('
		        while (!operatorStack.peek().equals('@')) {
					postfix.append(operatorStack.pop() + " ");
		        }
		        operatorStack.pop();
		        
	    	} else {
	    		postfix.append(token + " ");
	    	}	    	
	    }
	    
	    // 阶段 2: process all the remaining operators in the stack
	    while(!operatorStack.isEmpty()) {
	    	postfix.append(operatorStack.pop() + " ");
	    }
	    return postfix.toString();
	}
	
	/**
	 * 将用后缀表达式表示的正则表达式转化为等价的NFA对象
	 * @param postfix 表示正则表达式的后缀表达式
	 * @return 转化得到的NFA对象
	 */
	private FA evaluateExpression(String postfix) {
		//创建一个操作数栈来存储操作数		
		Stack<NFA> operandStack = new Stack<NFA>();
		//分离操作数与操作符
		StringTokenizer tokens = new StringTokenizer(postfix, "*|.@$ ", true);
		//遍历符号
		while(tokens.hasMoreTokens()) {
			String token = tokens.nextToken().trim();
			if(token.length() == 0) {	//空格
				continue;
			} else if(token.charAt(0) == '*') {	//*操作符(单目运算符)
				NFA nfa = operandStack.pop();
				nfa.closure();			//进行闭包运算
				operandStack.push(nfa);
			} else if(token.charAt(0) == '|'
					|| token.charAt(0) == '.') {
				processAnOperator(operandStack, token);
			}
			else {		//操作数
				operandStack.push(new NFA(token.charAt(0))); //为单个字符构造NFA对象
			}
		}
		NFA result=operandStack.pop();
//		result.printInfo();
		return result;
	}

	//处理一次双目运算符运算
	private void processAnOperator(Stack<NFA> operandStack, String token) {		
		char op = token.charAt(0);		//操作符
		NFA op1 = operandStack.pop();	//操作数1
		NFA op2 = operandStack.pop();	//操作数2
		if(op == '|') {			//union运算
			operandStack.push(op2.union(op1));
		} else if(op == '.') {	//concatenation运算
			operandStack.push(op2.concatenation(op1));
		}
	}
	
}
