/*
 * Copyright (c) 2014 University Nice Sophia Antipolis
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

package btrplace.safeplace.spec.term.func;

import btrplace.model.Node;
import btrplace.model.VM;
import btrplace.safeplace.spec.type.NodeType;
import btrplace.safeplace.spec.type.Type;
import btrplace.safeplace.spec.type.VMType;
import btrplace.safeplace.verification.spec.SpecModel;

import java.util.List;

/**
 * @author Fabien Hermenier
 */
public class Host extends Function<Node> {

    @Override
    public String id() {
        return "host";
    }

    @Override
    public NodeType type() {
        return NodeType.getInstance();
    }

    @Override
    public Node eval(SpecModel mo, List<Object> args) {
        VM vm = (VM) args.get(0);
        if (vm == null) {
            throw new UnsupportedOperationException();
        }
        return mo.getMapping().host(vm);
    }

    public Type[] signature() {
        return new Type[]{VMType.getInstance()};
    }
}