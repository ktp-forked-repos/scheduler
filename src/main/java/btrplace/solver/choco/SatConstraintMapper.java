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

package btrplace.solver.choco;

import btrplace.model.SatConstraint;
import btrplace.model.constraint.Spread;
import btrplace.solver.choco.constraint.ChocoContinuousSpread;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper that allow to convert {@link SatConstraint} to {@link ChocoConstraint}.
 *
 * @author Fabien Hermenier
 */
public class SatConstraintMapper {

    private Map<Class, ChocoConstraintBuilder> builders;

    /**
     * Make a new mapper.
     */
    public SatConstraintMapper() {
        builders = new HashMap<Class, ChocoConstraintBuilder>();

        builders.put(Spread.class, new ChocoContinuousSpread.ChocoContinuousSpreadBuilder());
    }

    /**
     * Register a constraint builder.
     *
     * @param ccb the builder to register
     * @return {@code false} if no builder previously registered for the given constraint was deleted
     */
    public boolean register(ChocoConstraintBuilder ccb) {
        return builders.put(ccb.getKey(), ccb) != null;
    }

    /**
     * Un-register the builder associated to a given {@link SatConstraint}.
     *
     * @param c the class of the {@link SatConstraint} to un-register
     * @return {@code true} if a builder was registered
     */
    public boolean unregister(Class c) {
        return builders.remove(c) != null;
    }

    /**
     * Get the builder associated to a {@link SatConstraint}.
     *
     * @param c the constraint
     * @return the associated builder if exists. {@code null} otherwise
     */
    public ChocoConstraintBuilder get(SatConstraint c) {
        return builders.get(c);
    }
}
