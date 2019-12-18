package ca.concordia.apr.invsdiff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.concordia.apr.invsdiff.ppt.*;

public class InvsFile {
	public static Pattern namePattern = Pattern.compile("^(.*):::([A-Z]+)(\\d+){0,1}(;condition.*){0,1}$");

	private String filename;
	private Map<String, ParentPpt> classPptsMap = new HashMap<String, ParentPpt>();
	private Map<String, ParentPpt> objectPptsMap = new HashMap<String, ParentPpt>();

	public final Set<String> getClassPptKeys() {
		return new HashSet<String>(classPptsMap.keySet());
	}
	public final Set<String> getObjectPptKeys() {
		return new HashSet<String>(objectPptsMap.keySet());
	}
	public final ParentPpt getClassPpt(String name) {
		return this.classPptsMap.get(name);
	}
	public final ParentPpt getObjectPpt(String name) {
		return this.getObjectPpt(name);
	}

	public final String getFilename() {
		return filename.substring(filename.lastIndexOf('/') + 1);
	}

	public InvsFile(String filename) throws FileNotFoundException, IOException {
		this.filename = filename;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		
		if (line == null || !line.matches("^=+$")) {
			br.close();
			throw new RuntimeException("invalid invs file, first line: " + line);
		}
		Ppt currPpt = null;
		while (line != null) {
			if (line.matches("^=+$")) {
				line = br.readLine();
				currPpt = this.createPpt(line);
			} else {
				currPpt.addInv(line);
			}
			line = br.readLine();
		}
		br.close();
	}
	
	private Ppt createPpt(String rawName) {
		Matcher m = namePattern.matcher(rawName);
		Ppt ppt = null;
		if (m.matches()) {
			String name = m.group(1);
			String type = m.group(2);
			String condition = m.group(4);
			if (type.equals("OBJECT")) {
				ppt = new ObjectPpt(name);
				this.objectPptsMap.put(name, (ParentPpt) ppt);
			} else if (type.equals("CLASS")) {
				ppt = new ClassPpt(name);
				this.classPptsMap.put(name, (ParentPpt) ppt);
			} else {
				String[] names = parseName(name);
				ParentPpt parent = this.classPptsMap.get(names[0]);
				if (parent == null) {
					parent = this.objectPptsMap.get(names[0]);
				}
				if (type.equals("EXIT")) {
					if (m.group(3) != null) {
						int exitPoint = Integer.parseInt(m.group(3));
						ppt = new ExitnnPpt(parent, names[1], exitPoint, condition);
					} else {
						ppt = new ExitPpt(parent, names[1], condition);
					}
				} else if (type.equals("ENTER") ) {
					ppt = new EnterPpt(parent, names[1], condition);
				} else {
					throw new RuntimeException("unexpected ppt: " + rawName);
				}
			}
		}
		return ppt;
	}
	
	private String[] parseName(String name) {
		int lastDot = name.substring(0, name.indexOf('(')).lastIndexOf('.');
		return new String[] {name.substring(0, lastDot), name.substring(lastDot + 1)};
	}
}
