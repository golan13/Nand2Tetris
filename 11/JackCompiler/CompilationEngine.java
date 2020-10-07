import java.io.IOException;
import java.util.LinkedList;

public class CompilationEngine {
    /**
     * Generates the compiler's output.
     */
    JackTokenizer tokenizer;
    VMWriter writer;
    SymbolTable classTable;
    LinkedList<SymbolTable> symbolTables;
    String className;
    int index;

    /**
     * Creates a new compilation engine with the given input and output.
     * The next routine called must be compileClass.
     *
     * @param input  - name of input file
     * @param output - name of output file
     */
    public CompilationEngine(String input, String output) throws IOException {
        this.writer = new VMWriter(output);
        this.tokenizer = new JackTokenizer(input);
        this.classTable = new SymbolTable();
        this.symbolTables = new LinkedList<>();
        this.symbolTables.add(this.classTable);
        index = 0;
    }

    /**
     * Compiles a complete class.
     */
    public void compileClass() throws IOException {
        tokenizer.advance();
        process("class");
        className = tokenizer.identifier();
        process(className);
        process("{");
        while (this.tokenizer.token.equals("static") || this.tokenizer.token.equals("field")) {
            compileClassVarDec();
        }
        while (this.tokenizer.token.equals("constructor") || this.tokenizer.token.equals("method") || this.tokenizer.token.equals("function")) {
            compileSubroutineDec();
        }
        process("}");
        writer.close();
    }

    /**
     * Compiles a static variable declaration, or a field declaration.
     */
    public void compileClassVarDec() throws IOException {
        int kind;
        if (this.tokenizer.token.equals("static")) {
            process("static");
            kind = SymbolTable.STATIC;
        } else {
            process("field");
            kind = SymbolTable.FIELD;
        }
        String type = this.tokenizer.token;
        process(type);
        classTable.define(tokenizer.identifier(),type,kind);
        process(tokenizer.identifier());
        while (this.tokenizer.symbol() == ',') {
            process(",");
            classTable.define(tokenizer.identifier(),type,kind);
            process(tokenizer.identifier());
        }
        process(";");
    }

    /**
     * Compiles a complete method, function or constructor.
     */
    public void compileSubroutineDec() throws IOException {
        String name;
        int n = symbolTables.getFirst().varCount(SymbolTable.FIELD);
        SymbolTable subroutine = new SymbolTable();
        subroutine.startSubroutine();
        symbolTables.addFirst(subroutine);
        String type = tokenizer.token;
        if (type.equals("method")){
            symbolTables.getFirst().define("this",className,SymbolTable.ARG);
        }
        process(type);
        String returnType = tokenizer.token;
        process(returnType);
        name = tokenizer.identifier();
        process(name);
        process("(");
        compileParameterList();
        process(")");
        process("{");
        while (tokenizer.token.equals("var")) {
            compileVarDec();
        }
        writer.writeFunction(className + "." + name,symbolTables.getFirst().varCount(SymbolTable.VAR));
        switch(type){
            case "constructor":
                writer.writePush("constant", n);
                writer.writeCall("Memory.alloc", 1);
                writer.writePop("pointer", 0);
                break;
            case "method":
                writer.writePush("argument", 0);
                writer.writePop("pointer", 0);
                break;
        }
        compileSubroutineBody();
        symbolTables.removeFirst();
    }

    /**
     * Compiles a (possibly empty) parameter list.
     * Does not handle the enclosing "()".
     */
    public void compileParameterList() throws IOException {
        String type = tokenizer.token;
        if (type.equals(")")){
            return;
        }
        process(type);
        symbolTables.getFirst().define(tokenizer.identifier(),type,SymbolTable.ARG);
        process(tokenizer.identifier());
        while(this.tokenizer.token.equals(",")) {
            process(",");
            type = tokenizer.token;
            process(type);
            symbolTables.getFirst().define(tokenizer.identifier(),type,SymbolTable.ARG);
            process(tokenizer.identifier());
        }
    }

    /**
     * Compiles a subroutine's body.
     */
    public void compileSubroutineBody() throws IOException {
        compileStatements();
        process("}");
    }

    /**
     * Compiles a var declaration.
     */
    public void compileVarDec() throws IOException {
        process("var");
        String type = tokenizer.token;
        process(type);
        symbolTables.getFirst().define(tokenizer.identifier(),type,SymbolTable.VAR);
        process(tokenizer.identifier());
        while (this.tokenizer.token.equals(",")) {
            process(",");
            symbolTables.getFirst().define(tokenizer.identifier(),type,SymbolTable.VAR);
            process(tokenizer.identifier());
        }
        process(";");
    }

    /**
     * Compiles a sequence of statements.
     * Does not handle the enclosing "{}".
     */
    public void compileStatements() throws IOException {
        boolean loop = true;
        while (loop) {
            switch (this.tokenizer.token) {
                case "let":
                    compileLet();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compileWhile();
                    break;
                case "do":
                    compileDo();
                    break;
                case "return":
                    compileReturn();
                    break;
                default:
                    loop = false;
            }
        }
    }

