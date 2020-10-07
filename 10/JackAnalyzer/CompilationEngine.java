import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    /**
     * Generates the compiler's output.
     */
    BufferedWriter out;
    JackTokenizer tokenizer;
    /**
     * Creates a new compilation engine with the given input and output.
     * The next routine called must be compileClass.
     * @param input - name of input file
     * @param output - name of output file
     */
    public CompilationEngine(String input, String output) throws IOException {
        this.out = new BufferedWriter(new FileWriter(output));
        this.tokenizer = new JackTokenizer(input);
    }

    /**
     * Compiles a complete class.
     */
    public void compileClass() throws IOException {
        tokenizer.advance();
        out.write("<class>\n");
        process("class");
        process(tokenizer.identifier());
        process("{");
        while (this.tokenizer.token.equals("static")|| this.tokenizer.token.equals("field")){
            compileClassVarDec();
        }
        while(this.tokenizer.token.equals("constructor") || this.tokenizer.token.equals("method") || this.tokenizer.token.equals("function")){
            compileSubroutineDec();
        }
        process("}");
        out.write("</class>\n");
    }

    /**
     * Compiles a static variable declaration, or a field declaration.
     */
    public void compileClassVarDec() throws IOException {
        out.write("<classVarDec>\n");
        if(this.tokenizer.token.equals("static")){
            process("static");
        } else {
            process("field");
        }
        switch (this.tokenizer.token){
            case "int": process("int");
            break;
            case "char": process("char");
            break;
            case "boolean": process("boolean");
            break;
            default: process(tokenizer.identifier());
        }
        process(tokenizer.identifier());
        while (this.tokenizer.symbol() == ','){
            process(",");
            process(tokenizer.identifier());
        }
        process(";");
        out.write("</classVarDec>\n");
        out.flush();
    }

    /**
     * Compiles a complete method, function or constructor.
     */
    public void compileSubroutineDec() throws IOException {
        out.write("<subroutineDec>\n");
        switch(this.tokenizer.token){
            case "constructor": process("constructor");
            break;
            case "function": process("function");
            break;
            default: process("method");
        }
        switch (this.tokenizer.token){
            case "void": process("void");
            break;
            case "int": process("int");
            break;
            case "boolean": process("boolean");
            break;
            case "char": process("char") ;
            break;
            default: process(this.tokenizer.identifier());
        }
        process(tokenizer.identifier());
        process("(");
        compileParameterList();
        process(")");
        compileSubroutineBody();
        out.write("</subroutineDec>\n");
    }

    /**
     * Compiles a (possibly empty) parameter list.
     * Does not handle the enclosing "()".
     */
    public void compileParameterList() throws IOException {
        out.write("<parameterList>\n");
        switch (this.tokenizer.token){
            case "int": process("int");
                break;
            case "char": process("char");
                break;
            case "boolean": process("boolean");
                break;
            case ")":
                out.write("</parameterList>\n");
            return;
            default: process(tokenizer.identifier());
        }
        process(tokenizer.identifier());
        while(this.tokenizer.token.equals(",")){
            process(",");
            switch (this.tokenizer.token){
                case "int": process("int");
                    break;
                case "char": process("char");
                    break;
                case "boolean": process("boolean");
                    break;
                default: process(tokenizer.identifier());
            }
            process(tokenizer.identifier());
        }
        out.write("</parameterList>\n");
    }

    /**
     * Compiles a subroutine's body.
     */
    public void compileSubroutineBody() throws IOException {
        out.write("<subroutineBody>\n");
        process("{");
        while(this.tokenizer.token.equals("var")){
            compileVarDec();
        }
        compileStatements();
        process("}");
        out.write("</subroutineBody>\n");
    }

    /**
     * Compiles a var declaration.
     */
    public void compileVarDec() throws IOException {
        out.write("<varDec>\n");
        process("var");
        switch (this.tokenizer.token){
            case "int": process("int");
                break;
            case "char": process("char");
                break;
            case "boolean": process("boolean");
                break;
            default: process(tokenizer.identifier());
        }
        process(tokenizer.identifier());
        while(this.tokenizer.token.equals(",")){
            process(",");
            process(tokenizer.identifier());
        }
        process(";");
        out.write("</varDec>\n");
    }

    /**
     * Compiles a sequence of statements.
     * Does not handle the enclosing "{}".
     */
    public void compileStatements() throws IOException {
        out.write("<statements>\n");
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
                default: loop = false;
            }
        }
        out.write("</statements>\n");
    }

    /**
     * Compiles a let statement.
     */
    public void compileLet() throws IOException {
        out.write("<letStatement>\n");
        process("let");
        process(tokenizer.identifier());
        if(this.tokenizer.token.equals("[")){
            process("[");
            compileExpression();
            process("]");
        }
        process("=");
        compileExpression();
        process(";");
        out.write("</letStatement>\n");
    }

    /**
     * Compiles an if statement, possibly with a trailing else clause.
     */
    public void compileIf() throws IOException {
       out.write("<ifStatement>\n");
        process("if");
        process("(");
        compileExpression();
        process(")");
        process("{");
        compileStatements();
        process("}");
        if (this.tokenizer.token.equals("else")){
            process("else");
            process("{");
            compileStatements();
            process("}");
        }
        out.write("</ifStatement>\n");
    }

    /**
     * Compiles a while statement.
     */
    public void compileWhile() throws IOException {
        out.write("<whileStatement>\n");
        process("while");
        process("(");
        compileExpression();
        process(")");
        process("{");
        compileStatements();
        process("}");
        out.write("</whileStatement>\n");
    }

    /**
     * Compiles a do statement.
     */
    public void compileDo() throws IOException {
        out.write("<doStatement>\n");
        process("do");
        process(tokenizer.identifier());
        if (!this.tokenizer.token.equals("(")) {
            process(".");
            process(tokenizer.identifier());
        }
        process("(");
        compileExpressionList();
        process(")");
        process(";");
        out.write("</doStatement>\n");
    }

    /**
     * Compiles a return statement.
     */
    public void compileReturn() throws IOException {
        out.write("<returnStatement>\n");
        process("return");
        if(!this.tokenizer.token.equals(";")){
            compileExpression();
        }
        process(";");
        out.write("</returnStatement>\n");
    }

    /**
     * Compiles an expression.
     */
    public void compileExpression() throws IOException {
        out.write("<expression>\n");
        compileTerm();
        while(tokenizer.token.equals("+") || tokenizer.token.equals("-") || tokenizer.token.equals("*") || tokenizer.token.equals("/") || tokenizer.token.equals("&") || tokenizer.token.equals("|") || tokenizer.token.equals("<") || tokenizer.token.equals(">") || tokenizer.token.equals("=")){
            process(tokenizer.symbol()+"");
            compileTerm();
        }
        out.write("</expression>\n");
    }

    /**
     * Compiles a term.
     * If the current token is an identifier, the routine must distinguish between a variable, an array entry,
     * or a subroutine call. A single look-ahead token, which may be on of "[", "(", or ".", suffices to
     * distinguish between the possibilities. Any other token is not part of this term and should not advance over.
     */
    public void compileTerm() throws IOException {
        out.write("<term>\n");
        int type = this.tokenizer.tokenType();
        if (type == JackTokenizer.INT_CONST || type == JackTokenizer.STRING_CONST || type == JackTokenizer.KEYWORD){
            process(this.tokenizer.token);
            out.write("</term>\n");
            return;
        }
        if(tokenizer.token.equals("-")){
            process("-");
            compileTerm();
            out.write("</term>\n");
            return;
        } else if (tokenizer.token.equals("~")){
            process("~");
            compileTerm();
            out.write("</term>\n");
            return;
        } else if (tokenizer.token.equals("(")) {
            process("(");
            compileExpression();
            process(")");
            out.write("</term>\n");
            return;
        }
        process(tokenizer.identifier());
        switch (this.tokenizer.token){
            case "[":
                process("[");
                compileExpression();
                process("]");
                break;
            case "(":
                process("(");
                compileExpressionList();
                process(")");
                break;
            case ".":
                process(".");
                process(tokenizer.identifier());
                process("(");
                compileExpressionList();
                process(")");
        }
        out.write("</term>\n");
    }

    /**
     * Compiles a (possibly empty) comma-separated list of expression.
     */
    public void compileExpressionList() throws IOException {
        out.write("<expressionList>\n");
        if (!tokenizer.token.equals(")")){
            compileExpression();
            while(tokenizer.token.equals(",")){
                process(",");
                compileExpression();
            }
        }
        out.write("</expressionList>\n");
    }

    private void process(String str) throws IOException {
        if (this.tokenizer.token.equals(str)){
            printXMLToken(str);
        } else {
            out.write("syntax error\n");
            System.out.println("syntax error\n");
            System.exit(1);
        }
        this.tokenizer.advance();
    }

    private void printXMLToken(String str) throws IOException {
        //TODO
        switch (this.tokenizer.tokenType()){
            case JackTokenizer.KEYWORD:
                out.write("<keyword> "+ str + " </keyword>\n");
            break;
            case JackTokenizer.SYMBOL:
                switch (str){
                    case "<":
                        str = "&lt;";
                        break;
                    case ">":
                        str = "&gt;";
                        break;
                    case "&":
                        str = "&amp;";
                        break;
                }
                out.write("<symbol> " + str + " </symbol>\n");
            break;
            case JackTokenizer.INT_CONST:
                out.write("<integerConstant> " + str + " </integerConstant>\n");
            break;
            case JackTokenizer.STRING_CONST:
                out.write("<stringConstant> " + str.substring(1,str.length()-1) + " </stringConstant>\n");
            break;
            case JackTokenizer.IDENTIFIER:
                out.write("<identifier> " + str + " </identifier>\n");
            break;
        }
    }
}
