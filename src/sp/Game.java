package sp;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
public class Game extends JFrame implements ItemListener, ActionListener{
	
	int leveys;
	int tyhjia;
	int korkeus;
	int merkkeja;
	int kentta[][];
	JButton b[];
	JButton reset = new JButton("RESET");
	boolean vuoro;
	Icon icon;
	ImageIcon risti =new ImageIcon("risti.jpg");
	ImageIcon nolla =new ImageIcon("nolla.jpg");
	ImageIcon risti2;
	ImageIcon nolla2;
	ActionListener eka;
	
	
	public Game(int leveys, int korkeus, int merkkeja) {
		super("Juhan ristinolla");
		this.leveys = leveys;
		this.korkeus = korkeus;
		this.merkkeja = merkkeja;
		this.b = new JButton[leveys*korkeus];
		this.kentta = new int[leveys][korkeus];
		this.vuoro = true;
		this.tyhjia = leveys*korkeus;
		//System.out.println("merkkeja: "+merkkeja);
		showButtons(leveys, korkeus);

	}
	
	public void showButtons(int leveys, int korkeus) {
		
		int skaala = 20/leveys;
		if(korkeus>leveys) {skaala = 20/ korkeus;}
		setLayout(null);
		setSize(18+leveys*skaala*20,100+korkeus*skaala*20);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		int x=5; 
		int y=skaala;
		int j=0;
		
		
		for(int i=0; i<korkeus*leveys; i++, x+=20*skaala, j++) {
			b[i]= new JButton();
			if(j==leveys){
				j=0; y+=20*skaala; x=5;
				}
			b[i].setBounds(x,y,20*skaala,20*skaala);
			add(b[i]);
			b[i].addActionListener(this);
		}//eof for

		reset.setBounds((18+leveys*skaala*20)/2-50,korkeus*skaala*20+15,100,50);
		add(reset);
		risti2 = new ImageIcon(getScaledImage(risti.getImage(),18*skaala,18*skaala));
		nolla2 = new ImageIcon(getScaledImage(nolla.getImage(),18*skaala,18*skaala));
		reset.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==reset){
		 for(int i=0; i<leveys*korkeus;i++){
		   b[i].setIcon(null);
		  }//eof for  
		}
		else{ 
		  for(int i=0; i<leveys*korkeus;i++){
		      if(e.getSource().equals(b[i])){
		           if(b[i].getIcon()==null){
		              if(vuoro==true){
		            	  icon=risti2;
		            	  int x = i%leveys;
		            	  int y = i/leveys;
		            	  this.kentta[x][y]=1;
		            	  tyhjia = tyhjia -1;
		            	  b[i].setIcon(icon);
		            	  vuoro=false;
		            	  if(voitto(kentta)) {
		            		  tyhjia=0;
		            	  }
		            	  if(tyhjia>0) {
		            		  koneenVuoro();
		            		  if(voitto(kentta)) {
			            		  tyhjia=0;
			            		  vuoro=false;
			            	  }
		            	  } 
		              }
		     //         else{
		     //       	  icon=nolla2;
		     //       	  vuoro=true;
		     //       	  kentta[i%leveys][i/leveys]=2;
		     //       	  
		            }
		       } 
		  }//eof for
		}//eof else
		}//eof logicfriend
		
	

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-gen)erated method stub
		
	}
	
	//https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	
	public int minimax(int[][] lauta, int depth, boolean Mplayer) {
		int kopio[][] = kopioiKentta(lauta);  // kopio laudan simulaatiota varten
		//System.out.println("Minimax "+kopio[0][0]);
		int value;
		int check;
		if (depth>tyhjia) {
			depth = tyhjia;
		}
		boolean voitto = voitto(kopio);
		if (voitto && !Mplayer) {
			return 1000000;
		}
		if (voitto && Mplayer) {
			return -1000000;
		}
		if (depth == 0 || voitto(kopio)) {
		//if(depth == 0) {
			value = checkHeuristic(kopio);
			//System.out.println("eka arvo "+value);
			value = value - checkHeuristicNeg(kopio);
			//System.out.println("toka arvo "+value);
			return value;
		}
		else if (Mplayer) {
			value = -999999;
			for (int i=0; i<leveys; i++) {
				for (int j=0; j<korkeus; j++) {
					if (kopio[i][j] != 0) {
						continue;
					}
					kopio[i][j]=2;
					check = minimax(kopioiKentta(kopio), depth-1, false);
					if (check>value) {
						value = check;
					}
					kopio[i][j]=0;
					}
				}
			return value;
			}
		else  {
			value = 999999;
			for (int i=0; i<leveys; i++) {
				for (int j=0; j<korkeus; j++) {
					if (kopio[i][j] != 0) {
						continue;
					}
					kopio[i][j]=1;
					check = minimax(kopioiKentta(kopio), depth-1, true);
					if (check<value) {
						value = check;
					}
					kopio[i][j]=0;
					}
				}
			return value;
		}
	}
	
	public void koneenVuoro(){
		int valittuI = 0;
		int valittuJ = 0;
		double pisteet[][] = new double[leveys][korkeus];
		int kopiolauta[][] = kopioiKentta(kentta);
		double maksimi = -99999999;
		for (int i = 0; i<leveys; i++) {
			for (int j = 0; j<korkeus; j++) {
				if(kopiolauta[i][j] != 0) {
					continue;
				}
				kopiolauta[i][j]=2;
				//System.out.println(kopiolauta[0][0]);
				pisteet[i][j]=minimax(kopiolauta, 4, false) + Math.random();
				kopiolauta[i][j]=0;
			}
		}
		for (int i = 0; i<leveys; i++) {
			for (int j = 0; j<korkeus; j++) {
				System.out.println(pisteet[i][j]);
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
		b[valittuJ*leveys+valittuI].setIcon(nolla2);
		this.kentta[valittuI][valittuJ]=2;
		//System.out.println("Kone valitsi "+valittuI+" "+valittuJ);
		tyhjia = tyhjia-1;
		
		vuoro = true;
		
	}
		
		/**
		function minimax(node, depth, maximizingPlayer) is
	    if depth = 0 or node is a terminal node then
	        return the heuristic value of node
	    if maximizingPlayer then
	        value := −∞
	        for each child of node do
	            value := max(value, minimax(child, depth − 1, FALSE))
	        return value
	    else (* minimizing player *)
	        value := +∞
	        for each child of node do
	            value := min(value, minimax(child, depth − 1, TRUE))
	        return value
		**/
	
	public int checkHeuristic(int[][] kopiokentta) {
		
		//int x = 0;
		//int y = 0;
		int tulos[] = new int[merkkeja+1];
		
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int vaakalaskuri = 0;
				int pystylaskuri = 0;
				int vinolaskuri1 = 0;
				int vinolaskuri2 = 0;
				if(kopiokentta[i][j]==1) {
					continue;
				}
				if(kopiokentta[i][j]==2) {
					vaakalaskuri = 1;
					pystylaskuri = 1;
					vinolaskuri1 = 1;
					vinolaskuri2 = 1;
				}
				//leveys
				for (int x =1; x+i<merkkeja; x++) {
					if(i+merkkeja>leveys) {
						vaakalaskuri = 0;
						break;
					}
					if (kopiokentta[i+x][j]==1) {
						vaakalaskuri = 0;
						break;
					}
					if (kopiokentta[i+x][j]==2){
						vaakalaskuri = vaakalaskuri+1;
					}
				}
				//korkeus
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus) {
						pystylaskuri = 0;
						break;
					}
					if (kopiokentta[i][j+y]==1) {
						pystylaskuri = 0;
						break;
					}
					if (kopiokentta[i][j+y]==2){
						pystylaskuri = pystylaskuri+1;
					}
				}
				//vino1
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i+merkkeja>leveys) {
						vinolaskuri1 = 0;
						break;
					}
					if (kopiokentta[i+y][j+y]==1) {
						vinolaskuri1 = 0;
						break;
					}
					if (kopiokentta[i+y][j+y]==2){
						vinolaskuri1 = vinolaskuri1+1;
					}
				}
				//vino2
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i-merkkeja<-1) {
						vinolaskuri2 = 0;
						break;
					}
					if (kopiokentta[i-y][j+y]==1) {
						vinolaskuri2 = 0;
						break;
					}
					if (kopiokentta[i-y][j+y]==2){
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
		if (tulos[merkkeja] != 0) {
			return 999999;
		}
		else {
			for (int i = 1; i<merkkeja; i++) {
				pisteet = pisteet + tulos[i]*i*i;
			}
		}
		//pisteet = pisteet - checkHeuristicNeg(kopiokentta);
		return pisteet;
	}

	
	
	
	public int checkHeuristic2(boolean pelaaja, int[][] kopiokentta) {
		int oma = 1;
		int vastustaja = 2;
		if (pelaaja) {
			oma = 2;
			vastustaja = 1;
		}
		int taulukko[] = new int[merkkeja+1];
		
		//vaaka
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int laskuri = 0;
				if (i+merkkeja>leveys) {
					break;	
				}
				for (int k=0; k<merkkeja; k++) {
					if (kopiokentta[i+k][j] == vastustaja){
						laskuri = 0;
						break;
					}
					else if (kopiokentta[i+k][j] == oma) {
						laskuri = laskuri+1;
					}
				}
				taulukko[laskuri]=taulukko[laskuri]+1;
				}
			}
		
		// pysty
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int laskuri = 0;
				if (j+merkkeja>korkeus) {
					break;	
				}
				for (int k=0; k<merkkeja; k++) {
					if (kopiokentta[i][j+k] == vastustaja){
						laskuri = 0;
						break;
					}
					else if (kopiokentta[i][j+k] == oma) {
						laskuri = laskuri+1;
					}
				}
				taulukko[laskuri]=taulukko[laskuri]+1;
				}
			}
		
		// vino1
		
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int laskuri = 0;
				if (i+merkkeja>leveys || j+merkkeja>korkeus) {
					break;	
				}
				for (int k=0; k<merkkeja; k++) {
					if (kopiokentta[i+k][j+k] == vastustaja){
						laskuri = 0;
						break;
					}
					else if (kopiokentta[i+k][j+k] == oma) {
						laskuri = laskuri+1;
					}
				}
				taulukko[laskuri]=taulukko[laskuri]+1;
				}
			}
		
		// vino2
		
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int laskuri = 0;
				if (i-merkkeja<0 || j+merkkeja>korkeus) {
					break;	
				}
				for (int k=0; k<merkkeja; k++) {
					if (kopiokentta[i-k][j+k] == vastustaja){
						laskuri = 0;
						break;
					}
					else if (kopiokentta[i-k][j+k] == oma) {
						laskuri = laskuri+1;
					}
				}
				taulukko[laskuri]=taulukko[laskuri]+1;
				}
			}
		int tulos = 0;
		for (int i=0; i<taulukko.length; i++) {
			tulos = tulos + (i+1)*(i+1)*taulukko[i];
			if(taulukko[merkkeja] != 0 && pelaaja) {
				tulos = 999999;
			}
			if(taulukko[merkkeja-1] != 0 && !pelaaja) {
				tulos = 999999;
			}
		}
		if(pelaaja) {
		tulos = tulos - checkHeuristic2(!pelaaja, kopiokentta);
		}
		return tulos;
	}
	
	public int[][] kopioiKentta(int[][] ap){
		int[][] uusi = new int[ap.length][ap[0].length];
		for (int i =0; i<ap.length;i++) {
			for (int j=0; j<ap[0].length; j++){
				uusi[i][j] = ap[i][j];
			}
		}
		return uusi;
	}
	
	public int checkHeuristicNeg(int[][] kopiokentta) {
		
		//int x = 0;
		//int y = 0;
		int tulos[] = new int[merkkeja+1];
		
		for (int i=0; i<leveys; i++) {
			for (int j=0; j<korkeus; j++) {
				int vaakalaskuri = 0;
				int pystylaskuri = 0;
				int vinolaskuri1 = 0;
				int vinolaskuri2 = 0;
				if(kopiokentta[i][j]==2) {
					continue;
				}
				if(kopiokentta[i][j]==1) {
					vaakalaskuri = 1;
					pystylaskuri = 1;
					vinolaskuri1 = 1;
					vinolaskuri2 = 1;
				}
				//leveys
				for (int x =1; x+i<merkkeja; x++) {
					if(i+merkkeja>leveys) {
						vaakalaskuri = 0;
						break;
					}
					if (kopiokentta[i+x][j]==2) {
						vaakalaskuri = 0;
						break;
					}
					if (kopiokentta[i+x][j]==1){
						vaakalaskuri = vaakalaskuri+1;
					}
				}
				//korkeus
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus) {
						pystylaskuri = 0;
						break;
					}
					if (kopiokentta[i][j+y]==2) {
						pystylaskuri = 0;
						break;
					}
					if (kopiokentta[i][j+y]==1){
						pystylaskuri = pystylaskuri+1;
					}
				}
				//vino1
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i+merkkeja>leveys) {
						vinolaskuri1 = 0;
						break;
					}
					if (kopiokentta[i+y][j+y]==2) {
						vinolaskuri1 = 0;
						break;
					}
					if (kopiokentta[i+y][j+y]==1){
						vinolaskuri1 = vinolaskuri1+1;
					}
				}
				//vino2
				for (int y =1; y+j<merkkeja; y++) {
					if(j+merkkeja>korkeus || i-merkkeja<-1) {
						vinolaskuri2 = 0;
						break;
					}
					if (kopiokentta[i-y][j+y]==2) {
						vinolaskuri2 = 0;
						break;
					}
					if (kopiokentta[i-y][j+y]==1){
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
			//System.out.println("Neg: "+(merkkeja-1)+" : "+tulos[merkkeja-1]);
			return 499999;
		}
		else {
			for (int i = 1; i<merkkeja; i++) {
				pisteet = pisteet + tulos[i]*i*i;
			}
		}
		return pisteet;
	}	
	public boolean voitto(int t[][]) {
		int tarkastettava = 0;
		int laskuri = 0;
		for (int i=0; i<leveys; i++) {
			//System.out.println(i);
			for (int j=0; j<korkeus; j++) {
				laskuri = 1;
				tarkastettava = 0;
				if(t[i][j]==0) {
					laskuri=1;
					continue;
				}
				tarkastettava=t[i][j];
				//leveys
				for (int x =1; x<merkkeja; x++) {
					if(i+merkkeja>leveys) {
						laskuri = 1;
						break;
					}
					if (!(t[i+x][j]==tarkastettava)) {
						laskuri = 1;
						break;
					}
					laskuri = laskuri+1;
					if(laskuri == merkkeja) {
						//System.out.println("vaaka "+tarkastettava+" "+i+" "+j);
						return true;
					}
				}
				//korkeus
				for (int y =1; y<merkkeja; y++) {
					if(j+merkkeja>korkeus) {
						laskuri=1;
						break;
					}
					if (!(t[i][j+y]==tarkastettava)) {
						laskuri=1;
						break;
					}
					laskuri=laskuri+1;
					if(laskuri == merkkeja) {
						//System.out.println("pustu "+tarkastettava+" "+i+" "+j);
						return true;
					}
				}
				//vino1
				for (int y =1; y<merkkeja; y++) {
					if(j+merkkeja>korkeus || i+merkkeja>leveys) {
						laskuri=1;
						break;
					}
					if (!(t[i+y][j+y]==tarkastettava)) {
						laskuri = 1;
						break;
					}
					laskuri=laskuri+1;
					if(laskuri == merkkeja) {
						//System.out.println("vino1 "+tarkastettava+" "+i+" "+j);
						return true;
					}
				}
				//vino2
				for (int y =1; y<merkkeja; y++) {
					if(j+merkkeja>korkeus || i-merkkeja<-1) {
						laskuri=1;
						break;
					}
					if (!(t[i-y][j+y]==tarkastettava)) {
						laskuri=1;
						break;
					}
					laskuri=laskuri+1;
					if(laskuri == merkkeja) {
						//System.out.println("vino2 "+tarkastettava+" "+i+" "+j);
						return true;
					}
				}
			} 
		}
		return false;
	}
}
