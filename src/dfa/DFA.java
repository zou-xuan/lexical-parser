package dfa;

import java.io.*;
import java.util.*;

import fa.*;

public class DFA extends FA {	
	//开始状态
	protected FAState startState;

	public DFA() {		
	}
	
	/**
	 * 构造函数
	 * @param filePath 包含有限状态自动机的构造所需的数据的文件的路径
	 * 		文件的格式,举例如下:
	 * 					 5 3
                         1 2 3 4 5
                         b a !
                         1
                         5
                         1 -1 -1
                         -1 2 -1
                         -1 3 -1
                         -1 3 4
                         -1 -1 -1
	      其中，第一行的两个数字分别表示 自动机状态的数目和符号串的数目
			第二行的数字为每一个状态的id
			第三行为符号串(符号串间以空格隔开)
			第四行的数字为开始状态的id
			第五行的数字为结束状态的id
			最后几行为状态转换矩阵		
	 */
	public DFA(String filePath) {
		super(filePath);	 
	}

	public DFA(FAState startState, List<FAState> acceptingStates,
			List<FAState> states, List<String> alphabet, 
			List<List<TransitMatElement>> stateTransitionMat) {
		this.startState = startState;
		this.acceptingStates = acceptingStates;
		this.states = states;
		this.alphabet = alphabet;
		this.stateTransitionMat = stateTransitionMat;
	}
	
	//从文件中解析状态转移矩阵
	public void getStateTransitionsFromFile(BufferedReader in, int statesNum, int alphaNum) 
			throws IOException {
		this.stateTransitionMat = new ArrayList<List<TransitMatElement>>();
		for(int i=0; i<statesNum; i++) {
			stateTransitionMat.add(new ArrayList<TransitMatElement>());
			
			String rowOfTransit = in.readLine();
			String[] transitions = rowOfTransit.trim().split(" ");				
			for(int j=0; j<alphaNum; j++) {
				//构造搜索节点
				int stateIndex = Integer.parseInt(transitions[j]);
				TransitMatElement transitEle = 
						new TransitMatElement(j, stateIndex);
				stateTransitionMat.get(i).add(transitEle);
			}
		}
	}

	/**
	 * 使用自动机识别符号串
	 * @param words 待匹配符号串
	 * @return 如果接受，则返回true,否则，返回false 
	 */
	public String recognize(String[] words) {
		FAState currentState = this.startState;
		int countOfWordsRecognized = 0;
		while(countOfWordsRecognized <= words.length) {
			if(isAccepted(currentState, countOfWordsRecognized, words.length)) {  //接收
//				System.out.println(acceptingStates.size());
//				System.out.println(acceptingStates.get(0).getId());
//				return true;
				return null;
			} else if(wordsTerminatedButNotAccepted(currentState, words.length,
					countOfWordsRecognized)) {
//				return false;
				return null;
			}				
			// 当前待识别的单词在alphabet中的下标
			int indexOfAlpha = alphabet.indexOf(words[countOfWordsRecognized]);
			//查找状态转移矩阵，获取将要跳转到的状态的下标
			int transition = 
					getIndexOfStateToSwitch(states.indexOf(currentState), indexOfAlpha);
			if(transition == -1) {			//不能匹配当前符号串，拒绝
//				return false;
				return null;
			} else {
				currentState = this.states.get(transition);   //进行下一个符号串的接收				
				countOfWordsRecognized++;								
			}								
		}		
//		return false;
		return null;
	}
	
	//判断字符串是否已经被识别
	protected boolean isAccepted(FAState state, int countOfWordsRecognized, int wordsArrLength) {
		
		return countOfWordsRecognized == wordsArrLength && acceptingStates.contains(state);	
	}
	
	// 输入的符号串已经识别完毕，但还未遇到接收状态
	protected boolean wordsTerminatedButNotAccepted(FAState currentState, 
			int wordsNumber, int countOfWordsRecognized) {
		return countOfWordsRecognized == wordsNumber && !acceptingStates.contains(currentState);
	}

	//查找状态转移矩阵，获取将要跳转到的状态在状态列表中的下标
	private int getIndexOfStateToSwitch(int currentStateIndex, int indexOfAlpha) {
		// 将要转到的状态在状态转移矩阵中所在行
		List<TransitMatElement> transitionMatRow = 
				this.stateTransitionMat.get(currentStateIndex);
		int transition = -1;
		//查找该状态遇到下标为indexOfAlpha的单词后要转到的状态在状态列表中的下标
		for(TransitMatElement transitEle: transitionMatRow) {
			if(transitEle.getAlphaIndex() == indexOfAlpha) {
				transition = transitEle.getStateIndex();
				break;
			}
		}
		return transition;
	}

