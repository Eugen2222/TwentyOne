
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameModel {
	private List<Deck> decks;	//Deck to store and shuffle the cards
	private List<Player> players; //Store players
	private List<Card> cards;	//Store 52 cards
	private int antes;	
	private int dealerIndex;	//store dealer index
	private int typeOfNatrualGameResult; //store game result

//	private boolean [] playerInGame;
	private String [] playerStatus; //store game result
//	private int [] playerStakesBalance;	
	private List<Integer> winnersIndex; //store winner in a round result
	private String gameMainInfo;	//store main info
	private String gameSubInfo;		//store sub info
	private boolean [] burstArray;	//store burst info
	private boolean [] blackJackArray;	//store blackjack info
	private int currentPlayer;
	final private int maxPlayer = 5; 	//store max player number
	
	
	//init game
	public GameModel() {
		this.decks=  new ArrayList<Deck>();
		this.players = new ArrayList<Player>(maxPlayer);
		this.cards = new ArrayList<Card>();
		for(int i =0; i<maxPlayer ; i++) {
			this.players.add(null);
		}

		playerStatus=new String[maxPlayer];
		burstArray=new boolean[maxPlayer];
		blackJackArray=new boolean[maxPlayer];
		winnersIndex =new LinkedList<Integer>();
		antes=1;
		dealerIndex=-1;
		typeOfNatrualGameResult=0;

		buildCards();
		decks.add(new Deck());
		decks.get(0).setCards(this.cards);
		gameMainInfo= "";

	}
	

	
	//restart game	
	public void restart() {
		antes=1;
		typeOfNatrualGameResult=0;
		decks.clear();
		decks.add(new Deck());
		decks.get(0).setCards(this.cards);
		gameMainInfo= "Waiting players to ready.";
		clearPlayersCard();
		this.cleanPlayerStatus();

		resetBurstArray();
		resetBackJackArray();
		resetPlayerReady();
	}
	
	//clear players card
	public void clearPlayersCard() {	//clear all players card
		for(int i=0 ; i<this.players.size();i++) {
			if(this.players.get(i)!=null) {
				this.players.get(i).clearCard();
			}
		}
	}
	
	
	public void buildCards() {
		List<String> color = new ArrayList<String>();
		//use abcd indicate card color
		color.add("a");
		color.add("b");
		color.add("c");
		color.add("d");
		
		for(String c : color) {
			for(int j = 1; j<14 ; j++) {
				String index = c + j; //file name is color + number
				
				if(j==1) {
					cards.add(new Card(c, j, 11, index, true));
				}
				else if(j>9) {
					cards.add(new Card(c, j, 10, index, false));
				}else {
					cards.add(new Card(c, j, j, index, false));
				}
			}
		}		
	}
	
	//return valid players in the game	
	public boolean[] getPlayerIsInGame() {
		boolean [] playersInGame = new boolean [this.maxPlayer];
		for(int i = 0 ; i<playersInGame.length;i++) {
			if(this.players.get(i)==null) {
				playersInGame[i]=false;
			}else {
				playersInGame[i]=true;
			}
		}
		return playersInGame;
	}
	
	
	//return valid players status string	
	public String[] getPlayerStatusArray() {
		return this.playerStatus;
		
	}
	
	//return valid players hand value
	public String[] getPlayerHandValueArray() {
		String [] s = new String[players.size()];
		for(int i = 0 ; i< players.size() ; i++) {
			if(players.get(i)==null) {
				s[i] = " ";				
			}
			else {
				s[i] = Integer.toString(players.get(i).getValue());
			}
		}
		return s;
	}
	//clear players status
	public void cleanPlayerStatus() {
		for(int i = 0 ; i< this.playerStatus.length;i++) {
			this.playerStatus[i] = " ";
		}
	}
	
	//player deal a card
	public synchronized String[]  dealACard(int playerID) {
		System.out.println("player "+playerID+ " deal a card");
		if(this.decks.get(0).cardList.isEmpty()) {
			this.decks.get(0).setCards(cards);
		}
		if(this.players.get(playerID)!=null) {
			String[] tem = new String[2];
			Card newC = this.decks.get(0).pop();
			tem[0]=newC.getIndex();
			tem[1]=(newC.isAce())? "1":"0";
			this.players.get(playerID).addCard(newC);
			this.players.get(playerID).addValue(newC.getActualValue());
			return tem;
		}
		return null;
	}
	

	
	
	
	
	public void setPlayerReady(int playerID, boolean input) {
		this.players.get(playerID).setReady(input);
	}
	
	
	public String[] getReadyArray() {
		String [] s = new String [this.maxPlayer]; 
 		for(int i = 0 ; i<players.size() ; i++) {
			if(players.get(i)==null||!players.get(i).getReady()) {
				s[i]="false";
			}else {
				s[i]="true";
			}
		}
		return s;
	}
	
	
	public boolean playerEnough() {
		int numOfPlayers = 0;
		for(int i = 0 ; i< players.size() ; i++) {
			if(players.get(i)!=null) {			
					numOfPlayers++;
			}
		}
		if(numOfPlayers<2) {
			System.out.println("model canPlayGame  numOfPlayer<2");
			this.gameMainInfo="Not enough players, game restarts!";
			return false;	
		}else {
			System.out.println("model canPlayGame numOfPlayer=>2");
			return true;
		}
	}
	
	
	
	//all players are ready and have enough players
	public boolean canStartGame() {
		int numOfPlayers = 0;
		System.out.println("model  canStartGame");

		for(int i = 0 ; i< players.size() ; i++) {
			if(players.get(i)!=null) {				
				if(!players.get(i).getReady()) {
					System.out.println("model canStartGame"+i+" player not ready");
					this.gameMainInfo="Waiting for players to ready!";
					return false;
				}else {
					numOfPlayers++;
				}
			}
		}
		if(numOfPlayers<2) {
			System.out.println("model  numOfPlayer<2");
			this.gameMainInfo="Not enough players, game restarts!";
			return false;	//first check player number
		}else {
			this.gameMainInfo="Ready to start the game!";
			System.out.println("model  numOfPlayer=>2");
			return true;
		}
	}
	
	
	public int getCurrentPlayerIndex() {
		return this.currentPlayer;
	}
	public int getNextPlayerDealIndex(int lastPlayerID) {		
		if(this.dealerIndex==lastPlayerID) {
			return -1;
		}else {
			currentPlayer=this.getNextValidPlayerIndex(lastPlayerID);
			return this.currentPlayer;
		}
		
	}
	
	public boolean[] getBurstArray() {		
		return this.burstArray;
		
	}
	
	public boolean[] getBackJackArray() {		
		return this.blackJackArray;
		
	}
	
	public boolean getPlayerReady(int playerID) {
		return this.players.get(playerID).getReady();
	}
	
	public void resetPlayerReady() {
 		for(int i = 0 ; i<players.size() ; i++) {
			if(players.get(i)==null) {
			}else {
				players.get(i).setReady(false);
			}
		}
	}
	
	public void resetBurstArray() {
		for(int i = 0; i<this.burstArray.length;i++) {
			this.burstArray[i]=false;
		}
		
	}
	
	public void resetBackJackArray() {		
		for(int i = 0; i<this.blackJackArray.length;i++) {
			this.blackJackArray[i]=false;
		}		
	}
	
	
	public int getNumOfPlayersAce(int playerID) {
		return this.players.get(playerID).getNumOfAce();
	}
	
	
	public void setAceValue(int playerID, int v) {
		this.players.get(playerID).reduceValue(11);
		this.players.get(playerID).addValue(v);
	}
	
	public void playerStand(int playerID) {
		this.gameMainInfo = this.getPlayerString(playerID) + "standeds.";
		this.currentPlayer=this.getNextValidPlayerIndex(playerID);
	}
	
	public void playerDeal(int playerID) {
		this.gameMainInfo = this.getPlayerString(playerID) + "deals a card.";
		this.currentPlayer=this.getNextValidPlayerIndex(playerID);
	}
	
	public int getDealResult(int playerID) {	
		if(this.players.get(playerID).getValue()<21) {
			this.gameMainInfo= this.getPlayerString(playerID) + " deals a card";
			return 1;
		}else if(this.players.get(playerID).getValue()==21) {
			blackJackArray[playerID]=true;
			this.gameMainInfo= this.getPlayerString(playerID) + "has a vingt-un!";
			return 2;
		}else {
			
			Player player = this.players.get(playerID);
			int numOfAce = player.getNumOfAce();			
			if(numOfAce>0) {
				for(int i = 0;i<numOfAce ; i++) {
					player.setAnAceToOne();
					System.out.println("getDealResult switch 1 ace");
					if(player.getValue()<=21) {
						return getDealResult(playerID);
					}
				}
			}
			
			//set player burst
			burstArray[playerID]=true;
			if(playerID==this.dealerIndex){		
				//if dealer got burst.	
				this.gameMainInfo="Oh no, the dealer"+ this.getPlayerString(playerID) + " has bursted! .";
				return -1;
			}			
			else {
				int stakes = 1;
				payStakesToDealer(playerID, stakes);
				this.gameMainInfo="Oh no, "+ this.getPlayerString(playerID) + " has bursted! " + this.getPlayerString(playerID)+
						"pays "+ stakes + " stakes to the dealer.";	
				playerStatus[playerID]= "Lose -"+ stakes+" stakes.";
				return -1;
				
			}
		}
	}
	
	public void payStakesToDealer(int playerID, int stakes) {
		this.players.get(playerID).reduceStakes(stakes);
		this.players.get(this.dealerIndex).addStakes(stakes);
		
		
	}
	
	
	
	public String[] testDealCard(int playerID, int cardID) {
		String[] tem = new String [2];
		Card newC = this.cards.get(cardID);
		tem[0]=newC.getIndex();
		tem[1]=(newC.isAce())? "1":"0";
		System.out.println(this.players.get(playerID).getName() + " deal " + newC.getActualValue());
		this.players.get(playerID).addCard(newC);
		this.players.get(playerID).addValue(newC.getActualValue());
		return tem;		
	}
	
	
	
	
	public List<List<String>> getPlayersHandCard(){
		List<List<String>> tem = new ArrayList<List<String>>();
		for(Player p : this.players) {
			if(p==null) {tem.add(null);}
			else {
			tem.add(p.getCardsIndex());}
		}
		return tem;
	}


	
	public boolean isDealer(int playerid) {
		return (this.dealerIndex==playerid)? true:false;
	}
	
	
	public String getGameMainInfo() {
		return this.gameMainInfo;
	}
	
	public int getPlayerStatus(int i) {
		if(this.players.get(i)==null) {
			return -1;
		}else {
			return this.players.get(i).getStatus();
		}
	}
	
	public int getPlayerStakes(int i) {
		if(this.players.get(i)==null) {
			return -1;
		}else {
			return this.players.get(i).getStakes();
		}
		
	}
	
	public int getNumOfPlayer() {
		int num=0;
		for(Player p : players) {
			if(p!=null) {
				num++;
			}
		}
		return num;
	}
	
	
	
	
	public int getPlayerStakesOnBoard(int i) {
		if(this.players.get(i)==null) {
			return -1;
		}else {
			return this.players.get(i).getStakesOnBoard();
		}
	}
	
	public int getFirstPlayerToDecideDealer() {
		for(int i = 0; i<this.players.size();i++) {
			if(players.get(i)!=null) {
				return i;
			}
		}
		return -1;
	}
	
	
	public int getNextValidPlayerIndex(int lastPlayerIndex) {
		int i = lastPlayerIndex;
		
		while(this.players.size()>1) {
			

			if(i+1==this.players.size()) {
				i=-1;
			}
			else if(this.players.get(i+1)==null) {
				i++;
			}
			else if(this.players.get(i+1)!=null){
				this.gameMainInfo="It's "+ this.getPlayerString(i+1) + "'s turn";
				return i+1;
			}else {
				return -1;
			}
		}	
		return -1;
	}
	
	public int getTypeOfNatrualGameResult() {
		return this.typeOfNatrualGameResult;
	}
	
	public int getDealtResult(int playerId) {
		if(this.players.get(playerId).getValue()>21&&playerId!=this.dealerIndex) { //check dealer later
			this.players.get(dealerIndex).addStakes(1);
			this.players.get(playerId).reduceStakes(1);
//			this.playerInGame[playerId]=false;
			return -1;
		}
		else return 0;
	}
	
	public List<Integer> getWinnerIndex(){
		return this.winnersIndex;
	}
	

	
	public void getFirstStageResult() {
		int [] playerStakes = new int[this.players.size()];
		int stakesFromLoser = 0;
		winnersIndex = new LinkedList<Integer>();
		for(Player p : this.players) {
			if(p==null) {}
			else {
				System.out.println(p.getName()+" cardpower "+ p.getValue() +" stakes " + p.getStakes());
			}
			
		}
		
		//if the deal got a 21
		if(this.players.get(this.dealerIndex).getValue()==21) {
			System.out.print("dealer"+this.players.get(this.dealerIndex).getValue());
			blackJackArray[this.dealerIndex]=true;
			this.typeOfNatrualGameResult=1;

			for(int i = 0; i<this.players.size() ; i++) {
				if(this.players.get(i)!=null&&i!=this.dealerIndex) {
					if(this.players.get(i).getValue()==21) {
	
						blackJackArray[i]=true;
						playerStatus[i]= "Draw";
					}else {

						this.players.get(i).reduceStakes(2);
						stakesFromLoser+=2;
						playerStatus[i]= "Lose "+ 2+" stakes.";
					}
				}else {

				}
			}			

			this.players.get(dealerIndex).addStakes(stakesFromLoser);
			gameMainInfo= "The winner is Dealer " + getPlayerString(dealerIndex) +".";

		}
		else {
			
			//find the players who got a 21
			for(int i = 0; i<this.players.size() ; i++) {
				if(this.players.get(i)!=null&&i!=this.dealerIndex) {
					if(this.players.get(i).getValue()==21) {
						winnersIndex.add(i);
					}
				}
			}
			
			//has any player who got 21
			if(!winnersIndex.isEmpty()&&winnersIndex.size()>0) {
				for(int i = 0; i<this.players.size() ; i++) {
					if(this.players.get(i)!=null&&i!=this.dealerIndex) {
						if(this.players.get(i).getValue()==21) {
							
							
						//collect stakes from everyone who did not get 21								
						}else if(this.players.get(i).getValue()<21) {
							this.players.get(i).reduceStakes(2);
							stakesFromLoser+=2;
							playerStatus[i]= "Lose "+ 2+" stakes.";;
						}
					}
				}	
			}
						
			//if only one player got 21
			if(!winnersIndex.isEmpty()&&winnersIndex.size()==1) {
				//get stakes from losers
				this.players.get(winnersIndex.get(0)).addStakes(stakesFromLoser);
	
				//set winner to be the dealer
				this.setDealerIndex(winnersIndex.get(0));
				this.typeOfNatrualGameResult=2;
				//update game info
				gameMainInfo= "The winner is "+ getPlayerString(winnersIndex.get(0)) +". " 
				+ getPlayerString(winnersIndex.get(0))+
						"is the new dealer.";
				playerStatus[winnersIndex.get(0)]= "Win +"+ stakesFromLoser+" stakes.";
			}
			//if more than one player got 21
			else if(winnersIndex.size()>1) {

				//first winner get stakes from losers
				this.players.get(winnersIndex.get(0)).addStakes(stakesFromLoser);
				//update game info			
				gameMainInfo= "The winners are ";
				for(int i=0 ; i< this.winnersIndex.size();i++) {
					if(i== this.winnersIndex.size()-1) {
						gameMainInfo += getPlayerString(winnersIndex.get(i))+".";
					}else {
						gameMainInfo += getPlayerString(winnersIndex.get(i)) + ", ";
					}					
				}
				playerStatus[winnersIndex.get(0)]= "Win +"+ stakesFromLoser+" stakes.";				
				this.typeOfNatrualGameResult=3;
			}else {
				//no player got 21, move to next stage
				//the turn start with the player next to the dealer.
				int nextPlayerId = this.getNextValidPlayerIndex(this.dealerIndex);
				gameMainInfo = "It's " + getPlayerString(nextPlayerId)+ "'s turn.";
				this.currentPlayer=nextPlayerId;
				typeOfNatrualGameResult=4;
			}

		}
	}
	
	public String getPlayerString(int index) {
		if(this.players.get(index)!=null) {
			return this.players.get(index).getName() + " (Player" + (index+1) +")";	
		}else {
			return "Error, player "+index +"does not exsit!";
		}

	}

	
	public int[] getPlayersStakes(){
		int [] tem = new int [this.players.size()];
		System.out.println("getPlayersStakes");
		for(int i = 0; i<this.players.size();i++) {			
			if(this.players.get(i)==null) {
				tem[i]=0;
			}else {
				tem[i]=this.players.get(i).getStakes();
			}

		}
		return tem;
	}
	
	public void setDealerIndex(int n) {
		this.dealerIndex=n;
		this.gameMainInfo="The dealer is player "+ (n+1);
	}
	
	public String[] getDealer() {
		String[] s = new String [this.maxPlayer];
		for(int i = 0 ; i<s.length ; i++) {
			s[i]=" ";
		}
		if(this.dealerIndex<0) {}
		else {
			s[this.dealerIndex]="Dealer ";
		}
		return s;
	}
	
	public void setDecideDealerInfo() {	
		this.gameMainInfo= "Decide a dealer.";
	}
	
	public void setFirstDealCardInfo() {
		this.gameMainInfo= "Start dealing card!";
	}
	
	public void setPlayerDealInfo(int playerId) {
		gameMainInfo = this.getPlayerString(playerId)+ " deals a card";
	}
	
	public void setPlayerStandInfo(int playerId) {
		gameMainInfo = this.getPlayerString(playerId)+ " stands a card";
	}
	
	
	public void setWaitPlayerInfo() {
		gameMainInfo = "Waiting for ready players. Required more than two players to play.";
	}
	
	public void setGameEndInfo() {
		
		this.gameMainInfo= "Game is over! Press ready to start the next round.";
	}
	
//	public boolean getGameOver() {
//		return this.gameOver;
//	}
	
	public String[] getFinalGameResult() {
		int stakes=1;
		int stakesFromLoser=0;
		int dealerValue = this.players.get(this.dealerIndex).getValue();
		System.out.println("Dealer value "+dealerValue);
		if(dealerValue>21) {
			for(int i =0; i<this.players.size(); i++) {
				if(this.players.get(i)!=null&&i!=this.dealerIndex) {
					if(this.players.get(i).getValue()>21) {
						
					}else {
						this.players.get(dealerIndex).reduceStakes(stakes);
						this.players.get(i).addStakes(stakes);
						stakesFromLoser+=stakes;
						playerStatus[i]= "Win "+ stakes +" stakes.";
					}
				}
			}
			this.typeOfNatrualGameResult=1;
			playerStatus[dealerIndex]= "Lose "+ stakesFromLoser +" stakes.";

		}
		else if (dealerValue<=21) {
			for(int i =0; i<this.players.size(); i++) {
				if(this.players.get(i)!=null&&i!=this.dealerIndex) {
					if(this.players.get(i).getValue()>21) {
						
					}
					else if(this.players.get(i).getValue()<dealerValue) {
						stakesFromLoser+=stakes;
						this.players.get(i).reduceStakes(stakes);

						playerStatus[i]= "Lose "+ stakes +" stakes.";
					}
					else if(this.players.get(i).getValue()>dealerValue) {
						stakesFromLoser-=stakes;
						this.players.get(i).addStakes(stakes);

						playerStatus[i]= "Win +"+ stakes +" stakes.";
					}else {

						playerStatus[i]= "Draw.";
					}					
				}else{
				}
			}
			
			this.players.get(dealerIndex).addStakes(stakesFromLoser);

			if(stakesFromLoser<0) {
				playerStatus[dealerIndex]= "Lose "+ (0-stakesFromLoser) +" stakes.";
			}
			else if(stakesFromLoser>0) {
				playerStatus[dealerIndex]= "Win + "+ stakesFromLoser +" stakes.";
			}
			

			typeOfNatrualGameResult=2;
		}
		kickOutPlayer(); //kickOut player who don't have any stakes
		return this.playerStatus;
	}
	
	
	
	
	public void kickOutPlayer() {
		this.gameSubInfo= "";
		for(int i =0 ; i<this.players.size() ; i++) {	
			//kickOut player who don't have any stakes
			if(players.get(i)!=null&&players.get(i).getStakes()<1) {
				this.gameSubInfo+= players.get(i).getName()+" lose all stakes and is kicked!\n ";
				this.players.set(i, null);
				this.blackJackArray[i]=false;
				this.burstArray[i]=false;
				
				if(this.dealerIndex==i) {
					this.dealerIndex=-2;
				}
				System.out.println("kickOutPlayer player"+(i));
			}	
		}
	}

	
	
	
	
	public String getSubInfo() {
		return this.gameSubInfo;
	}
	

	
	public int getDealerIndex() {
		return this.dealerIndex;
	}
	
	public boolean getDealerExist() {
		if(this.dealerIndex>-1) {
			return true;
		}
		
		return false;
	}
	
	public int addPlayer(String name) {
		int newPlayerId=-1;
		for(int i = 0; i<this.players.size();i++) {
			if(this.players.get(i)==null) {
				this.players.set(i, new Player(name));
				System.out.println("model add player "+ i+" join the game");
				newPlayerId= i;
				break;
			}		
		}
		if(newPlayerId==-1) {
			return -1;
		}
		gameSubInfo = getPlayerString(newPlayerId)+ " join the game.";
		return newPlayerId;
	}
	
	
	public void removePlayer(int index) {
		gameSubInfo=getPlayerString(index) + "has left.";
		this.players.set(index, null);
		this.blackJackArray[index]=false;
		this.burstArray[index]=false;
		this.playerStatus[index]=" ";
		if(this.dealerIndex==index) {
			this.dealerIndex=-2;
		}
		System.out.println("remove player"+(index+1));
	}
	
	
	public String[] getPlayersName() {
		 Player[] tem = new Player[players.size()];
		 players.toArray(tem);
		 String[] tem2= new String[players.size()+1];
		 for(int i =0 ;i<tem.length+1;i++) {
			 if(i==0) {tem2[i]="PlayersName";}
			 else if(tem[i-1]==null) {tem2[i]=null;}
			 else {
				 tem2[i]= tem[i-1].name;
			 }

		 }

		return tem2;
	}
	

	
	
	public void setNumOfDeck(int num) {
		for(int i =0 ; i< num ; i++) {
			Deck deck = new Deck();
			deck.setCards(this.cards);
			deck.shuffle();
			decks.add(deck);
		}
	}
	
}
