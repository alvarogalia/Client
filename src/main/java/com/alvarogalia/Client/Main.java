/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

import com.alvarogalia.Client.Obj.NowWatching;
import com.alvarogalia.Client.Obj.Spotted;
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
import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author GENESYS
 */
public class Main extends javax.swing.JFrame {

    /**
     * Creates new form Main2
     *
     * @param hist
     */
    String holding = "BRASIL";
    String ubicacion = "RIO";
    String camara = "CAM-01";

    FrameRegistrarVisita frameAgregarVisita;
    FrameRegistrarSalidaPPU frameRegistrarSalidaPPU;
    FrameListaNegra frameListaNegra;
    FrameListaBlanca frameListaBlanca;

    FirebaseDatabase database;
    DatabaseReference refNowWatching;

    //String url = "http://138.118.33.201/mjpg/video.mjpg?timestamp=1535125345478";
    //VideoCapture camera = new VideoCapture(url);
    //VideoCapture camera = new VideoCapture(0);
    VideoCapture camera = new VideoCapture("13.mp4");
    Thread thread;
    VideoCamera panelImagenInterior = new VideoCamera(camera);

    ValueEventListener listenerNowWatching;

    public void addRowToHistory(String key, Spotted spot) {
        DefaultTableModel model = (DefaultTableModel) tblSpotted.getModel();

        boolean registrado = false;
        String listaNegra = " ";
        String listaBlanca = " ";
        String encargo = " ";
        try {
            if (frameListaNegra.arrListaNegra.containsKey(spot.getPpu())) {
                listaNegra = "N";
            }
            if (frameListaBlanca.arrListaBlanca.containsKey(spot.getPpu())) {
                listaBlanca = "B";
            }
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }

        String alerta = encargo + listaNegra + listaBlanca;
        model.insertRow(0, new Object[]{key, spot.getPpu(), registrado, alerta});
    }

