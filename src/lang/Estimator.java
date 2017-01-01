/**
 * 
 */
package lang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import model.Corpus;
import ultis.IConstants;

/**
 * @author Nguyen
 *
 */
@Deprecated
public class Estimator {
	private Vector<Corpus> uUnigram;
	private Vector<Corpus> bBigram;

	boolean flag = true;
	private final static double EPSILON = 0.01;
	private double lambda1;
	private double lambda2;

	public Estimator() {
		init();
		loadModel(IConstants.UNIGRAM_FILEPATH);
		loadModel(IConstants.BIGRAM_FILEPATH);
	}

	private void init() {
		uUnigram = new Vector<>();
		bBigram = new Vector<>();
		lambda1 = 0.5;
		lambda2 = 0.5;
	}

	public void estimateLambda() {
		double hatEpsilon = 0.5;
		double hatLambda1 = 0;
		double hatLambda2 = 0;
		double c1 = 0;
		double c2 = 0;

		int loop = 0;
		while (hatEpsilon > EPSILON) {
			hatLambda1 = lambda1;
			hatLambda2 = lambda2;

			if (loop > 10)
				break;
			Iterator<Corpus> iter = bBigram.iterator();
			while (iter.hasNext()) {
				Corpus corpus = iter.next();
				// denominator
				double denominator = (lambda1 * corpus.getProbabilityLMOfWord()
						+ lambda2 * getUnigramProbability(corpus.getWord()));

				if (denominator > 0) {
					// calculate c1
					c1 += (corpus.getFreqencyOfWord() * lambda1 * corpus.getProbabilityLMOfWord()) / denominator;
					// calculate c2
					c2 += (corpus.getFreqencyOfWord() * lambda2 * getUnigramProbability(corpus.getWord()));
				}
			}

			// re-estimate lambda1 and lambda2
			lambda1 = c1 / (c1 + c2);
			validateProbabilityValue(lambda1);
			lambda2 = 1 - lambda1;

			// estimate hat-epsilon
			hatEpsilon = Math.sqrt(Math.pow(lambda1 - hatLambda1, 2) + Math.pow(lambda2 - hatLambda2, 2));

			// add 1 to number of loop
			++loop;
		}
	}

	private void validateProbabilityValue(double prob) {
		if (prob < 0 || prob > 1)
			System.err.println("Error! Invalid probability!");
	}

	private double getUnigramProbability(String word) {
		String[] uniTokens = word.split("\\|");

		int index = Collections.binarySearch(uUnigram, new Corpus(uniTokens[0]), new WordComparator());
		if (index >= 0) {
			return uUnigram.get(index).getProbabilityLMOfWord();
		}
		return 0d;
	}

	private void loadModel(String filepath) {
		BufferedReader in = null;
		Vector<Corpus> ngram = null;

		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF8"));
			ngram = (filepath.equals(IConstants.UNIGRAM_FILEPATH)) ? uUnigram : bBigram;
			String line = "";
			while ((line = in.readLine()) != null) {
				String[] list = line.split("\\s+");
				ngram.add(
						new Corpus(Integer.parseInt(list[0]), list[1], Double.parseDouble(list[2].replace(",", "."))));
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
			if (ngram.size() > 0)
				ngram.sort(new WordComparator());
		}

	}

	public double getLambda1() {
		return lambda1;
	}

	public double getLambda2() {
		return lambda2;
	}

	public void writeLambdaFile() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(IConstants.LAMBDA_FILEPATH)));
			out.write("lambda1 = ");
			out.write(new Double(lambda1).toString());
			out.write("\r\n");
			out.write("lambda2 = ");
			out.write(new Double(lambda2).toString());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			if(out!=null){
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	public static void main(String[] args) {
		Estimator estimator = new Estimator();
		estimator.estimateLambda();
		estimator.writeLambdaFile();
		System.out.println("Lambda1 = " + estimator.getLambda1());
		System.out.println("Lambda2 = " + estimator.getLambda2());
	}

}

@SuppressWarnings("deprecation")
class WordComparator implements Comparator<Corpus> {
	@Override
	public int compare(Corpus word1, Corpus word2) {
		return word1.getWord().compareTo(word2.getWord());
	}

}