

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HackAssembler {
    public static void main(String[] args){
        Parser firstPass;
        Parser secondPass;
        if (args.length > 1 | args.length == 0){
            usage();
            return;
        }
        try {
            int counter = 0;
            firstPass = new Parser(args[0]);
            SymbolTable symbolTable = new SymbolTable();
            //First pass to add label symbols
            while (firstPass.hasMoreLines()){
                firstPass.advance();
                if (firstPass.instructionType() == firstPass.L_INSTRUCTION){
                    symbolTable.addEntry(firstPass.symbol(), counter);
                } else {
                    counter++;
                }
            }
            String output = "";
            String line = "";
            int n = 16;
            secondPass = new Parser(args[0]);
            while (secondPass.hasMoreLines()){
                secondPass.advance();
                int type = secondPass.instructionType();
                if (type == secondPass.A_INSTRUCTION){
                    String symbol = secondPass.symbol();
                    if (!symbolTable.contains(symbol)){
                        //Check if referenced to memory address or label
                        int address;
                        try{
                            address = Integer.parseInt(symbol);
                            line = Integer.toBinaryString(address);
                        } catch (Exception e) {
                            symbolTable.addEntry(symbol, n++);
                            line = Integer.toBinaryString(n-1);
                        }
                    } else {
                        //Symbol is in symbol table
                        line = Integer.toBinaryString(symbolTable.getAddress(symbol));
                    }
                } else if (type == secondPass.C_INSTRUCTION){
                    String dest = secondPass.dest();
                    String comp = secondPass.comp();
                    String jump = secondPass.jump();
                    line = "111" + Code.comp(comp) + Code.dest(dest) + Code.jump(jump);
                } else {
                    continue;
                }
                line = "0".repeat(16-line.length()) + line;
                output += line + "\n";
            }
            String directory = Paths.get(args[0]).getParent().toString();
            String name = Paths.get(args[0]).getFileName().toString() + ".hack";
            name = name.replaceAll("\\.asm","");
            String filepath = Path.of(directory, name).toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            writer.write(output);
            writer.close();
        } catch (Exception e) {
            System.out.println("An error occurred");
            return;
        }
    }
    public static void usage(){
        System.out.println("Usage: java HackAssembler <path to asm file>");
    }
}
