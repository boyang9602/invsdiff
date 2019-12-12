package ca.concordia.apr.invsdiff.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
	private static final Pattern paramPattern = Pattern.compile("^(.*)\\((.+)\\)$");
	public static void writeTo(String filename, String content) throws IOException {
		Matcher m = paramPattern.matcher(filename);
		if (m.matches()) {
			StringBuffer sb = new StringBuffer();
			sb.append(m.group(1));
			for (String param : m.group(2).replace(" ", "").split(",")) {
				sb.append(param.hashCode()).append(',');
			}
			filename = sb.toString();
		}
		File file = new File(filename);
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		writer.write(content);
		writer.close();
	}
}
