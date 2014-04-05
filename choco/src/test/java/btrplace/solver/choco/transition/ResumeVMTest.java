/*
 * Copyright (c) 2013 University of Nice Sophia-Antipolis
 *
 * This file is part of btrplace.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package btrplace.solver.choco.transition;

import btrplace.model.*;
import btrplace.plan.ReconfigurationPlan;
import btrplace.plan.event.Action;
import btrplace.solver.SolverException;
import btrplace.solver.choco.DefaultReconfigurationProblemBuilder;
import btrplace.solver.choco.ReconfigurationProblem;
import btrplace.solver.choco.duration.ConstantActionDuration;
import btrplace.solver.choco.duration.DurationEvaluators;
import org.testng.Assert;
import org.testng.annotations.Test;
import solver.Cause;
import solver.Solver;
import solver.constraints.IntConstraintFactory;
import solver.exception.ContradictionException;

import java.util.HashSet;
import java.util.Iterator;


/**
 * Basic unit tests for {@link ResumeVM}.
 *
 * @author Fabien Hermenier
 */
public class ResumeVMTest {

    /**
     * Just resume a VM on its current node.
     */
    @Test
    public void testBasics() throws SolverException, ContradictionException {
        Model mo = new DefaultModel();
        Mapping map = mo.getMapping();
        final VM vm1 = mo.newVM();
        Node n1 = mo.newNode();
        Node n2 = mo.newNode();

        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addSleepingVM(vm1, n1);

        DurationEvaluators dev = DurationEvaluators.newBundle();
        dev.register(btrplace.plan.event.ResumeVM.class, new ConstantActionDuration(10));
        ReconfigurationProblem rp = new DefaultReconfigurationProblemBuilder(mo)
                .setDurationEvaluators(dev)
                .labelVariables()
                .setNextVMsStates(new HashSet<VM>(), map.getAllVMs(), new HashSet<VM>(), new HashSet<VM>())
                .build();
        rp.getNodeActions()[0].getState().instantiateTo(1, Cause.Null);
        rp.getNodeActions()[1].getState().instantiateTo(1, Cause.Null);
        ResumeVM m = (ResumeVM) rp.getVMActions()[0];
        Assert.assertEquals(vm1, m.getVM());
        Assert.assertNull(m.getCSlice());
        Assert.assertTrue(m.getDuration().instantiatedTo(10));
        Assert.assertTrue(m.getState().instantiatedTo(1));
        Assert.assertFalse(m.getDSlice().getHoster().instantiated());
        Assert.assertFalse(m.getDSlice().getStart().instantiated());
        Assert.assertFalse(m.getDSlice().getEnd().instantiated());

        ReconfigurationPlan p = rp.solve(0, false);
        Assert.assertNotNull(p);
        btrplace.plan.event.ResumeVM a = (btrplace.plan.event.ResumeVM) p.getActions().iterator().next();

        Node dest = rp.getNode(m.getDSlice().getHoster().getValue());
        Assert.assertEquals(vm1, a.getVM());
        Assert.assertEquals(dest, a.getDestinationNode());
        Assert.assertEquals(n1, a.getSourceNode());
        Assert.assertEquals(10, a.getEnd() - a.getStart());
    }

    /**
     * Test that check when the action is shorter than the end of
     * the reconfiguration process.
     * In practice, 2 resume actions have to be executed sequentially
     */
    @Test
    public void testResumeSequence() throws SolverException, ContradictionException {
        Model mo = new DefaultModel();
        Mapping map = mo.getMapping();
        VM vm1 = mo.newVM();
        VM vm2 = mo.newVM();
        Node n1 = mo.newNode();
        Node n2 = mo.newNode();

        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addSleepingVM(vm1, n1);
        map.addSleepingVM(vm2, n2);

        DurationEvaluators dev = DurationEvaluators.newBundle();
        dev.register(btrplace.plan.event.ResumeVM.class, new ConstantActionDuration(5));
        ReconfigurationProblem rp = new DefaultReconfigurationProblemBuilder(mo)
                .setDurationEvaluators(dev)
                .labelVariables()
                .setNextVMsStates(new HashSet<VM>(), map.getAllVMs(), new HashSet<VM>(), new HashSet<VM>())
                .build();
        ResumeVM m1 = (ResumeVM) rp.getVMActions()[rp.getVM(vm1)];
        ResumeVM m2 = (ResumeVM) rp.getVMActions()[rp.getVM(vm2)];
        rp.getNodeActions()[0].getState().instantiateTo(1, Cause.Null);
        rp.getNodeActions()[1].getState().instantiateTo(1, Cause.Null);
        Solver s = rp.getSolver();
        s.post(IntConstraintFactory.arithm(m2.getStart(), ">=", m1.getEnd()));

        ReconfigurationPlan p = rp.solve(0, false);
        Assert.assertNotNull(p);
        Iterator<Action> ite = p.iterator();
        btrplace.plan.event.ResumeVM b1 = (btrplace.plan.event.ResumeVM) ite.next();
        btrplace.plan.event.ResumeVM b2 = (btrplace.plan.event.ResumeVM) ite.next();
        Assert.assertEquals(vm1, b1.getVM());
        Assert.assertEquals(vm2, b2.getVM());
        Assert.assertTrue(b1.getEnd() <= b2.getStart());
        Assert.assertEquals(5, b1.getEnd() - b1.getStart());
        Assert.assertEquals(5, b2.getEnd() - b2.getStart());
    }
}
