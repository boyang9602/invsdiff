package ca.concordia.apr.invsdiff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InvsFile {
	private String filename;

	private Map<String, Ppt> ppts = new HashMap<String, Ppt>();

	public final Map<String, Ppt> getPpts() {
		return ppts;
	}

	public final String getFilename() {
		return filename;
	}

	public InvsFile(String filename) throws FileNotFoundException, IOException {
		this.filename = filename;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		
		Ppt currPpt = new Ppt();
		if (line.matches("=*")) {
			line = br.readLine();
			currPpt.setName(line);
			line = br.readLine();
		} else {
			br.close();
			throw new RuntimeException("invalid invs file");
		}
		while (line != null) {
			if (line.matches("=*")) {
				this.ppts.put(currPpt.getName(), currPpt);
				currPpt = new Ppt();
				line = br.readLine();
				currPpt.setName(line);
			} else {
				currPpt.addInv(line);
			}
			line = br.readLine();
		}
		this.ppts.put(currPpt.getName(), currPpt);
		br.close();
	}
}
