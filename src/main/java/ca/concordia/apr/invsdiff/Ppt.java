package ca.concordia.apr.invsdiff;

import java.util.HashSet;
import java.util.Set;

public class Ppt {
	private Set<String> invs = new HashSet<String>();
	private String name;
	public Ppt() {
	}
	public Ppt(String name, Set<String> invs) {
		this.name = name;
		this.invs = invs;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addInv(String inv) {
		this.invs.add(inv);
	}
	public final Set<String> getInvs() {
		return this.invs;
	}
	public Ppt diff(Ppt ppt) {
		Set<String> copy = new HashSet<String>(this.invs);
		copy.removeAll(ppt.getInvs());
		return new Ppt(this.name, copy);
	}
	public String toString() {
		return toString(true);
	}
	public String toString(boolean withName) {
		StringBuffer sb = new StringBuffer();
		if (withName) sb.append(this.name).append('\n');
		for (String inv : this.invs) {
			sb.append(inv).append('\n');
		}
		return sb.toString();
	}
	public boolean isEmpty() {
		return this.invs.isEmpty();
	}
}
