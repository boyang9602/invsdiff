package ca.concordia.apr.invsdiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	
	public void writeTo(String filename) throws IOException {
		PrintWriter writer = new PrintWriter(filename);
		Iterator<Ppt> itLeft = this.onlyLeftInvs.iterator();
		Iterator<Ppt> itRight = this.onlyRightInvs.iterator();
		writer.write("Following are different invs in common Ppt:\n");
		while(itLeft.hasNext()) {
			writer.write("===========================================================================\n");
			Ppt lPpt = itLeft.next();
			Ppt rPpt = itRight.next();
			writer.write(lPpt.getName() + "\n");
			writer.write("extra invs at left:\n");
			writer.write(lPpt.toString(false));
			writer.write("extra invs at right:\n");
			writer.write(rPpt.toString(false));
		}
		writer.write("\n\n\nFollowing Ppts only exist in " + this.leftName + ":\n");
		for (Ppt ppt : this.onlyLeftPpts) {
			writer.write("===========================================================================\n");
			writer.write(ppt.toString());
		}
		writer.write("\n\n\nFollowing Ppts only exist in " + this.rightName + ":\n");
		for (Ppt ppt : this.onlyRightPpts) {
			writer.write("===========================================================================\n");
			writer.write(ppt.toString());
		}
		writer.close();
	}
}
