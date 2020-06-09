import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Server implements Runnable{
	private GameModel model;
	private ServerSocket server;
	private ArrayList<ClientRunner> clients = new ArrayList<ClientRunner>();
	private boolean gameStart=false;
	private HashMap<Integer, ClientRunner> playerIDMap;
	private List<ClientRunner> waitList = new LinkedList<ClientRunner>();
	
	public static void main(String[] args) {
		Thread t = new Thread(new Server());
		t.start();
		try {
			t.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public Server() {
		model = new GameModel();
		model.setWaitPlayerInfo(); //Default mainInfo 
		playerIDMap =new HashMap();	//mapping playerid to client
		try {
			server = new ServerSocket(8765);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void run() {
		while(true) {
			Socket clientSocket = null;	
			try {
				clientSocket = server.accept();
				System.out.println("New client connected");
				ClientRunner client = new ClientRunner(clientSocket,this);
				clients.add(client);
				new Thread(client).start();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void waitPlayerStage() {
		System.out.println("waitPlayerStage");
		this.model.restart();  //reset model
		addWaitPlayerToGame();	//add waiting players to the game	
		this.updateGameGUI();	//update game gui
		setAndUpdateValidPlayersStage(0);	//active players' ready button
		this.gameStart=false;
		
	}
	
	//triggered by all client 
	public synchronized void startGame() {
		System.out.println("startGame");
		this.gameStart=true;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.model.resetPlayerReady();
		this.updateGameGUI();
		if(!this.model.getDealerExist()) {
			this.decideDealerStage();
		}else {
			this.dealTwoCardStage();
		}
	}
	
	
	
	//deal ace to decide dealer
	public synchronized void decideDealerStage() {
		System.out.println("decide dealer");
		if(!this.model.playerEnough()) {	// player not enough
			System.out.println("decide dealer can't play");
			setAndUpdateValidPlayersStage(0);	//reset players' button
		}else {
			System.out.println("decide dealer can play");
			gameStart=true;
			setAndUpdateValidPlayersStage(-1);
			this.model.setDecideDealerInfo();
			int playerId =  this.model.getFirstPlayerToDecideDealer();
			if(playerId==-1) {
				setAndUpdateValidPlayersStage(0);
			}

			this.updateGameGUI();
			while(true) {				
				try {
					Thread.sleep(300);				// deal a card 0.3s
					if(!this.model.playerEnough()) { //If the game does not have enough player
						gameStart=false;
						break;
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String [] cardInfo = modelDealACard(playerId);
				updateClientCardTable();
				if(cardInfo==null) {	//if the player is null
					playerId= this.model.getNextValidPlayerIndex(playerId);		//get the next id			
					
				}else {
					if(cardInfo[1].equals("1")) {	//If the player deals a Ace break the loop
						break;
					}
					
				}
				playerId= this.model.getNextValidPlayerIndex(playerId);		//get the next id			
			}
			if(gameStart) {
				this.model.setDealerIndex(playerId);
				this.updateGameGUI();
				try {
					Thread.sleep(4000);			//wait 4s to show dealer result		
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				model.clearPlayersCard();
				System.out.println("ClearPlayerCard");
				this.updateGameGUI();
				dealTwoCardStage();
			}
			else { 		//see above. If the game does not have enough player 
				this.waitPlayerStage();	//set all player to waiting stage
			}			
		}
		
	}
	
	//deal two cards for each player
	public synchronized void dealTwoCardStage() {
		System.out.println("dealTwoCardStage");

		if(!this.model.playerEnough()||!this.model.getDealerExist()) {	
			this.waitPlayerStage();
		}
		//only do if dealer exist and players are enough
		else {
			//disable all players' button
			setAndUpdateValidPlayersStage(-1);
			model.clearPlayersCard();
			System.out.println("ClearPlayerCard");
			try {
				Thread.sleep(100);
				updateClientCardTable();

				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//setup the information of dealing card 
			this.model.setFirstDealCardInfo();
			//get the player's id next to the dealer
			int nextPlayer = this.model.getNextValidPlayerIndex(this.model.getDealerIndex());
			System.out.println("startdealtwocard");
			
			//dealer two card for each player
			for(int turn = 0 ; turn<this.model.getNumOfPlayer()*2 ; turn++) {
				//if players are enough and game have a dealer
				if(this.model.playerEnough()&&this.model.getDealerExist()) {
					modelDealACard(nextPlayer);
					updateClientCardTable();
					nextPlayer= this.model.getNextValidPlayerIndex(nextPlayer);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					this.waitPlayerStage();
				}
			}
			

			this.updateGameGUI();
			//if game can continue 
			if(this.model.playerEnough()&&this.model.getDealerExist()) {	
				this.model.getFirstStageResult();	//get the result of players' hand value
			}else {
				//otherwise restart the game
				this.waitPlayerStage();
			}
			System.out.println("gameStart First round game result" + this.model.getTypeOfNatrualGameResult());
			this.updateGameGUI();
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//do things in different result
			if(this.model.getTypeOfNatrualGameResult()==1) {
				System.out.println("gameStart Firstdeal dealer win stat=1");
				gameEndStage();
			}else if(this.model.getTypeOfNatrualGameResult()==2) {
				if(!this.model.getWinnerIndex().isEmpty()&&this.model.getWinnerIndex().size()==1) {
					System.out.println("gameStart Firstdeal one player get 21 stat=2");
					gameEndStage();
				}
			}else if(this.model.getTypeOfNatrualGameResult()==3) {
				System.out.println("gameStart Firstdeal more players get 21 stat=3");

				gameEndStage();
			}else if(this.model.getTypeOfNatrualGameResult()==4) {
				System.out.println("gameStart Firstdeal no players get 21  stat=4");
				//enable deal and stand button for the player next to the dealer
				this.playerIDMap.get(this.model.getCurrentPlayerIndex()).setStage(1);
				this.playerIDMap.get(this.model.getCurrentPlayerIndex()).updateStage();
			}
		}
	}
	
	
	//deal a card
	protected  synchronized String[] modelDealACard(int playerID) {
		String[] cardInfo =this.model.dealACard(playerID);		
		return cardInfo;
	}


	
	//put a header in front of an instruction
	public String[] instruction(String f, String s) {
		String tem [] = new String [2];
		tem[0] = f;
		tem[1] = s;
		return tem;
	}
	
	//put a header in front of an instruction
	public String[] instruction(String f, int s) {
		String tem [] = new String [2];
		tem[0] = f;
		tem[1] = Integer.toString(s);
		return tem;
	}
	
	
	
	public void transitMainInfo() {
		this.transmit(this.instruction("MInfo", this.model.getGameMainInfo()));
	}
	
	//base transmit method
	public void transmit(String [] s) {
		for(ClientRunner c: clients) {
			if(c != null) {
//				System.out.println("transit to player "+c.playerID);
				c.transmitMessage(s);
			}
		}
	}
	
	
	//base transmit method for the list table
	public void transmitTable(CardTable t) {
		for(ClientRunner c: clients) {
			if(c != null) {
				c.transmitMessage(t);
			}
		}
	}
	
	//put a header in front of an array	
	public void transmitArray(String name, int [] a) {
		String [] s = new String[a.length+1];
		s[0] = name;
		for(int i = 1 ; i<s.length; i++) {
			s[i] = Integer.toString(a[i-1]);
		}
		this.transmit(s);
	}
	
	//put a header in front of an array		
	public void transmitArray(String name, String [] a) {
		String [] s = new String[a.length+1];
		s[0] = name;
		for(int i = 1 ; i<s.length; i++) {
			s[i] = a[i-1];
		}
		this.transmit(s);
	}
	
	
	public void transmitArray(String name, boolean [] a) {
		String [] s = new String[a.length+1];
		s[0] = name;
		for(int i = 1 ; i<s.length; i++) {
			s[i] = (a[i-1]==true) ? "true": "false" ;
		}
		this.transmit(s);
	}
	
	
	//update all players' GUI at once
	public synchronized void updateGameGUI() {
		this.transmit(this.model.getPlayersName());
		this.transmitTable(new CardTable(this.model.getPlayersHandCard()));
		this.transmitArray("Stakes", this.model.getPlayersStakes());
		this.updateClientGameMainInfo();
		this.updateClientGameSubInfo();
		this.updateClientBurst();
		this.updateClientReady();
		this.updateClientBlackJack();
		updateClientHandValue();
		this.updateClientStatus();
		this.updateClientDealer();
	}
	
	public void updateClientGameMainInfo() {
		this.transmit(this.instruction("MInfo", this.model.getGameMainInfo()));
	}
	
	public void updateClientGameSubInfo() {
		this.transmit(this.instruction("SInfo", this.model.getSubInfo()));
	}
	
	public void updateClientHandValue() {
		this.transmitArray("HandValue", this.model.getPlayerHandValueArray());
	}
	
	public void updateClientBurst() {
		this.transmitArray("Burst", this.model.getBurstArray());
	}
	
	public void updateClientBlackJack() {
		this.transmitArray("BlackJack", this.model.getBackJackArray());
	}
	
	public void updateClientReady() {
		this.transmitArray("Ready", this.model.getReadyArray());
	}
	
	public void updateClientCardTable() {
		this.transmitTable(new CardTable(this.model.getPlayersHandCard()));
	}
	
	public void updateClientStatus() {
		System.out.println("updateGUIStatus");
		this.transmitArray("Status", this.model.getPlayerStatusArray());
	}
	

	public void updateClientDealer() {
		transmitArray("Dealer", this.model.getDealer());
	}
	
	
	
	public void setAndUpdateValidPlayersStage(int stage) {
		boolean[] validPlayerIDInGame = this.model.getPlayerIsInGame(); 
		for(int i = 0 ; i< validPlayerIDInGame.length ; i++) {
			if(validPlayerIDInGame[i]==true) {
				this.playerIDMap.get(i).stage=stage;
				this.playerIDMap.get(i).updateStage();
			}
		}
	}
	

	
	
	public void updateValidPlayersStage() {
		boolean[] validPlayerIDInGame = this.model.getPlayerIsInGame(); 
		for(int i = 0 ; i< validPlayerIDInGame.length ; i++) {
//			System.out.println(validPlayerIDInGame[i]);
			if(validPlayerIDInGame[i]==true) {			
				System.out.println(i+" alive");
				this.playerIDMap.get(i).updateStage();
			}
		}
	}
	

	public synchronized void addWaitPlayerToGame() {
		System.out.println("addWaitPlayerToModel");
		//only add players in queue if the game has not started
		if(!gameStart&&!this.waitList.isEmpty()) {
			System.out.println("addWaitPlayerToModel true");
			System.out.println("waitlist :"+this.waitList.size());
			//add players if there is a space
			while(!waitList.isEmpty()) {				
				ClientRunner newplayer = this.waitList.get(0);
				System.out.println(newplayer.name);
				//get a id from model
				int newPlayerID = this.model.addPlayer(newplayer.name);
				System.out.println(newPlayerID);
				//if successfully added
				if(newPlayerID!=-1) {
					newplayer.playerID= newPlayerID;
					//mapping id to player in server
					this.playerIDMap.put(newplayer.playerID, newplayer);
					//send id to client
					newplayer.setAndSendPlayerID(newPlayerID);
					//enable client's ready button
					newplayer.readyStage();	//provide ready button when game has not start
					newplayer.updateStage();
					//remove player from waiting list
					this.waitList.remove(0);
					System.out.println("addWaitPlayerToModel player "+ newPlayerID+ " added");
					this.updateGameGUI();
				}else {
					break;
				}
			}

		}
	}
	
	
	

	public synchronized void gameEndStage() {
		System.out.println("gameEndStage");
		this.updateGameGUI();
		//get final game result
		String [] balance = this.model.getFinalGameResult();
		//remove players who lose all stakes
		removeLoseClient();
		System.out.println("gameEndStage remove lose client");	
		this.updateClientStatus();
		this.updateGameGUI();		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.gameStart=false; //reset game to no start
		this.setAndUpdateValidPlayersStage(-1); // disable all button
		System.out.println("gameEndStage update valid player stage");
		this.model.setGameEndInfo();
		this.updateGameGUI();
		//restart the game
		this.waitPlayerStage();

	}
	
	
	public synchronized void  removeLoseClient() {
		boolean[] validPlayerIDInGame = this.model.getPlayerIsInGame(); 
		for(int i = 0 ; i< validPlayerIDInGame.length ; i++) {
			//if the player in model is false and client in the map is not null
			if(validPlayerIDInGame[i]==false&&playerIDMap.get(i)!=null) {			
				System.out.println("removeLoseClient playerID: "+i);
				
				//update gui of the lose player
				playerIDMap.get(i).kickStage();
				playerIDMap.get(i).updateStage();
				System.out.println("removeLoseClient updateStage kickStage"+i);
				//set lose player's id to -1
				playerIDMap.get(i).playerID=-1;
				this.playerIDMap.get(i).transmitMessage(this.instruction("SInfo","You are kicked!"));
				//remove this client in server
				this.clients.remove(playerIDMap.get(i));
				//remove this client from map
				playerIDMap.remove(i);

			}
		}
	
	}

	private class ClientRunner implements Runnable {
		private Socket s = null;
		private Server parent = null;
		private ObjectInputStream inputStream = null;
		private ObjectOutputStream outputStream = null;
		private int playerID =-1;
		protected int stage=-1;
		protected String name="";
		public ClientRunner(Socket s, Server parent) {
			this.s = s;
			this.parent = parent;
			try {
				outputStream = new ObjectOutputStream(this.s.getOutputStream());
				inputStream = new ObjectInputStream(this.s.getInputStream());

			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public void setStage(int i) {
			
			this.stage=i;
		}
	
		public void run() {
			// receive messages
			String  [] s=null;
			try {
				while((s = (String[])inputStream.readObject())!= null)
				{	
					System.out.println("server receive");
					for(String str : s) {
						System.out.print(str);
					}
					System.out.println();
					// receive quit info from client
					if(s[0].equals("Quit")) {
						try {
							this.outputStream.close();
							inputStream.close();
							this.s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//receive client's name			
					else if(s[0].equals("Name")) {
						this.name=s[1];
						// add player to wait list
						this.parent.waitList.add(this);
						System.out.println("waitlist add one");
						//if the game has not started
						if(!this.parent.gameStart) {
							//add player to the model
							this.parent.addWaitPlayerToGame();
						}
					//receive client want to loaded gui
					}else if(s[0].equals("Load")) {
						this.stage=0; //disable player all buttons		
						this.updateStage();
						this.parent.updateGameGUI(); 
					}
					//receive client is ready
					else if(s[0].equals("Ready")) {
						this.parent.model.setPlayerReady(this.playerID, true);	
						this.defaultStage();
						this.updateStage();
						this.parent.updateClientReady();
						this.parent.updateClientStatus();
						//if all players are ready and game has not started
						if(this.parent.model.canStartGame()&&!this.parent.gameStart) {
							this.parent.updateGameGUI();				
							this.parent.startGame();
						}else {
							this.parent.updateGameGUI();							
						}
					//receive client click deal button
					}else if(s[0].equals("Deal")) {
						this.parent.modelDealACard(this.playerID);
						System.out.println("deal player deals a card");
						
						
						int dealResult= this.parent.model.getDealResult(playerID);
						if(dealResult==1) {	//player under 21 can deal
							this.parent.updateValidPlayersStage();
							System.out.println("deal under 21");
							updateGameGUI();
						}
						else if(dealResult==2) { //player got 21 
							this.stage=-1;	//disable player all buttons	
							this.parent.updateValidPlayersStage(); 
							updateClientBlackJack();
							 //inform all player this player got a 21
							updateGameGUI();
							try {
								Thread.sleep(2000); //wait 3 seconds
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("deal got 21");
							updateClientBurst();
							
							activeNextPlayer(playerID);
						}
						else if(dealResult==-1) { //player got bursted and pay stakes to the dealer
							this.stage=-1;	//disable player all buttons							
							this.parent.updateValidPlayersStage(); 
							updateClientBurst();
							updateGameGUI(); //inform all player this player has bursted
							try {
								Thread.sleep(2000); //wait 3 seconds
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("deal got bursted");
							activeNextPlayer(playerID);
						}
					}else if(s[0].equals("Stand")) {
						this.stage=-1; //disable player all buttons			
						this.parent.model.setPlayerStandInfo(playerID);
						this.parent.updateValidPlayersStage(); 
						updateGameGUI(); //inform all player this player stood
						try {
							Thread.sleep(2000); //wait 3 seconds
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("stand");
						activeNextPlayer(playerID);		
					}
				}
				
				inputStream.close();
			}catch(ClassNotFoundException e) {
				e.printStackTrace();
			}catch(IOException e) {
				e.printStackTrace();
				System.out.println("Player " + playerID + "disconnect");
				//if socket is close
				updatePlayerLeft();
			}catch(Exception e) {
				e.printStackTrace();
			}			
			finally {
														
			}			
		}
		
		
		public synchronized void activeNextPlayer(int playerID) {
			System.out.println("active next player");
			int nextId = this.parent.model.getNextPlayerDealIndex(playerID); //get the next available player's id in game
			System.out.println("active next player next id :" +this.parent.model.getNextPlayerDealIndex(playerID));
			if(nextId!=-1) {
				this.parent.playerIDMap.get(nextId).stage=1; //enable next player can deal
				System.out.println("active next player stage: "+this.parent.playerIDMap.get(nextId).stage);
				this.parent.updateValidPlayersStage(); //send stage instruction to all client
				this.parent.transitMainInfo(); //inform all player the next player
				System.out.println("active next player 2");
			}else {
					
				gameEndStage();
			}
		}
			
	
		public void transmitMessage(String []s) {
			try {
			
				outputStream.writeObject(s);
			}catch(IOException e) {
				//if socket is close
				e.printStackTrace();
				updatePlayerLeft();				
			}
		}
		
		
		public void transmitMessage(CardTable t) {
			try {
				outputStream.writeObject(t);
				
			}catch(Exception e) {
				//if socket is close
				e.printStackTrace();
				updatePlayerLeft();
			}

		}
		public void kickStage() {
			this.stage = -2;
		}
		
		
		public void readyStage() {
			this.stage = 0;
		}
		
		public void dealStage() {
			this.stage = 1;
		}
		
		public void defaultStage() {
			this.stage = -1;
		}
		
		public synchronized void updatePlayerLeft() {
			//if the player is in the game
			if(this.playerID>-1) {
				//remove player in the map
				playerIDMap.remove(playerID);
				System.out.println("updatePlayerLeft playerID>-1 playerID:"+playerID);
				//remove player in the game model
				this.parent.model.removePlayer(playerID);
				//remove client in the list
				this.parent.clients.remove(this);
				this.parent.transmit(this.parent.instruction("SInfo", this.parent.model.getSubInfo()));
				this.playerID=-1;
				this.parent.updateGameGUI();				
				//if the player is not enough or dealer exist the game
				if(!this.parent.model.playerEnough()||!this.parent.model.getDealerExist()) {
					//game restart
					this.parent.waitPlayerStage();
				//if its the client's turn and the client disconnect 
				}else if(this.stage==1){
					System.out.println("updatePlayerLeft stage=-1");
					//activate next player
					this.activeNextPlayer(this.playerID);
				}
				//if the game has not started
				if(!this.parent.gameStart) {
					System.out.println("updatePlayerLeft addWaitPlayerToModel");
					//add player in queue to model
					this.parent.addWaitPlayerToGame();
				}
				//close stream
				try {
					this.inputStream.close();
					this.outputStream.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		
		protected synchronized void updateStage() {
			String[] message = new String[2];
			message[0]="Stage";
			message[1] = Integer.toString(stage);
			this.transmitMessage(message);
		}
	
		
		protected synchronized void setAndSendPlayerID(int id) {			
				String[] playerID= new String[2];
				playerID[0]="PlayerID";
				playerID[1]=Integer.toString(id);
				this.playerID=id;
				System.out.println("send playerid player has" + this.playerID);
				transmitMessage(playerID);											
				this.parent.updateGameGUI();

		}
	

	}


}