package ca.concordia.apr.invsdiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Diff {
	String leftName;
	String rightName;
	List<Ppt> onlyLeftPpts = new LinkedList<Ppt>();
	List<Ppt> onlyRightPpts = new LinkedList<Ppt>();

	List<Ppt> onlyLeftInvs = new LinkedList<Ppt>();
	List<Ppt> onlyRightInvs = new LinkedList<Ppt>();
	
	public static Diff compare(InvsFile if1, InvsFile if2) {
		Diff diff = new Diff();
		diff.leftName = if1.getFilename();
		diff.rightName = if2.getFilename();

		Set<String> leftKeys = new HashSet<String>(if1.getPpts().keySet());
		Set<String> rightKeys = new HashSet<String>(if2.getPpts().keySet());
		leftKeys.retainAll(rightKeys);
		Set<String> commonKeys = new HashSet<String>(leftKeys);
		leftKeys = new HashSet<String>(if1.getPpts().keySet());
		leftKeys.removeAll(rightKeys);
		for(String k : leftKeys) {
			diff.onlyLeftPpts.add(if1.getPpts().get(k));
		}
		rightKeys.removeAll(commonKeys);
		for(String k : rightKeys) {
			diff.onlyRightPpts.add(if2.getPpts().get(k));
		}
		for(String k : commonKeys) {
			Ppt tLeft = if1.getPpts().get(k).diff(if2.getPpts().get(k));
			Ppt tRight = if2.getPpts().get(k).diff(if1.getPpts().get(k));
			if (tLeft.isEmpty() && tRight.isEmpty()) continue;
			diff.onlyLeftInvs.add(tLeft);
			diff.onlyRightInvs.add(tRight);
		}
		return diff;
	}
	
	public void writeTo(String filename, String content) throws IOException {
		PrintWriter writer = new PrintWriter(filename);
		writer.write(content);
		writer.close();
	}
	
	public void writeTxtTo(String filename) throws IOException {
		StringBuffer sb = new StringBuffer();
		Iterator<Ppt> itLeft = this.onlyLeftInvs.iterator();
		Iterator<Ppt> itRight = this.onlyRightInvs.iterator();
		sb.append("Following are different invs in common Ppt:\n");
		while(itLeft.hasNext()) {
			sb.append("===========================================================================\n");
			Ppt lPpt = itLeft.next();
			Ppt rPpt = itRight.next();
			sb.append(lPpt.getName() + "\n");
			sb.append("extra invs at left:\n");
			sb.append(lPpt.toString(false));
			sb.append("extra invs at right:\n");
			sb.append(rPpt.toString(false));
		}
		sb.append("\n\n\nFollowing Ppts only exist in " + this.leftName + ":\n");
		for (Ppt ppt : this.onlyLeftPpts) {
			sb.append("===========================================================================\n");
			sb.append(ppt.toString());
		}
		sb.append("\n\n\nFollowing Ppts only exist in " + this.rightName + ":\n");
		for (Ppt ppt : this.onlyRightPpts) {
			sb.append("===========================================================================\n");
			sb.append(ppt.toString());
		}
		writeTo(filename, sb.toString());
	}
	
	public void writeJSONTo(String filename) throws IOException {
		JSONObject outputJson = new JSONObject();
		JSONArray commonPptsArray = new JSONArray();
		Iterator<Ppt> itLeft = this.onlyLeftInvs.iterator();
		Iterator<Ppt> itRight = this.onlyRightInvs.iterator();
		while(itLeft.hasNext()) {
			Ppt lPpt = itLeft.next();
			Ppt rPpt = itRight.next();
			JSONObject commonPpt = new JSONObject();
			commonPpt.put("left", lPpt.toJSON());
			commonPpt.put("right", rPpt.toJSON());
			commonPptsArray.put(commonPpt);
		}
		outputJson.put("commonPpts", commonPptsArray);
		JSONArray leftPptsArray = new JSONArray();
		for (Ppt ppt : this.onlyLeftPpts) {
			leftPptsArray.put(ppt.toJSON());
		}
		outputJson.put("leftPpts", leftPptsArray);
		JSONArray rightPptsArray = new JSONArray();
		for (Ppt ppt : this.onlyLeftPpts) {
			rightPptsArray.put(ppt.toJSON());
		}
		outputJson.put("rightPpts", rightPptsArray);
		writeTo(filename, outputJson.toString());
	}
}
