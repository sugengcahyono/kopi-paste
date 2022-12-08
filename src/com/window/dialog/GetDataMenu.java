package com.window.dialog;

import com.koneksi.Database;
import com.koneksi.Koneksi;
import com.manage.Message;
import com.manage.Text;
import com.sun.glass.events.KeyEvent;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Achmad Baihaqi
 */
public class GetDataMenu extends javax.swing.JDialog {

    private String idSelected = "", keyword = "", idName = "";
    
    private boolean isSelected = false;
    
    Database barang = new Database();
    
    Text text = new Text();
    
    public GetDataMenu(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        
        TableColumnModel columnModel = tabelData.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(90);
        columnModel.getColumn(0).setMaxWidth(90);
        columnModel.getColumn(1).setPreferredWidth(330);
        columnModel.getColumn(1).setMaxWidth(330);
        
        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(248,249,250));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
        
        this.updateTabel();
    }

    private Object[][] getData(){
        try{
            Object obj[][];
            int rows = 0;
            String sql = "SELECT id_menu, nama_menu, harga FROM menu " + keyword;
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            obj = new Object[barang.getJumlahData("menu", keyword)][4];
            // mengeksekusi query
            barang.res = barang.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while(barang.res.next()){
                String id = barang.res.getString("id_menu");
                // menyimpan data dari tabel ke object
                obj[rows][0] = id;
                obj[rows][1] = barang.res.getString("nama_menu");
                obj[rows][2] = this.hitungStokMenu(id);
                obj[rows][3] = text.toMoneyCase(barang.res.getString("harga"));
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return obj;
        }catch(SQLException ex){
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex);
        }
        return null;
    }
    
    private void updateTabel(){
        this.tabelData.setModel(new javax.swing.table.DefaultTableModel(
            getData(),
            new String [] {
                "ID Menu", "Nama Menu", "Stok", "Harga"
            }
        ){
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TableColumnModel columnModel = tabelData.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(70);
        columnModel.getColumn(0).setMaxWidth(70);
        columnModel.getColumn(1).setPreferredWidth(230);
        columnModel.getColumn(1).setMaxWidth(230);
        columnModel.getColumn(2).setPreferredWidth(70);
        columnModel.getColumn(2).setMaxWidth(70);
    }
    
    private int getStokBahan(String idBahan){
        try{
            Connection conn = (Connection) Koneksi.configDB();
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery("SELECT stok FROM bahan WHERE id_bahan = '"+idBahan+"'");
            
            if(res.next()){
                int val = res.getInt("stok");
                conn.close();
                stat.close();
                res.close();
                return val;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage());
        }
        return -1;
    }
    
    private int getQuantity(String idMenu, String idBahan){
        try{
            Connection conn = (Connection) Koneksi.configDB();
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery("SELECT quantity FROM detail_menu WHERE id_menu = '"+idMenu+"' AND id_bahan = '"+idBahan+"'");
            
            if(res.next()){
                int val = res.getInt("quantity");
                conn.close();
                stat.close();
                res.close();
                return val;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage());
        }
        return -1;
    }
    
    private int getJumlahBahan(String idMenu){
        try{
            Connection conn = (Connection) Koneksi.configDB();
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery("SELECT COUNT(id_bahan) AS total FROM detail_menu WHERE id_menu = '"+idMenu+"'");
            
            if(res.next()){
                int val = res.getInt("total");
                conn.close();
                stat.close();
                res.close();
                return val;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage());
        }
        return -1;
    }
    
    private int hitungStokMenu(String idMenu){
        try{
            if(this.getJumlahBahan(idMenu) < 1){
                return 0;
            }
            Connection conn = (Connection) Koneksi.configDB();
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery("SELECT id_bahan FROM detail_menu WHERE id_menu = '"+idMenu+"'");
            int[] qCek = new int[this.getJumlahBahan(idMenu)];
            int stokBahan, quantity, index = 0;
            String idBahan;
            
            while(res.next()){
                idBahan = res.getString("id_bahan");
                stokBahan = this.getStokBahan(idBahan);
                quantity = this.getQuantity(idMenu, idBahan);
                
                if(quantity == 0){
                    return 0;
                }
                
                qCek[index] = (int)stokBahan / quantity;
                System.out.println(idBahan + " : " + (int)stokBahan / quantity);
                index++;
            }
            
            conn.close();
            stat.close();
            res.close();
            
            Arrays.sort(qCek);
            return qCek[0];
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage());
        }
        return -1;
    }
    
    public int getStokMenu(String idMenu){
        return 0;
    }
    
    public void setStokMenu(String idMenu){
        
    }
    
    public boolean isSelected(){
        return this.isSelected;
    }
    
    public String getIdSelected(){
        return this.idSelected;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        inpCari = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnPilih = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        lblInfoBahan = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pilih Data Bahan");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(248, 249, 250));

        tabelData.setBackground(new java.awt.Color(248, 249, 250));
        tabelData.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Menu", "Nama Menu", "Harga", "Status"
            }
        ));
        tabelData.setGridColor(new java.awt.Color(0, 0, 0));
        tabelData.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelData.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataMouseClicked(evt);
            }
        });
        tabelData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tabelDataKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tabelData);

        inpCari.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariKeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 78, 243));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Pilih Data Menu");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(245, 22, 32));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Cari ID / Nama Menu");

        btnPilih.setBackground(new java.awt.Color(255, 0, 255));
        btnPilih.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnPilih.setForeground(new java.awt.Color(255, 255, 255));
        btnPilih.setText("Pilih Menu");
        btnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPilihActionPerformed(evt);
            }
        });

        btnBatal.setBackground(new java.awt.Color(212, 13, 13));
        btnBatal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("Batalkan");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));

        jSeparator2.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Menu Dipilih");

        lblInfoBahan.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblInfoBahan.setForeground(new java.awt.Color(0, 0, 255));
        lblInfoBahan.setText(": ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(inpCari, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnPilih)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatal)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInfoBahan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(inpCari, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPilih, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInfoBahan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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

    private void tabelDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyPressed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if(evt.getKeyCode() == KeyEvent.VK_UP){
            this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() - 1, 0).toString();
            this.idName = this.idSelected + " | " + this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 1);
            this.lblInfoBahan.setText(": " + idName);
        }else if(evt.getKeyCode() == KeyEvent.VK_DOWN){
            this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() + 1, 0).toString();
            this.idName = this.idSelected + " | " + this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 1);
            this.lblInfoBahan.setText(": " + idName);
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataKeyPressed

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow(), 0).toString();
        this.idName = this.idSelected + " | " + this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 1);
        this.lblInfoBahan.setText(": " + idName);
    }//GEN-LAST:event_tabelDataMouseClicked

    private void btnPilihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPilihActionPerformed
        if(this.idSelected.equals("")){
            JOptionPane.showMessageDialog(this, "Tidak ada menu yang dipilih!");
        }else{
            this.isSelected = true;
            this.dispose();
        }
    }//GEN-LAST:event_btnPilihActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        this.isSelected = false;
        this.dispose();
        this.barang.closeConnection();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.isSelected = false;
        this.barang.closeConnection();
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.isSelected = false;
        this.barang.closeConnection();
    }//GEN-LAST:event_formWindowClosing

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_menu LIKE '%"+key+"%' OR nama_menu LIKE '%"+key+"%'";
        this.updateTabel();
//        this.lblTotalData.setText("Menampilkan data supplier dengan keyword '"+inpCari.getText()+"'");
    }//GEN-LAST:event_inpCariKeyReleased

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_menu LIKE '%"+key+"%' OR nama_menu LIKE '%"+key+"%'";
        this.updateTabel();
//        this.lblTotalData.setText("Menampilkan data supplier dengan keyword '"+inpCari.getText()+"'");
    }//GEN-LAST:event_inpCariKeyTyped

    private void tabelDataKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyReleased
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(this.idSelected.equals("")){
                JOptionPane.showMessageDialog(this, "Tidak ada menu yang dipilih!");
            }else{
                this.isSelected = true;
                this.dispose();
            }
        }
    }//GEN-LAST:event_tabelDataKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GetDataMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GetDataMenu dialog = new GetDataMenu(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnPilih;
    private javax.swing.JTextField inpCari;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblInfoBahan;
    private javax.swing.JTable tabelData;
    // End of variables declaration//GEN-END:variables
}