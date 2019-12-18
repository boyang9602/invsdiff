package ca.concordia.apr.invsdiff.comparasion;

import java.util.Set;

public class Result {
	private Set<String> commonInvs;
	private Set<String>[] distInvs;
	@SuppressWarnings("unchecked")
	public Result(int length) {
		distInvs = (Set<String>[])new Set[length];
	}
	public void setNull(int index) {
		distInvs[index] = null;
	}
	public void set(int index, Set<String> invs) {
		distInvs[index] = invs;
	}
	public void setCommonInvs(Set<String> commonInvs) {
		this.commonInvs = commonInvs;
	}
}
