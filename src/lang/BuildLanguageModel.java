/**
 * 	Build Ngram model languages (Unigram and Bigram)
 * 	Firstly, we count word (unigram) and word phrase (bigram).
 * 	Then calculating probability of each model.
 * 	Finally, write text file which haves following format:
 * 	
 * 	(Count of word)		(Word/Word Phrase)		(Probability Maximum Likelihood Estimation)
 * 		2					Prague				0,0000013388 
 * 		129					Đà Lạt				0,0000863549
 */
package lang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ultis.IConstants;
import ultis.Ultilities;

/**
 * @author Nguyen
 */
@Deprecated
public class BuildLanguageModel {
	private Set<String> uUnigram;
	private Set<String> bBigram;
	private Map<String, Integer> cContextCount;
	private Map<String, Double> pProbability;
	private int N = 0;
	private BufferedReader rReader = null;

	public BuildLanguageModel() {
		uUnigram = new TreeSet<>();
		bBigram = new TreeSet<>();
		cContextCount = new HashMap<>();
		pProbability = new HashMap<>();
		init();
	}

	public void init() {
		File fFolder = new File(IConstants.TRAINNING_FOLDER_DIRECTORY);
		File[] fFileList = fFolder.listFiles();

		StringBuffer sb = new StringBuffer();
		String regex = "</*\\w+>";
		try {
			for (int i = 0; i < fFileList.length; ++i) {
				rReader = new BufferedReader(new FileReader(fFileList[i]));
				String line = "";
				while ((line = rReader.readLine()) != null) {

					if (!line.matches(regex)) {
						line = Ultilities.concat(sb, "<s> ", line, " </s>");
						String[] sList = line.split("\\s+");
						sList = Ultilities.normalize(sList);
						for (int pos = 1; pos < sList.length; ++pos) {
							// count frequency of each token (unigram and
							// bigram)
							if (!cContextCount.containsKey(sList[pos]))
								cContextCount.put(sList[pos], new Integer(1));
							else
								cContextCount.put(sList[pos],
										new Integer(cContextCount.get(sList[pos]).intValue() + 1));

							// add to unigram
							uUnigram.add(sList[pos]);

							// add to bigram
							line = Ultilities.concat(sb, sList[pos - 1], "|", sList[pos]);
							bBigram.add(line);
							if (!cContextCount.containsKey(line))
								cContextCount.put(line, new Integer(1));
							else
								cContextCount.put(line, new Integer(cContextCount.get(line).intValue() + 1));
							// count number of word
							++N;
						}
					}
				}
			}
			uUnigram.add("<s>");
			uUnigram.remove("</s>");
			cContextCount.put("<s>", cContextCount.remove("</s>"));
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (rReader != null) {
				try {
					rReader.close();
				} catch (IOException e) {
					// System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void calcProbability() {
		Iterator<String> iter = uUnigram.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			pProbability.put(key, new Double((double) cContextCount.get(key) / N));
		}
		iter = bBigram.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String[] words = key.split("\\|");
			pProbability.put(key, new Double((double) cContextCount.get(key) / cContextCount.get(words[0])));
		}
	}

	public void writeLMFile(int ngram) {
		Writer out = null;
		File f = null;
		Set<String> list = null;

		DecimalFormat df = new DecimalFormat("0.0000000000");
		if (ngram == 1) {
			list = uUnigram;
			f = new File(IConstants.UNIGRAM_FILEPATH);
		} else if (ngram == 2) {
			list = bBigram;
			f = new File(IConstants.BIGRAM_FILEPATH);
		}
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				out.write(cContextCount.get(key).toString());
				out.write("\t");
				out.write(key);
				out.write("\t");
				out.write(df.format(pProbability.get(key)));
				out.write("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				BuildLanguageModel instance = new BuildLanguageModel();
				instance.calcProbability();

				// write unigram file
				instance.writeLMFile(1);
				// write bigram file
				instance.writeLMFile(2);
			}
		});
		t.start();
	}
}
