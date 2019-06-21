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

package smile.stat.distribution;

import java.util.List;
import java.util.ArrayList;

/**
 * The finite mixture of multivariate distributions.
 *
 * @author Haifeng Li
 */
public class MultivariateMixture extends AbstractMultivariateDistribution {
    private static final long serialVersionUID = 1L;

    /**
     * A component in the mixture distribution is defined by a distribution
     * and its weight in the mixture.
     */
    public static class Component {

        /**
         * The distribution of component.
         */
        public MultivariateDistribution distribution;
        /**
         * The priori probability of component.
         */
        public double priori;
    }
    List<Component> components;

    /**
     * Construct an empty Mixture.
     */
    MultivariateMixture() {
        components = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param mixture a list of multivariate distributions.
     */
    public MultivariateMixture(List<Component> mixture) {
        components = new ArrayList<>();
        components.addAll(mixture);
    }

    @Override
    public double[] mean() {
        if (components.isEmpty()) {
            throw new IllegalStateException("MultivariateMixture is empty!");
        }

        double w = components.get(0).priori;
        double[] m = components.get(0).distribution.mean();
        double[] mu = new double[m.length];
        for (int i = 0; i < m.length; i++) {
            mu[i] = w * m[i];
        }

        for (int k = 1; k < components.size(); k++) {
            w = components.get(0).priori;
            m = components.get(0).distribution.mean();
            for (int i = 0; i < m.length; i++) {
                mu[i] += w * m[i];
            }
        }

        return mu;
    }

    @Override
    public double[][] cov() {
        if (components.isEmpty()) {
            throw new IllegalStateException("MultivariateMixture is empty!");
        }

        double w = components.get(0).priori;
        double[][] v = components.get(0).distribution.cov();
        double[][] cov = new double[v.length][v[0].length];

        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v[i].length; j++) {
                cov[i][j] = w * w * v[i][j];
            }
        }

        for (int k = 1; k < components.size(); k++) {
            w = components.get(0).priori;
            v = components.get(0).distribution.cov();
            for (int i = 0; i < v.length; i++) {
                for (int j = 0; j < v[i].length; j++) {
                    cov[i][j] += w * w * v[i][j];
                }
            }
        }

        return cov;
    }

    /**
     * Shannon entropy. Not supported.
     */
    @Override
    public double entropy() {
        throw new UnsupportedOperationException("Mixture does not support entropy()");
    }

    @Override
    public double p(double[] x) {
        if (components.isEmpty()) {
            throw new IllegalStateException("MultivariateMixture is empty!");
        }

        double p = 0.0;

        for (Component c : components) {
            p += c.priori * c.distribution.p(x);
        }

        return p;
    }

    @Override
    public double logp(double[] x) {
        return Math.log(p(x));
    }

    @Override
    public double cdf(double[] x) {
        if (components.isEmpty()) {
            throw new IllegalStateException("MultivariateMixture is empty!");
        }

        double p = 0.0;

        for (Component c : components) {
            p += c.priori * c.distribution.cdf(x);
        }

        return p;
    }

    @Override
    public int npara() {
        if (components.isEmpty()) {
            throw new IllegalStateException("MultivariateMixture is empty!");
        }

        int f = components.size() - 1; // independent priori parameters
        for (int i = 0; i < components.size(); i++) {
            f += components.get(i).distribution.npara();
        }

        return f;
    }

    /**
     * Returns the number of components in the mixture.
     */
    public int size() {
        return components.size();
    }

    /**
     * BIC score of the mixture for given data.
     */
    public double bic(double[][] data) {
        if (components.isEmpty()) {
            throw new IllegalStateException("MultivariateMixture is empty!");
        }

        int n = data.length;

        double logLikelihood = 0.0;
        for (double[] x : data) {
            double p = p(x);
            if (p > 0) {
                logLikelihood += Math.log(p);
            }
        }

        return logLikelihood - 0.5 * npara() * Math.log(n);
    }

    /**
     * Returns the list of components in the mixture.
     */
    public List<Component> getComponents() {
        return components;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MultivariateMixture[");
        builder.append(components.size());
        builder.append("]:{");
        for (Component c : components) {
            builder.append(" (");
            builder.append(c.distribution);
            builder.append(':');
            builder.append(String.format("%.4f", c.priori));
            builder.append(')');
        }
        builder.append("}");
        return builder.toString();
    }
}
