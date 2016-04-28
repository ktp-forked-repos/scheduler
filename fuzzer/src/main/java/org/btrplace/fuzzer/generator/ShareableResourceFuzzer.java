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

package org.btrplace.fuzzer.generator;

import org.btrplace.model.Model;
import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.model.view.ShareableResource;
import org.btrplace.plan.ReconfigurationPlan;

import java.util.Random;

/**
 * @author Fabien Hermenier
 */
public class ShareableResourceFuzzer implements ModelViewFuzzer {

    private String id;

    private Random rnd;

    private int minCons, maxCons, minCapa, maxCapa;

    public ShareableResourceFuzzer(String rc, int minCons, int maxCons, int minCapa, int maxCapa) {
        id = rc;
        rnd = new Random();
        this.minCons = minCons;
        this.minCapa = minCapa;
        this.maxCapa = maxCapa;
        this.maxCons = maxCons;
    }

    @Override
    public void decorate(Model mo) {
        ShareableResource rc = new ShareableResource(id);
        for (VM v : mo.getMapping().getAllVMs()) {
            int c = rnd.nextInt(maxCons - minCons + 1) + minCons;
            rc.setConsumption(v, c);
        }

        for (Node n : mo.getMapping().getAllNodes()) {
            int c = rnd.nextInt(maxCapa - minCapa + 1) + minCapa;
            rc.setCapacity(n, c);
        }

        mo.attach(rc);
    }
}
