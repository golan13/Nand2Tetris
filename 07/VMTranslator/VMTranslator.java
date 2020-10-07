import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VMTranslator {
        /*
        Constructs a Parser to handle the input file.
        Constructs a CodeWriter to handle the output file.
        Marches through the input file, parsing each line and generating code from it.
         */
        public static void main(String[] args) {
                Parser parser;
                CodeWriter codeWriter;
                if (args.length != 1) {
                        usage();
                        return;
                }
                try {
                        if (Paths.get(args[0]).isAbsolute()) {
                                parser = new Parser(args[0]);
                                String directory = Paths.get(args[0]).getParent().toString();
                                String name = Paths.get(args[0]).getFileName().toString() + ".asm";
                                name = name.replaceAll("\\.vm", "");
                                String filepath = Path.of(directory, name).toString();
                                codeWriter = new CodeWriter(filepath);
                        } else {
                                String directory = System.getProperty("user.dir");
                                String outputname = args[0].replaceAll("\\.vm","") + ".asm";
                                parser = new Parser(Path.of(directory,args[0]).toString());
                                codeWriter = new CodeWriter(Path.of(directory,outputname).toString());
                        }
                        while(parser.hasMoreLines()){
                                parser.advance();
                                switch (parser.commandType()){
                                        case Parser.C_ARITHMETIC: codeWriter.writeArithmetic(parser.arg1());
                                        break;
                                        case Parser.C_POP: codeWriter.writePushPop(Parser.C_POP, parser.arg1(), parser.arg2());
                                        break;
                                        case Parser.C_PUSH: codeWriter.writePushPop(Parser.C_PUSH, parser.arg1(), parser.arg2());
                                        break;
                                }
                        }
                        codeWriter.close();
                } catch (Exception e){
                        System.out.println("An error occurred");
                        return;
                }
        }
        public static void usage(){
                System.out.println("Usage: java HackAssembler <path to asm file>");
        }
}
