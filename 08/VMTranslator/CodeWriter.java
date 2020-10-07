import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeWriter {

    String filename;
    BufferedWriter out;
    String function;
    int n = 0;
    int m = 0;

    /*
    Opens an output file / stream and gets ready to write into it.
    Writes the assembly instructions that effect the bootstrap code that starts the program's execution.
    This code must be placed at the beginning of the generated output file / stream.
     */
    public CodeWriter(String filename) throws IOException {
        //TODO check if filename is directory or file. Write bootstrap only for directories
        //Check if filename is directory or a single .vm file
        File file = new File(filename);
        if(file.isDirectory()) {
            this.filename = Paths.get(filename).getFileName().toString();
            this.out = new BufferedWriter(new FileWriter(Paths.get(filename,this.filename).toString() + ".asm"));
            //Bootstrap
            this.out.write("//Bootstrap Code\n" +
                    "@256\n" +
                    "D=A\n" +
                    "@SP\n" +
                    "M=D\n" +
                    "@LCL\n" +
                    "MD=-1\n" +
                    "@ARG\n" +
                    "MD=D-1\n" +
                    "@THIS\n" +
                    "MD=D-1\n" +
                    "@THAT\n" +
                    "M=D-1\n");
            this.writeCall("Sys.init",0);
        } else {
            this.filename = Paths.get(filename).getFileName().toString().replaceAll("\\.vm","");
            this.out = new BufferedWriter(new FileWriter(filename.replaceAll("\\.vm","") + ".asm"));
        }
    }

    /*
    Informs that the translation of a new VM file has started (called by the VMTranslator)
     */
    public void setFilename(String filename){
        this.filename = filename;
    }

    /*
    Writes to the output file the assembly code that implements the given arithmetic-logical command.
     */
    public void writeArithmetic(String command) throws IOException{
        String output = "//" + command + "\n";
        switch(command){
            case "add": output +=
                    "@SP\n" +
                    "M=M-1\n" +
                    "A=M\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M+D\n" +
                    "M=D\n";
                break;
            case "sub": output +=
                    "@SP\n" +
                    "M=M-1\n" +
                    "A=M\n" +
                    "D=-M\n" +
                    "A=A-1\n" +
                    "D=M+D\n" +
                    "M=D\n";
                break;
            case "neg": output +=
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-M\n";
                break;
            case "eq": output +=
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=D-M\n" +
                    "@EQ." + this.n +"\n"+
                    "D;JEQ\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@ENDeq." + this.n + "\n" +
                    "0;JMP\n" +
                    "(EQ." + this.n +")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(ENDeq." + this.n + ")\n";
                this.n++;
                break;
            case "gt": output +=
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "@GT." + this.n + "\n"+
                    "D;JGT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@ENDgt." + this.n + "\n" +
                    "0;JMP\n" +
                    "(GT." + this.n + ")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(ENDgt." + this.n + ")\n";
                this.n++;
                break;
            case "lt": output +=
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "@LT." + this.n +"\n"+
                    "D;JLT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@ENDlt." + this.n + "\n" +
                    "0;JMP\n" +
                    "(LT." + this.n +")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(ENDlt" + this.n + ")\n";
                this.n++;
                break;
            case "and": output +=
                    "@SP\n" +
                    "M=M-1\n" +
                    "A=M\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=D&M\n";
                break;
            case "or": output +=
                    "@SP\n" +
                    "M=M-1\n" +
                    "A=M\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=D|M\n";
                break;
            case "not": output +=
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=!M\n";
                break;
        }
        this.out.write(output);
    }

    /*
    Writes to the output file the assembly code that implements the given push or pop command.
     */
    public void writePushPop(int type, String segment, int index) throws IOException{
        String command = "";
        String output = "";
        String start = "";
        String finish = "";
        if (type == Parser.C_PUSH){
            command = "push";
            start  = "@" + index + "\nD=A\n";
            finish = "A=M+D\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "M=M+1\n" +
                    "A=M-1\n" +
                    "M=D\n";
            switch(segment){
                case "local": output = start +"@LCL\n" + finish;
                    break;
                case "argument": output = start + "@ARG\n" + finish;
                    break;
                case "this": output = start + "@THIS\n" + finish;
                    break;
                case "that": output = start + "@THAT\n" + finish;
                    break;
                case "constant": output =
                        "@" + index + "\n" +
                                "D=A\n" +
                                "@SP\n" +
                                "M=M+1\n" +
                                "A=M-1\n" +
                                "M=D\n";
                    break;
                case "static": output =
                        "@" + this.filename + "." + index + "\n" +
                                "D=M\n" +
                                "@SP\n" +
                                "M=M+1\n" +
                                "A=M-1\n" +
                                "M=D\n";
                    break;
                case "temp": output =
                        "@" + (index+5) + "\n" +
                                "D=M\n" +
                                "@SP\n" +
                                "M=M+1\n" +
                                "A=M-1\n" +
                                "M=D\n";
                    break;
                case "pointer":
                    if (index == 0) {
                        output = "@THIS\n";
                    } else {
                        output = "@THAT\n" ;
                    }
                    output += "D=M\n" +
                            "@SP\n" +
                            "M=M+1\n" +
                            "A=M-1\n" +
                            "M=D\n";
                    break;
            }
        } else if (type == Parser.C_POP){
            command = "pop";
            start = "@"+index+"\nD=A\n";
            finish = "D=M+D\n@13\nM=D\n@SP\nAM=M-1\nD=M\n@13\nA=M\nM=D\n";
            switch (segment) {
                case "local": output = start + "@LCL\n" + finish;
                    break;
                case "argument": output = start + "@ARG\n" + finish;
                    break;
                case "this": output = start + "@THIS\n" + finish;
                    break;
                case "that": output = start + "@THAT\n" + finish;
                    break;
                case "static": output = "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@" + this.filename+ "." + index + "\n" +
                        "M=D\n";
                    break;
                case "pointer":
                    start = "@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n";
                    finish = "M=D\n";
                    if (index == 0){
                        output = start + "@THIS\n" + finish;
                    } else {
                        output = start + "@THAT\n" + finish;
                    }
                    break;
                case "temp": output = "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@" + (index+5) + "\n" +
                        "M=D\n";
                    break;
            }
        }
        this.out.write("//"+command+ " " + segment + " " + index + "\n" + output);
    }

    /*
    Writes assembly code that effects the label command.
     */
    public void writeLabel(String label) throws IOException{
        String output = "//label " + label + "\n" +
                "(" + this.function + "$" + label + ")\n";
        this.out.write(output);
    }

    /*
    Writes assembly code that effects the goto command.
     */
    public void writeGoto(String label) throws IOException{
        String output = "//goto " + label + "\n" +
                "@" + this.function + "$" + label + "\n" +
                "0;JMP\n";
        this.out.write(output);
    }

    /*
    Writes assembly code that effects the if-goto command.
     */
    public void writeIf(String label) throws IOException{
        String output = "//if-goto " + label + "\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@" + this.function + "$" + label + "\n" +
                "D;JNE\n"; //Jump if true (-1)
        this.out.write(output);
    }

    /*
    Writes assembly code that effects the function command.
     */
    public void writeFunction(String functionName, int nVars) throws IOException{
        String output = "//function " + functionName + " " + nVars + "\n";
        output += "(" + functionName + ")\n";
        //Initialize local variables for function
        for(int i = 0; i < nVars; i++) {
            output += "@SP\n" +
                    "M=M+1\n" +
                    "A=M-1\n" +
                    "M=0\n";
        }
        this.function = functionName;
        this.out.write(output);
    }

    /*
    Writes assembly code that effects the call command.
     */
    public void writeCall(String functionName, int nArgs) throws IOException{
        String output = "//call " + functionName + " " + nArgs + "\n" +
                "@" + functionName + "$ret." + this.m + "\n" +
                "D=A\n" +   //D = return address label
                "@SP\n" +
                "M=M+1\n" +
                "A=M-1\n" +
                "M=D\n" +   //Pushed return address to the stack
                "@LCL\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M+1\n" +
                "A=M-1\n" +
                "M=D\n" +   //Pushed LCL to the stack
                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M+1\n" +
                "A=M-1\n" +
                "M=D\n" +   //Pushed ARG to the stack
                "@THIS\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M+1\n" +
                "A=M-1\n" +
                "M=D\n" +   //Pushed THIS to the stack
                "@THAT\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M+1\n" +
                "A=M-1\n" +
                "M=D\n" +   //Pushed THAT to the stack
                "@SP\n" +
                "D=M\n" +
                "@5\n" +
                "D=D-A\n" + //D = SP - 5
                "@" + nArgs + "\n" +
                "D=D-A\n" + //D = SP - 5 - nArgs
                "@ARG\n" +
                "M=D\n" +   //ARG = D = SP - 5 - nArgs
                "@SP\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n" +   //LCL = SP
                "@" + functionName + "\n" +
                "0;JMP\n" +
                "(" + functionName + "$ret." + this.m + ")\n";
        this.out.write(output);
        this.m++;
    }

    /*
    Writes assembly code that effects the return command.
     */
    public void writeReturn() throws IOException{
        String output = "//return\n" +
                "@LCL\n" +
                "D=M\n" +
                "@13\n" +
                "M=D\n" +   //RAM[13] = LCL = endFrame
                "@5\n" +
                "D=A\n" +
                "@13\n" +
                "A=M-D\n" +
                "D=M\n" +
                "@14\n" +
                "M=D\n" +   //RAM[14] = retAddr
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@ARG\n" +
                "A=M\n" +
                "M=D\n" +   //*ARG = pop()
                "D=A+1\n" +
                "@SP\n" +
                "M=D\n" +   //SP = ARG + 1
                "@13\n" +
                "AM=M-1\n" + //RAM[13] = endFrame - 1
                "D=M\n" +
                "@THAT\n" +
                "M=D\n" +   //RAM[THAT] = *(endFrame - 1)
                "@13\n" +
                "AM=M-1\n" +    //RAM[13] = endFrame - 2
                "D=M\n" +
                "@THIS\n" +
                "M=D\n" +   //RAM[THIS] = *(endFrame - 2)
                "@13\n" +
                "AM=M-1\n" +    //RAM[13] = endFrame - 3
                "D=M\n" +
                "@ARG\n" +
                "M=D\n" +   //RAM[ARG] = *(endFrame - 3)
                "@13\n" +
                "A=M-1\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n" +   //RAM[ARG] = *(endFrame - 4)
                "@14\n" +
                "A=M\n" +
                "0;JMP";
        this.out.write(output);
    }

    /*
    Closes the output file.
     */
    public void close() throws IOException{
        this.out.close();
    }
}
