package org.toubassi.littlescript.compiler;

/**
 * Created by gtoubassi on 7/20/15.
 */
public class AssemblyWriter {
    private StringBuilder functions = new StringBuilder();
    private StringBuilder builder = new StringBuilder();
    private int labelCounter = 1;

    public void emitComment(String comment) {
        builder.append("\n\t# " + comment + "\n");
    }

    public String getLabel() {
        return "L" + (labelCounter++);
    }

    public void emitLabel(String label) {
        builder.append(label);
        builder.append(":\n");
    }

    public void emit(String opcode, String... args) {
        builder.append('\t' + opcode);
        for (int i = 0; i < args.length; i++) {
            builder.append(i == 0 ? "\t" : ", ");
            builder.append(args[i]);
        }
        builder.append('\n');
    }

    public void begin() {
        builder.append("\t.section\t__TEXT,__text,regular,pure_instructions\n");
    }

    public String end() {
        builder.append(functions);

        builder.append("\n\n.subsections_via_symbols\n");
        builder.append("\t.section\t__TEXT,__cstring,cstring_literals\n");
        builder.append("L_.printfmt:\n");
        builder.append("\t.asciz  \"%d\\n\"\n");
        return builder.toString();
    }

    public void beginFunction() {
        StringBuilder tmp = builder;
        builder = functions;
        functions = tmp;
    }

    public void endFunction() {
        StringBuilder tmp = builder;
        builder = functions;
        functions = tmp;
    }
}
