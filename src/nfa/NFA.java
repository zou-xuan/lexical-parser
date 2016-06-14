package nfa;

import java.io.*;
import java.util.*;

import fa.*;

public class NFA extends FA {
	// 开始状态
	protected List<FAState> startState;

	public NFA() {
	}

	/**
	 * 构造函数
	 * 
	 * @param filePath
	 *            包含有限状态自动机的构造所需的数据的文件的路径 文件的格式,举例如下: 5 3 1 2 3 4 6 b a ! 1 6 1
	 *            -1 -1 -1 -1 2 -1 -1 -1 2,3 -1 -1 -1 3 4 -1 -1 -1 -1 -1
	 *            其中，第一行的两个数字分别表示 自动机状态的数目和符号串的数目 第二行的数字为每一个状态的id
	 *            第三行为符号串(符号串间以空格隔开) 第四行的数字为开始状态的id 第四行的数字为结束状态的id
	 *            最后几行为状态转换矩阵(最后几行的最后一列对应ε-转换)
	 */
	public NFA(String filePath) {
		super(filePath);
	}

	/**
	 * 构造函数
	 * 
	 * @param startStates
	 *            开始状态
	 * @param acceptingStates
	 *            接受状态
	 * @param states
	 *            全部状态
	 * @param alphabet
	 *            符号字母表
	 * @param stateTransitionMat
	 *            状态转移矩阵
	 */
	private NFA(List<FAState> startStates, List<FAState> acceptingStates,
			List<FAState> states, List<String> alphabet,
			List<List<TransitMatElement>> stateTransitionMat) {
		this.startState = startStates;
		this.acceptingStates = acceptingStates;
		this.states = states;
		this.alphabet = alphabet;
		this.stateTransitionMat = stateTransitionMat;
	}

	/**
	 * 创建只有单个字符的NFA对象
	 * 
	 * @param character
	 *            字符
	 */
	public NFA(char character) {
		Random random = new Random();
		this.startState = Arrays.asList(new FAState(System.currentTimeMillis()
				+ "" + random.nextInt() + "S"));
		this.acceptingStates = Arrays.asList(new FAState(System
				.currentTimeMillis() + "" + random.nextInt() + "E"));
		this.alphabet = Arrays.asList(character + "");
		// 构造状态列表
		this.states = new ArrayList<FAState>();
		this.states.addAll(this.startState);
		this.states.addAll(this.acceptingStates);
		// 构造状态转移矩阵
		this.stateTransitionMat = new ArrayList<List<TransitMatElement>>();
		this.stateTransitionMat.add(new ArrayList<TransitMatElement>());
		this.stateTransitionMat.add(new ArrayList<TransitMatElement>());
		// 开始状态遇到字符character之后就转向可接受状态
		this.stateTransitionMat.get(0).add(new TransitMatElement(0, 1));
	}

	// 从文件中解析状态转移矩阵
	protected void getStateTransitionsFromFile(BufferedReader in,
			int statesNum, int alphaNum) throws IOException {
		this.stateTransitionMat = new ArrayList<List<TransitMatElement>>();
		int numOfColumn = alphaNum + 1; // 状态转移矩阵的列数
		for (int i = 0; i < statesNum; i++) {
			this.stateTransitionMat.add(new ArrayList<TransitMatElement>());

			String rowOfTransit = in.readLine();
			String[] transitions = rowOfTransit.trim().split(" ");
			for (int j = 0; j < numOfColumn; j++) {
				// 出现','表明当前状态下遇到同意字符可以向多个状态转移
				if (transitions[j].contains(",")) {
					String[] allPossibleStates = transitions[j].split(",");
					for (String singleStates : allPossibleStates) {
						TransitMatElement transitEle = new TransitMatElement(j,
								Integer.parseInt(singleStates));
						stateTransitionMat.get(i).add(transitEle);
					}

				} else { // 这一列只有一个状态id
					int stateId = Integer.parseInt(transitions[j]);
					if (stateId != -1) {
						// 若j为状态转移矩阵最后一列，则表示遇到空串ε
						int indexOfAlpha = (j == numOfColumn - 1) ? -1 : j;
						TransitMatElement transitEle = new TransitMatElement(
								indexOfAlpha, stateId);
						stateTransitionMat.get(i).add(transitEle);
					}
				}
			}
		}
	}

