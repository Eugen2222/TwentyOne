
public class Card {
	final private String color;
	final private int cardValue;
	final private int actualValue;
    final private String index;
    final private boolean ace;
	
    public Card (String color, int cardValue, int actualValue, String index, boolean ace) {
    	this.color = color;
    	this.cardValue = cardValue;
    	this.actualValue = actualValue;
    	this.index = index;
    	this.ace = ace;
    }
    
    public String getColor() {
    	return color;
    }
    
    public int cardValue() {
    	return cardValue;
    }
    
    public int getActualValue() {
    	return actualValue;
    }
    
    public String getIndex() {
    	return index;
    }
    
    public boolean isAce() {
    	return ace;
    }
}
