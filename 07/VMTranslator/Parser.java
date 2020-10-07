import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
Handles the parsing of a single .vm file
Reads a VM command, parses the command into its lexical
components, and provides convenient access to these components
Ignores all white space and comments
 */
public class Parser {

    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public static final int C_GOTO = 4;
    public static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;
    String filename;
    BufferedReader in;
    String instruction;

    /*
    Opens the input file/stream, and gets ready to parse it.
     */
    public Parser(String filename) throws IOException {
        this.filename = filename;
        this.in = new BufferedReader(new FileReader(filename));
    }

    /*
    Are there more lines in the input?
     */
    public boolean hasMoreLines() throws IOException{
        return this.in.ready();
    }

    /*
    Reads the next command from the input and makes it the current command.
    This method should be called only if hasMoreLines is true.
    Initially there is no current command.
     */
    public void advance() throws IOException{
        String line = "";
        line = in.readLine();
        if (line.contains("//")){
            line = line.substring(0, line.indexOf("//"));
        }
        line.replaceAll("[\n\t]","");
        if (!line.equals("")){
            this.instruction = line.trim();
        } else {
            this.advance();
        }
    }

    /*
    Returns a constant representing the type of the current command.
    If the command is an arithmetic-logical command, returns C_ARITHMETIC.
     */
    public int commandType(){
        //Get first command of current instruction
        int index = this.instruction.indexOf(" ");
        String command;
        if (index > 0) {
            command = this.instruction.substring(0, index);
        } else {
            command = this.instruction;
        }
        switch (command){
            case "add":
            case "sub":
            case "neg":
            case "eq":
            case "gt":
            case "lt":
            case "and":
            case "or":
            case "not": return this.C_ARITHMETIC;
            case "pop": return this.C_POP;
            case "push": return this.C_PUSH;
            case "label": return this.C_LABEL;
            case "goto": return this.C_GOTO;
            case "if-goto": return this.C_IF;
            case "function": return this.C_FUNCTION;
            case "call": return this.C_CALL;
            case "return": return this.C_RETURN;
        }
        return -1;
    }

    /*
    Returns the first argument of the current command.
    In the case of C_ARITHMETIC, the command itself (add, sub, etc.) is returned.
    Should not be called if the current command is C_RETURN.
     */
    public String arg1(){
        int start = this.instruction.indexOf(" ");
        int end = this.instruction.indexOf(" ", start + 1);
        String argument;
        if (start > 0) {
            if (end > 0) {
                argument = this.instruction.substring(start + 1, end);
            } else {
                argument = this.instruction;
            }
        } else {
            argument = this.instruction;
        }
        return argument;
    }

    /*
    Returns the second argument of the current command.
    Should be called only if the current command is C_PUSH, C_POP, C_FUNCTION, or C_CALL.
     */
    public int arg2() {
        int first = this.instruction.indexOf(" ");
        int second = this.instruction.indexOf(" ", first + 1);
        String argument = this.instruction.substring(second + 1);
        return Integer.parseInt(argument);
    }
}
