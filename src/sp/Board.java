package sp;

/** 
 * Game status and heuristic assessment
**/

public class Board{
	
	private int[][] pelilauta;		// array to store game status
	private int tyhjia;				// spaces left on board
	private int merkkeja;			// number of aligned markers needed for win
	private int korkeus, leveys;	// height and width of the board
	
	// constructor for board linked to GUI
	public Board(int leveys, int korkeus, int merkkeja) {
		this.merkkeja = merkkeja;
		this.tyhjia = leveys*korkeus;
		this.pelilauta = new int[leveys][korkeus];
		this.korkeus = korkeus;
		this.leveys = leveys;
	}
	
	// constructor for copies used in minimax simulation
	public Board(Board kopioitava) {
		this.merkkeja = kopioitava.merkkeja;
		this.tyhjia = kopioitava.tyhjia;
		this.pelilauta = kopioitava.kopioiPeliLauta();
		this.korkeus = kopioitava.korkeus;
		this.leveys = kopioitava.leveys;
	}
	
	/**
	 * Method to deep copy int[][] array of a Board object
	 * @.pre			true
	 * @.post			for each i<this.peliLauta.length and j<this.peliLauta[0].length
	 * 					RESULT[i][j] == this.peliLauta[i][j]
	 */
	
	public int[][] kopioiPeliLauta() {
		int[][] ap = this.getPeliLauta();
		int[][] uusi = new int[ap.length][ap[0].length];
		for (int i =0; i<ap.length;i++) {
			for (int j=0; j<ap[0].length; j++){
				uusi[i][j] = ap[i][j];
			}
		}
		return uusi;
	}
	
	public int[][] getPeliLauta() {
		return pelilauta;
	}
	
	public int getMerkkeja() {
		return merkkeja;
	}
	
	public int getTyhjia() {
		return tyhjia;
	}
	
	/**
	 * updated a move to int[][] peliLauta
	 * @param merkki	int to add to peliLauta
	 * @param x			location of the addition (width)
	 * @param y			location of the addition (height)
	 * @.pre			x<peliLauta.length, y<peliLauta[0].length
	 * @.post			peliLauta[x][y]==merkki && tyhjia == pre.tyhjia-1
	 */
	
	public void teeSiirto(int merkki, int x, int y) {
		pelilauta[x][y]=merkki;
		tyhjia = tyhjia-1;
	}
	
	/**
	 * same as previous + calls peli.addIcon() to update GUI
	 */
	
	public void teeSiirto(Game peli, int merkki, int x, int y) {
		pelilauta[x][y]=merkki;
		tyhjia = tyhjia-1;
		int paikka = leveys*y+x;
		peli.addIcon(merkki, paikka);
	}
	
	/**
	 * removes a move from (copied) board
	 * @param x			location of the addition (width)
	 * @param y			location of the addition (height)
	 * @.pre			x<peliLauta.length, y<peliLauta[0].length
	 * @.post			peliLauta[x][y]==0 && tyhjia == pre.tyhjia+1
	 */
	
	public void poistaSiirto(int x, int y) {
		pelilauta[x][y]=0;
		tyhjia = tyhjia+1;
	}
	
	/**
	 * Goes through the entire board and checks if there are int 1 or 2 marks
	 * aligned in qty that equals merkkeja (required for win)
	 * Board is checked in four directions (up-down, left-right, top_left-bottom_right, top_right-bottom_left)
	 * @.pre			true
	 * @.post			if (number of aligned marks >= merkkeja) RESULT==true
	 * 					else RESULT==false
	 */				
	
