package btrplace.solver.api.cstrSpec.spec.prop;

import btrplace.model.Model;
import btrplace.solver.api.cstrSpec.spec.term.Term;
import btrplace.solver.api.cstrSpec.spec.term.UserVar;
import btrplace.solver.api.cstrSpec.spec.term.Var;
import btrplace.solver.api.cstrSpec.util.AllTuplesGenerator;

import java.util.*;

/**
 * @author Fabien Hermenier
 */
public class ForAll implements Proposition {

    private List<UserVar> vars;

    private Term<Set> from;

    private Proposition prop;

    public ForAll(List<UserVar> vars, Proposition p) {
        this.vars = vars;
        this.from = vars.get(0).getBackend();
        prop = p;
    }

    @Override
    public Proposition not() {
        return new Exists(vars, prop.not());
    }

    @Override
    public Boolean eval(Model m) {
        boolean ret = true;
        List<List<Object>> values = new ArrayList<>(vars.size());
        for (int i = 0; i < vars.size(); i++) {
            Collection<Object> o = from.eval(m);
            if (o == null) {
                return null;
            }
            values.add(new ArrayList<>(o));
        }
        AllTuplesGenerator<Object> tg = new AllTuplesGenerator<>(Object.class, values);
        for (Object[] tuple : tg) {
            for (int i = 0; i < tuple.length; i++) {
                vars.get(i).set(tuple[i]);
            }
            Boolean r = prop.eval(m);
            if (r == null) {
                return null;
            }
            ret &= r;
        }
        for (UserVar v : vars) {
            v.unset();
        }
        return ret;
    }

    public String toString() {
        StringBuilder b = new StringBuilder("!(");
        Iterator<UserVar> ite = vars.iterator();
        while (ite.hasNext()) {
            Var v = ite.next();
            if (ite.hasNext()) {
                b.append(v.label());
                b.append(",");
            } else {
                b.append(v.pretty());
            }
        }
        return b.append(") ").append(prop).toString();
    }
}
