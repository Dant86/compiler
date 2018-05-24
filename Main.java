import java.util.*;
import java.lang.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        Tokenizer t = new Tokenizer(args[0]);
        List<Token> toks = t.tokenGen();
        Parser p = new Parser(toks);
        try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(args[1], true));
            p.parse(writer);
            writer.close();
        }
        catch(IOException e){
        	System.out.println("No filename found.");
        }
    }
}