	public boolean tarkastaVoitto() {
		// variable that stores the mark that is checked
		int tarkastettava = 0;
		// count of aligned marks
		int laskuri = 0;
		
		// mark tarkastettava with the first mark and set counter to 1
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				laskuri = 1;
				tarkastettava = 0;
				// if empty space is found move to next iteration
				if(pelilauta[i][j]==0) {
					laskuri=1;
					continue;
				}
				tarkastettava=pelilauta[i][j];
				
				// check left to right
				for (int x =1; x<merkkeja; x++) {
					// break if there is no space for possible win
					if(i+merkkeja>leveys) {
						laskuri = 1;
						break;
					}
					// break if a different mark is found
					if (!(pelilauta[i+x][j]==tarkastettava)) {
						laskuri = 1;
						break;
					}
					laskuri = laskuri+1;
					// return true if sufficient amount of similar marks are found
					if(laskuri == merkkeja) {
						return true;
					}
				}
				// same checks top down
				for (int y =1; y<merkkeja; y++) {
					if(j+merkkeja>korkeus) {
						laskuri=1;
						break;
					}
					if (!(pelilauta[i][j+y]==tarkastettava)) {
						laskuri=1;
						break;
					}
					laskuri=laskuri+1;
					if(laskuri == merkkeja) {
						return true;
					}
				}
				// same checks from top left to bottom right
				for (int y =1; y<merkkeja; y++) {
					if(j+merkkeja>korkeus || i+merkkeja>leveys) {
						laskuri=1;
						break;
					}
					if (!(pelilauta[i+y][j+y]==tarkastettava)) {
						laskuri = 1;
						break;
					}
					laskuri=laskuri+1;
					if(laskuri == merkkeja) {
						return true;
					}
				}
				// same checks from top right to bottom left
				for (int y =1; y<merkkeja; y++) {
					if(j+merkkeja>korkeus || i-merkkeja<-1) {
						laskuri=1;
						break;
					}
					if (!(pelilauta[i-y][j+y]==tarkastettava)) {
						laskuri=1;
						break;
					}
					laskuri=laskuri+1;
					if(laskuri == merkkeja) {
						return true;
					}
				}
			} 
		}
		// return false if winning alignment not found
		return false;
	}
	
	/**
	 * Calculates heuristic value of the current game state for CPU player:
	 * 	If(tarkastaVoitto()): RESULT == 999999
	 *  if(!(tarkastaVoitto()): RESULT == sum of:
	 *  			1) go through every possible combination of squares that can result in CPU win
					2) count CPU marks in those areas
					3) for every found area add [number_of_CPU_marks]**2
	 * @.pre			true
	 * @.post			RESULT as described above
	 */		
	
	public int checkHeuristic() {
		
		// array to store the count of aligned marks
		int tulos[] = new int[merkkeja+1];
		
		// iteration to complete all counts
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int vaakalaskuri = 0;
				int pystylaskuri = 0;
				int vinolaskuri1 = 0;
				int vinolaskuri2 = 0;
				// skip count if starting square has player mark
				if(pelilauta[i][j]==1) {
					continue;
				}
				// add one to counter if starting square has CPU mark
				if(pelilauta[i][j]==2) {
					vaakalaskuri = 1;
					pystylaskuri = 1;
					vinolaskuri1 = 1;
					vinolaskuri2 = 1;
				}
				// complete left to right count. Continue as many steps as required for win
				for (int x =1; x+i<merkkeja; x++) {
					if(i+merkkeja>leveys) {
						vaakalaskuri = 0;
						break;
					}
					// player mark is found which means CPU cannot win -> break
					if (pelilauta[i+x][j]==1) {
						vaakalaskuri = 0;
						break;
					}
					// if CPU mark is found, update counter
					if (pelilauta[i+x][j]==2){
						vaakalaskuri = vaakalaskuri+1;
					}
				}
				// same count top down
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus) {
						pystylaskuri = 0;
						break;
					}
					if (pelilauta[i][j+y]==1) {
						pystylaskuri = 0;
						break;
					}
					if (pelilauta[i][j+y]==2){
						pystylaskuri = pystylaskuri+1;
					}
				}
				// same count diagonal(1)
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i+merkkeja>leveys) {
						vinolaskuri1 = 0;
						break;
					}
					if (pelilauta[i+y][j+y]==1) {
						vinolaskuri1 = 0;
						break;
					}
					if (pelilauta[i+y][j+y]==2){
						vinolaskuri1 = vinolaskuri1+1;
					}
				}
				//same count diagonal(2)
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i-merkkeja<-1) {
						vinolaskuri2 = 0;
						break;
					}
					if (pelilauta[i-y][j+y]==1) {
						vinolaskuri2 = 0;
						break;
					}
					if (pelilauta[i-y][j+y]==2){
						vinolaskuri2 = vinolaskuri2+1;
					}
				}
				// add counts of this starting square to result array and move to next square
				tulos[vaakalaskuri]=tulos[vaakalaskuri]+1;
				tulos[pystylaskuri]=tulos[pystylaskuri]+1;
				tulos[vinolaskuri1]=tulos[vinolaskuri1]+1;
				tulos[vinolaskuri2]=tulos[vinolaskuri2]+1;
			}
		}
		
		int pisteet = 0;
		// winning combination is found
		if (tulos[merkkeja] != 0) {
			return 999999;
		}
		// else sum up the heuristic score
		else {
			for (int i = 1; i<merkkeja; i++) {
				pisteet = pisteet + tulos[i]*i*i;
			}
		}
		//return the score
		return pisteet;
	}
	/**
	 * Calculates heuristic value of the current game state for human player. 
	 * Same as the one for CPU but human win value is lower (so that CPU would prefer instant win over
	 * blocking human win)
	 * 	If(tarkastaVoitto()): RESULT == 499999
	 *  if(!(tarkastaVoitto()): RESULT == sum of:
	 *  			1) go through every possible combination of squares that can result in human win
					2) count human marks in those areas
					3) for every found area add [number_of_human_marks]**2
	 * @.pre			true
	 * @.post			RESULT as described above
	 */		

	
	
	public int checkHeuristicNeg() {	
		int tulos[] = new int[merkkeja+1];
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int vaakalaskuri = 0;
				int pystylaskuri = 0;
				int vinolaskuri1 = 0;
				int vinolaskuri2 = 0;
				if(pelilauta[i][j]==2) {
					continue;
				}
				if(pelilauta[i][j]==1) {
					vaakalaskuri = 1;
					pystylaskuri = 1;
					vinolaskuri1 = 1;
					vinolaskuri2 = 1;
				}
				//left to right
				for (int x =1; x+i<merkkeja; x++) {
					if(i+merkkeja>leveys) {
						vaakalaskuri = 0;
						break;
					}
					if (pelilauta[i+x][j]==2) {
						vaakalaskuri = 0;
						break;
					}
					if (pelilauta[i+x][j]==1){
						vaakalaskuri = vaakalaskuri+1;
					}
				}
				//top down
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus) {
						pystylaskuri = 0;
						break;
					}
					if (pelilauta[i][j+y]==2) {
						pystylaskuri = 0;
						break;
					}
					if (pelilauta[i][j+y]==1){
						pystylaskuri = pystylaskuri+1;
					}
				}
				//diagonal(1)
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i+merkkeja>leveys) {
						vinolaskuri1 = 0;
						break;
					}
					if (pelilauta[i+y][j+y]==2) {
						vinolaskuri1 = 0;
						break;
					}
					if (pelilauta[i+y][j+y]==1){
						vinolaskuri1 = vinolaskuri1+1;
					}
				}
				//diagonal(2)
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i-merkkeja<-1) {
						vinolaskuri2 = 0;
						break;
					}
					if (pelilauta[i-y][j+y]==2) {
						vinolaskuri2 = 0;
						break;
					}
					if (pelilauta[i-y][j+y]==1){
						vinolaskuri2 = vinolaskuri2+1;
					}
				}
				tulos[vaakalaskuri]=tulos[vaakalaskuri]+1;
				tulos[pystylaskuri]=tulos[pystylaskuri]+1;
				tulos[vinolaskuri1]=tulos[vinolaskuri1]+1;
				tulos[vinolaskuri2]=tulos[vinolaskuri2]+1;
			}
		}
		int pisteet = 0;
		if (tulos[merkkeja] > 0) {
			return 499999;
		}
		else {
			for (int i = 1; i<merkkeja; i++) {
				pisteet = pisteet + tulos[i]*i*i;
			}
		}
		return pisteet;
	}	
}