	@Override
	protected void getStartStateFromFile(BufferedReader in) throws IOException {
		this.startState = new ArrayList<FAState>();
		// 从文件中解析开始状态ID(NFA开始状态可能有多个)
		String[] startStateIDStrs = in.readLine().trim().split(" ");
		for (String stateIdStr : startStateIDStrs) {
			for (FAState currentState : states) {
				if (currentState.getId().equals(stateIdStr)) {
					this.startState.add(currentState);
					break;
				}
			}
		}
	}

	/**
	 * 使用自动机识别符号串(深度优先遍历)
	 * 
	 * @param words
	 *            待匹配符号串
	 * @return 如果接受，则返回true,否则，返回false
	 */
	public String recognize(String[] words) {
		// 对于每一个开始状态，逐一尝试，看能否识别输入的符号串
		for (FAState state : this.startState) {
			FAState currentState = state;
			int countOfWordsRecognized = 0;
			// 用于存储识别的每一步中可能跳转到的所有状态
			Stack<FAState> agenda = new Stack<FAState>();
			while (countOfWordsRecognized <= words.length) {
				String r=isAccepted(currentState, countOfWordsRecognized,
						words.length);
				if (r!=null) {
					return r;
				} else if (wordsTerminatedButNotAccepted(currentState,
						words.length, countOfWordsRecognized)) {
					// 当前开始状态下不能识别，尝试下一个开始状态
					break;
				} else {
					int indexOfAlpha = this.alphabet
							.indexOf(words[countOfWordsRecognized]);
					// 当前符号串不在符号字母表中，识别失败
					if (indexOfAlpha < 0) {
						return null;
					} else {
						boolean isWordsRecgnized = generateNewStates(
								currentState, indexOfAlpha, agenda);
						if (isWordsRecgnized) {
							countOfWordsRecognized++;
						}
					}
				}
				/*
				 * 选当前开始状态时，当前步所有可能的状态都已经尝试，但未能匹配当前符号串。 尝试下一个开始状态
				 */
				if (agenda.isEmpty()) {
					break;
				} else {
					currentState = agenda.pop(); // 进入下一个状态
				}
			}
		}
		return null;
	}

	// 判断当前NFA状态能否接受传入的符号串
	protected String isAccepted(FAState state, int countOfWordsRecognized,
			int wordsArrLength) {
		if (countOfWordsRecognized != wordsArrLength) {
			return null;
		}
		if (acceptingStates.contains(state)) { // 在可接受状态中，接受该符号串
			return state.getId();
		}
		// 判断当前状态是否能够只通过ε-转换达到可接受状态
		Stack<FAState> stateStack = new Stack<FAState>();
		stateStack.add(state);
		while (!stateStack.isEmpty()) {
			FAState curState = stateStack.pop();
			int curIndex = this.states.indexOf(curState);
			List<TransitMatElement> transitMatEleRow = this.stateTransitionMat
					.get(curIndex);
			for (TransitMatElement transitEle : transitMatEleRow) {
				if (transitEle.getAlphaIndex() == -1) { // ε
					FAState state_1 = this.states.get(transitEle
							.getStateIndex());
					if (acceptingStates.contains(state_1)) {
//						System.out.println(acceptingStates.size());
//						System.out.println(acceptingStates.get(0).getId());
						return state_1.getId();
					} else if (!stateStack.contains(state_1)) {
						stateStack.add(state_1);
					}
				}
			}
		}
		return null;
	}

	// 输入的符号串已经识别完毕，但还未遇到接收状态
	protected boolean wordsTerminatedButNotAccepted(FAState currentState,
			int wordsNumber, int countOfWordsRecognized) {
		String s=isAccepted(currentState, wordsNumber,
				countOfWordsRecognized);
		if (s==null) {
			return countOfWordsRecognized == wordsNumber;
		}
		else {
			return false;
		}
//		return countOfWordsRecognized == wordsNumber
//				&& !isAccepted(currentState, wordsNumber,
//						countOfWordsRecognized);
	}

