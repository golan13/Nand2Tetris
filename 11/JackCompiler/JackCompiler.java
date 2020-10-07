import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class JackCompiler {
    public static void main(String[] args){
        CompilationEngine compiler = null;
        if (args.length != 1) {
            usage();
            return;
        }
        try {
            File file = new File(args[0]);
            // If input is directory
            if(file.isDirectory()) {
                for (File fileEntry : Objects.requireNonNull(file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jack");
                    }
                }))){
                    String output = Paths.get(fileEntry.getParent().toString(),fileEntry.getName().substring(0,fileEntry.getName().indexOf("."))+".vm").toString();
                    compiler = new CompilationEngine(fileEntry.getAbsolutePath().toString(), output);
                    compiler.compileClass();
                }
            } else {
                // If input is single .jack file
                String output = Paths.get(file.getParent().toString(),file.getName().substring(0,file.getName().indexOf("."))+".vm").toString();
                compiler = new CompilationEngine(file.getAbsolutePath().toString(),output);
                compiler.compileClass();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void usage(){
        System.out.println("Usage: java JackCompiler <path to jack file or folder>");
    }

}
