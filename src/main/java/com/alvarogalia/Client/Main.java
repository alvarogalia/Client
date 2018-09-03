/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

import com.alvarogalia.Client.Obj.DetalleListaBlanca;
import com.alvarogalia.Client.Obj.DetalleListaNegra;
import com.alvarogalia.Client.Obj.Historial;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author GENESYS
 */
public class Main extends javax.swing.JFrame {
      
    /**
     * Creates new form Main2
     * @param hist
     */
    FrameRegistrarVisita frameAgregarVisita;
    FrameRegistrarSalidaPPU frameRegistrarSalidaPPU;
    FrameListaNegra frameListaNegra;
    FrameListaBlanca frameListaBlanca;
    
    
    FirebaseDatabase database;
    DatabaseReference refNowWatching;
    
    ValueEventListener listenerNowWatching = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String ppu = dataSnapshot.getValue(String.class);
            lblPatente.setText(ppu);

            DefaultTableModel model = (DefaultTableModel)tblVisitas.getModel();
            while(model.getRowCount() > 0){
                model.removeRow(0);
            }

            Query qVisitas = database.getReference("historial/Ubicacion/Brasil/").orderByChild("ppu").equalTo(ppu);
            qVisitas.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    DefaultTableModel model = (DefaultTableModel)tblVisitas.getModel();
                    while(model.getRowCount() > 0){
                        model.removeRow(0);
                    }
                    int i = 0;
                    for(DataSnapshot snap : ds.getChildren()){
                        Historial hist2 = snap.getValue(Historial.class);
                        model.insertRow(i, new Object[]{Util.longToDate(hist2.getTimestamp()),hist2.getPpu()});
                        i++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError de) {

                }
            });

        }

        @Override
        public void onCancelled(DatabaseError de) {
        }
    };
    
    public void addRowToHistory(Historial hist) {
        DefaultTableModel model = (DefaultTableModel) tableHistorial.getModel();
        Map<String, DetalleListaNegra> arrListaNegra = frameListaNegra.arrListaNegra;
        Map<String, DetalleListaBlanca> arrListaBlanca = frameListaBlanca.arrListaBlanca;
        
        boolean registrado = false;
        String listaNegra = "";
        String listaBlanca = "";
        String encargo = "";
        try{
            if(arrListaNegra.containsKey(hist.getPpu())){
                listaNegra = "N";
            }
            if(arrListaBlanca.containsKey(hist.getPpu())){
                listaBlanca = "B";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        String alerta = encargo+listaNegra+listaBlanca;
        model.insertRow(0, new Object[]{String.valueOf(hist.getTimestamp()), hist.getPpu(), registrado, alerta});
    }

    public void addMasiveRowToHistory(DataSnapshot ds) {
        DefaultTableModel model = (DefaultTableModel) tableHistorial.getModel();
        for(DataSnapshot a : ds.getChildren()){
            String value = "";
            long hora = 0;
            for(DataSnapshot b : a.getChildren()){
                if("ppu".equals(b.getKey())){
                    value=(String)b.getValue();
                }
                if("timestamp".equals(b.getKey())){
                    hora=(long)b.getValue();
                }
            }
            model.insertRow(0, new Object[]{hora, value, false, ""});
        }
        model.removeRow(0);
        //jTable1.setModel(model);
    }
    public Main() throws FileNotFoundException, IOException {
        initComponents();
        
        tableHistorial.getColumnModel().removeColumn(tableHistorial.getColumnModel().getColumn(0));
        
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setMaximizedBounds(env.getMaximumWindowBounds());
        this.setExtendedState(this.getExtendedState() | Main.MAXIMIZED_BOTH);
        
        FileInputStream serviceAccount = new FileInputStream("controlacceso-fc68c-firebase-adminsdk-22zra-efe9ebaead.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://controlacceso-fc68c.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);
        database = FirebaseDatabase.getInstance();
        
        frameAgregarVisita = new FrameRegistrarVisita(database);
        frameRegistrarSalidaPPU = new FrameRegistrarSalidaPPU(database);
        frameListaNegra = new FrameListaNegra(database, this);
        frameListaBlanca = new FrameListaBlanca(database);
        
        refNowWatching = database.getReference("nowWatching/Ubicacion/Brasil/BR-CAM-1/ppu");
        refNowWatching.addValueEventListener(listenerNowWatching);

        Query qrefHistNew = database.getReference("historial/Ubicacion/Brasil").limitToLast(100);
        qrefHistNew.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {                        
                Historial hist = ds.getValue(Historial.class);
                addRowToHistory(hist);
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {

            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                DefaultTableModel model = (DefaultTableModel)tableHistorial.getModel();
                for(int i = 0; i < model.getRowCount(); i++){
                    if(ds.getKey().equals(model.getValueAt(i, 0))){
                        model.removeRow(i);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {

            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        tableHistorial.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tableHistorial.getSelectedRowCount() > 0) {
                    refNowWatching.removeEventListener(listenerNowWatching);

                    DefaultTableModel model = (DefaultTableModel)tblVisitas.getModel();
                    while(model.getRowCount() > 0){
                        model.removeRow(0);
                    }

                    String ppu = (String) tableHistorial.getModel().getValueAt(tableHistorial.getSelectedRow(), 1);
                    lblPatente.setText(ppu);
                    Query qVisitas = database.getReference("historial/Ubicacion/Brasil/").orderByChild("ppu").equalTo(ppu);
                    qVisitas.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds){
                            DefaultTableModel model = (DefaultTableModel)tblVisitas.getModel();
                            while(model.getRowCount() > 0){
                                model.removeRow(0);
                            }
                            
                            int i = 0;
                            for(DataSnapshot snap : ds.getChildren()){
                                Historial hist2 = snap.getValue(Historial.class);
                                model.insertRow(i, new Object[]{Util.longToDate(hist2.getTimestamp()),hist2.getPpu()});
                                i++;
                            }
                            //tableVisitas.setModel(model);
                        }

                        @Override
                        public void onCancelled(DatabaseError de) {

                        }
                    });
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableHistorial = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblPatente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtRutPropietario = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNombreRS = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChoferRegistrado = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblVisitas = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        btnVisitaRegistro = new javax.swing.JButton();
        btnEnVivo = new javax.swing.JButton();
        btnVisitaSalida = new javax.swing.JButton();
        btnListaNegra = new javax.swing.JButton();
        btnListaBlanca = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Control Acceso");
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(640, 480));
        setName("Control Acceso"); // NOI18N

        jScrollPane1.setMaximumSize(new java.awt.Dimension(453, 403));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(453, 403));

        tableHistorial.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tableHistorial.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tableHistorial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Placa Patente", "Registrada", "Alerta"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableHistorial.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tableHistorial.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableHistorial.setUpdateSelectionOnSort(false);
        tableHistorial.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                tableHistorialComponentAdded(evt);
            }
        });
        jScrollPane1.setViewportView(tableHistorial);

        lblPatente.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        lblPatente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPatente.setText("Obteniendo lectura");
        lblPatente.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblPatente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Última lectura");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblPatente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPatente, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 297, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel3.setText("Propietario Rut");

        txtRutPropietario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRutPropietarioActionPerformed(evt);
            }
        });

        jLabel4.setText("Propietario Nombre / Razon Social");

        txtNombreRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreRSActionPerformed(evt);
            }
        });

        jLabel5.setText("Chofer registrado");

        tblChoferRegistrado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rut", "Nombre"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblChoferRegistrado);

        tblVisitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Chofer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblVisitas);

        jLabel6.setText("Visitas Anteriores");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRutPropietario)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addComponent(txtNombreRS)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRutPropietario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreRS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("BR-CAM-1", jPanel1);

        btnVisitaRegistro.setText("Registrar visita");
        btnVisitaRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisitaRegistroActionPerformed(evt);
            }
        });

        btnEnVivo.setText("En Vivo");
        btnEnVivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnVivoActionPerformed(evt);
            }
        });

        btnVisitaSalida.setText("Registrar salida vehiculo");
        btnVisitaSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisitaSalidaActionPerformed(evt);
            }
        });

        btnListaNegra.setText("Lista Negra");
        btnListaNegra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListaNegraActionPerformed(evt);
            }
        });

        btnListaBlanca.setText("Lista Blanca");
        btnListaBlanca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListaBlancaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVisitaRegistro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVisitaSalida)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnListaNegra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnListaBlanca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEnVivo))
                    .addComponent(jTabbedPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVisitaRegistro)
                    .addComponent(btnEnVivo)
                    .addComponent(btnVisitaSalida)
                    .addComponent(btnListaNegra)
                    .addComponent(btnListaBlanca))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableHistorialComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tableHistorialComponentAdded
        
    }//GEN-LAST:event_tableHistorialComponentAdded

    private void btnEnVivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnVivoActionPerformed
        refNowWatching.addValueEventListener(listenerNowWatching);
        tableHistorial.clearSelection();
    }//GEN-LAST:event_btnEnVivoActionPerformed

    private void txtRutPropietarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRutPropietarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRutPropietarioActionPerformed

    private void txtNombreRSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreRSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreRSActionPerformed

    private void btnVisitaRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisitaRegistroActionPerformed
        frameAgregarVisita.setLocationRelativeTo(null);
        frameAgregarVisita.limpiaFormulario();
        frameAgregarVisita.setTimestamp();
        frameAgregarVisita.setVisible(true);
        frameAgregarVisita.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }//GEN-LAST:event_btnVisitaRegistroActionPerformed

    private void btnVisitaSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisitaSalidaActionPerformed
        frameRegistrarSalidaPPU.setLocationRelativeTo(null);
        //frameRegistrarSalidaPPU.limpiaTabla();
        frameRegistrarSalidaPPU.setVisible(true);
        frameRegistrarSalidaPPU.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }//GEN-LAST:event_btnVisitaSalidaActionPerformed

    private void btnListaNegraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListaNegraActionPerformed
        frameListaNegra.setLocationRelativeTo(null);
        frameListaNegra.setVisible(true);
        frameListaNegra.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }//GEN-LAST:event_btnListaNegraActionPerformed

    private void btnListaBlancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListaBlancaActionPerformed
        frameListaBlanca.setLocationRelativeTo(null);
        frameListaBlanca.setVisible(true);
        frameListaBlanca.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }//GEN-LAST:event_btnListaBlancaActionPerformed

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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Main().setVisible(true);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnVivo;
    private javax.swing.JButton btnListaBlanca;
    private javax.swing.JButton btnListaNegra;
    private javax.swing.JButton btnVisitaRegistro;
    private javax.swing.JButton btnVisitaSalida;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblPatente;
    private javax.swing.JTable tableHistorial;
    private javax.swing.JTable tblChoferRegistrado;
    private javax.swing.JTable tblVisitas;
    private javax.swing.JTextField txtNombreRS;
    private javax.swing.JTextField txtRutPropietario;
    // End of variables declaration//GEN-END:variables

    private void limpiaModel() {
        DefaultTableModel model = (DefaultTableModel) tableHistorial.getModel();
        if (model.getRowCount() > 100000) {
            for (int i = 100; i <= model.getRowCount(); i++) {
                model.removeRow(i);
            }
        }
    }
}
