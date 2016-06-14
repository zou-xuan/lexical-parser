package fa;

import java.io.*;
import java.util.*;

public abstract class FA {
	protected List<FAState> acceptingStates;		//可接收状态(结束状态)
	protected List<FAState> states;					//所有状态
	protected List<String> alphabet;				//符号字母表
	//状态转移矩阵(使用邻接链表表示)
	protected List<List<TransitMatElement>> stateTransitionMat;  
	
	public FA() {		
	}
	
	/**
	 * 构造函数
	 * @param filePath 包含有限状态自动机的构造所需的数据的文件的路径		
	 */
	public FA(String filePath) {
		initialize(filePath);
	}
	
	public void printInfo(){
		System.out.println("AllState:");
		for(int i=0;i<states.size();i++){
			System.out.print(states.get(i).getId()+"  ");
		}
		System.out.println();
		System.out.println("Alphabet: ");
		for(int i=0;i<alphabet.size();i++){
			System.out.print(alphabet.get(i)+"  ");
		}
		System.out.println();
		System.out.println("Accept: ");
		for(int i=0;i<acceptingStates.size();i++){
			System.out.print(acceptingStates.get(i).getId());
		}
		System.out.println();
		System.out.println("MAT: ");
		for(int i=0;i<stateTransitionMat.size();i++){
			List<TransitMatElement> l=stateTransitionMat.get(i);
			for(int j=0;j<l.size();j++){
				TransitMatElement t=l.get(j);
				int a_location=t.getAlphaIndex();
				int s_location=t.getStateIndex();
				if (a_location==-1) {
					System.out.print("by e- --->>"+states.get(s_location).getId()+"  ");
				}
				else {
					System.out.print("by "+alphabet.get(a_location)+" --->> "+states.get(s_location).getId()+"  ");
				}
				
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	//读取包含有限状态自动机的构造所需的数据的文件，构造自动机
	protected void initialize(String filePath) {
		try {
			BufferedReader input = 
				new BufferedReader(new FileReader(filePath));
			// 读取状态数目和符号的数目
			String[] nums = (input.readLine().trim()).split(" ");
			int statesNum = Integer.parseInt(nums[0]);
			int alphaNum = Integer.parseInt(nums[1]);			
			// 读取转移状态id序列
			getStatesFromFile(input, statesNum);			
			// 读取符号字母表
			getAlphabetFromFile(input);						
			// 读取开始状态id
			getStartStateFromFile(input);			
			// 读取结束状态id
			getEndStatesFromFile(input);			
			// 读取状态转移矩阵
			getStateTransitionsFromFile(input, statesNum, alphaNum);			
			input.close();			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	protected void getEndStatesFromFile(BufferedReader in) throws IOException {
		this.acceptingStates = new ArrayList<FAState>();
		String[] endStateIds = in.readLine().trim().split(" ");		
		for(String stateId : endStateIds) {
			for(FAState currentState : states) {
				if(currentState.getId().equals(stateId)) {						
					this.acceptingStates.add(currentState);
					break;
				}
			}
		}
	}	

	protected void getAlphabetFromFile(BufferedReader in) throws IOException {
		String line = in.readLine();
		String[] alphabetArr = line.trim().split(" ");		
		this.alphabet = new ArrayList<String>();
		for(int i=0; i<alphabetArr.length; i++) {
			this.alphabet.add(alphabetArr[i]);
		}
	}

	protected void getStatesFromFile(BufferedReader in, int statesNum)
			throws IOException {
		states = new ArrayList<FAState>();
		String[] stateIds = in.readLine().trim().split(" ");
		for(int i=0; i<statesNum; i++) {
			states.add(new FAState(stateIds[i]));
		}
	}
	
	protected abstract void getStateTransitionsFromFile(BufferedReader in, 
			int statesNum, int alphaNum) throws IOException;
	
	protected abstract void getStartStateFromFile(BufferedReader in) throws IOException;
	
	// 识别字符串
	public abstract String recognize(String[] words);	

	/**
	 * 获取可接受状态列表
	 * @return 可接受状态列表
	 */
	public List<FAState> getAcceptingStates() {
		return acceptingStates;
	}

	/**
	 * 获取状态列表
	 * @return 状态列表
	 */
	public List<FAState> getStates() {
		return states;
	}

	/**
	 * 获取符号字母表
	 * @return 符号字母表
	 */
	public List<String> getAlphabet() {
		return alphabet;
	}

	/**
	 * 获取状态转移矩阵
	 * @return 状态转移矩阵
	 */
	public List<List<TransitMatElement>> getStateTransitionMat() {
		return stateTransitionMat;
	}

	public void setAcceptingStates(List<FAState> acceptingStates) {
		this.acceptingStates = acceptingStates;
	}

	public void setStates(List<FAState> states) {
		this.states = states;
	}

	public void setAlphabet(List<String> alphabet) {
		this.alphabet = alphabet;
	}

	public void setStateTransitionMat(
			List<List<TransitMatElement>> stateTransitionMat) {
		this.stateTransitionMat = stateTransitionMat;
	}

	protected List<List<TransitMatElement>> deepCloneOfStateTransitionMat() {
		List<List<TransitMatElement>> copyOfStateTransitionMat = 
				new ArrayList<List<TransitMatElement>>();		
		for(List<TransitMatElement> rowOfStateransitMat: this.stateTransitionMat) {
			List<TransitMatElement> copyOfStateTransitList = 
					new ArrayList<TransitMatElement>();
			for(TransitMatElement transitEle: rowOfStateransitMat) {
				TransitMatElement copyOfTransitEle = 
					new TransitMatElement(transitEle.getAlphaIndex(), 
							transitEle.getStateIndex());
				copyOfStateTransitList.add(copyOfTransitEle);
			}
			copyOfStateTransitionMat.add(copyOfStateTransitList);
		}
		return copyOfStateTransitionMat;
	}

	public List<String> deepCloneOfAphabet() {
		List<String> copyOfalphabet = new ArrayList<String>();
		for(String alpha: this.alphabet) {
			// 不使用copyOfalphabet.add(alpha); (浅度复制String对象)
			copyOfalphabet.add(new String(alpha));  
		}
		return copyOfalphabet;
	}

	public List<FAState> deepCloneOfFAStateList(List<FAState> stateList) {
		List<FAState> copyOfStateList = new ArrayList<FAState>();
		for(FAState state: stateList) {
			// 不用FAState copyOfState = new FAState(state.getId()); (浅度复制String对象)
			FAState copyOfState = new FAState(new String(state.getId()));			
			copyOfStateList.add(copyOfState);
		}
		return copyOfStateList;
	}
		
}
