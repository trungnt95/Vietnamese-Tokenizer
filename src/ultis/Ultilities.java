/**
 * 
 */
package ultis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nguyen
 *
 */
public class Ultilities {
	// normalize a list of string by removing space at the beginning and the end
	// of each string
	public static String[] normalize(String[] sList) {
		for (int i = 1; i < sList.length - 1; ++i)
			sList[i] = sList[i].trim().toLowerCase();
		return sList;
	}

	@Deprecated
	// Concat two strings
	public static String concat(StringBuffer sb, String first, String second, String third) {
		sb.setLength(0);
		sb.append(first);
		sb.append(second);
		sb.append(third);
		return sb.toString();
	}

	public static List<String> getContentTextFile(String filepath) throws Exception {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filepath)), "UTF8"));
		String line = "";
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		if (br != null) {
			br.close();
		}
		return lines;
	}

	public static void writeToFile(String filepath, List<String> data) throws Exception {
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(new File(filepath)), "UTF8"));
		for (String sentence : data) {
			bw.write(sentence);
			bw.write("\r\n");
		}
		if (bw != null) {
			bw.close();
		}
	}
}
