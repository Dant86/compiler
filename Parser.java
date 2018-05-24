import java.util.*;
import java.lang.*;
import java.io.*;

public class Parser {

    private List<Token> terminals;
    private List<Token> toks;

    public Parser(List<Token> tokens) {
        this.terminals = tokens;
        this.toks = new ArrayList<Token>();
    }

    public void terminalsToXML(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            Token current_terminal = null;
            String current_type = null;
            String current_value = null;
            writer.append("<tokens>\n");
            for(int i = 0; i < this.terminals.size(); i++) {
                current_terminal  = this.terminals.get(i);
                current_type = current_terminal.type();
                current_value = current_terminal.token();
                writer.append("<" + current_type + "> " + current_value + " </" + current_type + ">\n");
            }
            writer.append("</tokens>\n");
            writer.close();
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
    }

    public void parse(BufferedWriter writer) {
        parseClass(0, writer, 0);
    }

    public int parseTerm(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            Token currTok = this.terminals.get(iOCT);
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<term>\n");
            if(currTok.type().equals("integerConstant")) {
                writer.append(curr + "\t" + "<integerConstant> " + currTok.token() + " </integerConstant>\n");
                writer.append(curr + "</term>\n");
                return iOCT + 1;
            }
            if(currTok.type().equals("stringConstant")) {
                writer.append(curr + "\t" + "<stringConstant> " + currTok.token() + " </stringConstant>\n");
                writer.append(curr + "</term>\n");
                return iOCT + 1;
            }
            if(currTok.token().equals("true") || currTok.token().equals("false") || currTok.token().equals("null") || currTok.token().equals("this")) {
                writer.append(curr + "\t" + "<keywordConstant> " + currTok.token() + " </keywordConstant>\n");
                writer.append(curr + "</term>\n");
                return iOCT + 1;
            }
            if(currTok.type().equals("identifier")) {
                if(this.terminals.get(iOCT + 1).equals("[")) {
                    writer.append(curr + "\t" + "<symbol> " + "[" + " </symbol>\n");
                    indexNext = parseExpression(iOCT + 2, writer, amtOfTabs + 1);
                    writer.append(curr + "\t" + "<symbol> " + "]" + " </symbol>\n");
                    writer.append(curr + "</term>\n");
                    return indexNext + 1;
                }
                else if(this.terminals.get(iOCT + 1).token().equals("(") || this.terminals.get(iOCT + 1).token().equals(".")) {
                    indexNext = parseSubroutineCall(iOCT, writer, amtOfTabs + 1);
                    writer.append(curr + "</term>\n");
                    return indexNext;
                }
                else {
                    writer.append(curr + "\t" + "<identifier> " + currTok.token() + " </identifier>\n");
                }
                writer.append(curr + "</term>\n");
                return iOCT + 1;
            }
            if(currTok.token().equals("(")) {
                writer.append(curr + "\t" + "<symbol> " + "(" + " </symbol>\n");
                indexNext = parseExpression(iOCT + 1, writer, amtOfTabs + 1);
                writer.append(curr + "\t" + "<symbol> " + ")" + " </symbol>\n");
                writer.append(curr + "</term>\n");
                return indexNext + 1;
            }
            if(currTok.token().equals(";")) {
                writer.append(curr + "\t" + "<symbol> " + ";" + " </symbol>\n");
                writer.append(curr + "</term>\n");
                return iOCT + 1;
            }
            if(currTok.isUnaryOp()) {
                writer.append(curr + "\t" + "<symbol> " + currTok.token() + " </symbol>\n");
                indexNext = parseTerm(iOCT + 1, writer, amtOfTabs + 1);
                writer.append(curr + "</term>\n");
                return indexNext;
            }
            return -1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseExpression(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<expression>\n");
            indexNext = parseTerm(iOCT, writer, amtOfTabs + 1);
            while(this.terminals.get(indexNext).isOp()) {
                writer.append(curr + "\t" + "<symbol> " + this.terminals.get(indexNext).token() + " </symbol>\n");
                indexNext = parseTerm(indexNext + 1, writer, amtOfTabs + 1);
            }
            writer.append(curr + "</expression>\n");
            return indexNext;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseExpressionList(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<expressionList>\n");
            if(this.terminals.get(iOCT).token().equals(")")) {
                writer.append(curr + "</expressionList>\n");
                return iOCT;
            }
            indexNext = parseExpression(iOCT, writer, amtOfTabs + 1);
            while(this.terminals.get(indexNext).token().equals(",")) {
                writer.append(curr + "\t" + "<symbol> " + "," + " </symbol>\n");
                indexNext = parseExpression(indexNext + 1, writer, amtOfTabs + 1);
            }
            writer.append(curr + "</expressionList>\n");
            return indexNext;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseSubroutineCall(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<subroutineCall>\n");
            if(this.terminals.get(iOCT + 1).token().equals("(")) {
                writer.append(curr + "\t" + "<identifier> " + this.terminals.get(iOCT).token() + " </identifier>\n");
                writer.append(curr + "\t" + "<symbol> ( </symbol>\n");
                indexNext = parseExpressionList(iOCT + 2, writer, amtOfTabs + 1);
                writer.append(curr + "\t" + "<symbol> ) </symbol>\n");
                writer.append(curr + "</subroutineCall>\n");
            }
            else {
                writer.append(curr + "\t" + "<identifier> " + this.terminals.get(iOCT).token() + " </identifier>\n");
                writer.append(curr + "\t" + "<symbol> . </symbol>\n");
                writer.append(curr + "\t" + "<identifier> " + this.terminals.get(iOCT + 2).token() + " </identifier>\n");
                writer.append(curr + "\t" + "<symbol> ( </symbol>\n");
                indexNext = parseExpressionList(iOCT + 4, writer, amtOfTabs + 1);
                writer.append(curr + "\t" + "<symbol> ) </symbol>\n");
                writer.append(curr + "</subroutineCall>\n");
            }
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseStatements(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<statements>\n");
            while(this.terminals.get(indexNext).isStatement()) {
                indexNext = parseStatement(indexNext, writer, amtOfTabs + 1);
            }
            writer.append(curr + "</statements>\n");
            return indexNext;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseStatement(int iOCT, BufferedWriter writer, int amtOfTabs) {
        int indexNext = iOCT;
        if(this.terminals.get(iOCT).token().equals("if")) {
            indexNext = parseIf(iOCT, writer, amtOfTabs + 1);
        }
        if(this.terminals.get(iOCT).token().equals("while")) {
            indexNext = parseWhile(iOCT, writer, amtOfTabs + 1);
        }
        if(this.terminals.get(iOCT).token().equals("let")) {
            indexNext = parseLet(iOCT, writer, amtOfTabs + 1);
        }
        if(this.terminals.get(iOCT).token().equals("do")) {
            indexNext = parseDo(iOCT, writer, amtOfTabs + 1);
        }
        if(this.terminals.get(iOCT).token().equals("return")) {
            indexNext = parseReturn(iOCT, writer, amtOfTabs + 1);
        }
        return indexNext;
    }

    public int parseLet(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<letStatement>\n");
            writer.append(curr + "\t" + "<keyword> let </keyword>\n");
            writer.append(curr + "\t" + "<identifier> " + this.terminals.get(iOCT + 1).token() + " </identifier>\n");
            if(this.terminals.get(iOCT + 2).token().equals("[")) {
                writer.append(curr + "\t" + "<symbol> [ </symbol>\n");
                indexNext = parseTerm(iOCT + 3, writer, amtOfTabs + 1);
                writer.append(curr + "\t" + "<symbol> ] </symbol>\n");
                indexNext += 2;
            }
            else {
                indexNext = iOCT + 3;
            }
            writer.append(curr + "\t" + "<symbol> = </symbol>\n");
            indexNext = parseExpression(indexNext, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<symbol> ; </symbol>\n");
            writer.append(curr + "</letStatement>\n");
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseIf(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<ifStatement>\n");
            writer.append(curr + "\t" + "<keyword> if </keyword>\n");
            writer.append(curr + "\t" + "<symbol> ( </symbol>\n");
            indexNext = parseExpression(iOCT + 2, writer, amtOfTabs + 1) + 2;
            writer.append(curr + "\t" + "<symbol> ) </symbol>\n");
            writer.append(curr + "\t" + "<symbol> { </symbol>\n");
            indexNext = parseStatements(indexNext, writer, amtOfTabs + 1) + 1;
            writer.append(curr + "\t" + "<symbol> } </symbol>\n");
            if(indexNext < this.terminals.size() - 1) {
                if(this.terminals.get(indexNext).token().equals("else")) {
                    writer.append(curr + "\t" + "<keyword> else </keyword>\n");
                    writer.append(curr + "\t" + "<symbol> { </symbol>\n");
                    indexNext = parseStatements(indexNext + 2, writer, amtOfTabs + 1);
                    writer.append(curr + "\t" + "<symbol> } </symbol>\n");
                    writer.append(curr + "</ifStatement>\n");
                    return indexNext + 1;
                }
            }
            writer.append(curr + "</ifStatement>\n");
            return indexNext;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseWhile(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<whileStatement>\n");
            writer.append(curr + "\t" + "<keyword> while </keyword>\n");
            writer.append(curr + "\t" + "<symbol> ( </symbol>\n");
            indexNext = parseExpression(iOCT + 2, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<symbol> ) </symbol>\n");
            writer.append(curr + "\t" + "<symbol> { </symbol>\n");
            indexNext = parseStatements(indexNext + 2, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<symbol> } </symbol>\n");
            writer.append(curr + "</whileStatement>\n");
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseDo(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<doStatement>\n");
            writer.append(curr + "\t" + "<keyword> do </keyword>\n");
            indexNext = parseSubroutineCall(iOCT + 1, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<symbol> ; </symbol>\n");
            writer.append(curr + "</doStatement>\n");
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseReturn(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<returnStatement>\n");
            writer.append(curr + "\t" + "<keyword> return </keyword>\n");
            if(this.terminals.get(iOCT + 1).token().equals(";")) {
                writer.append(curr + "\t" + "<symbol> ; </symbol>\n");
                writer.append(curr + "</returnStatement>\n");
                return iOCT + 2;
            }
            indexNext = parseExpression(iOCT + 1, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<symbol> ; </symbol>\n");
            writer.append(curr + "</returnStatement>\n");
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseVarDec(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<varDec>\n");
            writer.append(curr + "\t" + "<keyword> var </keyword>\n");
            indexNext = parseType(iOCT + 1, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<identifier> " + this.terminals.get(indexNext).token() + " </identifier>\n");
            indexNext += 1;
            while(this.terminals.get(indexNext).token().equals(",")) {
                writer.append(curr + "\t" + "<symbol> , </symbol>\n");
                writer.append(curr + "\t" + "<identifier> " + this.terminals.get(indexNext + 1).token() + " </identifier>\n");
                indexNext += 2;
            }
            writer.append(curr + "\t" + "<symbol> ; </symbol>\n");
            writer.append(curr + "</varDec>\n");
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseSubroutineBody(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<subroutineBody>\n");
            writer.append(curr + "\t" + "<symbol> { </symbol>\n");
            while(this.terminals.get(indexNext).token().equals("var")) {
                indexNext = parseVarDec(indexNext, writer, amtOfTabs + 1);
            }
            indexNext = parseStatements(indexNext, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<symbol> } </symbol>\n");
            writer.append(curr + "</subroutineBody>\n");
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseParameterList(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<parameterList>\n");
            if(this.terminals.get(iOCT).token().equals(")")) {
                writer.append(curr + "</parameterList>\n");
                return iOCT + 1;
            }
            else {
                parseType(iOCT, writer, amtOfTabs + 1);
                writer.append(curr + "\t" + "<identifier> " + this.terminals.get(iOCT + 1).token() + " </identifier>\n");
                indexNext = iOCT + 2;
                while(this.terminals.get(indexNext).token().equals(",")) {
                    writer.append(curr + "\t" + "<symbol> , </symbol>\n");
                    parseType(indexNext + 1, writer, amtOfTabs + 1);
                    writer.append(curr + "\t" + "<identifier> " + this.terminals.get(indexNext + 2).token() + " </identifier>\n");
                    indexNext += 3;
                }
                writer.append(curr + "</parameterList>\n");
                return indexNext + 1;
            }
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseType(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            if(this.terminals.get(iOCT).isPrimitive()) {
                writer.append(curr + "<keyword> " + this.terminals.get(iOCT).token() + " </keyword>\n");
            }
            else {
                writer.append(curr + "<identifier> " + this.terminals.get(iOCT).token() + " </keyword>\n");
            }
            return iOCT + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseSubroutineDec(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<subroutineDec>\n");
            writer.append(curr + "\t" + "<keyword> " + this.terminals.get(iOCT).token() + " </keyword>\n");
            if(this.terminals.get(iOCT + 1).token().equals("void")) {
                writer.append(curr + "\t" + "<keyword> " + "void" + " </keyword>\n");
                indexNext = iOCT + 2;
            }
            else {
                indexNext = parseType(iOCT + 1, writer, amtOfTabs + 1);
            }
            writer.append(curr + "\t" + "<identifier> " + this.terminals.get(indexNext).token() + " </identifier>\n");
            writer.append(curr + "\t" + "<symbol> ( </symbol>\n");
            indexNext = parseParameterList(indexNext + 2, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<symbol> ) </symbol>\n");
            indexNext = parseSubroutineBody(indexNext + 1, writer, amtOfTabs + 1);
            writer.append(curr + "</subroutineDec>\n");
            return indexNext;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseClassVarDec(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<classVarDec>\n");
            writer.append(curr + "\t" + "<keyword> " + this.terminals.get(iOCT).token() + " </keyword>\n");
            indexNext = parseType(iOCT + 1, writer, amtOfTabs + 1);
            writer.append(curr + "\t" + "<identifier> " + this.terminals.get(indexNext).token() + " </identifier>\n");
            indexNext += 1;
            while(this.terminals.get(indexNext).token().equals(",")) {
                writer.append(curr + "\t" + "<identifier> " + this.terminals.get(indexNext + 1).token() + " </identifier>\n");
                indexNext += 2;
            }
            writer.append(curr + "\t" + "<symbol> ; </symbol>\n");
            writer.append(curr + "</classVarDec>\n");
            return indexNext + 1;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public int parseClass(int iOCT, BufferedWriter writer, int amtOfTabs) {
        try {
            String curr = "";
            int indexNext = iOCT;
            for(int i = 0; i < amtOfTabs; i++) {
                curr += "\t";
            }
            writer.append(curr + "<class>\n");
            writer.append(curr + "\t"+ "<keyword> class </keyword>\n");
            writer.append(curr + "\t" + "<identifier> " + this.terminals.get(iOCT).token() + " </identifier>\n");
            writer.append(curr + "\t" + "<symbol> { </symbol>\n");
            indexNext = iOCT + 3;
            while(this.terminals.get(indexNext).isCVInit()) {
                indexNext = parseClassVarDec(indexNext, writer, amtOfTabs + 1);
            }
            while(this.terminals.get(indexNext).isSDInit()) { 
                indexNext = parseSubroutineDec(indexNext, writer, amtOfTabs + 1);
            }
            writer.append(curr + "\t" + "<symbol> } </symbol>\n");
            writer.append(curr + "</class>\n");
            return indexNext;
        }
        catch(IOException e) {
            System.out.println("File not found.");
        }
        return -1;
    }

    public List<Token> toks() {
        return this.terminals;
    }

}