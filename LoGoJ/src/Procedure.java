import java.util.ArrayList;
import java.util.*;

public class Procedure {
	private String name;	// The name of the procedure
	private ArrayList<String> args;	// The arguments (that is, the variable names)
	private ArrayList<Double> argVals;	// The values of the arguments (the actual values of the variables, in a one-to-one correspondence with the above args)
	private String text;	// The code of the procedure
	private StringTokenizer st;	// String tokenizer
	private String delim = " ";	// The delimiter
	
	public Procedure() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getText() {
		return text;
	}
	
	public int getNumArgs() {	// Returns the number of arguments the procedure has
		return args.size();
	}
	
	public void addArgVal(double d) {	// Adds an argument value
		argVals.add(d);
	}
	
	public Procedure (String s) {	// This constructor initializes a procedure
		args = new ArrayList<String>();
		argVals = new ArrayList<Double>();
		
		int wordCntr = 0;	// Used to determine whether or not the first token is being read (first token is always the name of the procedure)
		st = new StringTokenizer(s, delim);	// The string tokenizer
		String currWord;	// The current word
		text = "";	// Text of the procedure (the actual commands, minus arguments and the name, and minus 'end')
		
		while (st.hasMoreElements()) {
			currWord = st.nextToken();	// Get the next token
			if (wordCntr == 0)
				name = currWord;	// Name is always the first word
			else if (currWord.charAt(0) == ':')	// If it starts with a colon, it's an argument name, so add it to args
				args.add(currWord.substring(1));
			else	// Otherwise it's part of the procedure's command list
				text += currWord + " ";
			wordCntr++;	
		}
	}

	public String getArg(int i) {	// Returns the name of the argument at a certain index
		return args.get(i);
	}
}