    /**
     * Compiles a let statement.
     */
    public void compileLet() throws IOException {
        process("let");
        String argument = tokenizer.identifier();
        String segment = getSegment(searchKind(argument));
        process(argument);
        if (tokenizer.token.equals("[")) {
            writer.writePush(segment, searchIndex(argument));
            process("[");
            compileExpression();
            process("]");
            writer.writeArithmetic("add");
            process("=");
            compileExpression();
            process(";");
            writer.writePop("temp", 0);
            writer.writePop("pointer", 1);
            writer.writePush("temp", 0);
            writer.writePop("that", 0);
            return;
        }
        process("=");
        compileExpression();
        writer.writePop(segment, searchIndex(argument));
        process(";");
    }

    /**
     * Compiles an if statement, possibly with a trailing else clause.
     */
    public void compileIf() throws IOException {
        SymbolTable ifTable = new SymbolTable();
        ifTable.startSubroutine();
        symbolTables.addFirst(ifTable);
        process("if");
        process("(");
        compileExpression();
        process(")");
        writer.writeArithmetic("not");
        int i = index;
        writer.writeIf("L" + index);
        index++;
        process("{");
        compileStatements();
        process("}");
        writer.writeGoto("L" + index);
        int j = index;
        writer.writeLabel("L" + i);
        index++;
        if (this.tokenizer.token.equals("else")) {
            SymbolTable elseTable = new SymbolTable();
            elseTable.startSubroutine();
            symbolTables.addFirst(elseTable);
            process("else");
            process("{");
            compileStatements();
            process("}");
            symbolTables.removeFirst();
        }
        writer.writeLabel("L" + j);
        symbolTables.removeFirst();
    }

    /**
     * Compiles a while statement.
     */
    public void compileWhile() throws IOException {
        SymbolTable whileTable = new SymbolTable();
        whileTable.startSubroutine();
        symbolTables.addFirst(whileTable);
        int i = index;
        writer.writeLabel("L" + index);
        index++;
        process("while");
        process("(");
        compileExpression();
        writer.writeArithmetic("not");
        int j = index;
        writer.writeIf("L" + index);
        index++;
        process(")");
        process("{");
        compileStatements();
        writer.writeGoto("L" + i);
        writer.writeLabel("L" + j);
        process("}");
        symbolTables.removeFirst();
    }

    /**
     * Compiles a do statement.
     */
    public void compileDo() throws IOException {
        process("do");
        String name = tokenizer.identifier();
        String type = "";
        String function = "";
        int i = 0;
        process(name);
        if (!tokenizer.token.equals("(")) {
            process(".");
            type = searchType(name);
            if (!type.equals("")){
                String segment = getSegment(searchKind(name));
                writer.writePush(segment, searchIndex(name));
                name = type;
                i = 1;
            }
            function = tokenizer.identifier();
            process(function);
        } else {
            function = name;
            name = className;
            i = 1;
            writer.writePush("pointer", 0);
        }
        process("(");
        i += compileExpressionList();
        writer.writeCall(name + "." + function, i);
        process(")");
        writer.writePop("temp",0);
        process(";");
    }

    /**
     * Compiles a return statement.
     */
    public void compileReturn() throws IOException {
        process("return");
        if (!this.tokenizer.token.equals(";")) {
            compileExpression();
        } else {
            writer.writePush("constant", 0);
        }
        writer.writeReturn();
        process(";");
    }

    /**
     * Compiles an expression.
     */
    public void compileExpression() throws IOException {
        compileTerm();
        while (tokenizer.token.equals("+") || tokenizer.token.equals("-") || tokenizer.token.equals("*") || tokenizer.token.equals("/") || tokenizer.token.equals("&") || tokenizer.token.equals("|") || tokenizer.token.equals("<") || tokenizer.token.equals(">") || tokenizer.token.equals("=")) {
            char symbol = tokenizer.symbol();
            process(tokenizer.symbol() + "");
            compileTerm();
            switch (symbol){
                case '+':
                    writer.writeArithmetic("add");
                    break;
                case '-':
                    writer.writeArithmetic("sub");
                    break;
                case '*':
                    writer.writeCall("Math.multiply", 2);
                    break;
                case '/':
                    writer.writeCall("Math.divide", 2);
                    break;
                case '&':
                    writer.writeArithmetic("and");
                    break;
                case '|':
                    writer.writeArithmetic("or");
                    break;
                case '<':
                    writer.writeArithmetic("lt");
                    break;
                case '>':
                    writer.writeArithmetic("gt");
                    break;
                case '=':
                    writer.writeArithmetic("eq");
            }
        }
    }

