package com.window;

import com.koneksi.Database;
import com.koneksi.Koneksi;
import com.manage.Text;
import com.manage.UIManager;
import com.manage.User;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.window.dialog.UpdateDataMenu;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Achmad Baihaqi
 */
public class DataMenu extends javax.swing.JFrame {
    
    private String keyword = "", idSelected = "";
    
    private Database menu = new Database();
    
    private final UIManager win = new UIManager();
    
    private Text text = new Text();
    
    public DataMenu() {
        initComponents();
        
        this.setTitle("Test Window");
        this.setExtendedState(this.getExtendedState() | javax.swing.JFrame.MAXIMIZED_BOTH);
        this.lblNamaUser.setText(User.getNamaUser());
        
        // set ui button
        this.btnAdd.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnEdit.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnHapus.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.inpBahan.removeAllList();
        
        // set hover sidebar
        this.win.btns = new JLabel[]{
            this.btnKaryawan, this.btnSupplier, this.btnPembeli, 
            this.btnTransaksi, this.btnLaporan, this.btnBahan, this.btnLogout, this.btnDashboard
        };
        // set update tabel
        JTextField[] fields = {this.inpNama, this.inpJenis, this.inpHarga};
        
//        this.inpIdBahan.setEditable(false);
        this.inpNama.setEditable(false);
        this.inpJenis.setEditable(false);
        this.inpHarga.setEditable(false);
        
        
        this.win.hoverButton();
        this.win.updateData(fields);

        // set desain tabel
        TableColumnModel columnModel = tabelData.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(110);
        columnModel.getColumn(0).setMaxWidth(110);
        
        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(248,249,250));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
//        this.tabelData.setModel(this.);
        
        this.inpCari.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        this.inpId.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        this.inpBahan.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        this.inpNama.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        this.inpHarga.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        this.inpJenis.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
//        this.inpAlamat.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));   
//        this.tabelData = new com.manage.StripedEvenInWhitePartsTable(new String[][]{}, new String[]{});
        this.updateTable();
        
        this.btnPembeli.setVisible(false);
        this.btnLogout.setVisible(false);
        
