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
package org.btrplace.scheduler.choco.extensions.env.trail.chuncked;

import org.btrplace.scheduler.choco.extensions.env.StoredBool;
import org.btrplace.scheduler.choco.extensions.env.trail.BoolTrail;


public class ChunkedBoolTrail extends ChunkedTrail<BoolWorld> implements BoolTrail {

    /**
     * Constructs a trail with predefined size.
     */
    public ChunkedBoolTrail(int size) {
        worlds = new BoolWorld[size];
    }

    @Override
    public void worldPush(int worldIndex) {
        current = new BoolWorld(DEFAULT_SIZE);
        worlds[worldIndex] = current;
        if (worldIndex == worlds.length) {
            resizeWorlds();
        }
    }

    private void resizeWorlds() {
        int newCapacity = ((worlds.length * 3) / 2);
        BoolWorld [] tmp = new BoolWorld[newCapacity];
        System.arraycopy(worlds, 0, tmp, 0, worlds.length);
        worlds = tmp;
    }

    @Override
    public void savePreviousState(StoredBool v, boolean oldValue, int oldStamp) {
        current.savePreviousState(v, oldValue, oldStamp);
    }

    @Override
    public void buildFakeHistory(StoredBool v, boolean initValue, int olderStamp) {
        throw new UnsupportedOperationException();
    }
}
