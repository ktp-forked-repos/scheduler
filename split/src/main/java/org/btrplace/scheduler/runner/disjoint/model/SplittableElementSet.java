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

package org.btrplace.scheduler.runner.disjoint.model;

import gnu.trove.map.hash.TIntIntHashMap;
import org.btrplace.model.Element;
import org.btrplace.model.Node;
import org.btrplace.model.VM;

import java.util.*;

/**
 * A collection of supposed unique {@link Element} that can be split
 * into multiple sub-sets.
 * <p>
 * The partitioning is provided by an index that indicate, at instantiation,
 * the right partition for each element.
 * <p>
 * The backend is a simple array of elements. Elements belonging to the same
 * partition are contiguous for efficiency.
 *
 * @author Fabien Hermenier
 */
public class SplittableElementSet<E extends Element> implements Comparator<E> {

    private TIntIntHashMap index;

    private List<E> values;

    /**
     * Make a new splittable set.
     *
     * @param c   the elements, no duplicates are supposed
     * @param idx the partition associated to each element. Format {@link org.btrplace.model.Element#id()} -> key
     */
    public SplittableElementSet(Collection<E> c, TIntIntHashMap idx) {

        values = new ArrayList<>();
        for (E e : c) {
            values.add(e);
        }
        this.index = idx;
        values.parallelStream().sorted(this);
        Collections.sort(values, this);
    }

    /**
     * Make a new splittable set from a collection of VM.
     * We consider the collection does not have duplicated elements.
     *
     * @param c   the collection to wrap
     * @param idx the partition for each VM
     * @return the resulting set
     */
    public static SplittableElementSet<VM> newVMIndex(Collection<VM> c, TIntIntHashMap idx) {
        return new SplittableElementSet<>(c, idx);
    }

    /**
     * Make a new splittable set from a collection of nodes.
     * We consider the collection does not have duplicated elements.
     *
     * @param c   the collection to wrap
     * @param idx the partition for each node
     * @return the resulting set
     */
    public static SplittableElementSet<Node> newNodeIndex(Collection<Node> c, TIntIntHashMap idx) {
        return new SplittableElementSet<>(c, idx);
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder("{");
        forEachPartition((idx, k, from, to) -> {
            b.append('{');
            b.append(values.get(from));
            for (int i = from + 1; i < to; i++) {
                b.append(", ").append(values.get(i));
            }
            b.append('}');
            return true;
        });
        return b.append('}').toString();
    }

    /**
     * Execute a procedure on each partition.
     * The partition is indicated by its bounds on the backend array.
     *
     * @param p the procedure to execute
     */
    public boolean forEachPartition(IterateProcedure<E> p) {
        int curIdx = index.get(values.get(0).id());
        int from;
        int to;
        for (from = 0, to = 0; to < values.size(); to++) {
            int cIdx = index.get(values.get(to).id());
            if (curIdx != cIdx) {
                if (!p.extract(this, curIdx, from, to)) {
                    return false;
                }
                from = to;
                curIdx = cIdx;
            }
        }
        return p.extract(this, curIdx, from, to);
    }

    /**
     * Get a subset for the given partition.
     *
     * @param k the partition key
     * @return the resulting subset. Empty if no elements belong to the given partition.
     */
    public Set<E> getSubSet(int k) {
        int from = -1;
        //TODO: very bad. Bounds should be memorized
        for (int x = 0; x < values.size(); x++) {
            int cIdx = index.get(values.get(x).id());
            if (cIdx == k && from == -1) {
                from = x;
            }
            if (from >= 0 && cIdx > k) {
                return new ElementSubSet<>(this, k, from, x);
            }
        }
        if (from >= 0) {
            return new ElementSubSet<>(this, k, from, values.size());
        }
        return Collections.emptySet();
    }

    @Override
    public int compare(E o1, E o2) {
        return index.get(o1.id()) - index.get(o2.id());
    }

    /**
     * Get the index associated to each element.
     *
     * @return a map of {@link org.btrplace.model.Element#id()} -> index value
     */
    public TIntIntHashMap getRespectiveIndex() {
        return index;
    }

    /**
     * Get the backend array of elements.
     *
     * @return a non-empty array
     */
    public List<E> getValues() {
        return values;
    }

    /**
     * Get all the partitions as subsets.
     *
     * @return a collection of {@link ElementSubSet}.
     */
    public List<ElementSubSet<E>> getPartitions() {
        final List<ElementSubSet<E>> partitions = new ArrayList<>();
        forEachPartition((idx, key, from, to) -> {
            partitions.add(new ElementSubSet<>(SplittableElementSet.this, key, from, to));
            return true;
        });
        return partitions;
    }

    /**
     * Get the size of the set.
     *
     * @return a positive integer
     */
    public int size() {
        return values.size();
    }
}


