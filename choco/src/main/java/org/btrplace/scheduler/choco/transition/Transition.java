/*
 * Copyright (c) 2016 University Nice Sophia Antipolis
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

package org.btrplace.scheduler.choco.transition;

import org.btrplace.plan.ReconfigurationPlan;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

/**
 * A model skeleton for a transition.
 *
 * @author Fabien Hermenier
 */
public interface Transition<E extends Enum<E>> {

    /**
     * Get the moment the action starts.
     *
     * @return a variable that must be positive
     */
    IntVar getStart();

    /**
     * Get the moment the action ends.
     *
     * @return a variable that must be greater than {@link #getStart()}
     */
    IntVar getEnd();

    /**
     * Get the action duration.
     *
     * @return a duration equals to {@code getEnd() - getStart()}
     */
    IntVar getDuration();

    /**
     * Insert into a plan the actions resulting from the model.
     * The variable values must be extracted from the solution object {@code s} and not directly.
     * @param s the solution computed by the solver.
     * @param plan the plan to modify
     * @return {@code true} iff success
     */
    boolean insertActions(Solution s, ReconfigurationPlan plan);

    /**
     * Get the next state of the subject manipulated by the action.
     *
     * @return {@code 0} for offline, {@code 1} for online.
     */
    BoolVar getState();
}
