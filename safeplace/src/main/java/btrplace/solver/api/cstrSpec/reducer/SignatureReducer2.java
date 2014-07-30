package btrplace.solver.api.cstrSpec.reducer;

import btrplace.plan.ReconfigurationPlan;
import btrplace.solver.api.cstrSpec.CTestCase;
import btrplace.solver.api.cstrSpec.CTestCaseResult;
import btrplace.solver.api.cstrSpec.Constraint;
import btrplace.solver.api.cstrSpec.spec.term.Constant;
import btrplace.solver.api.cstrSpec.spec.type.ColType;
import btrplace.solver.api.cstrSpec.spec.type.Type;
import btrplace.solver.api.cstrSpec.verification.Verifier;
import btrplace.solver.api.cstrSpec.verification.spec.SpecVerifier;

import java.util.*;

/**
 * Reduce a constraint signature to the minimum possible.
 * <p/>
 * In practice the sets are reduced one by one by removing values one by one.
 * A value will stay in the set if its removal lead to a different error.
 *
 * @author Fabien Hermenier
 */
public class SignatureReducer2 extends Reducer {

    //Not thread safe;
    private Constant curConstant;

    private Constant oldConstant;


    private List<Constant> deepCopy(List<Constant> in) {
        List<Constant> cpy = new ArrayList<>(in.size());
        for (Constant c : in) {
            Type t = c.type();
            Object v = c.eval(null);
            Object o = v; //Assume immutable if not a collection
            if (v instanceof Collection) {
                o = toCollection((Collection) v);
                //Deep transformation into lists
            }
            cpy.add(new Constant(o, t));
        }
        return cpy;
    }

    private Collection toCollection(Collection v) {
        Collection l;
        if (v instanceof List) {
            l = new ArrayList();
        } else {
            l = new HashSet();
        }
        for (Object o : v) {
            if (o instanceof Collection) {
                l.add(toCollection((Collection) o));
            } else {
                l.add(o);
            }
        }
        return l;
    }

    @Override
    public CTestCase reduce(CTestCase tc, SpecVerifier v1, Verifier v2, CTestCaseResult.Result errType) throws Exception {
        //System.err.println("Reduce " + tc.getConstraint().toString(tc.getParameters()));
        ReconfigurationPlan p = tc.getPlan();
        Constraint cstr = tc.getConstraint();
        List<Constant> in = tc.getParameters();
        List<Constant> cpy = deepCopy(in);
        for (int i = 0; i < cpy.size(); i++) {
            Constant c = cpy.get(i);
            if (c.type() instanceof ColType) {
                Collection res = (Collection) c.eval(null);
                reduceCollection(v1, v2, p, cstr, cpy, res, tc.continuous(), errType);
                Constant c2 = new Constant(res, c.type());
                cpy.set(i, c2);
                //System.out.println("Result " + cpy);
            }
        }
        //System.out.println("Reduced " + tc.getConstraint().toString(tc.getParameters()) + " to " + tc.getConstraint().toString(cpy));
        return derive(tc, cpy, p);
    }

    private void reduceCollection(SpecVerifier v1, Verifier v2, ReconfigurationPlan p, Constraint cstr, List<Constant> in, Collection c, boolean b, CTestCaseResult.Result errType) {
        //System.out.println("Reduce " + c + " in " + in);
        if (c instanceof Set) {
            reduceSet(v1, v2, p, cstr, in, (Set) c, b, errType);
        } else {
            reduceList(v1, v2, p, cstr, in, (List) c, b, errType);
        }
        //System.out.println("Result: " + c + " in " + in);
    }

    //s is a reference to the set to reduce
    private void reduceSet(SpecVerifier v1, Verifier v2, ReconfigurationPlan p, Constraint cstr, List<Constant> in, Set s, boolean c, CTestCaseResult.Result errType) {
        //The elements to try to remove
        List removable = new ArrayList(s);
        for (Object o : removable) {
            s.remove(o);

            if (consistent(v1, v2, cstr, in, p, c, errType)) {
                //Cannot remove the element
                s.add(o);
                //System.out.println("Cannot remove  " + o + " from " + s);
                //If the element is also a collection, maybe we can reduce it also
                if (o instanceof Collection) {
                    reduceCollection(v1, v2, p, cstr, in, (Collection) o, c, errType);
                }
            } else {
                //System.out.println("Ok to remove " + o);
            }
        }
    }

    //l is a reference to the list to reduce
    private void reduceList(SpecVerifier v1, Verifier v2, ReconfigurationPlan p, Constraint cstr, List<Constant> in, List l, boolean c, CTestCaseResult.Result errType) {
        //The elements to try to remove
        for (int i = 0; i < l.size(); ) {
            Object o = l.remove(i);
            if (consistent(v1, v2, cstr, in, p, c, errType)) {
                //Cannot remove the element
                l.set(i, o);
                //If the element is also a collection, maybe we can reduce it also
                if (o instanceof Collection) {
                    reduceCollection(v1, v2, p, cstr, in, (Collection) o, c, errType);
                }
            } else {
                i++;
            }
        }
    }
}