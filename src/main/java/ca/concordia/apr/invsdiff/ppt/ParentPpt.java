package ca.concordia.apr.invsdiff.ppt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ParentPpt extends Ppt {
	private Map<String, ChildPpt> children = new HashMap<String, ChildPpt>();
	public void addChild(ChildPpt child) {
		this.children.put(child.getRawName(), child);
	}
	public final ChildPpt getChild(String rawName) {
		return this.children.get(rawName);
	}
	public final Set<String> getChildrenNameSet() {
		return this.children.keySet();
	}
}
