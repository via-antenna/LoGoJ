import java.util.ArrayList;

public class Tokenizer {
	// This class receives a string of input and tokenizes it, space = delimiter
	public ArrayList<String> comms = new ArrayList<String>();
	
	public void Tokenize(String commands) {
		
		for (int i = 0; i < commands.length(); i++)
			if (commands.charAt(i) != ' ')	// If the current char isn't a space
				for (int j = i; j < commands.length(); j++)	// Enter a subloop that continues until it sees a space, then adds that string to the arraylist and breaks
					if (commands.charAt(j) == ' ')
					{
						comms.add(commands.substring(i, j));
						i = j;
						break;
					}
	}
	
	public ArrayList<String> getAll() {	// Returns the arraylist of tokens
		return comms;
	}
}