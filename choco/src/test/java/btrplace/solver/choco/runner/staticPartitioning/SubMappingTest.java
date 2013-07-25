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

package btrplace.solver.choco.runner.staticPartitioning;

import btrplace.model.DefaultMapping;
import btrplace.model.Mapping;
import btrplace.model.Node;
import btrplace.model.VM;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for {@link SubMapping}.
 *
 * @author Fabien Hermenier
 */
public class SubMappingTest {

    public SubMapping make() {
        Mapping m = new DefaultMapping();
        Set<Node> scope = new HashSet<>();
        Set<VM> subReady = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Node n = new Node(i);
            if (i < 5) {
                m.addOnlineNode(n);
                m.addRunningVM(new VM((i + 1) * 100), n);
                m.addSleepingVM(new VM((i + 1) * 100 + i), n);
            } else {
                m.addOfflineNode(n);
            }

            if (i % 2 == 0) {
                scope.add(n);
            }
        }
        for (int i = 1000; i < 1010; i++) {
            VM v = new VM(i);
            m.addReadyVM(v);
            if (i % 2 == 0) {
                subReady.add(v);
            }
        }
        return new SubMapping(m, scope, subReady);
    }

    /**
     * Check the content of the getters.
     */
    @Test
    public void testInstantiation() {
        SubMapping sm = make();
        //Set getters
        Assert.assertEquals(sm.getAllNodes().size(), 5);
        Assert.assertEquals(sm.getOnlineNodes().size(), 3); //0, 2, 4
        for (Node n : sm.getOnlineNodes()) {
            Assert.assertTrue(n.id() % 2 == 0);
        }

        Assert.assertEquals(sm.getOfflineNodes().size(), 2); //6, 8
        for (Node n : sm.getOfflineNodes()) {
            Assert.assertTrue(n.id() % 2 == 0);
        }


        Assert.assertEquals(sm.getRunningVMs().size(), 2);
        for (VM v : sm.getRunningVMs()) {
            Assert.assertTrue(v.id() % 2 == 0);
        }

        Assert.assertEquals(sm.getSleepingVMs().size(), 3);
        for (VM v : sm.getSleepingVMs()) {
            Assert.assertTrue(v.id() % 2 == 0);
        }

        Assert.assertEquals(sm.getReadyVMs().size(), 5);
        for (VM v : sm.getReadyVMs()) {
            Assert.assertTrue(v.id() % 2 == 0);
        }
        Assert.assertEquals(sm.getAllVMs().size(), 10);

        //State checkers
        Assert.assertTrue(sm.contains(new VM(100)));
        Assert.assertTrue(sm.contains(new VM(1000)));
        Assert.assertFalse(sm.contains(new VM(201))); //In the parent, out of scope
        Assert.assertFalse(sm.contains(new VM(1009))); //In the parent, out of scope

        Assert.assertTrue(sm.isRunning(new VM(300)));
        Assert.assertFalse(sm.isRunning(new VM(1000)));
        Assert.assertTrue(sm.isReady(new VM(1000)));
        Assert.assertTrue(sm.isSleeping(new VM(504)));
        Assert.assertFalse(sm.isRunning(new VM(201)));
    }

    @Test
    public void testAddRunning() {
        SubMapping sm = make();
        //Set running a VM that does not belong to the submapping but belong to the parent
        //System.err.println(sm.getParent());
        Assert.assertFalse(sm.addRunningVM(new VM(201), new Node(2)));
        Assert.assertFalse(sm.addRunningVM(new VM(200), new Node(2)));
        //Node not in the scope
        Assert.assertFalse(sm.addRunningVM(new VM(1000), new Node(3)));

        //1000 is in the ready scope, it's ok
        Assert.assertTrue(sm.addRunningVM(new VM(1000), new Node(2)));
    }

    @Test
    public void testAddReady() {
        SubMapping sm = make();
        //Set running a VM that does not belong to the submapping but belong to the parent
        Assert.assertFalse(sm.addReadyVM(new VM(400))); //VM is elsewhere. No way
        Assert.assertTrue(sm.addReadyVM(new VM(300))); //in my scope, it's ok
        Assert.assertTrue(sm.addReadyVM(new VM(678))); //new VM, it's ok
    }
}