	/**
	 * 添加指定的状态遇到对应的符号串时所用可能进入的状态列表到状态栈agend
	 * 
	 * @param state
	 * @param indexOfAlpha
	 * @param agend
	 *            存放状态的栈
	 * @return 当前单词是否被识别
	 */
	private boolean generateNewStates(FAState state, int indexOfAlpha,
			Stack<FAState> agend) {
		int indexOfState = this.states.indexOf(state);
		// 获取下标为 indexOfState状态在状态转移矩阵中所对应的行
		List<TransitMatElement> transitMatEleRow = this.stateTransitionMat
				.get(indexOfState);
		List<FAState> states = new ArrayList<FAState>();
		boolean isWordRecognized = false;
		for (TransitMatElement transEle : transitMatEleRow) {
			// 按照遇到的符号串的下标查找对应的要转移到的状态
			if (transEle.getAlphaIndex() == indexOfAlpha) {
				states.add(this.states.get(transEle.getStateIndex()));
				isWordRecognized = true; // 当前单词被识别
			} else if (transEle.getAlphaIndex() == -1) { // ε-转移
				states.add(this.states.get(transEle.getStateIndex()));
			}
		}
		for (FAState curState : states) {
			if (!agend.contains(curState)) { // 当栈中不含有该状态时，才压入栈中
				agend.add(curState);
			}
		}
		return isWordRecognized;
	}

	/**
	 * 深度复制当前的NFA对象
	 * 
	 * @return 深度复制得到的NFA对象
	 */
	public NFA deepClone() {
		// 深度复制开始状态
		List<FAState> copyOfStartStates = deepCloneOfFAStateList(this.startState);
		// 深度复制结束状态
		List<FAState> copyOfAcceptingStates = deepCloneOfFAStateList(this.acceptingStates);
		// 深度复制所有状态
		List<FAState> copyOfStates = deepCloneOfFAStateList(this.states);
		// 深度复制符号字母表
		List<String> copyOfalphabet = deepCloneOfAphabet();
		// 状态转移矩阵(使用邻接链表表示)
		List<List<TransitMatElement>> copyOfStateTransitionMat = deepCloneOfStateTransitionMat();
		NFA copyOfNFA = new NFA(copyOfStartStates, copyOfAcceptingStates,
				copyOfStates, copyOfalphabet, copyOfStateTransitionMat);
		return copyOfNFA;
	}

	/**
	 * 将当前nfa对象与传入的nfa使用ε-转移进行毗邻(concatenation)运算，得到一个新的NFA对象
	 * 
	 * @param nfa
	 *            需要与之进行连接运算的NFA对象
	 * @return 连接运算得到的新的NFA对象
	 */
	public NFA concatenation(NFA nfa) {
		NFA resultNFA = new NFA();
		resultNFA.startState = deepCloneOfFAStateList(this.startState);
		resultNFA.acceptingStates = deepCloneOfFAStateList(nfa.acceptingStates);
		/* 获取连接后得到的NFA对象的符号字母表 */
		List<String> newAlphabet = this.deepCloneOfAphabet();
		newAlphabet.addAll(nfa.deepCloneOfAphabet());
		// 注意: 使用Set去掉重复符号
		resultNFA.alphabet = new ArrayList<String>(new LinkedHashSet<String>(
				newAlphabet));
		/* 获取连接后得到的NFA对象的状态列表 */
		resultNFA.states = new ArrayList<FAState>();
		resultNFA.states.addAll(deepCloneOfFAStateList(this.states));
		resultNFA.states.addAll(deepCloneOfFAStateList(nfa.states));
		/* 获取连接后得到的NFA对象的状态转移矩阵 */
		setMergedStateTransitMatForConcatenation(nfa, resultNFA);
		return resultNFA;
	}

	/* 设置连接(concatenation)后得到的NFA对象的状态转移矩阵 */
	private void setMergedStateTransitMatForConcatenation(NFA nfa, FA resultNFA) {
		// 将两个NFA的状态转移矩阵合并成到新的NFA对象的状态转移矩阵中
		List<List<TransitMatElement>> newTransitMat = combineTwoStateTransitMats(
				nfa, resultNFA);
		resultNFA.setStateTransitionMat(newTransitMat);
		// 从resultNFA获取当前NFA对象的可接受状态的拷贝
		List<FAState> originalCurEndStatesCopy = getOriginalEndStatesInNewStateList(
				this, resultNFA);
		// 从resultNFA获取传入的NFA对象的开始状态的拷贝
		List<FAState> originalInputStartStatesCopy = getOriginalStartStatesInNewStateList(
				nfa, resultNFA);
		// 将当前NFA对象的可接受状态与传入的NFA对象的初始状态连接起来
		addεTransfromBetweenStates(// this.acceptingStates,
				originalCurEndStatesCopy,
				// nfa.startState,
				originalInputStartStatesCopy, resultNFA);
	}