	@Override
	protected void getStartStateFromFile(BufferedReader in) throws IOException {
		String startStateId = in.readLine().trim();
		for(FAState currentState : states) {
			if(currentState.getId().equals(startStateId)) {
				this.startState = currentState;
				break;
			}
		}
	}

	/**
	 * 最小化当前DFA对象
	 */
	public void simplify() {
		//用于存放最小化的过程中产生的状态划分
		List<List<FAState>> stateLists = new ArrayList<List<FAState>>();
		// phrase 1: 将化简前的DFA的状态分为非可接受状态和可接受状态两部分
		List<FAState> nonTerminalStates = new ArrayList<FAState>();
		List<FAState> copyOfOriginalState = deepCloneOfFAStateList(this.states);
		for(FAState state : copyOfOriginalState) {
			if(!this.acceptingStates.contains(state)) {
				nonTerminalStates.add(state);
			}
		}
		List<FAState> terminalStates = deepCloneOfFAStateList(this.acceptingStates);
		stateLists.add(nonTerminalStates);
		stateLists.add(terminalStates);
		
		// phrase 2: 看nonTerminalStates能否再分,如果可以，则进行划分
		splitStateListIfCould(stateLists, nonTerminalStates);		
		
		// phrase 3: 看terminalStates能否再分，如果可以，则进行划分
		int leftMostEndStateIndex = splitStateListIfCould(stateLists, terminalStates);
		
		// phrase 4: 根据存储状态列表的列表的每一个元素作为一个状态，构造最小化DFA
		rebuildDFAWithSimplifiedStateList(stateLists, leftMostEndStateIndex);
	}

	/**
	 * 判断一个状态列表能否再分，如果可以，则继续划分该列表为多个列表
	 * @param stateLists 存储划分得到的状态列表的列表
	 * @param statesToCheck 待划分状态列表
	 */
	private int splitStateListIfCould(List<List<FAState>> stateLists,
			List<FAState> statesToCheck) {
		int index = stateLists.size() - 1;
		//存储不在属于当前划分的状态
		List<FAState> statesToRemove = new ArrayList<FAState>();  
		for(int i=0; i<this.alphabet.size(); i++) {
			for(FAState state : statesToCheck) {
				int stateIndex = this.states.indexOf(state);
				//获取状态state遇到下标为i的符号时状态跳转情况
				TransitMatElement transitEleRow = 
					this.stateTransitionMat.get(stateIndex).get(i);
				FAState nextState = this.states.get(transitEleRow.getStateIndex());
				if(!statesToCheck.contains(nextState)) {  
					//经过状态转移达到的状态不包含在状态集合statesToCheck中，
					//则nonTerminalStates继续划分为state和去掉state之后的nonTerminalStates					
					stateLists.add(stateLists.size()-1, Arrays.asList(state));
					statesToRemove.add(state);
					if(index > stateLists.size() - 1) {
						index = stateLists.size() - 1;
					}
				}
			}
		}
		statesToCheck.removeAll(statesToRemove);  //移除不再属于当前划分的状态
		return index;
	}
	
	/**
	 * 根据存储状态列表的列表的每一个元素作为一个状态，构造最小化DFA
	 * @param stateLists 存储状态列表的列表
	 */
	private void rebuildDFAWithSimplifiedStateList(List<List<FAState>> stateLists,
			int leftMostEndStateIndex) {
		List<FAState> copyOfStates = deepCloneOfFAStateList(this.states);
		this.states.clear();
		List<List<TransitMatElement>> copyOfTransitMat = deepCloneOfStateTransitionMat();
		this.stateTransitionMat.clear();	
		
		// phrase 1: 重新构造状态列表
		rebuildStateList(stateLists, leftMostEndStateIndex);
		
		// phrase 2: 重新构建状态转移矩阵
		rebuildStateTransitMat(copyOfStates, copyOfTransitMat, stateLists);
	}

