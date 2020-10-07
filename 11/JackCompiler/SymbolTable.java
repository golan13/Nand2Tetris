import java.nio.file.Path;
import java.util.Hashtable;

public class SymbolTable {

    int field;
    int staticI;
    int argument;
    int local;
    Hashtable<String,String> classType;
    Hashtable<String,Integer> classKind;
    Hashtable<String,Integer> classIndex;
    Hashtable<String,String> subroutineType = null;
    Hashtable<String,Integer> subroutineKind = null;
    Hashtable<String,Integer> subroutineIndex = null;
    static final int NONE = -1;
    static final int STATIC = 0;
    static final int FIELD = 1;
    static final int ARG = 2;
    static final int VAR = 3;

    /**
     * Creates a new symbol table.
     */
    public SymbolTable(){
        field = 0;
        staticI = 0;
        classType = new Hashtable<>();
        classKind = new Hashtable<>();
        classIndex = new Hashtable<>();
    }

    /**
     * Starts a new subroutine scope (i.e, resets the subroutine's symbol table)
     */
    public void startSubroutine(){
        argument = 0;
        local = 0;
        subroutineType = new Hashtable<>();
        subroutineKind = new Hashtable<>();
        subroutineIndex = new Hashtable<>();
    }

    /**
     * Defines a new identifier of the given name, type and kind and assigns it a running index.
     * @param name
     * @param type
     * @param kind
     */
    public void define(String name, String type, int kind){
        if (kind == SymbolTable.STATIC || kind == SymbolTable.FIELD){
            classType.put(name,type);
            classKind.put(name,kind);
            if (kind == SymbolTable.STATIC){
                classIndex.put(name,staticI);
                staticI++;
            } else {
                classIndex.put(name,field);
                field++;
            }
        } else if (kind == SymbolTable.VAR || kind == SymbolTable.ARG) {
            subroutineType.put(name,type);
            subroutineKind.put(name,kind);
            if (kind == SymbolTable.VAR){
                subroutineIndex.put(name,local);
                local++;
            } else {
                subroutineIndex.put(name,argument);
                argument++;
            }
        }
    }

    /**
     * Returns the number of variables of the given kind already defined in the current scope.
     * @param kind
     * @return
     */
    public int varCount(int kind){
        switch (kind){
            case SymbolTable.STATIC: return staticI;
            case SymbolTable.VAR: return local;
            case SymbolTable.FIELD: return field;
            case SymbolTable.ARG: return argument;
        }
        return -1;
    }

    /**
     * Returns the kind of the named identifier in the current scope.
     * If the identifier is unknown in the current scope, returns NONE.
     * @param name
     * @return
     */
    public int kindOf(String name){
        if (this.subroutineKind!= null) {
            if (this.subroutineKind.get(name) != null) {
                return this.subroutineKind.get(name);
            }
        }
        if (this.classKind.get(name) != null){
            return this.classKind.get(name);
        }
        return SymbolTable.NONE;
    }

    /**
     * Returns the type of the named identifier in the current scope.
     * @param name
     * @return
     */
    public String typeOf(String name){
        if (this.subroutineType != null){
            if (this.subroutineType.get(name) != null){
                return this.subroutineType.get(name);
            }
        }
        if (this.classType.get(name) != null){
            return this.classType.get(name);
        }
        return "";
    }

    /**
     * Returns the index assigned to the named identifier.
     * @param name
     * @return
     */
    public int indexOf(String name){
        if (this.subroutineIndex != null){
            if (this.subroutineIndex.get(name) != null){
                return this.subroutineIndex.get(name);
            }
        }
        if (this.classIndex.get(name) != null){
            return this.classIndex.get(name);
        }
        return -1;
    }
}