	private List<FAState> getOriginalStartStatesInNewStateList(NFA nfa,
			FA resultNFA) {
		List<FAState> originalStartStatesCopy = new ArrayList<FAState>();
		for (FAState state : nfa.startState) {
			for (FAState curState : resultNFA.getStates()) {
				if (curState.getId().equals(state.getId())) { // 不用==
					originalStartStatesCopy.add(curState);
					break;
				}
			}
		}
		return originalStartStatesCopy;
	}

	private List<FAState> getOriginalEndStatesInNewStateList(FA nfa,
			FA resultNFA) {
		List<FAState> originalEndStatesCopy = new ArrayList<FAState>();
		for (FAState state : nfa.getAcceptingStates()) {
			for (FAState curState : resultNFA.getStates()) {
				if (curState.getId().equals(state.getId())) {
					originalEndStatesCopy.add(curState);
					break;
				}
			}
		}
		return originalEndStatesCopy;
	}

	// 将两个NFA的状态转移矩阵合并成到新的NFA对象的状态转移矩阵中
	private List<List<TransitMatElement>> combineTwoStateTransitMats(FA nfa,
			FA resultNFA) {
		List<List<TransitMatElement>> newTransitMat = new ArrayList<List<TransitMatElement>>();
		// deepCloneOfStateTransitionMat();
		// 将传入的NFA对象的状态转移矩阵添加到新的NFA对象的状态转移矩阵中
		addStateTransitMatToNewNFA(this, resultNFA, newTransitMat);
		// 将传入的NFA对象的状态转移矩阵添加到新的NFA对象的状态转移矩阵中
		addStateTransitMatToNewNFA(nfa, resultNFA, newTransitMat);
		return newTransitMat;
	}

	/**
	 * 将源NFA对象的状态转移矩阵复制到目标NFA对象的相应位置
	 * 
	 * @param srcNFA
	 *            源NFA对象
	 * @param dstNFA
	 *            目标NFA对象
	 * @param newTransitMat
	 *            目标NFA对象的状态转移矩阵
	 */
	private void addStateTransitMatToNewNFA(FA srcNFA, FA dstNFA,
			List<List<TransitMatElement>> newTransitMat) {
		for (List<TransitMatElement> transitEleRow : srcNFA
				.getStateTransitionMat()) {
			List<TransitMatElement> newTransitEleRow = new ArrayList<TransitMatElement>();
			for (TransitMatElement transitEle : transitEleRow) {
				FAState relativeState = srcNFA.getStates().get(
						transitEle.getStateIndex());
				int curStateIndex = dstNFA.getStates().indexOf(relativeState);
				int curAlphaIndex;
				if (transitEle.getAlphaIndex() == -1) { // ε-转换
					curAlphaIndex = -1;
				} else {
					String alpha = srcNFA.getAlphabet().get(
							transitEle.getAlphaIndex());
					curAlphaIndex = dstNFA.getAlphabet().indexOf(alpha);
				}
				newTransitEleRow.add(new TransitMatElement(curAlphaIndex,
						curStateIndex));
			}
			newTransitMat.add(newTransitEleRow);
		}
	}

	/**
	 * 对当前NFA对象进行闭包(Kleene*)运算
	 */
	public void closure() {
		/* 一、建立一个新的开始状态，并指向原来的开始状态 */
		Random random = new Random();
		List<FAState> originalStartStates = this.startState;
		FAState newStartState = new FAState(System.currentTimeMillis() + ""
				+ random.nextInt() + "S");
		this.startState = Arrays.asList(newStartState);
		this.states.add(newStartState);
		// 为新添加的开始状态在状态转移矩阵中添加一行
		this.stateTransitionMat.add(new ArrayList<TransitMatElement>());
		// 让新的开始状态使用ε-转移指向原来的开始状态
		addεTransfromBetweenStates(this.startState, originalStartStates, this);

		/* 二、建立一个新的结束状态，并让原来的可接受状态使用ε-转移指向新的结束状态 */
		List<FAState> originalAcceptStates = this.acceptingStates;
		FAState newAcceptingState = new FAState(System.currentTimeMillis() + ""
				+ random.nextInt() + "E");
		this.acceptingStates = Arrays.asList(newAcceptingState);
		this.states.add(newAcceptingState);
		// 为新添加的可接受状态在状态转移矩阵中添加一行
		this.stateTransitionMat.add(new ArrayList<TransitMatElement>());
		// 让原来的可接受状态使用ε-转移指向新的结束状态
		addεTransfromBetweenStates(originalAcceptStates, this.acceptingStates,
				this);

		/* 三、让新的开始状态使用ε-转移指向新的可接受状态 */
		addεTransfromBetweenStates(this.startState, this.acceptingStates, this);

		/* 四、让原来的可接受状态使用ε-转移指向原来的开始状态 */
		addεTransfromBetweenStates(originalAcceptStates, originalStartStates,
				this);
	}

