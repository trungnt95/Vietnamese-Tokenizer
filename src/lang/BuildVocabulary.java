package lang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ultis.IConstants;
import ultis.Ultilities;

/**
 * @author Nguyen
 *
 */
public class BuildVocabulary {
	private Set<String> vVocabulary;

	public BuildVocabulary() {
		vVocabulary = new TreeSet<String>();
		init();
	}

	public void init() {
		getWordsTreeBank();
		getWordsTrainingTexts();
	}
	
	private void getWordsTreeBank() {
		try {
			File fFolder = new File(IConstants.TREEBANK_DIRECTORY);
			File[] fList = fFolder.listFiles();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			for (File fFile : fList) {
				Document doc = dBuilder.parse(fFile);
				doc.getDocumentElement().normalize();
				
				NodeList nList = doc.getElementsByTagName("HeadWord");
				for (int index = 0; index < nList.getLength(); ++index) {
					Node nNode = nList.item(index);
					vVocabulary.add(nNode.getTextContent());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.out.println("Loading treebank done!");
		}
		
	}
	
	private void getWordsTrainingTexts(){
		File fFolder = new File(IConstants.TRAINNING_FOLDER_DIRECTORY);
		File[] fFileList = fFolder.listFiles();

		String regex = "</*\\w+>";
		BufferedReader rReader = null;
		try {
			for (int i = 0; i < fFileList.length; ++i) {
				rReader = new BufferedReader(new FileReader(fFileList[i]));
				String line = "";
				while ((line = rReader.readLine()) != null) {
						String[] sList = line.split("\\s+");
						sList = Ultilities.normalize(sList);
						
						for (int index = 0; index < sList.length; ++index)
							if(!sList[index].matches(regex))
								vVocabulary.add(sList[index].replaceAll("_", " "));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rReader != null) {
				try {
					rReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Loading from training texts done!");
		}
	}

	public Set<String> getvVocabulary() {
		return vVocabulary;
	}

	// Write data to UTF8 files
	public static void writeVocaToUTF8TextFile(String filepath, Set<String> wordList) {
		Writer out = null;
		try {
			File f = new File(filepath);
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
			Iterator<String> iter = wordList.iterator();
			while (iter.hasNext()) {
				out.write(iter.next());
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
				BuildVocabulary instance = new BuildVocabulary();
				BuildVocabulary.writeVocaToUTF8TextFile(IConstants.VOCABULARY_FILEPATH,
						instance.getvVocabulary());
			}
		});
		t.start();
	}
}
