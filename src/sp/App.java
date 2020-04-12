package sp;
import java.awt.event.*;
import javax.swing.*;

/** Initial menu
 * contains main method
**/

class App extends JFrame{

	private static final long serialVersionUID = 1L;
	private final JSpinner spinner1, spinner2, spinner3;				// spinners to choose game parameters
	private final JButton button;										// confirm button
	private final JLabel label1, label2, label3;						// labels for spinners
	private final SpinnerNumberModel value1a, value1b, value2;			// number models for spinners

	App(){
		super("Juhan ristinolla");										// header
		
		// create labels
		label1 = new JLabel("<html>Kentän leveys (3-6)<html>", SwingConstants.CENTER);          
		label1.setBounds(0,10,330,0);    
		label1.setSize(330,100);

		label2 = new JLabel("<html>Kentän korkeus (3-6)<html>", SwingConstants.CENTER);            
		label2.setBounds(0,110,330,0);      
		label2.setSize(330,100);

		label3 = new JLabel("<html>Voittoon tarvittava määrä merkkejä (3-6)<html>", SwingConstants.CENTER);
		label3.setBounds(0,210,330,0);      
		label3.setSize(330,100);

		//create spinners
		value1a =  new SpinnerNumberModel(3,3,6,1);  
		value1b =  new SpinnerNumberModel(3,3,6,1); 
		value2 =  new SpinnerNumberModel(3,3,6,1); 

		spinner1 = new JSpinner(value1a);   
		spinner1.setBounds(130,70,50,30);
		spinner2 = new JSpinner(value1b);   
		spinner2.setBounds(130,170,50,30);  
		spinner3 = new JSpinner(value2);   
		spinner3.setBounds(130,270,50,30);  
		
		//create confirm button
		button = new JButton("Vahvista");
		button.setBounds(80,370,150,30);
		
		// add all elements
		add(spinner1);  
		add(spinner2);   
		add(spinner3);      
		add(label1);
		add(label2);
		add(label3);
		add(button);

		// setup window
		setLayout(null);
		setSize(330,450);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/** Action listener for button
		 * Saves values given to spinners. Starts new game. Disposes of this window.
		 * 
		 * @.pre	true
		 * @.post 	performs call Game(spinner1.getValue, spinner2.getValue, spinner3.getValue)
		 * 			disposes of this window	
		**/
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				int leveys = (int) spinner1.getValue();
				int korkeus = (int) spinner2.getValue();
				int merkkeja = (int) spinner3.getValue();
				new Game(leveys,korkeus, merkkeja);
				dispose();
			}});
	}


	public static void main(String []args){
		new App();
	}
}