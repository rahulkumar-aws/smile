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

package smile.demo.clustering;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.SingleLinkage;
import smile.clustering.linkage.UPGMALinkage;
import smile.clustering.linkage.UPGMCLinkage;
import smile.clustering.linkage.WPGMALinkage;
import smile.clustering.linkage.WPGMCLinkage;
import smile.clustering.linkage.WardLinkage;
import smile.plot.Palette;
import smile.plot.PlotCanvas;
import smile.math.MathEx;
import smile.plot.Dendrogram;
import smile.plot.ScatterPlot;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class HierarchicalClusteringDemo extends ClusteringDemo {
    JComboBox<String> linkageBox;

    public HierarchicalClusteringDemo() {
        linkageBox = new JComboBox<>();
        linkageBox.addItem("Single");
        linkageBox.addItem("Complete");
        linkageBox.addItem("UPGMA");
        linkageBox.addItem("WPGMA");
        linkageBox.addItem("UPGMC");
        linkageBox.addItem("WPGMC");
        linkageBox.addItem("Ward");
        linkageBox.setSelectedIndex(0);

        optionPane.add(new JLabel("Linkage:"));
        optionPane.add(linkageBox);
    }

    @Override
    public JComponent learn() {
        long clock = System.currentTimeMillis();
        double[][] data = dataset[datasetIndex];
        int n = data.length;
        double[][] proximity = new double[n][];
        for (int i = 0; i < n; i++) {
            proximity[i] = new double[i+1];
            for (int j = 0; j < i; j++)
                proximity[i][j] = MathEx.distance(data[i], data[j]);
        }
        HierarchicalClustering hac = null;
        switch (linkageBox.getSelectedIndex()) {
            case 0:
                hac = new HierarchicalClustering(new SingleLinkage(proximity));
                break;
            case 1:
                hac = new HierarchicalClustering(new CompleteLinkage(proximity));
                break;
            case 2:
                hac = new HierarchicalClustering(new UPGMALinkage(proximity));
                break;
            case 3:
                hac = new HierarchicalClustering(new WPGMALinkage(proximity));
                break;
            case 4:
                hac = new HierarchicalClustering(new UPGMCLinkage(proximity));
                break;
            case 5:
                hac = new HierarchicalClustering(new WPGMCLinkage(proximity));
                break;
            case 6:
                hac = new HierarchicalClustering(new WardLinkage(proximity));
                break;
            default:
                throw new IllegalStateException("Unsupported Linkage");
        }
        System.out.format("Hierarchical clusterings %d samples in %dms\n", dataset[datasetIndex].length, System.currentTimeMillis()-clock);

        int[] membership = hac.partition(clusterNumber);
        int[] clusterSize = new int[clusterNumber];
        for (int i = 0; i < membership.length; i++) {
            clusterSize[membership[i]]++;
        }

        JPanel pane = new JPanel(new GridLayout(1, 3));
        PlotCanvas plot = ScatterPlot.plot(dataset[datasetIndex], pointLegend);
        plot.setTitle("Data");
        pane.add(plot);

        for (int k = 0; k < clusterNumber; k++) {
            double[][] cluster = new double[clusterSize[k]][];
            for (int i = 0, j = 0; i < dataset[datasetIndex].length; i++) {
                if (membership[i] == k) {
                    cluster[j++] = dataset[datasetIndex][i];
                }
            }

            plot.points(cluster, pointLegend, Palette.COLORS[k % Palette.COLORS.length]);
        }

        plot = Dendrogram.plot("Dendrogram", hac.getTree(), hac.getHeight());
        plot.setTitle("Dendrogram");
        pane.add(plot);
        return pane;
    }

    @Override
    public String toString() {
        return "Hierarchical Agglomerative Clustering";
    }

    public static void main(String argv[]) {
        ClusteringDemo demo = new HierarchicalClusteringDemo();
        JFrame f = new JFrame("Agglomerative Hierarchical Clustering");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
