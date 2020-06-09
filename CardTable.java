import java.io.Serializable;
import java.util.List;

public class CardTable implements Serializable {
	private List<List<String>> table;
	public CardTable(List<List<String>> table) {
		this.table = table;
	}
	public List<List<String>> getTable(){
		return this.table;
	}
}
