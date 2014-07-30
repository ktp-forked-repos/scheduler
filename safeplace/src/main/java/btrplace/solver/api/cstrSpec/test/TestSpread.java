package btrplace.solver.api.cstrSpec.test;

import btrplace.solver.api.cstrSpec.annotations.CstrTest;
import btrplace.solver.api.cstrSpec.runner.CTestCasesRunner;

/**
 * @author Fabien Hermenier
 */
public class TestSpread {

    @CstrTest(constraint = "spread", groups = {"vm2vm", "long"})
    public void testContinuous(CTestCasesRunner r) {
        TestUtils.longCheck(r.continuous());
    }

    @CstrTest(constraint = "spread", groups = {"vm2vm", "long"})
    public void testContinuousRepair(CTestCasesRunner r) {
        TestUtils.longCheck(r.continuous()).impl().repair(true);
    }

    @CstrTest(constraint = "spread", groups = {"vm2vm", "long"})
    public void testDiscrete(CTestCasesRunner r) {
        TestUtils.longCheck(r.discrete());
    }

    @CstrTest(constraint = "spread", groups = {"vm2vm", "long"})
    public void testDiscreteRepair(CTestCasesRunner r) {
        TestUtils.longCheck(r.discrete()).impl().repair(true);
    }

}