    /**
     * Compiles a term.
     * If the current token is an identifier, the routine must distinguish between a variable, an array entry,
     * or a subroutine call. A single look-ahead token, which may be on of "[", "(", or ".", suffices to
     * distinguish between the possibilities. Any other token is not part of this term and should not advance over.
     */
    public void compileTerm() throws IOException {
        int type = tokenizer.tokenType();
        int varType = 0;
        int j = 0;
        switch (type){
            case JackTokenizer.INT_CONST:
                writer.writePush("constant", tokenizer.intVal());
                process(tokenizer.token);
                return;
            case JackTokenizer.KEYWORD:
                if (tokenizer.token.equals("true")){
                    writer.writePush("constant", 1);
                    writer.writeArithmetic("neg");
                    process(tokenizer.token);
                    return;
                } else if (tokenizer.token.equals("false") || tokenizer.token.equals("null")){
                    writer.writePush("constant", 0);
                    process(tokenizer.token);
                    return;
                } else if (tokenizer.token.equals("this")){
                    writer.writePush("pointer", 0);
                    process(tokenizer.token);
                    return;
                }
                break;
            case JackTokenizer.STRING_CONST:
                String str = tokenizer.stringVal();
                process(tokenizer.token);
                int length = str.length();
                writer.writePush("constant", length);
                writer.writeCall("String.new", 1);
                for(int i = 0; i < length; i++){
                    int c = str.charAt(i);
                    writer.writePush("constant", c);
                    writer.writeCall("String.appendChar", 2);
                }
                return;
        }
        if (tokenizer.token.equals("-")) {
            process("-");
            compileTerm();
            writer.writeArithmetic("neg");
            return;
        } else if (tokenizer.token.equals("~")) {
            process("~");
            compileTerm();
            writer.writeArithmetic("not");
            return;
        } else if (tokenizer.token.equals("(")) {
            process("(");
            compileExpression();
            process(")");
            return;
        }
        String name = tokenizer.identifier();
        process(name);
        switch (tokenizer.token) {
            case "[":
                process("[");
                String segment = getSegment(searchKind(name));
                writer.writePush(segment, searchIndex(name));
                compileExpression();
                writer.writeArithmetic("add");
                writer.writePop("pointer", 1);
                writer.writePush("that", 0);
                process("]");
                break;
            case "(":
                process("(");
                compileExpressionList();
                process(")");
                break;
            case ".":
                process(".");
                String subroutine = tokenizer.identifier();
                process(subroutine);
                segment = getSegment(searchKind(name));
                if (!segment.equals("")){
                    writer.writePush(segment, searchIndex(name));
                    j +=1 ;
                }
                process("(");
                j += compileExpressionList();
                process(")");
                if(searchKind(name) != SymbolTable.NONE){
                    name = searchType(name);
                }
                writer.writeCall(name + "." + subroutine,j);
                break;
            default:
                segment = getSegment(searchKind(name));
                writer.writePush(segment, searchIndex(name));
        }
    }

    /**
     * Compiles a (possibly empty) comma-separated list of expression.
     */
    public int compileExpressionList() throws IOException {
        int i = 0;
        if (!tokenizer.token.equals(")")) {
            i++;
            compileExpression();
            while (tokenizer.token.equals(",")) {
                process(",");
                compileExpression();
                i++;
            }
        }
        return i;
    }

    /**
     * Processes the current token and advances.
     * If the token processed is different than expected, exits the program and prints syntax error.
     * @param str
     * @throws IOException
     */
    private void process(String str) throws IOException {
        if (!this.tokenizer.token.equals(str)) {
            System.out.println("syntax error\n");
            writer.close();
            System.exit(1);
        }
        this.tokenizer.advance();
    }

    /**
     * Searches all symbol tables for the kind.
     * @param name
     * @return
     */
    private int searchKind(String name){
        for (SymbolTable table: symbolTables) {
            int kind = table.kindOf(name);
            if (kind != SymbolTable.NONE){
                return kind;
            }
        }
        return SymbolTable.NONE;
    }

    /**
     * Searches all symbol tables for the type.
     * @param name
     * @return
     */
    private String searchType(String name){
        for (SymbolTable table: symbolTables) {
            String type = table.typeOf(name);
            if (!type.equals("")){
                return type;
            }
        }
        return "";
    }

    /**
     * Searches all symbol tables for the index.
     * @param name
     * @return
     */
    private int searchIndex(String name){
        for (SymbolTable table: symbolTables) {
            int index = table.indexOf(name);
            if (index != -1){
                return index;
            }
        }
        return -1;
    }

    /**
     * Converts the type to a segment.
     * @param type
     * @return
     */
    private String getSegment(int type){
        String segment = "";
        switch (type){
            case SymbolTable.VAR:
                segment = "local";
                break;
            case SymbolTable.FIELD:
                segment = "this";
                break;
            case SymbolTable.ARG:
                segment = "argument";
                break;
            case SymbolTable.STATIC:
                segment = "static";
                break;
        }
        return segment;
    }
}