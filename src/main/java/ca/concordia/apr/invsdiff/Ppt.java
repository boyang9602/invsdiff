package ca.concordia.apr.invsdiff;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class Ppt {
	public static Pattern namePattern = Pattern.compile("^(.*):::([A-Z]+)(\\d*)$");
	public enum PPT_TYPE {
		CLASS,
		OBJECT,
		ENTER,
		EXIT,
		EXITNN
	};
	private Set<String> invs = new HashSet<String>();
	private String rawName;
	private String name;
	private PPT_TYPE type;
	private int exitPoint = -1;
	public Ppt() {
	}
	public Ppt(String name, Set<String> invs) {
		this.rawName = name;
		Matcher m = namePattern.matcher(name);
		this.name = m.group(1);
		this.invs = invs;
		if (m.matches()) {
			this.type = Enum.valueOf(PPT_TYPE.class, m.group(2));
			if (this.type == PPT_TYPE.EXIT) {
				if (!m.group(3).equals("")) {
					this.type = PPT_TYPE.EXITNN;
					this.exitPoint = Integer.parseInt(m.group(3));
				}
			} else {
				if (!m.group(3).equals("")) {
					throw new RuntimeException("unexpected ppt: " + name);
				}
			}
		}
	}
	public final String getRawName() {
		return rawName;
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
	public final PPT_TYPE getType() {
		return type;
	}
	public final int getExitPoint() {
		return exitPoint;
	}
	public Ppt diff(Ppt ppt) {
		Set<String> copy = new HashSet<String>(this.invs);
		copy.removeAll(ppt.getInvs());
		return new Ppt(this.name, copy);
	}
	public JSONObject toJSON() {
		return this.toJSON(true);
	}
	public JSONObject toJSON(boolean withName) {
		JSONObject pptJson = new JSONObject();
		if (withName) {
			pptJson.put("name", this.name);
		}
		pptJson.put("invs", this.invs);
		
		return pptJson;
	}
	public boolean isEmpty() {
		return this.invs.isEmpty();
	}
}
