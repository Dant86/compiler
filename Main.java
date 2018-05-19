import java.util.*;
import java.lang.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        Tokenizer t = new Tokenizer("SquareGame.jack");
        List<Token> toks = t.tokenGen();
        Parser p = new Parser(toks);
        p.terminalsToXML("foo.xml");
        try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter("foo1.xml", true));
        	System.out.println(p.toks());
            p.parseClass(0, writer, 0);
            writer.close();
        }
        catch(IOException e){
        	System.out.println("No filename found.");
        }
        
    }

}