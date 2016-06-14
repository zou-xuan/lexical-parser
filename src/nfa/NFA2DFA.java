package nfa;

import java.util.*;

import dfa.DFA;
import fa.*;

/**
 * 将NFA对象转换为等价的DFA对象的类
 * @author Administrator
 *
 */
public class NFA2DFA {
	private FA nfa;	//NFA对象
	
	//将当前NFA对象转化成DFA
	public DFA toDFA(NFA nfa) {
		//深度复制当前的NFA对象
		NFA copyOfNFA = nfa.deepClone();								
		/* 一、对NFA的状态图进行改造 */		
		transformNFA(copyOfNFA);
		this.nfa = copyOfNFA;
		/* 二、对改造后的NFA使用子集法进行确定化  */		
		return subsetToDeterminateNFA(copyOfNFA);
	}
	
	/**
	 *	对改造后的NFA使用子集法进行确定化 
	 * @param nfa 待确定化的NFA对象
	 * @return NFA转化得到的DFA对象
	 */
	private DFA subsetToDeterminateNFA(NFA nfa) {
		//开始状态
		FAState startState;
		//结束状态
		List<FAState> acceptingStates = new ArrayList<FAState>();
		//所有状态
		List<FAState> states = new ArrayList<FAState>();	
		//符号字母表
		List<String> alphabet = nfa.deepCloneOfAphabet();	
		//状态转移矩阵(使用邻接链表表示)
		List<List<TransitMatElement>> stateTransitionMat = 
				new ArrayList<List<TransitMatElement>>();
		// 求出初始状态改造后的NFA的唯一初始状态的ε-闭包
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set<String> firstClosure = ε_closure(new HashSet(nfa.startState));
		//开始状态
		startState = new FAState(firstClosure);
		states.add(startState);
		Queue<FAState> stateStack = new LinkedList<FAState>();
		stateStack.add(startState);
		while(!stateStack.isEmpty()) {
			FAState currentState = stateStack.poll();
			List<FAState> originalStates = 
					getStateListFromStateIdList(nfa, currentState.getOriginalStatesIds());
			List<TransitMatElement> trainsitMatRow = new ArrayList<TransitMatElement>();
			for(String alpha: alphabet) {
				Set<String> arcTransformResult = arcTransform(originalStates, alpha);				
				//当前状态尚未被添加
				if(!isStateAdded(states, arcTransformResult)) {		
					FAState newState = new FAState(arcTransformResult);
					states.add(newState);
					stateStack.add(newState);
					//构造DFA的接受状态
					for(FAState endState: nfa.getAcceptingStates()) {
						//DFA的终态就是所有包含了NFA终态的DFA的状态
						if(arcTransformResult.contains(endState.getId())) {
							acceptingStates.add(newState);
						}
					}
					
					int indexOfAlpha = alphabet.indexOf(alpha);
					int stateIndex = states.indexOf(newState);
					trainsitMatRow.add(new TransitMatElement(indexOfAlpha, stateIndex));
				} else {
					int indexOfAlpha = alphabet.indexOf(alpha);
					FAState stateAdded = getStateByOriginalStates(states, arcTransformResult);
					int stateIndex = states.indexOf(stateAdded);
					trainsitMatRow.add(new TransitMatElement(indexOfAlpha, stateIndex));
				}
				
			}
			stateTransitionMat.add(trainsitMatRow);
		}		
		//构造DFA对象
		DFA dfa = new DFA(startState, acceptingStates, states, alphabet, stateTransitionMat);
		return dfa;
	}

	/**
	 * 将NFA进行改造，便于后面进行NFA的确定化
	 * @param nfa 待改造的NFA对象
	 */
	private void transformNFA(NFA nfa) {
		//确定NFA对象只有一个开始状态
		makeSureNFAHasOnlyOneStartState(nfa);
		//确定NFA对象只有一个结束状态
		makeSureNFAHasOnlyOneEndState(nfa);
	}

	//确定状态是否已经被添加	
	private boolean isStateAdded(List<FAState> states, Set<String> originalStateIds) {
		for(FAState state: states) {
			if(state.getOriginalStatesIds().containsAll(originalStateIds)
			   && originalStateIds.containsAll(state.getOriginalStatesIds())) {
				return true;
			}
		}
		return false;
	}
	
	//根据originalStates来查找FAState
	private FAState getStateByOriginalStates(List<FAState> states, Set<String> originalStates) {
		for(FAState state: states) {
			if(state.getOriginalStatesIds().containsAll(originalStates)
			&& originalStates.containsAll(state.getOriginalStatesIds())) {
					return state;
			}
		}
		return null;
	}
	
	private List<FAState> getStateListFromStateIdList(FA copyOfNFA, Set<String> stateIdList) {
		List<FAState> originalStates = new ArrayList<FAState>();
		for(String stateId: stateIdList) {
			originalStates.add(getStateById(stateId));
		}
		return originalStates;
	}

	//状态集合I的ε-闭包
	private Set<String> ε_closure(Set<FAState> stateList) {
		Set<String> closure = new HashSet<String>();
		//(1)若q∈I，则q属于ε_Closure(I)
		for(FAState state: stateList) {
			closure.add(state.getId());
		}
		//(2)若q属于I,那么从q出发经任意条ε边而能够到达的状态q'都属于ε_closure(I)
		Stack<FAState> stateStack = new Stack<FAState>();
		for(FAState state: stateList) {
			stateStack.push(state);
		}
		while(!stateStack.empty()) {
			FAState state = stateStack.pop();
			int indexOfState = this.nfa.getStates().indexOf(state);
			List<TransitMatElement> trasitEleList =
					this.nfa.getStateTransitionMat().get(indexOfState);
			for(TransitMatElement transitEle : trasitEleList) {
				// alphaIndex 为-1表示遇到空串ε
				if(transitEle.getAlphaIndex() == -1) {
					FAState stateSearched = this.nfa.getStates().get(transitEle.getStateIndex()); 
					String stateId = stateSearched.getId();
					closure.add(stateId);
					//将通过ε能够达到的状态压入栈中
					stateStack.push(stateSearched);
				}
			}
		}
		return closure;
	}
	
