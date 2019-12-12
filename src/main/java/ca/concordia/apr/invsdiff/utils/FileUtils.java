package ca.concordia.apr.invsdiff.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {
	
	public static void writeTo(String filename, String content) throws IOException {
		File file = new File(filename);
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		writer.write(content);
		writer.close();
	}
}
