import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;

public class VMTranslator {
        /*
        Constructs a Parser to handle the input file.
        Constructs a CodeWriter to handle the output file.
        Marches through the input file, parsing each line and generating code from it.
         */
        public static void main(String[] args) {
                Parser parser = null;
                CodeWriter codeWriter = null;
                if (args.length != 1) {
                        usage();
                        return;
                }
                try {
                        //TODO merge if into one statement and send to codeWriter
                        codeWriter = new CodeWriter(Paths.get(args[0]).toAbsolutePath().toString());
                        File file = new File(args[0]);
                        if(file.isDirectory()) {
                                for (File fileEntry : Objects.requireNonNull(file.listFiles(new FilenameFilter() {
                                        @Override
                                        public boolean accept(File dir, String name) {
                                                return name.toLowerCase().endsWith(".vm");
                                        }
                                }))){
                                        parser = new Parser(fileEntry.getAbsolutePath().toString());
                                        codeWriter.setFilename(fileEntry.getName().replaceAll("\\.vm",""));
                                        parseAndWrite(parser, codeWriter);

                                }
                        } else {
                                String path = Paths.get(args[0]).toAbsolutePath().toString(); //path to .vm file
                                parser = new Parser(path);
                                codeWriter = new CodeWriter(args[0]);
                                parseAndWrite(parser, codeWriter);
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
        public static void parseAndWrite(Parser parser, CodeWriter codeWriter) throws IOException {
                while (parser.hasMoreLines()) {
                        parser.advance();
                        switch (parser.commandType()) {
                                case Parser.C_ARITHMETIC:
                                        codeWriter.writeArithmetic(parser.arg1());
                                        break;
                                case Parser.C_POP:
                                        codeWriter.writePushPop(Parser.C_POP, parser.arg1(), parser.arg2());
                                        break;
                                case Parser.C_PUSH:
                                        codeWriter.writePushPop(Parser.C_PUSH, parser.arg1(), parser.arg2());
                                        break;
                                case Parser.C_CALL:
                                        codeWriter.writeCall(parser.arg1(), parser.arg2());
                                        break;
                                case Parser.C_FUNCTION:
                                        codeWriter.writeFunction(parser.arg1(), parser.arg2());
                                        break;
                                case Parser.C_GOTO:
                                        codeWriter.writeGoto(parser.arg1());
                                        break;
                                case Parser.C_IF:
                                        codeWriter.writeIf(parser.arg1());
                                        break;
                                case Parser.C_LABEL:
                                        codeWriter.writeLabel(parser.arg1());
                                        break;
                                case Parser.C_RETURN:
                                        codeWriter.writeReturn();
                                        break;
                        }
                }
        }
}
