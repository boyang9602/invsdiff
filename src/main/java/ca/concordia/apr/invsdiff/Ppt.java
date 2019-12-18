package ca.concordia.apr.invsdiff;

import java.util.HashSet;
import java.util.Set;

public class Ppt {
	private Set<String> invs = new HashSet<String>();
	private String rawName;
	public Ppt(String rawName) {
		this.rawName = rawName;
	}
	public void addInv(String inv) {
		this.invs.add(inv);
	}
	public final Set<String> getInvs() {
		return this.invs;
	}
	public String getRawName() {
		return this.rawName;
	}
}
