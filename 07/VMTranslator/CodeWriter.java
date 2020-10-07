import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class CodeWriter {

    String filename;
    String programname;
    BufferedWriter out;
    int n = 0;

    /*
    Opens an output file / stream and gets ready to write into it.
     */
    public CodeWriter(String filename) throws IOException {
        this.filename = filename;
        this.out = new BufferedWriter(new FileWriter(filename));
        this.programname = Paths.get(filename).getFileName().toString();
        this.programname = this.programname.substring(0,this.programname.indexOf("."));
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
                    "@EQ"+ this.n +"\n"+
                    "D;JEQ\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@END" + this.n + "\n" +
                    "0;JMP\n" +
                    "(EQ" + this.n +")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(END"+this.n+")\n";
                this.n++;
                break;
            case "gt": output +=
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "@GT"+ this.n +"\n"+
                    "D;JGT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@END" + this.n + "\n" +
                    "0;JMP\n" +
                    "(GT" + this.n +")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(END"+this.n+")\n";
                this.n++;
                break;
            case "lt": output +=
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "D=M-D\n" +
                    "@LT"+ this.n +"\n"+
                    "D;JLT\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=0\n" +
                    "@END" + this.n + "\n" +
                    "0;JMP\n" +
                    "(LT" + this.n +")\n" +
                    "@SP\n" +
                    "A=M-1\n" +
                    "M=-1\n" +
                    "(END"+this.n+")\n";
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
                        "@" + this.programname + "." + index + "\n" +
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
                        "@"+this.programname+ "." + index + "\n" +
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
    Closes the output file.
     */
    public void close() throws IOException{
        this.out.close();
    }
}
