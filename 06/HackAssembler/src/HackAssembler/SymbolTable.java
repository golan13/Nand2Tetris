

import java.util.HashMap;
import java.util.Map;

public class SymbolTable{

    Map<String, Integer> map;

    /*
    Creates a new empty symbol table
     */
    public SymbolTable(){
        map = new HashMap<String, Integer>();
        map.put("R0", 0);
        map.put("R1", 1);
        map.put("R2", 2);
        map.put("R3", 3);
        map.put("R4", 4);
        map.put("R5", 5);
        map.put("R6", 6);
        map.put("R7", 7);
        map.put("R8", 8);
        map.put("R9", 9);
        map.put("R10", 10);
        map.put("R11", 11);
        map.put("R12", 12);
        map.put("R13", 13);
        map.put("R14", 14);
        map.put("R15", 15);
        map.put("SCREEN", 16384);
        map.put("KBD", 24576);
        map.put("SP", 0);
        map.put("LCL", 1);
        map.put("ARG", 2);
        map.put("THIS", 3);
        map.put("THAT", 4);
    }

    /*
    Adds <symbol, address> to the table
     */
    public void addEntry(String symbol, int address){
        this.map.put(symbol,address);
    }

    /*
    Does the symbol table contain the given symbol?
     */
    public boolean contains(String symbol){
        return this.map.containsKey(symbol);
    }

    /*
    Returns the address associated with the symbol
     */
    public int getAddress(String symbol){
        return this.map.get(symbol);
    }
}
