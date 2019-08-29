/*******************************************************************************
 * Copyright (c) 2010-2019 Haifeng Li
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package smile.data.vector;

import java.util.Arrays;
import java.util.stream.LongStream;

/**
 * An immutable long vector.
 *
 * @author Haifeng Li
 */
class LongVectorImpl implements LongVector {
    /** The name of vector. */
    private String name;
    /** The vector data. */
    private long[] vector;

    /** Constructor. */
    public LongVectorImpl(String name, long[] vector) {
        this.name = name;
        this.vector = vector;
    }

    @Override
    public long[] array() {
        return vector;
    }

    @Override
    public double[] toDoubleArray() {
        double[] a = new double[vector.length];
        for (int i = 0; i < a.length; i++) a[i] = vector[i];
        return a;
    }

    @Override
    public long getLong(int i) {
        return vector[i];
    }

    @Override
    public Long get(int i) {
        return vector[i];
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int size() {
        return vector.length;
    }

    @Override
    public LongStream stream() {
        return Arrays.stream(vector);
    }

    @Override
    public String toString() {
        return toString(10);
    }
}