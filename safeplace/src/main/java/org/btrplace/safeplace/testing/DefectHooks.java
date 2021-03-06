/*
 * Copyright (c) 2017 University Nice Sophia Antipolis
 *
 * This file is part of btrplace.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.btrplace.safeplace.testing;

import org.testng.Assert;

import java.util.function.Consumer;

/**
 * A utility class to predefine hooks when a defect is reported
 *
 * @author Fabien Hermenier
 */
public final class DefectHooks {

    /**
     * A hook that ignore the defect
     */
    public static final Consumer<TestCaseResult> ignore = res -> {
        //Ignore silently the defect
    };

    /**
     * A hook that print the defect and continue.
     */
    public static final Consumer<TestCaseResult> print = res -> {
        System.out.println(res);
    };

    /**
     * A hook that use an assertion to signal the defect
     */
    public static final Consumer<TestCaseResult> failedAssertion = res -> {
        assert false : res.toString();
    };

    /**
     * A hook that uses testng to signal a defect.
     */
    public static final Consumer<TestCaseResult> testNgFailure = res ->
            Assert.assertEquals(res.result(), Result.SUCCESS, res.toString());

    /**
     * Utility class. No instantiation.
     */
    private DefectHooks() {
    }
}
