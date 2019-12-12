package ca.concordia.apr.invsdiff;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class Ppt {
	public static Pattern namePattern = Pattern.compile("^(.*):::([A-Z]+)(\\d+){0,1}(;condition.*){0,1}$");
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
	private String className;
	private String methodName;
	private PPT_TYPE type;
	private int exitPoint = -1;
	private String condition = null;
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
				if (m.group(3) != null) {
					this.type = PPT_TYPE.EXITNN;
					this.exitPoint = Integer.parseInt(m.group(3));
				}
				if (m.group(4) != null) {
					this.condition = m.group(4);
				}
			} else {
				if (m.group(3) != null) {
					throw new RuntimeException("unexpected ppt: " + rawName);
				}
			}
		} else {
			throw new RuntimeException("unexpected ppt: " + rawName);
		}
		if (this.type != PPT_TYPE.CLASS && this.type != PPT_TYPE.OBJECT) {
			int lastDot = name.lastIndexOf('.');
			className = name.substring(0, lastDot);
			methodName = name.substring(lastDot + 1);
		} else {
			className = name;
		}
	}
	public final String getClassName() {
		return className;
	}
	public final String getMethodName() {
		return methodName;
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
	public final String getCondition() {
		return condition;
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
			pptJson.put("name", this.name);
			pptJson.put("type", this.type);
			if (this.type == PPT_TYPE.EXITNN) {
				pptJson.put("exitpoint", this.exitPoint);
			}
			if (this.condition != null) {
				pptJson.put("condition", this.condition.substring(1));
			}
		}
		pptJson.put("invs", this.invs);
		
		return pptJson;
	}
	public boolean isEmpty() {
		return this.invs.isEmpty();
	}
}