        if(!User.isAdmin()){
            this.btnAdd.setVisible(false);
            this.btnEdit.setVisible(false);
            this.btnHapus.setVisible(false);
        }
    }

    private Object[][] getData() {
        try {
            Object obj[][];
            int rows = 0;
            String sql = "SELECT id_menu, nama_menu, jenis, harga FROM menu " + keyword;
            obj = new Object[menu.getJumlahData("menu", keyword)][5];
            menu.res = menu.stat.executeQuery(sql);

            while (menu.res.next()) {
                String id = menu.res.getString("id_menu");
                obj[rows][0] = id;
                obj[rows][1] = menu.res.getString("nama_menu");
                obj[rows][2] = menu.res.getString("jenis");
                obj[rows][3] = this.hitungStokMenu(id);
                obj[rows][4] = text.toMoneyCase(menu.res.getString("harga"));
                rows++;
            }
            return obj;

        } catch (SQLException ex) {
//                Message.showException(this, ex.getMessage());
        }
        return null;
    }

    private void updateTable() {
        this.tabelData.setModel(new javax.swing.table.DefaultTableModel(
                getData(),
                new String[]{
                    "ID Menu", "Nama Menu", "Jenis", "Stok", "Harga"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        // set desain tabel
        TableColumnModel columnModel = tabelData.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(0).setMaxWidth(80);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(1).setMaxWidth(150);
        columnModel.getColumn(2).setPreferredWidth(145);
        columnModel.getColumn(2).setMaxWidth(145);
        columnModel.getColumn(3).setPreferredWidth(60);
        columnModel.getColumn(3).setMaxWidth(60);
        columnModel.getColumn(4).setPreferredWidth(140);
        columnModel.getColumn(4).setMaxWidth(140);
    }
        
    private void showData(){
        this.inpBahan.removeAllList();
        try{
            String sql = "SELECT menu.nama_menu, menu.jenis, menu.jenis, menu.harga, "
                       + "detail_menu.id_bahan, detail_menu.nama_bahan, detail_menu.quantity, detail_menu.satuan, bahan.nama_bahan "
                       + "FROM menu "
                       + "JOIN detail_menu AS detail_menu "
                       + "ON menu.id_menu = detail_menu.id_menu "
                       + "JOIN bahan "
                       + "ON bahan.id_bahan = detail_menu.id_bahan "
                       + "WHERE menu.id_menu = '"+this.idSelected+"'";
            System.out.println(sql);
            Connection c = (Connection) Koneksi.configDB();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);
            
            if(r.next()){
                this.inpId.setText(this.idSelected);
                this.inpNama.setText(r.getString("menu.nama_menu"));
                this.inpJenis.setText(r.getString("menu.jenis"));
                this.inpHarga.setText(text.toMoneyCase(r.getString("menu.harga")));
                // BA001 | 10.gr, Nama Bahab
                this.inpBahan.addList(
                        String.format(
                                "%s | %s %s %s", r.getString("detail_menu.id_bahan"), r.getString("detail_menu.quantity"), r.getString("detail_menu.satuan"), r.getString("bahan.nama_bahan")
                        ));
//                this.inpBahan.addList(r.getString("detail_menu.id_bahan") + " | " + r.getString("bahan.nama_bahan"));
                while(r.next()){
               this.inpBahan.addList(
                        String.format(
                                "%s | %s %s %s", r.getString("detail_menu.id_bahan"), r.getString("detail_menu.quantity"), r.getString("detail_menu.satuan"), r.getString("bahan.nama_bahan")
                        ));
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error : " + ex.getMessage());
        }
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlMain = new javax.swing.JPanel();
        pnlSidebar = new javax.swing.JPanel();
        btnMenu = new javax.swing.JLabel();
        btnBahan = new javax.swing.JLabel();
        lblNamaUser = new javax.swing.JLabel();
        lineSideMenu1 = new javax.swing.JSeparator();
        lblProfileSidebar = new javax.swing.JLabel();
        btnTransaksi = new javax.swing.JLabel();
        btnDataMaster = new javax.swing.JLabel();
        btnLaporan = new javax.swing.JLabel();
        lblMenu = new javax.swing.JLabel();
        btnLogout = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JLabel();
        btnKaryawan = new javax.swing.JLabel();
        btnPembeli = new javax.swing.JLabel();
        btnSupplier = new javax.swing.JLabel();
        pnlTop = new com.manage.RoundedPanel();
        lblIconWindow = new javax.swing.JLabel();
        lblNamaWindow = new javax.swing.JLabel();
        lblTopSetting = new javax.swing.JLabel();
        lblTopInfo = new javax.swing.JLabel();
        lblTopProfile = new javax.swing.JLabel();
        pnlContent = new com.manage.RoundedPanel();
        lblTotalData = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelData = this.tabelData = new com.manage.StripedTabelModel(new String[][]{}, new String[]{});
        inpCari = new com.manage.RoundedTextField(15);
        lblCari = new javax.swing.JLabel();
        lblInfoData = new javax.swing.JLabel();
        lineVerCen = new javax.swing.JSeparator();
        lineHorTop = new javax.swing.JSeparator();
        lblId = new javax.swing.JLabel();
        inpId = new com.manage.RoundedTextField(50);
        inpNama = new com.manage.RoundedTextField(15);
        lblTelephone = new javax.swing.JLabel();
        lblAlamat = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JToggleButton();
        btnHapus = new javax.swing.JToggleButton();
        lblCariIcon = new javax.swing.JLabel();
        lineHorBot = new javax.swing.JSeparator();
        lblGajelas = new javax.swing.JLabel();
        lblHarga = new javax.swing.JLabel();
        inpHarga = new com.manage.RoundedTextField(15);
        inpJenis = new com.manage.RoundedTextField(15);
        lblHarga1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        inpBahan = new haqiachd.list.JListCustom();
        lblBottom = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnlMain.setBackground(new java.awt.Color(112, 160, 227));

        pnlSidebar.setBackground(new java.awt.Color(35, 99, 210));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(254, 730));

        btnMenu.setBackground(new java.awt.Color(166, 203, 227));
        btnMenu.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-coffe-dark.png"))); // NOI18N
        btnMenu.setText("Data Menu");
        btnMenu.setIconTextGap(7);
        btnMenu.setOpaque(true);
        btnMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnMenuMouseClicked(evt);
            }
        });

        btnBahan.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnBahan.setForeground(new java.awt.Color(255, 255, 255));
        btnBahan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidebar-bahan.png"))); // NOI18N
        btnBahan.setText("Data Bahan");
        btnBahan.setIconTextGap(7);
        btnBahan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBahanMouseClicked(evt);
            }
        });

        lblNamaUser.setFont(new java.awt.Font("Swis721 LtEx BT", 1, 20)); // NOI18N
        lblNamaUser.setForeground(new java.awt.Color(255, 255, 255));
        lblNamaUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNamaUser.setText("Achmad Baihaqi");

        lineSideMenu1.setForeground(new java.awt.Color(255, 255, 255));

        lblProfileSidebar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProfileSidebar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidebar-userprofile.png"))); // NOI18N
        lblProfileSidebar.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        btnTransaksi.setBackground(new java.awt.Color(96, 180, 235));
        btnTransaksi.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        btnTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-trjual.png"))); // NOI18N
        btnTransaksi.setText("Transaksi");
        btnTransaksi.setIconTextGap(7);
        btnTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTransaksiMouseClicked(evt);
            }
        });

        btnDataMaster.setFont(new java.awt.Font("Dialog", 1, 21)); // NOI18N
        btnDataMaster.setForeground(new java.awt.Color(255, 255, 255));
        btnDataMaster.setText("Data Master");

        btnLaporan.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnLaporan.setForeground(new java.awt.Color(255, 255, 255));
        btnLaporan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-lpjual.png"))); // NOI18N
        btnLaporan.setText("Laporan");
        btnLaporan.setIconTextGap(7);
        btnLaporan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLaporanMouseClicked(evt);
            }
        });

        lblMenu.setFont(new java.awt.Font("Dialog", 1, 21)); // NOI18N
        lblMenu.setForeground(new java.awt.Color(255, 255, 255));
        lblMenu.setText("Menu");

        btnLogout.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-logout.png"))); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.setIconTextGap(7);
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLogoutMouseClicked(evt);
            }
        });

        btnDashboard.setBackground(new java.awt.Color(166, 203, 227));
        btnDashboard.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(255, 255, 255));
        btnDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-home.png"))); // NOI18N
        btnDashboard.setText("Dashboard");
        btnDashboard.setIconTextGap(7);
        btnDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDashboardMouseClicked(evt);
            }
        });

        btnKaryawan.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnKaryawan.setForeground(new java.awt.Color(255, 255, 255));
        btnKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-karyawan.png"))); // NOI18N
        btnKaryawan.setText("Data Karyawan");
        btnKaryawan.setIconTextGap(7);
        btnKaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnKaryawanMouseClicked(evt);
            }
        });

        btnPembeli.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnPembeli.setForeground(new java.awt.Color(255, 255, 255));
        btnPembeli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-pembeli.png"))); // NOI18N
        btnPembeli.setText("Data Pembeli");
        btnPembeli.setIconTextGap(7);
        btnPembeli.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPembeliMouseClicked(evt);
            }
        });

        btnSupplier.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnSupplier.setForeground(new java.awt.Color(255, 255, 255));
        btnSupplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-sidemenu-supplier.png"))); // NOI18N
        btnSupplier.setText("Data Supplier");
        btnSupplier.setIconTextGap(7);
        btnSupplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSupplierMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSidebarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNamaUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblProfileSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSidebarLayout.createSequentialGroup()
                        .addGap(0, 29, Short.MAX_VALUE)
                        .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDataMaster, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                            .addComponent(lineSideMenu1))
                        .addGap(10, 10, 10)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSidebarLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBahan, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPembeli, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProfileSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNamaUser, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lineSideMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(lblMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(btnDataMaster, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPembeli, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBahan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        pnlTop.setBackground(new java.awt.Color(248, 249, 250));
        pnlTop.setRoundBottomLeft(20);
        pnlTop.setRoundBottomRight(20);
        pnlTop.setRoundTopLeft(20);
        pnlTop.setRoundTopRight(20);

        lblIconWindow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-topleft-dashboard.png"))); // NOI18N

        lblNamaWindow.setFont(new java.awt.Font("Ebrima", 1, 24)); // NOI18N
        lblNamaWindow.setForeground(new java.awt.Color(0, 21, 39));
        lblNamaWindow.setText("Data Menu");

        lblTopSetting.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTopSetting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-top-setting.png"))); // NOI18N
        lblTopSetting.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTopSettingMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTopSettingMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTopSettingMouseExited(evt);
            }
        });

        lblTopInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTopInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-top-info.png"))); // NOI18N
        lblTopInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTopInfoMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTopInfoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTopInfoMouseExited(evt);
            }
        });

        lblTopProfile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTopProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-top-miniuser.png"))); // NOI18N
        lblTopProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTopProfileMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTopProfileMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTopProfileMouseExited(evt);
            }
        });

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIconWindow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNamaWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTopSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTopInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTopProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblIconWindow, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
            .addComponent(lblNamaWindow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblTopSetting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblTopInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblTopProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnlContent.setBackground(new java.awt.Color(248, 249, 250));
        pnlContent.setRoundBottomLeft(20);
        pnlContent.setRoundBottomRight(20);
        pnlContent.setRoundTopLeft(20);
        pnlContent.setRoundTopRight(20);

        lblTotalData.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblTotalData.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalData.setText("Menampilkan 20 data menu dengan keyword \"test\"");

        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"SP001", "Achmad Baihaqi", null, null},
                {"SP002", "Mohammad Ilham Islamy", null, null},
                {"SP003", "Septian Yoga Pamungkas", null, null},
                {"SP004", "Widyasari Raisya", null, null}
            },
            new String [] {
                "ID Menu", "Nama Menu", "Jenis", "Harga"
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
        });
        jScrollPane2.setViewportView(tabelData);

        inpCari.setBackground(new java.awt.Color(248, 249, 250));
        inpCari.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpCari.setForeground(new java.awt.Color(33, 94, 234));
        inpCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(22, 155, 22)));
        inpCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariKeyTyped(evt);
            }
        });

        lblCari.setFont(new java.awt.Font("Dialog", 1, 17)); // NOI18N
        lblCari.setForeground(new java.awt.Color(33, 94, 234));
        lblCari.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCari.setText("Cari ID / Nama Menu");

        lblInfoData.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblInfoData.setForeground(new java.awt.Color(250, 22, 22));
        lblInfoData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInfoData.setText("Informasi Menu");

        lineVerCen.setBackground(new java.awt.Color(8, 8, 9));
        lineVerCen.setForeground(new java.awt.Color(8, 8, 9));
        lineVerCen.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lineHorTop.setBackground(new java.awt.Color(8, 8, 9));
        lineHorTop.setForeground(new java.awt.Color(8, 8, 9));

        lblId.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblId.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-data-idmenu.png"))); // NOI18N
        lblId.setText("ID Menu");

        inpId.setBackground(new java.awt.Color(231, 235, 239));
        inpId.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpId.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        inpId.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        inpId.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        inpId.setEnabled(false);
        inpId.setSelectionStart(5);

        inpNama.setBackground(new java.awt.Color(248, 249, 250));
        inpNama.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpNama.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblTelephone.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblTelephone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-data-menu.png"))); // NOI18N
        lblTelephone.setText("Nama Menu");

        lblAlamat.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblAlamat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-data-jenismenu.png"))); // NOI18N
        lblAlamat.setText("Jenis");

        btnAdd.setBackground(new java.awt.Color(41, 180, 50));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-data-tambah.png"))); // NOI18N
        btnAdd.setText("Tambah Data");
        btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAddMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAddMouseExited(evt);
            }
        });
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnEdit.setBackground(new java.awt.Color(34, 119, 237));
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-data-edit.png"))); // NOI18N
        btnEdit.setText("Edit Data");
        btnEdit.setMaximumSize(new java.awt.Dimension(109, 25));
        btnEdit.setMinimumSize(new java.awt.Dimension(109, 25));
        btnEdit.setPreferredSize(new java.awt.Dimension(109, 25));
        btnEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEditMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnEditMouseExited(evt);
            }
        });
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(220, 41, 41));
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-data-hapus.png"))); // NOI18N
        btnHapus.setText("Hapus Data ");
        btnHapus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapusMouseExited(evt);
            }
        });
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        lblCariIcon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCariIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window searchdata.png"))); // NOI18N

        lineHorBot.setBackground(new java.awt.Color(0, 0, 0));
        lineHorBot.setForeground(new java.awt.Color(0, 0, 0));

        lblGajelas.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblGajelas.setText("Kopi Paste version 1.0");

        lblHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblHarga.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-data-harga.png"))); // NOI18N
        lblHarga.setText("Harga");

        inpHarga.setBackground(new java.awt.Color(248, 249, 250));
        inpHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpHarga.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        inpJenis.setBackground(new java.awt.Color(248, 249, 250));
        inpJenis.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpJenis.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblHarga1.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblHarga1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/icons/ic-window-data-idbahan.png"))); // NOI18N
        lblHarga1.setText("Bahan Menu");

        inpBahan.setBackground(new java.awt.Color(248, 249, 250));
        inpBahan.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        jScrollPane4.setViewportView(inpBahan);

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfoData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineHorTop)
                            .addGroup(pnlContentLayout.createSequentialGroup()
                                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(pnlContentLayout.createSequentialGroup()
                                            .addComponent(lblAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(inpJenis))
                                        .addGroup(pnlContentLayout.createSequentialGroup()
                                            .addComponent(lblTelephone, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(inpNama))
                                        .addGroup(pnlContentLayout.createSequentialGroup()
                                            .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnHapus))
                                        .addGroup(pnlContentLayout.createSequentialGroup()
                                            .addComponent(lblId, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(inpId, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlContentLayout.createSequentialGroup()
                                            .addComponent(lblHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(inpHarga))
                                        .addGroup(pnlContentLayout.createSequentialGroup()
                                            .addComponent(lblHarga1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)))
                                    .addComponent(lblGajelas, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(lineHorBot))
                .addGap(18, 18, 18)
                .addComponent(lineVerCen, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addComponent(lblCari, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCariIcon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inpCari, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                        .addComponent(lblTotalData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18))
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(inpCari)
                            .addComponent(lblCari, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                            .addComponent(lblCariIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalData))
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlContentLayout.createSequentialGroup()
                                .addComponent(lblInfoData, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lineHorTop, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(inpId, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblTelephone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(inpNama, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(inpJenis, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblHarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(inpHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblHarga1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lineHorBot, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEdit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnHapus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lineVerCen, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addComponent(lblGajelas)))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addComponent(pnlSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 9, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane1.setViewportView(pnlMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1360, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMenuMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DataMenu window = new DataMenu();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnMenuMouseClicked

    private void btnBahanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBahanMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DataBahan window = new DataBahan();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnBahanMouseClicked

    private void btnTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTransaksiMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        MenuTransaksi window = new MenuTransaksi();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnTransaksiMouseClicked

    private void btnLaporanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLaporanMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        MenuLaporan window = new MenuLaporan();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnLaporanMouseClicked

    private void btnLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLogoutMouseClicked
        new User().logout();
        this.setVisible(false);
    }//GEN-LAST:event_btnLogoutMouseClicked

    private void btnDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDashboardMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        Dashboard window = new Dashboard();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnDashboardMouseClicked

    private void btnKaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnKaryawanMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DataKaryawan window = new DataKaryawan();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnKaryawanMouseClicked

    private void btnPembeliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPembeliMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DataPembeli window = new DataPembeli();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnPembeliMouseClicked

    private void btnSupplierMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSupplierMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DataSupplier window = new DataSupplier();
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                window.setLocation(getX(), getY());
                window.setVisible(true);
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnSupplierMouseClicked

    private void lblTopProfileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopProfileMouseClicked
        JOptionPane.showMessageDialog(null, "Coming Soon!");
    }//GEN-LAST:event_lblTopProfileMouseClicked

    private void lblTopProfileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopProfileMouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.lblTopProfile.setIcon(Gambar.getIcon("ic-window-top-miniuser-entered.png"));
    }//GEN-LAST:event_lblTopProfileMouseEntered

    private void lblTopProfileMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopProfileMouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        this.lblTopProfile.setIcon(Gambar.getIcon("ic-window-top-miniuser.png"));
    }//GEN-LAST:event_lblTopProfileMouseExited

    private void lblTopSettingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopSettingMouseClicked
        JOptionPane.showMessageDialog(null, "Coming Soon!");
    }//GEN-LAST:event_lblTopSettingMouseClicked

    private void lblTopSettingMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopSettingMouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.lblTopSetting.setIcon(Gambar.getIcon("ic-window-top-setting-entered.png"));
    }//GEN-LAST:event_lblTopSettingMouseEntered

    private void lblTopSettingMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopSettingMouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        this.lblTopSetting.setIcon(Gambar.getIcon("ic-window-top-setting.png"));
    }//GEN-LAST:event_lblTopSettingMouseExited

    private void lblTopInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopInfoMouseClicked
        JOptionPane.showMessageDialog(null, "Coming Soon!");
    }//GEN-LAST:event_lblTopInfoMouseClicked

    private void lblTopInfoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopInfoMouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.lblTopInfo.setIcon(Gambar.getIcon("ic-window-top-info-entered.png"));
    }//GEN-LAST:event_lblTopInfoMouseEntered

    private void lblTopInfoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTopInfoMouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        this.lblTopInfo.setIcon(Gambar.getIcon("ic-window-top-info.png"));
    }//GEN-LAST:event_lblTopInfoMouseExited

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        System.out.println(this.getClass().getName() + " opened");
    }//GEN-LAST:event_formWindowOpened

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        System.out.println(this.getClass().getName() + " activated");
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        System.out.println(this.getClass().getName() + " deactivated");
    }//GEN-LAST:event_formWindowDeactivated

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        System.out.println(this.getClass().getName() + " closed");
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        System.out.println(this.getClass().getName() + " closing");
    }//GEN-LAST:event_formWindowClosing

    private void btnAddMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseEntered
        this.btnAdd.setIcon(Gambar.getIcon("ic-data-tambah-entered.png"));
    }//GEN-LAST:event_btnAddMouseEntered

    private void btnAddMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseExited
        this.btnAdd.setIcon(Gambar.getIcon("ic-data-tambah.png"));
    }//GEN-LAST:event_btnAddMouseExited

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        UpdateDataMenu d = new UpdateDataMenu(null, true, 1, "");
        d.setVisible(true);
        this.updateTable();
        // referesh data
        this.inpId.setText("");
        this.inpNama.setText("");
        this.inpJenis.setText("");
        this.inpHarga.setText("");
        this.inpBahan.removeAllList();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        this.btnEdit.setIcon(Gambar.getIcon("ic-data-edit-entered.png"));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        this.btnEdit.setIcon(Gambar.getIcon("ic-data-edit.png"));
    }//GEN-LAST:event_btnEditMouseExited

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if(this.idSelected.equals("") || this.idSelected == null){
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih!");
        }else{
            UpdateDataMenu d = new UpdateDataMenu(null, true, 2, this.idSelected);
            d.setVisible(true);
            this.updateTable();
            this.showData();
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseEntered
        this.btnHapus.setIcon(Gambar.getIcon("ic-data-hapus-entered.png"));
    }//GEN-LAST:event_btnHapusMouseEntered

    private void btnHapusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseExited
        this.btnHapus.setIcon(Gambar.getIcon("ic-data-hapus.png"));
    }//GEN-LAST:event_btnHapusMouseExited

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int status;
        // mengecek apakah ada data yang dipilih atau tidak
        if(tabelData.getSelectedRow() > -1){
            // membuka confirm dialog untuk menghapus data
            Audio.play(Audio.SOUND_INFO);
            status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            // mengecek pilihan dari barang
            switch(status){
                // jika yes maka data akan dihapus
                case JOptionPane.YES_OPTION : 
                    // menghapus data pembeli
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        try{
                            String idBahan = this.inpId.getText(),
                                   sql = "DELETE FROM menu WHERE id_menu = '" + idBahan + "'";

                                Connection koneksi = (Connection) Koneksi.configDB();
                                Statement stat = koneksi.createStatement();
                                // mengecek apakah data supplier berhasil terhapus atau tidak
                                if(stat.executeUpdate(sql) > 0){
                                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                                    // mengupdate tabel
                                        this.updateTable();
                                    // referesh data
                                    this.inpId.setText("");
                                    this.inpNama.setText("");
                                    this.inpJenis.setText("");
                                    this.inpHarga.setText("");
                                    this.inpBahan.removeAllList();
                                    this.idSelected = "";
                                }else{
                                    JOptionPane.showMessageDialog(this, "Data gagal dihapus!");
                                }
                                koneksi.close();
                        }catch(SQLException ex){
                            JOptionPane.showMessageDialog(this, ex.getMessage());
                        }
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    break;
            }            
        }else{
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih!");
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.idSelected = this.tabelData.getValueAt(this.tabelData.getSelectedRow(), 0).toString();
        this.showData();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataMouseClicked

    private void tabelDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyPressed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if(evt.getKeyCode() == KeyEvent.VK_UP){
            this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() - 1, 0).toString();
            System.out.println("ID SELECTED : " + this.idSelected);
            this.showData();
        }else if(evt.getKeyCode() == KeyEvent.VK_DOWN){
            this.idSelected = this.tabelData.getValueAt(tabelData.getSelectedRow() + 1, 0).toString();
            this.showData();
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_tabelDataKeyPressed

    private void inpCariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyTyped
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_menu LIKE '%"+key+"%' OR nama_menu LIKE '%"+key+"%'";
        this.updateTable();
    }//GEN-LAST:event_inpCariKeyTyped

    private void inpCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariKeyReleased
        String key = this.inpCari.getText();
        this.keyword = "WHERE id_menu LIKE '%"+key+"%' OR nama_menu LIKE '%"+key+"%'";
        this.updateTable();
    }//GEN-LAST:event_inpCariKeyReleased

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
            java.util.logging.Logger.getLogger(DataMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DataMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JLabel btnBahan;
    private javax.swing.JLabel btnDashboard;
    private javax.swing.JLabel btnDataMaster;
    private javax.swing.JToggleButton btnEdit;
    private javax.swing.JToggleButton btnHapus;
    private javax.swing.JLabel btnKaryawan;
    private javax.swing.JLabel btnLaporan;
    private javax.swing.JLabel btnLogout;
    private javax.swing.JLabel btnMenu;
    private javax.swing.JLabel btnPembeli;
    private javax.swing.JLabel btnSupplier;
    private javax.swing.JLabel btnTransaksi;
    private haqiachd.list.JListCustom inpBahan;
    private javax.swing.JTextField inpCari;
    private javax.swing.JTextField inpHarga;
    private javax.swing.JTextField inpId;
    private javax.swing.JTextField inpJenis;
    private javax.swing.JTextField inpNama;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblAlamat;
    private javax.swing.JLabel lblBottom;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblCariIcon;
    private javax.swing.JLabel lblGajelas;
    private javax.swing.JLabel lblHarga;
    private javax.swing.JLabel lblHarga1;
    private javax.swing.JLabel lblIconWindow;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblInfoData;
    private javax.swing.JLabel lblMenu;
    private javax.swing.JLabel lblNamaUser;
    private javax.swing.JLabel lblNamaWindow;
    private javax.swing.JLabel lblProfileSidebar;
    private javax.swing.JLabel lblTelephone;
    private javax.swing.JLabel lblTopInfo;
    private javax.swing.JLabel lblTopProfile;
    private javax.swing.JLabel lblTopSetting;
    private javax.swing.JLabel lblTotalData;
    private javax.swing.JSeparator lineHorBot;
    private javax.swing.JSeparator lineHorTop;
    private javax.swing.JSeparator lineSideMenu1;
    private javax.swing.JSeparator lineVerCen;
    private com.manage.RoundedPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSidebar;
    private com.manage.RoundedPanel pnlTop;
    private javax.swing.JTable tabelData;
    // End of variables declaration//GEN-END:variables
}