/**
 * 
 */
package model;

/**
 * @author Nguyen
 *
 */
@Deprecated
public class Corpus {
	private String word;
	private int freqencyOfWord;
	private double probabilityLMOfWord;

	public Corpus(int freqencyOfWord, String word, double probabilityOfWord) {
		this.word = word;
		this.freqencyOfWord = freqencyOfWord;
		this.probabilityLMOfWord = probabilityOfWord;
	}

	public Corpus(String word) {
		this.word = word;
		this.freqencyOfWord = 0;
		this.probabilityLMOfWord = 0d;
	}

	public String getWord() {
		return word;
	}

	public int getFreqencyOfWord() {
		return freqencyOfWord;
	}

	public double getProbabilityLMOfWord() {
		return probabilityLMOfWord;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void setFreqencyOfWord(int freqencyOfWord) {
		this.freqencyOfWord = freqencyOfWord;
	}

	public void setProbabilityLMOfWord(double probabilityOfWord) {
		this.probabilityLMOfWord = probabilityOfWord;
	}

}
