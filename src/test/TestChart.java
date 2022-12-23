/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.koneksi.Koneksi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;

public class TestChart extends javax.swing.JFrame {

    /**
     * Creates new form TestChart
     */
    public TestChart() {
        initComponents();
        this.pieChart();
    }

    public void pieChart() {
        try {
            String sql = "SELECT MONTH(`tgl_transaksi`) AS bulan, YEAR(`tgl_transaksi`) AS tahun, SUM(harga_total) AS `total` from `transaksi_jual` GROUP BY MONTH(`tgl_transaksi`);";
//            String sql = "SELECT MONTH(`tanggal`) AS bulan, YEAR(`tanggal`) AS tahun, SUM(total_harga) AS `total` from `transaksi_jual` GROUP BY MONTH(`tanggal`);";
            DefaultPieDataset pieSet = new DefaultPieDataset();
            Connection conn = Koneksi.configDB();
            PreparedStatement prp = conn.prepareStatement(sql);
            ResultSet rs = prp.executeQuery(sql);
            while (rs.next()) {
                System.out.println("data");
                if (rs.getString("bulan").equals(String.valueOf(1))) {
                    pieSet.setValue("Januari" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(2))) {
                    pieSet.setValue("Febuary" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(3))) {
                    pieSet.setValue("Maret" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(4))) {
                    pieSet.setValue("April" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(5))) {
                    pieSet.setValue("Mei" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(6))) {
                    pieSet.setValue("Juni" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(7))) {
                    pieSet.setValue("Juli" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(8))) {
                    pieSet.setValue("Agustus" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(9))) {
                    pieSet.setValue("September" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(10))) {
                    pieSet.setValue("October" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(11))) {
                    pieSet.setValue("November" + rs.getString("tahun"), rs.getDouble("total"));
                } else if (rs.getString("bulan").equals(String.valueOf(12))) {
                    pieSet.setValue("Desember" + rs.getString("tahun"), rs.getDouble("total"));
                }
            }
            JFreeChart chartPie = ChartFactory.createPieChart3D("Penjualan Perbulan",
                    pieSet, true, true, false);
            ChartPanel framePieChart = new ChartPanel(chartPie);
            panel_chart.add(framePieChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panel_chart = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panel_chart.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestChart().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panel_chart;
    // End of variables declaration//GEN-END:variables
}