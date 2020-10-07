

import java.util.Map;

public class Code {

    private static Map<String,String> destMap = Map.ofEntries(
            Map.entry("", "000"),
            Map.entry("0","0101010"),
            Map.entry("1", "0111111"),
            Map.entry("-1", "0111010"),
            Map.entry("D", "0001100"),
            Map.entry("A", "0110000"),
            Map.entry("!D", "0001101"),
            Map.entry("!A", "0110001"),
            Map.entry("-D", "0001111"),
            Map.entry("-A", "0110011"),
            Map.entry("D+1", "0011111"),
            Map.entry("A+1", "0110111"),
            Map.entry("D-1", "0001110"),
            Map.entry("A-1", "0110010"),
            Map.entry("D+A", "0000010"),
            Map.entry("D-A", "0010011"),
            Map.entry("A-D", "0000111"),
            Map.entry("D&A", "0000000"),
            Map.entry("D|A", "0010101"),
            Map.entry("M", "1110000"),
            Map.entry("!M", "1110001"),
            Map.entry("-M", "1110011"),
            Map.entry("M+1", "1110111"),
            Map.entry("M-1", "1110010"),
            Map.entry("D+M", "1000010"),
            Map.entry("D-M", "1010011"),
            Map.entry("M-D", "1000111"),
            Map.entry("D&M", "1000000"),
            Map.entry("D|M", "1010101")
    );

    /*
    Returns the binary code for the dest mnemonic
     */
    public static String dest(String code){
        String binary = "";
        if (code.contains("A")){
            binary += "1";
        } else {
            binary += "0";
        }
        if (code.contains("D")){
            binary += "1";
        } else {
            binary += "0";
        }
        if (code.contains("M")){
            binary += "1";
        } else {
            binary += "0";
        }
        return binary;
    }

    /*
    Returns the binary code the for comp mnemonic
     */
    public static String comp(String code){
        return destMap.get(code);
    }

    /*
    Returns the binary code for the jump mnemonic
     */
    public static String jump(String code){
        String binary = "";
        switch (code){
            case "JGT": binary = "001";
            break;
            case "JEQ": binary = "010";
            break;
            case "JGE": binary = "011";
            break;
            case "JLT": binary = "100";
            break;
            case "JNE": binary = "101";
            break;
            case "JLE": binary = "110";
            break;
            case "JMP": binary = "111";
            break;
            default: binary = "000";
        }
        return binary;
    }
}
