package org.toubassi.littlescript.compiler;

import com.sun.xml.internal.bind.v2.TODO;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gtoubassi on 7/20/15.
 */
public class SymbolTable {

    private Map<String, Integer> locals = new HashMap();

    /*
    TODO
    3. need to copy parameters from registers to locals
    */


    public int getFrameOffsetFor(String identifier, AssemblyWriter writer) {
        Integer offset = locals.get(identifier);
        if (offset == null) {
            // Not declared yet, so must be a local variable
            // vs an argument
            writer.emitComment("Defining local " + identifier);
            writer.emit("pushq", "$0");
            offset = - 8 - 8 * locals.size();
            locals.put(identifier, offset);
        }
        return offset;
    }

    public void popLocals(AssemblyWriter writer) {
        if (locals.size() > 0) {
            writer.emit("addq", "$" + (8 * locals.size()), "%rsp");
        }
    }
}
