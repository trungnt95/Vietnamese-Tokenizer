/**
 * 
 */
package gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Nguyen
 *
 */
public class SubFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea log;
	
	public SubFrame() {
		setTitle("Log");
		setSize(400, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(getParent());

		log = new JTextArea(20,30);
		log.setEditable(false);
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(log);
		getContentPane().add(scrollPane);
		setResizable(false);
		pack();
	}
	
	public void updateLog(String content) {
		log.append(content);
		log.append("\n");
		log.revalidate();
	}
}
