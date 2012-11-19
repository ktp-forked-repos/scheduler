/*
 * Copyright (c) 2012 University of Nice Sophia-Antipolis
 *
 * This file is part of btrplace.
 *
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

package btrplace.solver.choco.actionModel;

import btrplace.plan.Action;
import btrplace.plan.SolverException;
import btrplace.plan.action.ShutdownNode;
import btrplace.solver.choco.ActionModel;
import btrplace.solver.choco.ReconfigurationProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model an action that allow a node to boot if necessary.
 *
 * @author Fabien Hermenier
 */
public class ShutdownableNodeModel extends ActionModel {

    public ShutdownableNodeModel(ReconfigurationProblem rp, UUID e) throws SolverException {
        super(rp, e);
        state = rp.getSolver().createBooleanVar("");

    }


    @Override
    public List<Action> getResultingActions(ReconfigurationProblem rp) {
        List<Action> a = new ArrayList<Action>();
        if (state.getVal() == 0) {
            a.add(new ShutdownNode(getSubject(), start.getVal(), end.getVal()));
        }
        return a;
    }
}
