import java.util.*;
import java.lang.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        Tokenizer t = new Tokenizer("Foo.jack");
        List<Token> toks = t.tokenGen();
        Parser p = new Parser(toks);
        p.terminalsToXML("foo.xml");
        System.out.println(p.toks());
        try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter("foo1.xml", true));
        	p.parse(writer);
        	writer.close();
        }
        catch(IOException e){
        	System.out.println("No filename found.");
        }
        
    }

}