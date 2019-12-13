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
	private Map<String, Ppt> classPpts = new HashMap<String, Ppt>();
	private Map<String, Ppt> objectPpts = new HashMap<String, Ppt>();
	private Map<String, Map<String, Ppt>> enterPpts = new HashMap<String, Map<String, Ppt>>();
	private Map<String, Map<String, List<Ppt>>> exitPpts = new HashMap<String, Map<String, List<Ppt>>>();
	private Map<String, Map<String, List<Ppt>>> exitnnPpts = new HashMap<String, Map<String, List<Ppt>>>();

	public final Map<String, Ppt> getClassPpts() {
		return classPpts;
	}

	public final Map<String, Ppt> getObjectPpts() {
		return objectPpts;
	}

	public final Map<String, Map<String, Ppt>> getEnterPpts() {
		return enterPpts;
	}

	public final Map<String, Map<String, List<Ppt>>> getExitPpts() {
		return exitPpts;
	}

	public final Map<String, Map<String, List<Ppt>>> getExitnnPpts() {
		return exitnnPpts;
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
					addCurrPpt(currPpt);
				}
				currPpt = new Ppt();
				line = br.readLine();
				currPpt.setRawName(line);
			} else {
				currPpt.addInv(line);
			}
			line = br.readLine();
		}
		addCurrPpt(currPpt);
		br.close();
		if (!exitPpts.keySet().containsAll(exitnnPpts.keySet())) {
			throw new RuntimeException("unmatched exit and exitnn");
		}
	}

	private void addCurrPpt(Ppt currPpt) {
		switch (currPpt.getType()) {
		case CLASS:
			this.classPpts.put(currPpt.getName(), currPpt);
			break;
		case OBJECT:
			this.objectPpts.put(currPpt.getName(), currPpt);
			break;
		case ENTER:
			Map<String, Ppt> methodEnterPptMap = this.enterPpts.get(currPpt.getClassName());
			if (methodEnterPptMap == null) {
				methodEnterPptMap = new HashMap<String, Ppt>();
				this.enterPpts.put(currPpt.getClassName(), methodEnterPptMap);
			}
			methodEnterPptMap.put(currPpt.getMethodName(), currPpt);
			break;
		case EXIT:
			Map<String, List<Ppt>> methodExitPptMap = this.exitPpts.get(currPpt.getClassName());
			if (methodExitPptMap == null) {
				methodExitPptMap = new HashMap<String, List<Ppt>>();
				this.exitPpts.put(currPpt.getClassName(), methodExitPptMap);
			}
			String methodName = currPpt.getMethodName();
			List<Ppt> exitPptsList = methodExitPptMap.get(methodName);
			if (exitPptsList == null) {
				exitPptsList = new LinkedList<Ppt>();
				methodExitPptMap.put(methodName, exitPptsList);
			}
			exitPptsList.add(currPpt);
			break;
		case EXITNN:						
			Map<String, List<Ppt>> ennPptsMap = this.exitnnPpts.get(currPpt.getClassName());
			if (ennPptsMap == null) {
				ennPptsMap = new HashMap<String, List<Ppt>>();
				this.exitnnPpts.put(currPpt.getClassName(), ennPptsMap);
			}
			methodName = currPpt.getMethodName();
			List<Ppt> ennPptsList = ennPptsMap.get(methodName);
			if (ennPptsList == null) {
				ennPptsList = new LinkedList<Ppt>();
				ennPptsMap.put(methodName, ennPptsList);
			}
			ennPptsList.add(currPpt);
			break;
		}
	}
}
