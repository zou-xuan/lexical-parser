package fa;

// 搜索节点
public class TransitMatElement {
	private int alphaIndex;		//该节点对应的符号串在 alphabet中的下标
	private int stateIndex;		//该节点所对应的状态在 states中的下标
	
	public TransitMatElement(int alphaIndex, int stateIndex) {
		super();
		this.alphaIndex = alphaIndex;
		this.stateIndex = stateIndex;
	}

	public int getAlphaIndex() {
		return alphaIndex;
	}

	public int getStateIndex() {
		return stateIndex;
	}
				
}
