import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Deck {
	protected List<Card> cardList;
	
	public Deck() {
		cardList = new LinkedList<Card>();
	}
	
	protected void setCards(List<Card>cards) {
		this.cardList = new LinkedList<Card>(cards);
		shuffle();
	}
	
	protected void shuffle() {
		Collections.shuffle(cardList);
	}
	
	public int size() {
		if(!this.cardList.isEmpty()) {
			return this.cardList.size();
		}else {
			return 0;
		}
	}
	
	public Card pop() {
		if(!this.cardList.isEmpty()) {
			return this.cardList.remove(0);
		}else {
			return null;
		}
	}
}
