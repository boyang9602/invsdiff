package ca.concordia.apr.invsdiff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InvsFile {
	private String filename;
	private Map<String, Ppt> pptMap = new HashMap<String, Ppt>();

	public final String getFilename() {
		return filename.substring(filename.lastIndexOf('/') + 1);
	}
	
	public final Set<String> getPptNamesSet() {
		return pptMap.keySet();
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
				currPpt = new Ppt(br.readLine());
				pptMap.put(currPpt.getRawName(), currPpt);
			} else {
				currPpt.addInv(line);
			}
			line = br.readLine();
		}
		br.close();
	}

	public Ppt getPptByName(String pptName) {
		return this.pptMap.get(pptName);
	}
}