	/**
	 * 重新构造状态列表
	 * @param stateLists 包含状态列表的列表
	 * @param leftMostEndStateIndex 可接受状态中下标最小的状态在stateLists中的下标
	 */
	private void rebuildStateList(List<List<FAState>> stateLists,
			int leftMostEndStateIndex) {
		Random random = new Random();		
		//stateLists中的第一个元素中的所有状态构成新的DFA对象的开始状态
		this.startState = 
			new FAState(System.currentTimeMillis() + "" + random.nextInt() + "S");
		this.states.add(startState);
				
		//添加既不是开始状态节点，也不是可接受状态节点的状态节点
		for(int i=1; i<leftMostEndStateIndex; i++) {			
			FAState newState = 
				new FAState(System.currentTimeMillis() + "" + random.nextInt() + "N");
			this.states.add(newState);
		}
		
		// stateLists中原来DFA对象的可接受状态构成新的DFA对象的可接受状态
		for(int i=leftMostEndStateIndex; i<stateLists.size(); i++) {
			FAState newState = 
				new FAState(System.currentTimeMillis() + "" + random.nextInt() + "E");
			this.acceptingStates.add(newState);
			this.states.add(newState);
		}
	} 

	/**
	 * 为最小化DFA重新构造状态转移矩阵
	 * @param originalStateList 原来DFA的状态列表
	 * @param originalStateTransitMat 原来DFA的状态转移矩阵
	 * @param stateists 存储状态列表的列表 
	 */
	private void rebuildStateTransitMat(List<FAState> originalStateList,
			List<List<TransitMatElement>> originalStateTransitMat,
			List<List<FAState>> stateLists) {
		for(int i=0; i<stateLists.size(); i++) {
			List<FAState> stateList = stateLists.get(i);		//当前状态划分
			List<TransitMatElement> stateTransitEleRow = 
					new ArrayList<TransitMatElement>();
			//建立到其它划分中状态的状态转移
			buildTansitWithStatesInOtherPartition(
					originalStateList,originalStateTransitMat, 
					stateLists, stateList, stateTransitEleRow);
			//建立划分内的状态转移(针对划分内某个状态遇到某一符号串不转向下一状态的情况)
			buildTransitWithStatesInInnerPartition(
					originalStateList, originalStateTransitMat, 
					stateLists, stateList, stateTransitEleRow);			
			this.stateTransitionMat.add(stateTransitEleRow);
		}
	}

	//建立划分内的状态转移(针对划分内某个状态遇到某一符号串不转向下一状态的情况)
	private void buildTransitWithStatesInInnerPartition(
			List<FAState> originalStateList,
			List<List<TransitMatElement>> originalStateTransitMat,
			List<List<FAState>> stateLists, List<FAState> stateList,
			List<TransitMatElement> stateTransitEleRow) {
		for(FAState stateInPartition : stateList) {
			int stateIndex = originalStateList.indexOf(stateInPartition);
			List<TransitMatElement> transitEleRow = 
					originalStateTransitMat.get(stateIndex);
			for(TransitMatElement transitEle : transitEleRow) {
				if(transitEle.getStateIndex() == stateIndex) { //在该状态上存在转向自己的循环  
					//获取在最小化的DFA对象中的下标
					int currentStateIndex = 
						 getStateIndexInNewDFA(stateLists, stateInPartition);
					stateTransitEleRow.add(
						new TransitMatElement(transitEle.getAlphaIndex(), currentStateIndex));
				}
			}
		}
	}

	//建立到其它划分中状态的状态转移
	private void buildTansitWithStatesInOtherPartition(
			List<FAState> originalStateList,
			List<List<TransitMatElement>> originalStateTransitMat,
			List<List<FAState>> stateLists, List<FAState> stateList,
			List<TransitMatElement> stateTransitEleRow) {
		int originalStateIndex = originalStateList.indexOf(stateList.get(0));
		List<TransitMatElement> stateTransitMatRow =
				originalStateTransitMat.get(originalStateIndex);
		for(TransitMatElement transitEle : stateTransitMatRow) {
			//当前转向的状态
			FAState currentState = originalStateList.get(transitEle.getStateIndex());
			if(!stateList.contains(currentState)) {  
				//不是在同一个划分中，存在到其它划分则状态的状态转移
				int currentStateIndex = getStateIndexInNewDFA(stateLists, currentState);
				stateTransitEleRow.add(
					new TransitMatElement(transitEle.getAlphaIndex(), currentStateIndex));
			} 
		}
	}
	
	/**
	 * 查找某一状态在最小化DFA中所在划分的下标
	 * @param stateLists 状态列表的划分
	 * @param state 将要查找的FAState对象
	 * @return 某一状态在最小化DFA中所在划分的下标
	 */
	private int getStateIndexInNewDFA(List<List<FAState>> stateLists, FAState state) {
		for(int i=0; i<stateLists.size(); i++) {
			List<FAState> currentStateList = stateLists.get(i);
			if(currentStateList.contains(state)) {
				return i;
			}
		}
		return -1;
	}

}