	/**
	 * 将当前NFA对象与传入的NFA对象进行结合(|)运算,得到新的NFA对象
	 * 
	 * @param nfa
	 *            另一个NFA对象
	 * @return 结合运算得到的新的NFA对象
	 */
	public NFA union(NFA nfa) {
		Random random = new Random();
		NFA resultNFA = new NFA();
		// 创建一个新的状态作为开始状态
		resultNFA.startState = Arrays.asList(new FAState(System
				.currentTimeMillis() + "" + random.nextInt() + "S"));
		// 创建一个新的状态作为可接受状态
		resultNFA.acceptingStates = Arrays.asList(new FAState(System
				.currentTimeMillis() + "" + random.nextInt() + "E"));
		/* 获取连接后得到的NFA对象的符号字母表 */
		List<String> newAlphabet = this.deepCloneOfAphabet();
		newAlphabet.addAll(nfa.deepCloneOfAphabet());
		resultNFA.alphabet = new ArrayList<String>(new LinkedHashSet<String>(
				newAlphabet));
		/* 获取连接后得到的NFA对象的状态列表 */
		List<FAState> newStateList = new ArrayList<FAState>();
		newStateList.addAll(resultNFA.startState); // 加入新创建的开始状态
		newStateList.addAll(deepCloneOfFAStateList(this.states));
		newStateList.addAll(deepCloneOfFAStateList(nfa.states));
		newStateList.addAll(resultNFA.acceptingStates); // 加入新创建的可接受状态
		resultNFA.states = newStateList;
		/* 构造NFA的状态转移矩阵 */
		setMergedStateTransitMatForUnion(nfa, resultNFA);
		return resultNFA;
	}

	/* 设置合并(union)后得到的NFA对象的状态转移矩阵 */
	private void setMergedStateTransitMatForUnion(NFA nfa, NFA resultNFA) {
		// 将两个NFA的状态转移矩阵合并成到新的NFA对象的状态转移矩阵中
		List<List<TransitMatElement>> newTransitMat = new ArrayList<List<TransitMatElement>>();
		// 为新建的开始状态在状态转移矩阵中添加一行
		newTransitMat.add(new ArrayList<TransitMatElement>());
		newTransitMat.addAll(combineTwoStateTransitMats(nfa, resultNFA));
		// 为新建的可接受状态在状态转移矩阵中添加一行
		newTransitMat.add(new ArrayList<TransitMatElement>());
		// 设置状态转移矩阵
		resultNFA.setStateTransitionMat(newTransitMat);

		// 从resultNFA获取输入的NFA对象的开始状态的拷贝
		List<FAState> originalInputStartStatesCopy = getOriginalStartStatesInNewStateList(
				nfa, resultNFA);
		// 从resultNFA获取输入的NFA对象的可接受状态的拷贝
		List<FAState> originalInputEndStatesCopy = getOriginalEndStatesInNewStateList(
				nfa, resultNFA);
		// 从resultNFA获取当前NFA对象的开始状态的拷贝
		List<FAState> originalCurStartStatesCopy = getOriginalStartStatesInNewStateList(
				this, resultNFA);
		// 从resultNFA获取当前NFA对象的可接受状态的拷贝
		List<FAState> originalCurEndStatesCopy = getOriginalEndStatesInNewStateList(
				this, resultNFA);

		// 将新创建的开始状态使用ε-转换分别与两个NFA对象的开始状态连接
		addεTransfromBetweenStates(resultNFA.startState,
		// this.startState,
				originalCurStartStatesCopy, resultNFA);
		addεTransfromBetweenStates(resultNFA.startState,
		// nfa.startState,
				originalInputStartStatesCopy, resultNFA);
		// 用原来的两个NFA对象的可接受状态使用ε-转换与新的可接受状态连接
		addεTransfromBetweenStates(
		// this.acceptingStates,
				originalCurEndStatesCopy, resultNFA.acceptingStates, resultNFA);
		addεTransfromBetweenStates(
				// nfa.acceptingStates,
				originalInputEndStatesCopy, resultNFA.acceptingStates,
				resultNFA);
	}

