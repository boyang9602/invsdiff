package ca.concordia.apr.invsdiff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InvsFile {
	private String filename;
	private Map<String, Ppt> ppts = new HashMap<String, Ppt>();
	private Map<String, Ppt> classPpts = new HashMap<String, Ppt>();
	private Map<String, Ppt> objectPpts = new HashMap<String, Ppt>();
	private Map<String, Ppt> enterPpts = new HashMap<String, Ppt>();
	private Map<String, Ppt> exitPpts = new HashMap<String, Ppt>();
	private Map<String, List<Ppt>> exitnnPpts = new HashMap<String, List<Ppt>>();
	private Map<String, List<Ppt>> condExitPpts = new HashMap<String, List<Ppt>>();

	public final Map<String, List<Ppt>> getCondExitPpts() {
		return condExitPpts;
	}

	public final Map<String, Ppt> getClassPpts() {
		return classPpts;
	}

	public final Map<String, Ppt> getObjectPpts() {
		return objectPpts;
	}

	public final Map<String, Ppt> getEnterPpts() {
		return enterPpts;
	}

	public final Map<String, Ppt> getExitPpts() {
		return exitPpts;
	}

	public final Map<String, List<Ppt>> getExitnnPpts() {
		return exitnnPpts;
	}

	public final Map<String, Ppt> getPpts() {
		return ppts;
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
				if (currPpt != null) {
					this.ppts.put(currPpt.getRawName(), currPpt);
					switch (currPpt.getType()) {
					case CLASS:
						this.classPpts.put(currPpt.getName(), currPpt);
						break;
					case OBJECT:
						this.objectPpts.put(currPpt.getName(), currPpt);
						break;
					case ENTER:
						this.enterPpts.put(currPpt.getName(), currPpt);
						break;
					case EXIT:
						if (currPpt.getCondition() != null) {
							List<Ppt> list = condExitPpts.get(currPpt.getName());
							if (list == null) {
								list = new LinkedList<Ppt>();
								condExitPpts.put(currPpt.getName(), list);
							}
							list.add(currPpt);
						} else {
							this.exitPpts.put(currPpt.getName(), currPpt);
						}
						break;
					case EXITNN:						
						List<Ppt> ennPpts = this.exitnnPpts.get(currPpt.getName());
						if (ennPpts == null) {
							ennPpts = new LinkedList<Ppt>();
							this.exitnnPpts.put(currPpt.getName(), ennPpts);
						}
						ennPpts.add(currPpt);
						break;
					}
				}
				currPpt = new Ppt();
				line = br.readLine();
				currPpt.setRawName(line);
			} else {
				currPpt.addInv(line);
			}
			line = br.readLine();
		}
		this.ppts.put(currPpt.getRawName(), currPpt);
		br.close();

		if (!exitPpts.keySet().containsAll(exitnnPpts.keySet())) {
			throw new RuntimeException("unmatched exit and exitnn");
		}
	}
}
