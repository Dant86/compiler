import java.util.*;
import java.lang.*;
import java.io.*;

public class Tokenizer {

    private List<String> lines;

    public Tokenizer(String filename) {
        try {
            File f = new File(filename);
            FileReader fileR = new FileReader(f);
            BufferedReader reader = new BufferedReader(fileR);
            String line;
            this.lines = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                this.lines.add(line);
            }
        }
        catch (IOException e) {
            System.out.println("IOException: file not found.");
        }
    }

    public static List<Token> lineToTokens(String line, int lineNum) {
        List<Token> result = new ArrayList<Token>();
        String current = "";
        String symCurr = "";
        String currChar = "";
        boolean activeQuo = false;
        for(int i = 0; i < line.length(); i++) {
            currChar = Character.toString(line.charAt(i));
            if(currChar.equals("\"")) {
                activeQuo = !activeQuo;
            }
            // If current character is not a symbol,
            if(Token.typeOfSym(currChar) == -1 && !(currChar.equals(" ") && activeQuo == false)) {
                current += currChar;
            }
            else {
                if(current != "") {
                    result.add(new Token(current));
                }
                current = "";
                if(Token.typeOfSym(currChar) != -1) {
                    symCurr += currChar;
                    if(i != line.length() - 1) {
                        if(currChar.equals("<") || currChar.equals(">")) {
                            if(Character.toString(line.charAt(i + 1)).equals("=")) {
                                symCurr += "=";
                            }
                        }
                        if(currChar.equals("+")) {
                            if(Character.toString(line.charAt(i + 1)).equals("+")) {
                                 symCurr += "+";
                            }
                        }
                        if(currChar.equals("-")) {
                            if(Character.toString(line.charAt(i + 1)).equals("-")) {
                                 symCurr += "-";
                            }
                        }
                    }
                    result.add(new Token(symCurr));
                    symCurr = "";
                }
            }
        }
        return result;
    }

    public List<Token> tokenGen() {
        List<Token> result = new ArrayList<Token>();
        List<Token> currToks = null;
        int startChar = 0;
        int endChar = 0;
        int counter = 0;
        for(String line : this.lines) {
            line = line.trim();
            counter += 1;
            // Parse multi-line comments
            if((line.indexOf("/**") != -1) || (line.indexOf("*/") != -1)) {
                if((line.indexOf("/**") != -1) && (line.indexOf("*/") == -1)) {
                    line = line.substring(0, line.indexOf("/**"));
                }
                else if((line.indexOf("/**") != -1) && (line.indexOf("*/") != -1)) {
                    if(line.indexOf("/**") < line.indexOf("*/")) {
                        line = line.substring(0, line.indexOf("/**"));
                        line = line + " ";
                        line = line + line.substring(line.indexOf("*/") + 2, line.length());
                    }
                    else {
                        line = line.substring(line.indexOf("*/") + 2, line.indexOf("**/"));
                    }
                }
                else if((line.indexOf("*/") != -1) && (line.indexOf("**/") == -1)) {
                    line = line.substring(line.indexOf("*/") + 2, line.length());
                }
            }
            // Parse single-line comments
            if(line.indexOf("//") != -1) {
                if(line.indexOf("//") == 0) {
                    line = "";
                }
                else {
                    endChar = line.indexOf("//");
                    line = line.substring(0, endChar);
                }
            }
            else {
                endChar = line.length();
                line = line.substring(0, endChar);
            }
            System.out.println(line);
            currToks = Tokenizer.lineToTokens(line, counter);
            for(int i = 0; i < currToks.size(); i++) {
                result.add(currToks.get(i));
            }
        }
        return result;
    }

}