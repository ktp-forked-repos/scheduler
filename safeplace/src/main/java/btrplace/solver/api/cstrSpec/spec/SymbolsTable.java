package btrplace.solver.api.cstrSpec.spec;

import btrplace.solver.api.cstrSpec.Constraint;
import btrplace.solver.api.cstrSpec.spec.term.Primitive;
import btrplace.solver.api.cstrSpec.spec.term.UserVar;
import btrplace.solver.api.cstrSpec.spec.term.Var;
import btrplace.solver.api.cstrSpec.spec.term.func.*;
import btrplace.solver.api.cstrSpec.spec.type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fabien Hermenier
 */
public class SymbolsTable {

    private Map<String, Var> table;

    private Map<String, Constraint> cstrs;

    private Map<String, Function> funcs;

    private SymbolsTable parent;

    private List<Primitive> primitives;

    public SymbolsTable() {
        this(null);
    }

    public SymbolsTable(SymbolsTable p) {
        table = new HashMap<>();
        this.cstrs = new HashMap<>();
        this.funcs = new HashMap<>();
        parent = p;
        primitives = new ArrayList<>();
    }


    public static SymbolsTable newBundle() {
        SymbolsTable root = new SymbolsTable();
        //Core functions
        root.put(new Host());
        root.put(new Hosted());
        root.put(new Running());
        root.put(new Ready());
        root.put(new Sleeping());
        root.put(new Cons());
        root.put(new Capa());
        root.put(new Colocated());
        root.put(new VMState());
        root.put(new NodeState());
        root.put(new Card());
        root.put(new Sum());
        root.put(new P());
        return root;
    }

    public SymbolsTable enterSpec() {
        SymbolsTable syms = new SymbolsTable(this);
        //Copy the primitives
        syms.put(new Primitive("vms", VMType.getInstance()));
        syms.put(new Primitive("nodes", NodeType.getInstance()));
        syms.put(new Primitive("vmState", VMStateType.getInstance()));
        syms.put(new Primitive("nodeState", NodeStateType.getInstance()));
        syms.put(new Primitive("int", IntType.getInstance()));
        syms.put(new Primitive("bool", BoolType.getInstance()));
        syms.put(new Primitive("real", RealType.getInstance()));
        syms.put(new Primitive("string", StringType.getInstance()));
        return syms;
    }

    public SymbolsTable enterScope() {
        return new SymbolsTable(this);
    }

    public SymbolsTable leaveScope() {
        return parent;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (parent != null) {
            b.append(parent.toString());
            b.append("--------------\n");
        }
        for (Map.Entry<String, Var> e : table.entrySet()) {
            b.append("var \t").append(e.getKey()).append("\t").append(e.getValue().type()).append("\n");
        }
        for (Map.Entry<String, Function> e : funcs.entrySet()) {
            b.append("func\t").append(e.getValue()).append("\t").append(e.getValue().type()).append("\n");
        }
        for (Map.Entry<String, Constraint> e : cstrs.entrySet()) {
            b.append("cstr\t").append(e.getValue()).append("\n");
        }
        return b.toString();
    }

    public boolean put(Function f) {
        if (funcs.containsKey(f.id())) {
            return false;
        }
        funcs.put(f.id(), f);
        return true;
    }

    public Function getFunction(String id) {
        if (funcs.containsKey(id)) {
            return funcs.get(id);
        }
        if (parent != null) {
            return parent.getFunction(id);
        }
        return null;
    }


    private boolean putVar(Var v) {
        if (table.containsKey(v.label())) {
            return false;
        }
        table.put(v.label(), v);
        return true;
    }

    public Var getVar(String n) {
        if (table.containsKey(n)) {
            return table.get(n);
        }
        if (parent != null) {
            return parent.getVar(n);
        }
        return null;
    }

    public void put(Primitive p) {
        if (putVar(p)) {
            primitives.add(p);
        }
    }

    public boolean put(UserVar v) {
        return putVar(v);
    }

    public void put(Constraint cstr) {
        cstrs.put(cstr.id(), cstr);
    }

    public Constraint getConstraint(String id) {
        if (cstrs.containsKey(id)) {
            return cstrs.get(id);
        }
        return parent == null ? null : parent.getConstraint(id);
    }

    public List<Primitive> getPrimitives() {
        return primitives;
    }

}
