package ca.concordia.apr.invsdiff.comparasion;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class Result {
	public static Pattern namePattern = Pattern.compile("^(([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*)[^\\w](.+\\(.*\\)){0,1}:+([A-Z]+)(\\d+){0,1};{0,1}(condition.*){0,1}$");
	private Matcher m;
	private String pptName;
	private Set<String> common;
	private Map<String, Set<String>> distinctMap = new HashMap<String, Set<String>>();
	
	public Result(String pptName) {
		this.pptName = pptName;
		m = namePattern.matcher(this.pptName);
		if (!m.matches()) {
			throw new RuntimeException("unexpected name: " + this.pptName);
		}
	}
	public void setCommon(Set<String> common) {
		this.common = common;
	}
	public void set(String key, Set<String> invs) {
		this.distinctMap.put(key, invs);
	}
	public String getClassName() {
		return m.group(1);
	}
	public JSONObject toJSON() {
//		JSONObject root = new JSONObject();
//		JSONObject currNode = root;
//		for (int i = 1; i <= 5; i++) {
//			if (i == 2) {
//				continue;
//			}
//			String name = m.group(i);
//			if (name == null) {
//				continue;
//			}
//			if (i == 3 && name.equals("EXIT") && m.group(4) != null) {
//				name = "EXITNN";
//			}
//			
//			JSONObject tmp = new JSONObject();
//			currNode.append(name, tmp);
//			currNode = tmp;
//		}
//		currNode.append("common", this.common);
//		for (String key : distinctMap.keySet()) {
//			currNode.append(key, distinctMap.get(key));
//		}
//		return root;
		return appendToJSON(new JSONObject());
	}

	public JSONObject appendToJSON(JSONObject anotherJSON) {
		JSONObject currNode = anotherJSON;
		for (int i = 1; i <= 5; i++) {
			if (i == 2) {
				continue;
			}
			String name = m.group(i);
			if (name == null) {
				continue;
			}
			if (i == 3 && name.equals("EXIT") && m.group(4) != null) {
				name = "EXITNN";
			}

			JSONObject tmp;
			if (currNode.has(name)) {
				tmp = currNode.getJSONObject(name);
			} else {
				tmp = new JSONObject();
				currNode.put(name, tmp);
			}
			currNode = tmp;
		}
		currNode.put("common", this.common);
		for (String key : distinctMap.keySet()) {
			currNode.put(key, distinctMap.get(key));
		}
		return anotherJSON;
	}
}