	// 将两个NFA合并！利用union

	public NFA getBigNFA(NFA nfa) {
		Random random = new Random();
		NFA resultNFA = new NFA();
		resultNFA.startState = Arrays.asList(new FAState(System
				.currentTimeMillis() + "" + random.nextInt() + "S"));
		resultNFA.acceptingStates = new ArrayList<>();
		resultNFA.acceptingStates.addAll(this.acceptingStates);
		resultNFA.acceptingStates.addAll(nfa.acceptingStates);
		List<String> newAlphabet = this.deepCloneOfAphabet();
		newAlphabet.addAll(nfa.deepCloneOfAphabet());

		resultNFA.alphabet = new ArrayList<String>(new LinkedHashSet<String>(
				newAlphabet));
		List<FAState> newStateList = new ArrayList<FAState>();
		newStateList.addAll(resultNFA.startState); // 加入新创建的开始状态
		newStateList.addAll(deepCloneOfFAStateList(this.states));
		newStateList.addAll(deepCloneOfFAStateList(nfa.states));
		resultNFA.states = newStateList;
		/* 构造NFA的状态转移矩阵 */
		setBigMat(nfa, resultNFA);
		return resultNFA;
	}

	public void setBigMat(NFA nfa, NFA resultNFA) {
		List<List<TransitMatElement>> newTransitMat = new ArrayList<List<TransitMatElement>>();
		// 为新建的开始状态在状态转移矩阵中添加一行
		newTransitMat.add(new ArrayList<TransitMatElement>());
		newTransitMat.addAll(combineTwoStateTransitMats(nfa, resultNFA));
		// 为新建的可接受状态在状态转移矩阵中添加一行
		// 设置状态转移矩阵
		resultNFA.setStateTransitionMat(newTransitMat);
		// 从resultNFA获取输入的NFA对象的开始状态的拷贝
		List<FAState> originalInputStartStatesCopy = getOriginalStartStatesInNewStateList(
				nfa, resultNFA);
		// 从resultNFA获取输入的NFA对象的可接受状态的拷贝
		List<FAState> originalInputEndStatesCopy = getOriginalEndStatesInNewStateList(
				nfa, resultNFA);
		// 从resultNFA获取当前NFA对象的开始状态的拷贝
		List<FAState> originalCurStartStatesCopy = getOriginalStartStatesInNewStateList(
				this, resultNFA);
		// 从resultNFA获取当前NFA对象的可接受状态的拷贝
		List<FAState> originalCurEndStatesCopy = getOriginalEndStatesInNewStateList(
				this, resultNFA);
		addεTransfromBetweenStates(resultNFA.startState, 
				//this.startState,
				originalCurStartStatesCopy,
				resultNFA);
		addεTransfromBetweenStates(resultNFA.startState, 
				//nfa.startState,
				originalInputStartStatesCopy,
				resultNFA);
	}

	/**
	 * 对于stateList_1中的每一个状态节点，添加到stateList_2中的每一个状态节点的ε-转换
	 * 
	 * @param stateList_1
	 *            状态列表1
	 * @param stateList_2
	 *            状态列表2
	 * @param nfa
	 *            进行运算的NFA对象
	 */
	private void addεTransfromBetweenStates(List<FAState> stateList_1,
			List<FAState> stateList_2, FA nfa) {
		for (FAState startState : stateList_1) {
			int index = nfa.getStates().indexOf(startState);
			List<TransitMatElement> transitMatEleRow = nfa
					.getStateTransitionMat().get(index);
			for (FAState startStateOfThis : stateList_2) {
				int index_1 = nfa.getStates().indexOf(startStateOfThis);
				transitMatEleRow.add(new TransitMatElement(-1, index_1));
			}
		}
	}

}
