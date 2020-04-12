package sp;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * GUI and game logic
**/

public class Game extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	int leveys;										// width of the board
	int korkeus;									// height of the board
	JButton b[];									// array of Jbuttons to represent squares on the board
	JButton reset = new JButton("RESET");			// reset button to go back to menu
	ImageIcon risti =new ImageIcon("risti.jpg");	// image for crosses
	ImageIcon nolla =new ImageIcon("nolla.jpg");	// image for zeros
	ImageIcon risti2;								// scaled image for crosses
	ImageIcon nolla2;								// scaled image for zeros
	ActionListener eka;								// actionlistener for all buttons
	int skaala, y, x, j;							// integers to construct the GUI
	Board peliTila;									// stores the status of the game
	boolean vuoro;									// true == player turn, false == cpu turn
	boolean loppu;									// indicates if game has ended
	
	
	public Game(int leveys, int korkeus, int merkkeja) {
		
		super("Juhan ristinolla");
		this.leveys = leveys;
		this.korkeus = korkeus;
		this.b = new JButton[leveys*korkeus];
		this.vuoro = true;
		this.peliTila = new Board(leveys, korkeus, merkkeja);
		showButtons(leveys, korkeus);
	}
	
	/**
	 * Constructs the GUI
	 * @param leveys	width of the game field
	 * @param korkeus	height of the game field
	 * 
	 * @.pre			true
	 * @.post			GUI constructed
	 */
	
	public void showButtons(int leveys, int korkeus) {
		
		// set aid integers
		skaala = 20/leveys;
		if(korkeus>leveys) {skaala = 20/ korkeus;}
		x=5; 
		y=skaala;
		j=0;
		
		// create window scaled to field size
		setLayout(null);
		setSize(18+leveys*skaala*20,100+korkeus*skaala*20);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create buttons representing game field
		for(int i=0; i<korkeus*leveys; i++, x+=20*skaala, j++) {
			b[i]= new JButton();
			if(j==leveys){
				j=0; y+=20*skaala; x=5;
				}
			b[i].setBounds(x,y,20*skaala,20*skaala);
			add(b[i]);
			b[i].addActionListener(this);
		}
		// scale icons
		risti2 = new ImageIcon(getScaledImage(risti.getImage(),18*skaala,18*skaala));
		nolla2 = new ImageIcon(getScaledImage(nolla.getImage(),18*skaala,18*skaala));
				
		// add reset button
		reset.setBounds((18+leveys*skaala*20)/2-50,korkeus*skaala*20+15,100,50);
		add(reset);
		reset.addActionListener(this);
	}
	
	/**
	 * Action listener including game logic
	 * @param e			ActionEvent listener
	 * 
	 * @.pre			true
	 * @.post			move completed || returned to menu if reset is pressed
	 * 					&& game end message shown if game ends
	 */
	
	public void actionPerformed(ActionEvent e) {
		// if reset pressed, close window and go back to App menu
		if(e.getSource()==reset){
			new App();
			dispose();
		  }
		// else game field button is pressed
		else{ 
			// identify button pressed
		  for(int i=0; i<leveys*korkeus;i++){
		      if(e.getSource().equals(b[i])){
		    	  // if button already has icon do nothing, else continue
		           if(b[i].getIcon()==null){
		        	   // complete player move if it is player turn
		              if(vuoro==true){
		            	  int x = i%leveys;
		            	  int y = i/leveys;
		            	  peliTila.teeSiirto(this, 1, x, y);
		            	  vuoro=false;
		            	  // check if move results in player win
		            	  if(peliTila.tarkastaVoitto() && !loppu) {
		            		  loppu = true;
		            		  loppu("Sinä voitit!");
		            	  }
		            	  // check if results in tie
		            	  if(peliTila.getTyhjia()==0 && !loppu) {
		            		  loppu = true;
		            		  loppu("Tasapeli!");
		            	  }
		            	  // if game continues, pass turn to CPU
		            	  if(!(loppu)) {
		            		  koneenVuoro();
		            		  // check if CPU won
		            		  if(peliTila.tarkastaVoitto()) {
		            			  loppu = true;
		            			  loppu("Tietokone voitti!");
			            		  vuoro=false;
			            	  }
		            		  // check if tie
			            	  if(peliTila.getTyhjia()==0 && !loppu) {
			            		  loppu = true;
			            		  loppu("Tasapeli!");
			            	  }
		            	  } 
		              }  
		            }
		       } 
		  }
		}
	}
	
	/**
	 * Method adds icons to pushed buttons
	 * @param merkki	1 represents cross, else zero is added
	 * @param paikka 	location counted from top left corner (width first)
	 * @.pre			paikka < leveys*korkeus
	 * @.post			icon added
	 */
	
	public void addIcon(int merkki, int paikka) {
		Icon icon;
		if (merkki == 1) {icon = risti2;}
		else {icon = nolla2;}
		b[paikka].setIcon(icon);
	}
	
	/**
	 * Method to play CPU turn. CPU chooses move based on minimax-function
	 * @.pre			true
	 * @.post			move completed
	 */
	
	public void koneenVuoro(){
		// variables for selected move
		int valittuI = 0;
		int valittuJ = 0;
		// array for storing the heuristic value of each move
		double pisteet[][] = new double[leveys][korkeus];
		// deep copy of current board for simulation
		Board kopioPeliTila = new Board(peliTila);
		// array representing board from copied Board object
		int kopiolauta[][] = kopioPeliTila.getPeliLauta();
		// variable to store max heuristic value
		double maksimi = -99999999;
		// depth of minimax search set to max(4, moves left)
		int depth = 4;
		if (peliTila.getMerkkeja()<depth) {
			depth = peliTila.getMerkkeja();
		}
		// simulate all moves and collect their heuristic value
		for (int i = 0; i<leveys; i++) {
			for (int j = 0; j<korkeus; j++) {
				if(kopiolauta[i][j] != 0) {
					continue;
				}
				kopioPeliTila.teeSiirto(2, i, j);
				// random decimals are added for tie-breaking
				pisteet[i][j]=minimax(kopioPeliTila, depth, false) + Math.random();
				kopioPeliTila.poistaSiirto(i,j);
			}
		}
		// select the move with highest value
		for (int i = 0; i<leveys; i++) {
			for (int j = 0; j<korkeus; j++) {
				if(kopiolauta[i][j] != 0) {
					continue;
				}
				if(pisteet[i][j]>maksimi) {
					maksimi = pisteet[i][j];
					valittuI = i;
					valittuJ = j;
				}
			}
		}
		// complete the move and pass turn to player
		peliTila.teeSiirto(this, 2, valittuI, valittuJ);
		vuoro = true;	
	}
	
	/**
	 * Minimax algorithm to calculate move values
	 * @.param kpt		Board object representing game state
	 * @.param depth	depth to search
	 * @.param Mplayer	true == Maximizing player turn
	 * @.pre			Board != null 
	 * @.post			RESULT.length == kpt.peliLauta.length && RESULT.length[0] = kpt.peliLauta.length &&
	 * 					(each RESULT[i][j] represents move value)
	 */
	
	public int minimax(Board kpt, int depth, boolean Mplayer) {
		// deep copy of passed board for simulation
		Board kopio = new Board(kpt);
		int kl[][] = kopio.getPeliLauta();
		// variables for finding min and max
		int value;
		int check;
		// checks if current status is terminal status and returns it's value
		boolean voitto = kopio.tarkastaVoitto();
		if (voitto && !Mplayer) {
			return 1000000;
		}
		if (voitto && Mplayer) {
			return -1000000;
		}
		// checks if max depth has been reached and returns the current value
		if (depth == 0) {
			value = kopio.checkHeuristic();
			value = value - kopio.checkHeuristicNeg();
			return value;
		}
		// finds the maximum value for maximizing player
		else if (Mplayer) {
			value = -999999;
			for (int i=0; i<leveys; i++) {
				for (int j=0; j<korkeus; j++) {
					if (kl[i][j] != 0) {
						continue;
					}
					// simulates all moves and launches recursive call
					kopio.teeSiirto(2, i, j);
					check = minimax(kopio, depth-1, false);
					if (check>value) {
						value = check;
					}
					kopio.poistaSiirto(i,j);
					}
				}
			return value;
			}
		// finds the minimum value for maximizing player
		else  {
			value = 999999;
			for (int i=0; i<leveys; i++) {
				for (int j=0; j<korkeus; j++) {
					if (kl[i][j] != 0) {
						continue;
					}
					// simulates all moves and launches recursive call
					kopio.teeSiirto(1, i, j);
					check = minimax(kopio, depth-1, true);
					if (check<value) {
						value = check;
					}
					kopio.poistaSiirto(i, j);
					}
				}
			return value;
		}
	}
	
	/**
	 * Creates pop-up window with a message
	 * @.param infoMessage	message to show
	 * @.pre			true
	 * @.post			pop up window shown with infoMessage
	 */
	
	public void loppu(String infoMessage) {
	        JOptionPane.showMessageDialog(null, infoMessage, "Peli loppui", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * This is a method that scales an image to desired scale
	 * I copied it directly from
	 * https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
	 */

	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();
	    return resizedImg;
	}
}