    public Main() throws FileNotFoundException, IOException {
        this.listenerNowWatching = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NowWatching nowWatching = dataSnapshot.getValue(NowWatching.class);
                lblPatente.setText(nowWatching.getPpu());

                DefaultTableModel model = (DefaultTableModel) tblHistorial.getModel();
                while (model.getRowCount() > 0) {
                    model.removeRow(0);
                }

                Query qVisitas = database.getReference("Historial").child(holding).child(nowWatching.getPpu());
                qVisitas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        DefaultTableModel model = (DefaultTableModel) tblHistorial.getModel();
                        while (model.getRowCount() > 0) {
                            model.removeRow(0);
                        }
                        int i = 0;
                        for (DataSnapshot snap : ds.getChildren()) {
                            Historial hist = snap.getValue(Historial.class);
                            long timestamp = Long.parseLong(snap.getKey());
                            model.insertRow(i, new Object[]{Util.longToDate(timestamp), hist.getUbicacion(), hist.getCamara()});
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
        camera.set(3, 640);
        camera.set(4, 480);
        Mat mat = null;
        if(camera.read(mat)){
            System.out.println(mat.cols() + "x" + mat.rows());
        }
        
        this.redraw = () -> {
            
            while (true) {
                if (camera.isOpened()) {
                    try {
                        panelImagenInterior.repaint();
                        long sleep = 33;
                        Thread.sleep(sleep);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                } else {
                    camera.open(0);
                }
            }
        };
        initComponents();

        tblSpotted.getColumnModel().removeColumn(tblSpotted.getColumnModel().getColumn(0));

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

        refNowWatching = database.getReference("NowWatching").child(holding).child(ubicacion).child(camara);
        refNowWatching.addValueEventListener(listenerNowWatching);

        Query qrefHistNew = database.getReference("Spotted").child(holding).child(ubicacion).limitToLast(100);
        qrefHistNew.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                Spotted spot = ds.getValue(Spotted.class);
                addRowToHistory(ds.getKey(), spot);
                if (frameListaNegra.arrListaNegra.containsKey(spot.getPpu())) {
                    try {
                        SystemTray tray = SystemTray.getSystemTray();
                        Image image = Toolkit.getDefaultToolkit().createImage("listanegra.png");
                        TrayIcon trayIcon = new TrayIcon(image, "Lista Negra");
                        trayIcon.setImageAutoSize(true);
                        trayIcon.setToolTip("Lista negra");
                        tray.add(trayIcon);
                        trayIcon.displayMessage("Patente en lista negra!", "Patente " + spot.getPpu()
                                + " visualizada en cámara " + spot.getCamara() + ". Registra "
                                + frameListaNegra.arrListaNegra.get(spot.getPpu()).getRazon(), MessageType.WARNING);
                    } catch (AWTException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {

            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                DefaultTableModel model = (DefaultTableModel) tblSpotted.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (ds.getKey().equals(model.getValueAt(i, 0))) {
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

        tblSpotted.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && tblSpotted.getSelectedRowCount() > 0) {
                btnEnVivo.setVisible(true);
                refNowWatching.removeEventListener(listenerNowWatching);

                DefaultTableModel model = (DefaultTableModel) tblHistorial.getModel();
                while (model.getRowCount() > 0) {
                    model.removeRow(0);
                }

                String ppu = (String) tblSpotted.getModel().getValueAt(tblSpotted.getSelectedRow(), 1);
                lblPatente.setText(ppu);
                Query qVisitas = database.getReference("Historial").child(holding).child(ppu);
                qVisitas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        DefaultTableModel model = (DefaultTableModel) tblHistorial.getModel();
                        while (model.getRowCount() > 0) {
                            model.removeRow(0);
                        }

                        int i = 0;
                        for (DataSnapshot snap : ds.getChildren()) {
                            Historial hist = snap.getValue(Historial.class);
                            long timestamp = Long.parseLong(snap.getKey());
                            model.insertRow(i, new Object[]{Util.longToDate(timestamp), hist.getUbicacion(), hist.getCamara()});
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError de) {

                    }
                });
            }
        });

        Timer timer = new Timer(100, (ActionEvent e) -> {
            if (frameListaNegra.hayCambios || frameListaBlanca.hayCambios) {
                DefaultTableModel model = (DefaultTableModel) tblSpotted.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    String patente = (String) model.getValueAt(i, 1);
                    StringBuilder value = new StringBuilder((String) model.getValueAt(i, 3));
                    if (frameListaNegra.arrListaNegra.containsKey(patente)) {
                        value.setCharAt(1, 'N');
                    } else {
                        value.setCharAt(1, ' ');
                    }
                    if (frameListaBlanca.arrListaBlanca.containsKey(patente)) {
                        value.setCharAt(2, 'B');
                    } else {
                        value.setCharAt(2, ' ');
                    }
                    model.setValueAt(value.toString(), i, 3);
                }
                frameListaNegra.hayCambios = false;
                frameListaBlanca.hayCambios = false;
            }
        });
        timer.start();

        panelImagen.add(panelImagenInterior);
        panelImagenInterior.setVisible(true);
        panelImagen.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                panelImagenInterior.setBounds(panelImagen.getBounds());
            }
        });

        this.thread = new Thread(redraw);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
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
        tblSpotted = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panelNowWatching = new javax.swing.JPanel();
        lblPatente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChoferRegistrado = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblHistorial = new javax.swing.JTable();
        panelImagen = new javax.swing.JPanel();
        btnVisitaRegistro = new javax.swing.JButton();
        btnEnVivo = new javax.swing.JButton();
        btnVisitaSalida = new javax.swing.JButton();
        btnListaNegra = new javax.swing.JButton();
        btnListaBlanca = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Control Acceso");
        setFont(new java.awt.Font("Lucida Sans", 0, 10)); // NOI18N
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(800, 480));
        setName("Control Acceso"); // NOI18N
        setSize(new java.awt.Dimension(800, 480));

        jScrollPane1.setMaximumSize(new java.awt.Dimension(250, 9000000));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(250, 250));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 900));

        tblSpotted.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblSpotted.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        tblSpotted.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSpotted.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblSpotted.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblSpotted.setUpdateSelectionOnSort(false);
        tblSpotted.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                tblSpottedComponentAdded(evt);
            }
        });
        jScrollPane1.setViewportView(tblSpotted);

        lblPatente.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        lblPatente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPatente.setText("Obteniendo lectura");
        lblPatente.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblPatente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Última lectura");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel5.setText("Visitas registradas");

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

        jLabel6.setText("Visto anteriormente");

        tblHistorial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Camara"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblHistorial);

        javax.swing.GroupLayout panelNowWatchingLayout = new javax.swing.GroupLayout(panelNowWatching);
        panelNowWatching.setLayout(panelNowWatchingLayout);
        panelNowWatchingLayout.setHorizontalGroup(
            panelNowWatchingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblPatente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        panelNowWatchingLayout.setVerticalGroup(
            panelNowWatchingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNowWatchingLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPatente, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        javax.swing.GroupLayout panelImagenLayout = new javax.swing.GroupLayout(panelImagen);
        panelImagen.setLayout(panelImagenLayout);
        panelImagenLayout.setHorizontalGroup(
            panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 301, Short.MAX_VALUE)
        );
        panelImagenLayout.setVerticalGroup(
            panelImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(panelNowWatching, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(panelImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(panelNowWatching, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addComponent(btnVisitaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVisitaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnListaNegra, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnListaBlanca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnVivo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
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

    private void tblSpottedComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tblSpottedComponentAdded

    }//GEN-LAST:event_tblSpottedComponentAdded

    private void btnEnVivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnVivoActionPerformed
        refNowWatching.addValueEventListener(listenerNowWatching);
        tblSpotted.clearSelection();
        btnEnVivo.setVisible(false);
    }//GEN-LAST:event_btnEnVivoActionPerformed

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
    public static void main(String args[]) throws Exception {
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
 /*Added compatibility to Mac OS brew opencv*/
        Util.addLibraryPath("/usr/local/Cellar/opencv/3.4.3/share/OpenCV/java/");

        System.out.println("Library PATH:");
        System.out.println(System.getProperty("java.library.path"));

        java.awt.EventQueue.invokeLater(() -> {
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                new Main().setVisible(true);

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblPatente;
    private javax.swing.JPanel panelImagen;
    private javax.swing.JPanel panelNowWatching;
    private javax.swing.JTable tblChoferRegistrado;
    private javax.swing.JTable tblHistorial;
    private javax.swing.JTable tblSpotted;
    // End of variables declaration//GEN-END:variables

    private final Runnable redraw;

}
