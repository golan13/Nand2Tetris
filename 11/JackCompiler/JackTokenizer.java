import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.*;

public class JackTokenizer {
    /**
     * Ignores all comments and white space in the input stream, and serializes it into
     * Jack-language tokens. The token types are specifies according to the Jack grammar.
     */
    public static final int KEYWORD = 0;
    public static final int SYMBOL = 1;
    public static final int IDENTIFIER = 2;
    public static final int INT_CONST = 3;
    public static final int STRING_CONST = 4;
    public static final int CLASS = 0;
    public static final int METHOD = 1;
    public static final int FUNCTION = 2;
    public static final int CONSTRUCTOR = 3;
    public static final int INT = 4;
    public static final int BOOLEAN = 5;
    public static final int CHAR = 6;
    public static final int VOID = 7;
    public static final int VAR = 8;
    public static final int STATIC = 9;
    public static final int FIELD = 10;
    public static final int LET = 11;
    public static final int DO = 12;
    public static final int IF = 13;
    public static final int ELSE = 14;
    public static final int WHILE = 15;
    public static final int RETURN = 16;
    public static final int TRUE = 17;
    public static final int FALSE = 18;
    public static final int NULL = 19;
    public static final int THIS = 20;
    private static final Map<String,Integer> keywords = new HashMap<String, Integer>(){
        {
            put("class",JackTokenizer.CLASS);
            put("method",JackTokenizer.METHOD);
            put("function",JackTokenizer.FUNCTION);
            put("constructor",JackTokenizer.CONSTRUCTOR);
            put("int",JackTokenizer.INT);
            put("boolean",JackTokenizer.BOOLEAN);
            put("char",JackTokenizer.CHAR);
            put("void",JackTokenizer.VOID);
            put("var",JackTokenizer.VAR);
            put("static",JackTokenizer.STATIC);
            put("field",JackTokenizer.FIELD);
            put("do",JackTokenizer.DO);
            put("if",JackTokenizer.IF);
            put("let",JackTokenizer.LET);
            put("else",JackTokenizer.ELSE);
            put("while",JackTokenizer.WHILE);
            put("return",JackTokenizer.RETURN);
            put("true",JackTokenizer.TRUE);
            put("false",JackTokenizer.FALSE);
            put("null",JackTokenizer.NULL);
            put("this",JackTokenizer.THIS);
        }
    };
    public static final Set<Character> symbols = new HashSet<Character>(){
        {
            add('{');
            add('}');
            add('(');
            add(')');
            add('[');
            add(']');
            add('.');
            add(',');
            add(';');
            add('+');
            add('-');
            add('*');
            add('/');
            add('&');
            add('|');
            add('<');
            add('>');
            add('=');
            add('~');
        }
    };
    String filename;
    StreamTokenizer stream;
    String token;

    /**
     * Opens the input .jack file and gets ready to tokenize it.
     * @param filename - name of .jack file.
     * @throws IOException
     */
    public JackTokenizer(String filename) throws IOException {
        this.filename = filename;
        this.stream = new StreamTokenizer(new BufferedReader(new FileReader(this.filename)));
        stream.ordinaryChar('/');
        stream.slashSlashComments(true);
        stream.slashStarComments(true);
        stream.ordinaryChar('.');
        stream.ordinaryChar('-');
    }

    /**
     * Are there more tokens in the input?
     * @return - true if there are more tokens, false otherwise.
     */
    public boolean hasMoreTokens() throws IOException {
        int token = this.stream.nextToken();
        if (token != StreamTokenizer.TT_EOF){
            this.stream.pushBack();
            return true;
        }
        return false;
    }

    /**
     * Gets the next token from the input, and makes it the current token.
     * This method should only be called if hasMoreTokens is true.
     * Initially there is no current token.
     */
    public void advance() throws IOException {
        this.stream.nextToken();
        switch (this.stream.ttype){
            case StreamTokenizer.TT_WORD: this.token = this.stream.sval;
            break;
            case 34:
                this.token = '"'+this.stream.sval + '"';
            break;
            case StreamTokenizer.TT_NUMBER: this.token = String.valueOf((int)this.stream.nval);
            break;
            default: this.token = (char)(this.stream.ttype)+"";
        }
    }

    /**
     * Returns the type of the current token, as a constant.
     * @return - type of current token.
     */
    public int tokenType(){
        if (keywords.containsKey(this.token)){
            return KEYWORD;
        } else if (symbols.contains(this.token.charAt(0))){
            return SYMBOL;
        }
        try{
            Integer.parseInt(this.token);
            return INT_CONST;
        } catch (Exception ignored){}
        if (this.token.charAt(0) == '"' && this.token.charAt(this.token.length()-1) == '"'){
            return STRING_CONST;
        }
        return IDENTIFIER;
    }

    /**
     * Returns the keyword which is the current token, as a constant.
     * This method should only be called if tokenType is KEYWORD.
     * @return - current keyword as constant
     */
    public int keyword(){
        return keywords.get(this.token);
    }

    /**
     * Returns the character which is the current token.
     * Should only be called if tokenType is SYMBOL.
     * @return - current symbol.
     */
    public char symbol(){
        return this.token.charAt(0);
    }

    /**
     * Returns the identifier which is the current token.
     * Should only be called if tokenType is IDENTIFIER.
     * @return - current identifier.
     */
    public String identifier(){
        return this.token;
    }

    /**
     * Returns the integer value of the current token.
     * Should only be called if tokenType is INT_CONST.
     * @return - integer value of current token.
     */
    public int intVal(){
        return Integer.parseInt(this.token);
    }

    /**
     * Returns the string value of the current token, without the two enclosing double quotes.
     * Should only be called if tokenType is STRING_CONST.
     * @return - the string of the current token.
     */
    public String stringVal(){
        return this.token.substring(1,this.token.length()-1);
    }

}
