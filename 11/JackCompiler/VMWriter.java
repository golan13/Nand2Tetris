import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

    BufferedWriter out;

    /**
     * Creates a new output .vm file and prepares it for writing.
     * @param output
     */
    public VMWriter(String output) throws IOException {
        this.out = new BufferedWriter(new FileWriter(output));
    }

    /**
     * writes a VM push command.
     * @param segment
     * @param index
     */
    public void writePush(String segment, int index) throws IOException {
        out.write("push " + segment + " " + index + "\n");
    }

    /**
     * Writes a VM pop command.
     * @param segment
     * @param index
     */
    public void writePop(String segment, int index) throws IOException {
        out.write("pop " + segment + " " + index + "\n");
    }

    /**
     * Writes a VM arithmetic-logical command.
     * @param command
     */
    public void writeArithmetic(String command) throws IOException {
        out.write(command + "\n");
    }

    /**
     * Writes a VM label command.
     * @param label
     */
    public void writeLabel(String label) throws IOException {
        out.write( "label " + label + "\n");
    }

    /**
     * Writes a VM goto command.
     * @param label
     */
    public void writeGoto(String label) throws IOException {
        out.write("goto " + label + "\n");
    }

    /**
     * Writes a VM if-goto command.
     * @param label
     */
    public void writeIf(String label) throws IOException {
        out.write("if-goto " + label + "\n");
    }

    /**
     * Writes a VM call command.
     * @param name
     * @param nArgs
     */
    public void writeCall(String name, int nArgs) throws IOException {
        out.write("call " + name + " " + nArgs + "\n");
    }

    /**
     * Writes a VM function command.
     * @param name
     * @param nLocals
     */
    public void writeFunction(String name, int nLocals) throws IOException {
        out.write("function " + name + " " + nLocals + "\n");
    }

    /**
     * Writes a VM return command.
     */
    public void writeReturn() throws IOException {
        out.write("return\n");
    }

    /**
     * closes the output file.
     */
    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