	/**
	 * 状态集合I的α弧转换 
	 * @param state 状态集合I
	 * @param alpha 符号alpha的弧
	 * @return 状态集合I的α弧转换
	 */
	private Set<String> arcTransform(List<FAState> stateListI, String alpha) {
		//从I中的某一状态节点经过一条弧alpha而到达的状态的集合
		Set<FAState> stateFromIPassAlpha = 
				getStatesFromStateListIViaArcAlpha(stateListI, alpha);
		//Ia = ε_closure(J)
		return ε_closure(stateFromIPassAlpha);
	}

	//获取从I中的某一状态节点经过一条弧alpha而到达的状态的集合
	private Set<FAState> getStatesFromStateListIViaArcAlpha(
			List<FAState> stateListI, String alpha) {
		Set<FAState> stateFromIPassAlpha = new HashSet<FAState>();
		for(FAState state: stateListI) {
			int indexOfState = this.nfa.getStates().indexOf(state);
			List<TransitMatElement> transitMatList =
					this.nfa.getStateTransitionMat().get(indexOfState);
			for(TransitMatElement transitEle : transitMatList) {
				//经过符号alpha
				if(transitEle.getAlphaIndex() == this.nfa.getAlphabet().indexOf(alpha)) {
					FAState stateSearched = this.nfa.getStates().get(transitEle.getStateIndex());
					stateFromIPassAlpha.add(stateSearched);
				}
			}
		}
		return stateFromIPassAlpha;
	} 
	
	//根据唯一标识来查找FAState对象
	private FAState getStateById(String stateId) {
		 for(FAState state: this.nfa.getStates()) {
			 if(stateId.equals(state.getId())) {
				 return state;
			 }
		 }
		 return null;
	}
	
	private void makeSureNFAHasOnlyOneEndState(FA nfa) {
		//当前NFA不只一个开始状态，则需要插入一个状态作为新的唯一的结束状态
		if(nfa.getAcceptingStates().size() > 1 || oneEndStateWithPointToItself(nfa)) {			
			FAState newEndState = new FAState(System.currentTimeMillis() + "Y"); //Y表示唯一的结束状态
			List<FAState> originalAcceptingStates = 
					nfa.deepCloneOfFAStateList(nfa.getAcceptingStates());
			nfa.getAcceptingStates().clear();
			//同步更新结束状态
			nfa.getAcceptingStates().add(newEndState);
			nfa.getStates().add(newEndState);
			nfa.getStateTransitionMat().add(new ArrayList<TransitMatElement>());
			//原来的结束状态添加对新的结束状态的引用
			for(FAState startState: originalAcceptingStates) {
				//alpha的下标为-1表示空串ε(当遇到ε 时，不匹配符号串，转向下一个状态)
				int indexOfState = nfa.getStates().indexOf(startState);
				List<TransitMatElement> currentTansitEleRow = 
						nfa.getStateTransitionMat().get(indexOfState); 
				currentTansitEleRow.add(new TransitMatElement(-1, 
						nfa.getStates().indexOf(newEndState)));
			}					
		}
	}

	private void makeSureNFAHasOnlyOneStartState(NFA nfa) {
		//当前NFA不只一个开始状态，则需要插入一个状态作为新的唯一的开始状态
		if(nfa.startState.size() > 1 || oneStartStateWithPointToItself(nfa)) {
			FAState newStartState = new FAState(System.currentTimeMillis() + "X"); //X表示唯一的开始状态
			List<FAState> originalStartStates = 
					nfa.deepCloneOfFAStateList(nfa.startState);
			nfa.startState.clear();
			//同步更新开始状态
			nfa.startState.add(newStartState);
			nfa.getStates().add(newStartState);
			//同步更新状态转移矩阵(在状态转移矩阵中为新插入的状态添加一行)
			List<TransitMatElement> transitMatEleRow =
					new ArrayList<TransitMatElement>();
			//新添加的链表中添加对原来的开始状态的引用
			for(FAState startState: originalStartStates) {
				//alpha的下标为-1表示空串ε(当遇到ε 时，不匹配符号串，转向下一个状态)
				transitMatEleRow.add(new TransitMatElement(-1, 
						nfa.getStates().indexOf(startState)));
			}			
			nfa.getStateTransitionMat().add(transitMatEleRow);		
		}
	}
	
	//判断NFA对象是否只有一个开始状态，但这个开始状态遇到某些字符串时不进入下一个状态
	private boolean oneStartStateWithPointToItself(NFA nfa) {
		int indexOfStartState = nfa.getStates().indexOf(nfa.startState.get(0));
		return nfa.startState.size() == 1 
				&& nfa.getStateTransitionMat().get(indexOfStartState).size() > 1;
	}
	
	//判断NFA对象是否只有一个终止状态，但这个终止状态遇到某些字符串时不进入下一个状态
	private boolean oneEndStateWithPointToItself(FA nfa) {
		int indexOfEndState = nfa.getStates().indexOf(nfa.getAcceptingStates().get(0));
		return nfa.getAcceptingStates().size() == 1 
				&& nfa.getStateTransitionMat().get(indexOfEndState).size() > 1;
	}
	
}
