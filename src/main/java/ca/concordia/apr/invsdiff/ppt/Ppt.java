package ca.concordia.apr.invsdiff.ppt;

import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;

public abstract class Ppt {
	private Set<String> invs = new HashSet<String>();
	protected String condition = null;
	
	public Ppt() {}

	public abstract String getRawName();

	public abstract String getName();
	public void addInv(String inv) {
		this.invs.add(inv);
	}
	public final Set<String> getInvs() {
		return this.invs;
	}
	public final String getCondition() {
		return condition;
	}

	public String toString() {
		return this.toJSON().toString();
	}
	public JSONObject toJSON() {
		return this.toJSON(true);
	}
	public JSONObject toJSON(boolean withName) {
		JSONObject pptJson = new JSONObject();
		if (withName) {
			pptJson.put("name", this.getRawName());
		}
		pptJson.put("invs", this.invs);
		
		return pptJson;
	}
	public boolean isEmpty() {
		return this.invs.isEmpty();
	}
}
