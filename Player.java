import java.util.ArrayList;
import java.util.List;

public class Player {
	final protected String name;
	protected List<Card> hand;
	protected int stakes;
	protected int totalcardValue;
	protected boolean lose;
	protected int stakesOnBoard;
	protected boolean ready;
	protected int numOfAce;
	
	public Player(String name) {
		hand = new ArrayList<Card>();
		this.name = name;
		stakes = 10;
		this.totalcardValue=0;
		stakesOnBoard=0;
		totalcardValue=0;
	}	
	
	public boolean getReady() {
		return this.ready;
	}
	
	public void setReady(boolean input) {
		this.ready=input;
	}
	
	public String getName() {
		return name;
	}
	public List<String> getCardsIndex(){
		List<String> tem = new ArrayList<String>();
		for(Card c : this.hand) {
			tem.add(c.getIndex());
		}
		return tem;
	}
	
	public void addCard(Card card) {
		hand.add(card);
		if(card.isAce()) {
			numOfAce++;
		}
	}
	
	public void setAnAceToOne() {
		addValue(1);
		this.reduceValue(11);
		numOfAce--;
	}
	
	
	
	public void clearCard() {
		hand.clear();
		this.totalcardValue=0;
		this.numOfAce=0;
	}
	
	
	public void reduceStakes(int num) {
		this.stakes -= num;
		
	}
	
	public void addStakes(int num) {
		this.stakes += num;
		
	}
	
	public void addValue(int num) {
		this.totalcardValue += num;
	}
	
	public void reduceValue(int num) {
		this.totalcardValue-=num;
	}

	public int getStakes() {
		return this.stakes;
		
	}
	
	public int getNumOfAce()
	{	
		return numOfAce;
	}
	public int getStakesOnBoard(){
		return this.stakesOnBoard;
	}
	
	public int getValue() {
		return this.totalcardValue;
		
	}
	
	
	
	public int getStatus() {
		if(this.totalcardValue<21) {
			return 0;
		}
		else if(this.totalcardValue>21) {
			return -1;
		}else {
			return 1;
		}
	}
	
	protected void setLose(boolean b) {
		this.lose=b;
	}
	
	protected boolean getLose() {
		return this.lose;
	}
}
