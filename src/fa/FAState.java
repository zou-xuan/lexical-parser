package fa;

import java.util.*;

public class FAState {
	private static int count = 0;
	private String id;		//状态标识,每个状态都要一个唯一的状态标识		
	private Set<String> originalStatesIds = 
			new HashSet<String>();   //用于NFA转化为DFA，表示NFA的状态集合的标识 
	  
	public FAState() {
		this.id = (++count) + "";
	}

	public FAState(String id) {
		super();
		this.id = id;
	}		

	public FAState(Set<String> originalStatesIds) {
		super();
		this.id = (++count) + "";
		this.originalStatesIds = originalStatesIds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getOriginalStatesIds() {
		return originalStatesIds;
	}

	public void setOriginalStatesIds(Set<String> originalStatesIds) {
		this.originalStatesIds = originalStatesIds;
	}	

	//重写equals方法
	public boolean equals(Object state) {
		if(! (state instanceof FAState)) {
			return false;
		}
		FAState anotherState = (FAState)state;		
		return this.getId().equals(anotherState.getId());
	}
	
	//重写hashCode方法
	public int hashCode() {
		return this.id.length();
	}
}
