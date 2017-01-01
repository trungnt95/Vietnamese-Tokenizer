/**
 * 
 */
package tok;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import model.Pair;
import ultis.Ultilities;

/**
 * @author Nguyen
 *
 */
public class Tokenizer {
	// private static String regexFilePath = "resources\\regexp.txt";
	private Map<String, Pattern> patternList;
	private Set<String> vocabulary;
	private List<String> sentences;
	private List<String> tokSentences;

	public Tokenizer() {
		sentences = new ArrayList<String>();
		vocabulary = new HashSet<String>();
		patternList = new HashMap<String, Pattern>();
		tokSentences = new ArrayList<String>();
	}

	private void tokenize(String sentence) {
		if (sentence.length() == 0) {
			System.out.println(sentence);
		} else {
			StringBuilder result = new StringBuilder();
			while (true) {
				int maxLen = 0;
				String nextToken = "";
				String type = "";

				// Longest pattern matching
				for (String patternName : patternList.keySet()) {
					Matcher matcher = patternList.get(patternName).matcher(sentence);
					if (matcher.lookingAt()) {
						int len = matcher.end() - matcher.start();
						if (maxLen < len) {
							maxLen = len;
							nextToken = matcher.group();
							type = patternName;
						}
					}
				}
				// System.out.println(type + "\t" + nextToken);
				// Xét đến các token vừa được so khớp bằng pattern phù hợp nhất
				// (dài nhất)
				nextToken = nextToken.trim();
				// System.out.println("next token:"+nextToken.length());
				if (nextToken.length() > 0) {
					sentence = sentence.substring(maxLen).trim();
					if (type.contains("name") && sentence.length() > 0) {
						Pair<String, String> pair = reconstructName(nextToken, sentence);
						if (pair.getFirst().length() != nextToken.length()) {
							nextToken = pair.getFirst();
							sentence = pair.getSecond();
							type = "word";
						}
					} else if (type.contains("unit") && sentence.length() > 0) {
						Pair<String, String> pair = reconstructUnit(nextToken, sentence);
						if (pair.getFirst().length() != nextToken.length()) {
							nextToken = pair.getFirst();
							sentence = pair.getSecond();
							type = "unit";
						}
					} else if (type.contains("phrase")) {
						// multi-syllabic phrase
						// System.out.println("phrase " + sentence);
						if (nextToken.indexOf(' ') > 0) {
							List<String> tokens = segmentPhrase(nextToken);
							if (tokens != null) {
								for (String word : tokens) {
									// System.out.println("Type: word\tContent:
									// " + word);
									result.append(word.replaceAll(" ", "_") + " ");

								}
							} else {
								// System.out.println("Type: error\tContent: " +
								// nextToken);
								JOptionPane.showMessageDialog(null, "Error when tokenizing phrase: " + nextToken);
							}
						} else {
							// mono-syllabic phrase
							// System.out.println("Type: word\tContent: " +
							// nextToken);
							result.append(nextToken.replaceAll(" ", "_") + " ");
						}
					}
					if (!type.contains("phrase")) {
						// System.out.println("Type: " + type + "\t" + "Content:
						// " + nextToken);
						result.append(nextToken.replaceAll(" ", "_") + " ");
					}

				} else {
					if (sentence.trim().length() > 0) {
						// System.out.println("Unprocessed substring: " +
						// sentence);
						JOptionPane.showMessageDialog(null, "Unprocessed substring: " + sentence);
					}
					break;
				}
				if (sentence.length() == 0) {
					break;
				}
			}

			// System.out.println(result.toString().trim());
			tokSentences.add(result.toString().trim());
		}
	}

