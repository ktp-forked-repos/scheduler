package btrplace.solver.api.cstrSpec.test;

import btrplace.solver.api.cstrSpec.annotations.CstrTest;
import btrplace.solver.api.cstrSpec.runner.CTestCasesRunner;

/**
 * @author Fabien Hermenier
 */
public class TestReady {

    /*@CstrTest(constraint = "ready", groups = {"states"})
    public void testContinuous(CTestCasesRunner r) {
        r.continuous().timeout(5).maxTests(1000);
    }

    @CstrTest(constraint = "ready", groups = {"states"})
    public void testContinuousRepair(CTestCasesRunner r) {
        r.continuous().timeout(5).maxTests(1000).impl().repair(true);
    }
               */
    @CstrTest(constraint = "ready", groups = {"states", "long"})
    public void testDiscrete(CTestCasesRunner r) {
        TestUtils.longCheck(r.discrete());
    }

    @CstrTest(constraint = "ready", groups = {"states", "long"})
    public void testDiscreteRepair(CTestCasesRunner r) {
        TestUtils.longCheck(r.discrete()).impl().repair(true);
    }

}
