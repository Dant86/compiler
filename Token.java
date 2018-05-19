public class Token {

    private String token;
    private String type;
    public static final String[] kwds = {"class", "constructor", "function",
                                          "var", "int", "char", "boolean", "void",
                                          "true", "false", "null", "this", "let",
                                          "do", "if", "else", "while", "return"};
    public static final String[] syms = {"(", ")", "{", "}", "[", "]", ".", ",", ";", "+",
                                         "-", "*", "/", "&", "|", "<", ">", "=", "~",
                                         ">=", "<=", "++", "--"};
    public static final String[] terminals = {"keyword", "symbol", "integerConstant",
                                              "stringConstant", "identifier"};

    public Token(String token) {
        this.type = "";
        for(String kwd : Token.kwds) {
            if(kwd.equals(token)) {
                this.type = "keyword";
            }
        }
        for(String sym : Token.syms) {
            if(sym.equals(token)) {
                this.type = "symbol";
            }
        }
        int val = 0;
        try {
            val = Integer.valueOf(token);
            this.type = "integerConstant";
        }
        catch (Exception e) {
            if(token.charAt(0) == '\"' && token.charAt(token.length() - 1) == '\"') {
                this.type = "stringConstant";
            }
            if(this.type.equals("")) {
                this.type = "identifier";
            }
            this.token = token;
        }
        this.token = token;
        if(this.type.equals("stringConstant")) {
            this.token = this.token.substring(1, this.token.length() - 1);
        }
    }

    public Token(String type, String tok) {
        this.type = type;
        this.token = tok;
    }

    public String type() {
        return this.type;
    }

    public String token() {
        return this.token;
    }

    public static int typeOfSym(String foo) {
        for(int i = 0; i < syms.length; i++) {
            if(foo.equals(syms[i])) {
                return i;
            }
        }
        return -1;
    }

    public boolean isOp() {
        if(this.token().equals("+")) {
            return true;
        }
        if(this.token().equals("-")) {
            return true;
        }
        if(this.token().equals("*")) {
            return true;
        }
        if(this.token().equals("/")) {
            return true;
        }
        if(this.token().equals("&")) {
            return true;
        }
        if(this.token().equals("|")) {
            return true;
        }
        if(this.token().equals("<")) {
            return true;
        }
        if(this.token().equals(">")) {
            return true;
        }
        if(this.token().equals("=")) {
            return true;
        }
        return false;
    }

    public boolean isUnaryOp() {
        if(this.token().equals("_")) {
            return true;
        }
        if(this.token().equals("~")) {
            return true;
        }
        return false;
    }

    public boolean isStatement() {
        if(this.token().equals("let")) {
            return true;
        }
        if(this.token().equals("if")) {
            return true;
        }
        if(this.token().equals("while")) {
            return true;
        }
        if(this.token().equals("do")) {
            return true;
        }
        if(this.token().equals("return")) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isPrimitive() {
        if(this.token().equals("int")) {
            return true;
        }
        if(this.token().equals("char")) {
            return true;
        }
        if(this.token().equals("boolean")) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isCVInit() {
        if(this.token().equals("static")) {
            return true;
        }
        if(this.token().equals("field")) {
            return true;
        }
        return false;
    }

    public boolean isSDInit() {
        if(this.token().equals("constructor")) {
            return true;
        }
        if(this.token().equals("function")) {
            return true;
        }
        if(this.token().equals("method")) {
            return true;
        }
        return false;
    }

    public String toString() {
        String retVal = "{";
        retVal += this.type;
        retVal += " | ";
        retVal += this.token;
        return retVal + "}";
    }

}