	private Pair<String, String> reconstructName(String currentToken, String sentence) {
		// Name Pattern chỉ nhận diện được "Thủ" hoặc "Bộ Giáo" trong
		// "Thủ tướng" hoặc "Bộ Giáo dục". Vì vậy phân tách ra để kết hợp
		// âm tiết cuối cùng của currentToken với âm tiết đầu của sentence
		int i = sentence.indexOf(' ');
		String nextSyllable = (i > 0) ? sentence.substring(0, i) : sentence;

		int n = nextSyllable.length();
		while (n > 0 && !Character.isAlphabetic(nextSyllable.charAt(--n)))
			;
		nextSyllable = nextSyllable.substring(0, n + 1);

		int k = currentToken.lastIndexOf(' ');
		String lastSyllable = (k > 0) ? currentToken.substring(k + 1) : currentToken;
		String nextTokenPrefix = (k > 0) ? currentToken.substring(0, k + 1) : "";
		String word = lastSyllable.toLowerCase() + ' ' + nextSyllable;
		if (vocabulary.contains(word)) {
			currentToken = nextTokenPrefix + lastSyllable + ' ' + nextSyllable;
			sentence = sentence.substring(nextSyllable.length()).trim();
		}

		// Tách prefix trong tên, ví dụ như Ông, Bà, Anh, Chị, Em,...
		i = currentToken.indexOf(' ');
		if (i > 0) {
			String firstSyllable = currentToken.substring(0, i);
			Matcher matcher = patternList.get("prefix").matcher(firstSyllable);
			if (matcher.matches()) {
				StringBuilder sb = new StringBuilder(currentToken.substring(i + 1));
				sb.append(' ');
				sb.append(sentence);
				sentence = sb.toString();
				currentToken = firstSyllable;
			}
		}
		return new Pair<String, String>(currentToken, sentence);
	}

	private Pair<String, String> reconstructUnit(String currentToken, String sentence) {
		// tách ra các đơn vị, ví dụ như ở trên, hoặc trong một đoạn văn bản
		// có đoạn ví dụ "tỉ giá tiền: 22000 đồng/đô la Mỹ" thì cần phải tách
		// thành [đồng/đô la] [Mỹ]. Tương tự với các ví dụ khác.
		String lastSyllable = currentToken.substring(currentToken.indexOf('/') + 1);
		int i = sentence.indexOf(' ');
		String nextSyllable = (i > 0) ? sentence.substring(0, i + 1) : sentence;

		int n = nextSyllable.length();
		// loại bỏ trường hợp có các dấu câu ngay phía sau âm tiết thứ 2
		while (n > 0 && !Character.isAlphabetic(nextSyllable.charAt(--n)))
			;
		nextSyllable = nextSyllable.substring(0, n + 1);

		String word = lastSyllable + ' ' + nextSyllable;
		if (vocabulary.contains(word)) {
			currentToken = currentToken + ' ' + nextSyllable;
			sentence = sentence.substring(nextSyllable.length()).trim();
		}
		return new Pair<String, String>(currentToken, sentence);
	}

	// Sử dụng từ điển để so khớp cực đại (longest matching word) trong phrase
	private List<String> segmentPhrase(String phrase) {
		List<String> list = new ArrayList<String>();
		String clonePhrase = phrase;
		while (true) {
			if (vocabulary.contains(clonePhrase) || clonePhrase.indexOf(' ') < 0) {
				list.add(clonePhrase);
				break;
			}
			String token = clonePhrase.substring(0, clonePhrase.lastIndexOf(' ')).trim();
			if (vocabulary.contains(token) || token.indexOf(' ') < 0) {
				list.add(token);
				phrase = phrase.substring(token.length()).trim();
				clonePhrase = phrase;
			} else {
				clonePhrase = clonePhrase.substring(0, clonePhrase.lastIndexOf(' ')).trim();
			}
		}
		return list;
	}

	public void loadVocabulary(String filepath) throws Exception {
		if (vocabulary.isEmpty()) {
			List<String> lines = Ultilities.getContentTextFile(filepath);

			for (String line : lines) {
				vocabulary.add(line);
			}
		}
	}

	public void loadPatternFile(String filepath) throws Exception {
		if (patternList.isEmpty()) {
			List<String> lines = Ultilities.getContentTextFile(filepath);
			for (String line : lines) {
				line = line.trim();
				if (!line.startsWith("#")) {
					String[] s = line.split("\\s+");
					if (s.length == 2) {
						patternList.put(s[0], Pattern.compile(s[1]));
					}
				}
			}
		}
	}

	public void loadInputFile(String filepath) throws Exception {
		sentences.clear();
		sentences = Ultilities.getContentTextFile(filepath);
	}

	public void performsTokenizingEachSentence() throws Exception {
		tokSentences.clear();
		for (String sentence : sentences) {
			tokenize(sentence);
		}
	}

	public void exportResultsToFile(String filepath) throws Exception {
		Ultilities.writeToFile(filepath, tokSentences);
	}

	@Deprecated
	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Tokenizer tokenizer = new Tokenizer();
				List<String> sentences;
				try {
					sentences = Ultilities.getContentTextFile("16424.txt");
					for (String sentence : sentences) {
						tokenizer.tokenize(sentence);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

}
