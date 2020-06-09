import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class View extends JFrame{

	protected JButton dealB, standB, readyB;
	protected JLabel mainInfoL = new JLabel("Hello! Waiting to join the game", SwingConstants.CENTER);
	protected JLabel subInfoL = new JLabel("-", SwingConstants.CENTER);
	protected List<JLayeredPane> cardsPList = new ArrayList<JLayeredPane>();
	protected List<JLabel> nameLList = new ArrayList<JLabel>();
	protected List<JLabel> statusList = new ArrayList<JLabel>();
	protected List<JLabel> stakesLList = new ArrayList<JLabel>();
	protected List<JLabel> dealerLabelList = new ArrayList<JLabel>();
	protected List<JLabel> valueLList = new ArrayList<JLabel>();
	protected List<JPanel> burstPList = new ArrayList<JPanel>();
	protected List<JPanel> vingtUnPList = new ArrayList<JPanel>();
	protected List<JPanel> readyPList = new ArrayList<JPanel>();
	protected List<JPanel> kickPList = new ArrayList<JPanel>();
	
	private JPanel mainP; 
	final protected int cardAreaWidth = 142;
	final protected int cardAreaHeight = 96;	
	final protected Color green = new Color(0,200,0);
	final protected Color gold = new Color(255, 180, 50);	
	final protected Color darkgold = new Color(230, 100, 50);
	protected int youID;
	public View() {
		this.setSize(800,580);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	public String askName() {
		JOptionPane op = new JOptionPane();
		String name = op.showInputDialog(this, "What's your name?");
		int n = op.getOptionType();
	
		return name.trim();
	}
	
	public String askIP() {
		JOptionPane op = new JOptionPane();
		String IP = op.showInputDialog("What's the server's IP?", "169.254.108.14" );
	
		return IP.trim();
	}
	
	
	public String reAskIP() {
		JOptionPane op = new JOptionPane();
		String IP = op.showInputDialog("Unreachable IP. What's the server's IP?", "169.254.108.14" );
	
		return IP.trim();
	}
	
	public void init() {

		
		mainP = new BoardP();
		mainP.setLayout(new BorderLayout());
		
		
		JPanel boardP =  new JPanel(new BorderLayout());
		boardP.setOpaque(false);
		
		JPanel mainInfoP =  new JPanel(new BorderLayout());
		mainInfoP.setOpaque(false);
		
		JPanel subInfoP = new JPanel(new BorderLayout());
		subInfoP.setOpaque(false);
		
		JPanel topP = new JPanel(new GridLayout(2,1,1,1));
		topP.setOpaque(false);		
		mainInfoL.setForeground(Color.white);
		subInfoL.setForeground(Color.white);		
		mainInfoL.setFont(new Font("Arial", Font.PLAIN, 26));
		mainInfoL.setBorder(new EmptyBorder(1, 1, 1, 1));
		topP.add(mainInfoL);
		topP.add(subInfoL);
		mainP.add(topP ,BorderLayout.NORTH);						
		this.add(mainP);
		JPanel bottomPanel = new JPanel();
		dealB = buildBlackButton("Deal");
		standB = buildBlackButton("Stand");
		readyB = buildRedButton("Ready");
		bottomPanel.setBackground(Color.white);
		bottomPanel.add(readyB);
		bottomPanel.add(dealB);
		bottomPanel.add(standB);
		bottomPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
		mainP.add(bottomPanel,BorderLayout.SOUTH);
			
		this.setVisible(true);
	}
	
	public void displayBoard(int youID) {
		this.youID = youID;
		JPanel mainBoardP = new JPanel(new GridLayout(2,1,2,12));
		mainBoardP.setOpaque(false);
		JPanel upperPlayerBoardP = new JPanel(new GridLayout(1,4,2,12));

		JPanel buttomPlayerBoardP = new JPanel(new GridLayout(1,1,2,12));
		for(int i =0;i<5; i++) {
			if(i!=this.youID) {			
				buildPlayerPanel(upperPlayerBoardP, i);
			}else {
				buildPlayerPanel(buttomPlayerBoardP, i);
			}
		}

		mainBoardP.add(upperPlayerBoardP);
		mainBoardP.add(buttomPlayerBoardP);
		
		mainP.add(mainBoardP ,BorderLayout.CENTER);
	}
	
	
	private void buildPlayerPanel(JPanel base, int playerID) {
		JLayeredPane cardP  =new JLayeredPane();
		cardsPList.add(cardP);
		cardP.setBounds(3, 0, cardAreaWidth, cardAreaHeight);
		cardP.setOpaque(false);	
		JLabel burstL = createHandStatusLabel("Burst",  new Color(230, 50, 13));
		JLabel VingtUnL = createHandStatusLabel("Blackjack",  gold);
		JLabel readyL = createHandStatusLabel("Ready",  new Color(255, 255, 255));
		JLabel kickL = createHandStatusLabel("KickOut",  new Color(230, 50, 13));
		
		JPanel VingtUnP  = createCardAreaPanel(VingtUnL, 2, 0, cardAreaWidth, cardAreaHeight);
		JPanel burstP  = createCardAreaPanel(burstL, 2, 0, cardAreaWidth, cardAreaHeight);
		JPanel readyP  = createCardAreaPanel(readyL, 2, 0, cardAreaWidth, cardAreaHeight);
		JPanel kickP  = createCardAreaPanel(kickL, 2, 0, cardAreaWidth, cardAreaHeight);
		
		burstPList.add(burstP);
		vingtUnPList.add(VingtUnP);
		readyPList.add(readyP);
		kickPList.add(kickP);
		
		
		VingtUnP.setVisible(false);
		burstP.setVisible(false);			
		readyP.setVisible(false);				
		kickP.setVisible(false);
		
		VingtUnP.setBackground(new Color(30, 30, 80, 120) );
		burstP.setBackground(new Color(30, 30, 80, 120) );
		kickP.setBackground(new Color(30, 30, 80, 120) );
		readyP.setBackground(new Color(30, 30, 80, 0) );
		JLayeredPane cardAreaInnerP =new JLayeredPane();
		cardAreaInnerP.setPreferredSize(new Dimension(cardAreaWidth+4, cardAreaHeight));			
		cardAreaInnerP.add(cardP,new Integer(300));
		cardAreaInnerP.add(burstP,new Integer(400));
		cardAreaInnerP.add(VingtUnP,new Integer(500));
		cardAreaInnerP.add(readyP,new Integer(600));
		cardAreaInnerP.add(kickP,new Integer(700));
		cardAreaInnerP.setBorder(BorderFactory.createLineBorder(Color.white, 2));
		JPanel cardAreaP = new JPanel();
		cardAreaP.add(cardAreaInnerP);
		cardAreaP.setOpaque(false);
		
		JPanel playerCenterP = new JPanel(new BorderLayout());
		playerCenterP.setOpaque(false);	
		
		JLabel handValueL = new JLabel(" " , SwingConstants.CENTER);	
		handValueL.setForeground(Color.white);
		handValueL.setFont(new Font("Arial", Font.PLAIN, 16));
		
		valueLList.add(handValueL);
		JPanel handValueP = new JPanel(new BorderLayout());
		handValueP.setBorder(new EmptyBorder(0, 0, 0, 0));
		handValueP.add(handValueL);
		handValueP.setOpaque(false);
		
		JLabel dealerL = new JLabel(" " , SwingConstants.CENTER);
		this.dealerLabelList.add(dealerL);
		
		dealerL.setForeground(Color.white);
		dealerL.setFont(new Font("Arial", Font.PLAIN, 16));
		JPanel dealerP = new JPanel(new BorderLayout());
		dealerP.setBorder(new EmptyBorder(0, 0, 0, 0));
		dealerP.add(dealerL);
		dealerP.setOpaque(false);
		
	
		playerCenterP.add(dealerL,BorderLayout.NORTH); 

		playerCenterP.add(handValueP,BorderLayout.SOUTH); 
		
		playerCenterP.add(cardAreaP,BorderLayout.CENTER); 			

		JPanel playerButtomP = new JPanel(new BorderLayout());

		playerButtomP.setOpaque(false);
		
		
		JPanel playerInfoP = new JPanel(new GridLayout(2,1,0,0));		
		JPanel nameP = new JPanel(new FlowLayout());
		JLabel nameLT = new JLabel("Player "+(playerID+1));
		JLabel nameL = new JLabel(" ");
		nameL.setFont(new Font("Arial", Font.PLAIN, 16));
		nameL.setForeground(Color.white);
//		nameP.add(nameLT);
		nameP.add(nameL);
		nameP.setOpaque(false);
		playerInfoP.add(nameP);			
		nameLList.add(nameL);
		playerInfoP.setOpaque(false);
		
		JPanel stakeP = new JPanel(new FlowLayout());
		JLabel stakesLT = new JLabel("Stakes: ");
		JLabel stakesL = new JLabel("");			
		this.stakesLList.add(stakesL);
		stakesL.setFont(new Font("Arial", Font.PLAIN, 16));
		stakesL.setForeground(Color.white);
//		stakeP.add(stakesLT);
		stakeP.add(stakesL);
		stakeP.setOpaque(false);
		
		playerInfoP.add(stakeP);
		playerInfoP.setPreferredSize(new Dimension(100,50));

		JLabel statusL = new JLabel("", SwingConstants.CENTER);
		JPanel infoP = new JPanel(new BorderLayout());
		infoP.setOpaque(false);
		statusL.setPreferredSize(new Dimension(100,26));
		statusL.setForeground(Color.white);
		statusList.add(statusL);
		infoP.add(playerInfoP, BorderLayout.CENTER);
		infoP.add(statusL, BorderLayout.SOUTH);
		playerButtomP.add(infoP, BorderLayout.SOUTH);
		
		JPanel playerP = new JPanel(new BorderLayout());
		playerP.setOpaque(false);
		playerP.add(playerButtomP,  BorderLayout.SOUTH);
		playerP.add(playerCenterP,  BorderLayout.CENTER);
		base.add(playerP);
		base.setOpaque(false);	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void clearBtn() {
		dealB.setEnabled(false);
		standB.setEnabled(false);
		readyB.setVisible(false);
		
	}
	
	
	public void setTopLabel(int index, String s) {
		this.dealerLabelList.get(index).setText(s);		
		update();		
	}
	
	public void setCardValueLabel(int index, String s) {
		this.valueLList.get(index).setText(s);		
		update();		
	}
	
	
	public void setStakes(int playerId, String stakes) {
		System.out.println(this.nameLList.size()+"this");
		System.out.println("setStakes + "+stakes);
		if(this.nameLList.get(playerId).getText()==null||this.nameLList.get(playerId).getText().trim().equals("")) {
			this.stakesLList.get(playerId).setText(" ");
		}else {
			this.stakesLList.get(playerId).setText(stakes +" stakes");
		}
		this.repaint();
		this.revalidate();
	}
	
	public void setBurst(int playerId, boolean input) {
		this.burstPList.get(playerId).setVisible(input);
		this.repaint();
		this.revalidate();
	}
	
	public void setBlackJack(int playerId, boolean input) {
		this.vingtUnPList.get(playerId).setVisible(input);
		this.repaint();
		this.revalidate();
	}
	
	public void setReady(int playerId, boolean input) {
		this.readyPList.get(playerId).setVisible(input);
		this.repaint();
		this.revalidate();
	}
	
	
	public void setSubInfo(String s) {
		this.subInfoL.setText(s);
	}
	

	 
	public void setPlayerNameGold(int playerID) {
		this.nameLList.get(playerID).setForeground(darkgold);
		this.stakesLList.get(playerID).setForeground(darkgold);		 
	}

	private JLabel createHandStatusLabel(String s, Color color) {
	     JLabel label = new JLabel(s);
	     label.setFont(new Font("Arial", Font.BOLD, 16));
	     label.setForeground(color);
	     label.setHorizontalAlignment(SwingConstants.CENTER);
	     label.setVerticalAlignment(SwingConstants.CENTER);
	     return label;	   
	}
	
	public void setName(int playerId, String name) {
				System.out.println(this.nameLList.size()+"this");
				this.nameLList.get(playerId).setText(name);
				this.update();
			}
			
			public void setStatus(int playerId, String status) {
				this.statusList.get(playerId).setText(status);
				this.update();				
			}
			
	private JPanel createCardAreaPanel(JLabel label, int x, int y, int width, int height) {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.setBounds(x, y, width, height);	       
		if(label!=null) {	        	
			panel.add(label);
			}
		else {
			panel.setOpaque(true);
		}
		 return panel;
	}
			 
	public void activePlayerborder() {
				 			
	}
	public class CardP extends JPanel{
		
		private BufferedImage image;	
		final protected int width= 71;
		public CardP(String cardId) {
			this.setBackground(null);
			try {                
				image = ImageIO.read(new File("img/"+cardId+".png"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, width,96,this); // see javadoc for more info on the parameters            
		}

	}
	
	
	public class BoardP extends JPanel{
		
		private BufferedImage image;	
		public BoardP() {
			this.setBackground(null);
			try {                
				image = ImageIO.read(new File("img/board.jpg"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, 800,580,null); // see javadoc for more info on the parameters            
		}

	}
			
	
	
	
	
	
	
	
	
	
			
	public void paintCard(List<List<String>> t) {
		for(List<String> l : t) {
			if(l!=null) {
				for(String i : l) {
					System.out.print(i);
				}
			}else {
				System.out.print("null");
			}
				System.out.println();
		}
	
		for(int i = 0 ; i< t.size() ;i++) {
			this.cardsPList.get(i).removeAll();
			this.cardsPList.get(i).updateUI();
			if(t.get(i)!=null) {
				int cardNum = t.get(i).size();			
				
				for(int j=0; j< cardNum ;j++) {
					String cardIndex = t.get(i).get(j);					
					CardP c = new CardP(cardIndex);
					if(cardNum!=1) {
						c.setBounds(((cardAreaWidth-c.width)/(cardNum-1))*j, 0, c.width, cardAreaHeight);	
					}else {
						c.setBounds(((cardAreaWidth-c.width)/(cardNum))*j, 0, c.width, cardAreaHeight);					
					}
					this.cardsPList.get(i).add(c, new Integer(j+10));
				}
			}
		}
		update();
	}
			
	
	
	
	
	public void clearBoard() {
		for(JLayeredPane p : this.cardsPList) {
			p.removeAll();
			p.updateUI();
		}
		for(JPanel p : this.burstPList) {
			p.setVisible(false);
		}
		for(JPanel p : this.vingtUnPList) {
			p.setVisible(false);
		}
	}
			
			
			
			
			
			public void update() {
				this.repaint();
				this.revalidate();
			}
			
			private JButton buildBlackButton(String name) {
				JButton btn = new JButton(name);
				Color lightGrey = new Color(30,30,30);
				btn.setBackground(lightGrey);
				btn.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
				btn.setForeground(Color.WHITE);
				btn.setFont(new Font("Arial", Font.PLAIN, 15));
				btn.setFocusPainted(false);
				btn.setOpaque(true);
				btn.getModel().addChangeListener(new ChangeListener() {
					    @Override
					    public void stateChanged(ChangeEvent e) {
					        ButtonModel model = (ButtonModel) e.getSource();
					        if (model.isRollover()) {
					        	btn.setBackground(Color.white);
						    	btn.setForeground(lightGrey);
						    	btn.setBorder(BorderFactory.createLineBorder(lightGrey, 1));
					        } else if (model.isPressed()) {
					        	btn.setBackground(Color.white);
						    	btn.setForeground(lightGrey);
						    	btn.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
					        } else {
					        	btn.setBackground(lightGrey);
						    	btn.setForeground(Color.white);
						    	btn.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
					        }
					    }
			
				});		
				return btn;
			}
			
			private JButton buildRedButton(String name) {
				JButton btn = new JButton(name);
				Color red = new Color(227, 23, 13);
				Color lightGrey = new Color(150,150,150);
				btn.setBackground(red);
				btn.setBorder(BorderFactory.createEmptyBorder(2,1,2,1));
				btn.setForeground(Color.WHITE);
				btn.setFont(new Font("Arial", Font.PLAIN, 15));
				btn.setFocusPainted(false);
				btn.setOpaque(true);
				btn.getModel().addChangeListener(new ChangeListener() {
					    @Override
					    public void stateChanged(ChangeEvent e) {
					        ButtonModel model = (ButtonModel) e.getSource();
					        if (model.isRollover()) {
					        	btn.setBackground(Color.white);
						    	btn.setForeground(red);
						    	btn.setBorder(BorderFactory.createLineBorder(red, 1));
					        } else if (model.isPressed()) {
					        	btn.setBackground(Color.white);
						    	btn.setForeground(red);
						    	btn.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
					        } else {
					        	btn.setBackground(red);
						    	btn.setForeground(Color.white);
						    	btn.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
					        }
					    }
			
				});		
				return btn;
			}
}
