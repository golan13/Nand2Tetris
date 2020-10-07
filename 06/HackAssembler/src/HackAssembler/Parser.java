

import java.io.*;

public class Parser {

    BufferedReader in;
    String filename;
    String instruction;
    public static final int A_INSTRUCTION = 0;
    public static final int C_INSTRUCTION = 1;
    public static final int L_INSTRUCTION = 2;
    /*
        Constructor for creating a parser instance
         */
    public Parser(String fileName) throws IOException{
        filename = fileName;
        in = new BufferedReader(new FileReader(fileName));
    }
    /*
    Check if we are finished
     */
    public boolean hasMoreLines() throws IOException {
        return this.in.ready();
    }

    /*
    Getting the next instruction and making it the current instruction
     */
    public void advance() throws IOException{
        String line = "";
        if (hasMoreLines()){
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
    }

    /*
    Returns the type of the current instruction
    0 == A instruction
    1 == C instruction
    2 == L instruction
     */
    public int instructionType(){
        if (this.instruction.charAt(0) == '@'){
            return A_INSTRUCTION;
        } else if (this.instruction.charAt(0) == '(' & this.instruction.charAt(this.instruction.length()-1) == ')'){
            return L_INSTRUCTION;
        } else {
            return C_INSTRUCTION;
        }
    }

    /*
    If the current instruction is (xxx), returns the symbol xxx
    If the current instruction is @xxx, returns the symbol of decimal xxx
     */
    public String symbol(){
        if (this.instructionType() == A_INSTRUCTION){
            return this.instruction.substring(1);
        } else {
            return this.instruction.substring(1, this.instruction.length()-1);
        }
    }

    /*
    Returns the symbolic dest part of the current C-instruction
     */
    public String dest(){
        String dest = "";
        if (this.instruction.indexOf('=') != -1) {
            dest = this.instruction.substring(0, this.instruction.indexOf('='));
        }
        return dest;
    }

    /*
    Returns the symbolic comp part of the current C-instruction
     */
    public String comp(){
        String comp = "";
        int indexEq = this.instruction.indexOf('=');
        int indexCol = this.instruction.indexOf(';');
        if (indexEq != -1) {
            if (indexCol != -1){
                comp = this.instruction.substring(indexEq + 1,indexCol);
            } else {
                comp = this.instruction.substring(indexEq + 1);
            }
        } else {
            comp = this.instruction.substring(0, indexCol);
        }
        return comp;
    }

    /*
    Returns the symbolic jump part of the current C-instruction
     */
    public String jump(){
        String jump = "";
        int indexCol = this.instruction.indexOf(';');
        if (indexCol != -1){
            jump = this.instruction.substring(indexCol + 1);
        }
        return jump;
    }
}
