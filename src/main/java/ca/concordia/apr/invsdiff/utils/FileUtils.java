package ca.concordia.apr.invsdiff.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
	private static final Pattern paramPattern = Pattern.compile("^(.*)\\((.+)\\)$");
	public static void writeTo(String filename, String content) throws IOException, NoSuchAlgorithmException {
		Matcher m = paramPattern.matcher(filename);
		if (m.matches()) {
			StringBuffer sb = new StringBuffer();
			sb.append(m.group(1));
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] digest = md.digest(m.group(2).getBytes());
			sb.append(new BigInteger(1, digest).toString(16));
			filename = sb.toString();
		}
		File file = new File(filename);
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file);
		writer.write(content);
		writer.close();
	}
}
