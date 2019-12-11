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
	public Ppt(String rawName, Set<String> invs) {
		this.rawName = rawName;
		this.invs = invs;
		this.parseRawName();
	}
	private void parseRawName() {
		Matcher m = namePattern.matcher(rawName);
		if (m.matches()) {
			this.name = m.group(1);
			this.type = Enum.valueOf(PPT_TYPE.class, m.group(2));
			if (this.type == PPT_TYPE.EXIT) {
				if (!m.group(3).equals("")) {
					this.type = PPT_TYPE.EXITNN;
					this.exitPoint = Integer.parseInt(m.group(3));
				}
			} else {
				if (!m.group(3).equals("")) {
					throw new RuntimeException("unexpected ppt: " + rawName);
				}
			}
		}
	}
	public final String getRawName() {
		return rawName;
	}
	public void setRawName(String rawName) {
		this.rawName = rawName;
		this.parseRawName();
	}
	public String getName() {
		return name;
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
		return new Ppt(this.rawName, copy);
	}
	public JSONObject toJSON() {
		return this.toJSON(true);
	}
	public JSONObject toJSON(boolean withName) {
		JSONObject pptJson = new JSONObject();
		if (withName) {
			pptJson.put("name", this.rawName);
		}
		pptJson.put("invs", this.invs);
		
		return pptJson;
	}
	public boolean isEmpty() {
		return this.invs.isEmpty();
	}
}
