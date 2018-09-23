/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

import com.alvarogalia.Client.Obj.Visita;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author Alvaro
 */
public class FrameRegistrarSalidaPPU extends javax.swing.JFrame {

    /**
     * Creates new form FrameRegistrarSalidaPPU
     */
    String ubicacion = "";
    FirebaseDatabase database;
    FrameRegistrarSalidaPPU(FirebaseDatabase pDatabase) {
        initComponents();
        database = pDatabase;
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        jTable1.getColumnModel().removeColumn(jTable1.getColumnModel().getColumn(0));
        ubicacion = "Brasil";
        Timer timer = new Timer(1000, new TimerListener());
        timer.start();
        
        Query qVehiculosVisita = database.getReference("visitas/Ubicacion/"+ ubicacion + "/Vehiculo" ).orderByChild("timestampSalida").equalTo(0);
        qVehiculosVisita.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                Visita visita = ds.getValue(Visita.class);
                model.addRow(new Object[]{ds.getKey(),visita.getPpu(),visita.getDptoCondominio(),Util.longToDate(visita.getTimestampEntrada()),""});
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {
                Visita visita = ds.getValue(Visita.class);
                DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
                for(int i = 0; i < model.getRowCount(); i++){
                    if(ds.getKey().equals(model.getValueAt(i, 0))){
                        if(visita.getTimestampSalida()>0){
                            model.removeRow(i);
                        }else{
                            model.setValueAt(ds.getKey(), i, 0);
                            model.setValueAt(visita.getPpu(), i, 1);
                            model.setValueAt(visita.getDptoCondominio(), i, 2);
                            model.setValueAt(Util.longToDate(visita.getTimestampEntrada()), i, 3);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
                for(int i = 0; i < model.getRowCount(); i++){
                    if(ds.getKey().equals(model.getValueAt(i, 0))){
                        model.removeRow(i);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {
                System.out.println(ds);
            }

            @Override
            public void onCancelled(DatabaseError de) {
                System.out.println(de);
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
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Key", "Patente", "Departamento/Condominio", "Hora ingreso ", "Tiempo transcurrido"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Registro de salida de vehiculo");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButton1.setText("Registrar salida");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Timestamp(System.currentTimeMillis()));
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        String key = (String) model.getValueAt(jTable1.getSelectedRow(), 0);
        DatabaseReference qVehiculosVisita = database.getReference("visitas/Ubicacion/"+ ubicacion + "/Vehiculo/").child(key);
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("timestampSalida", timestamp);
        hopperUpdates.put("duracion", model.getValueAt(jTable1.getSelectedRow(), 4));
        qVehiculosVisita.updateChildrenAsync(hopperUpdates);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    void limpiaTabla() {
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        while(model.getRowCount() > 0){
            model.removeRow(0);
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            org.joda.time.format.DateTimeFormatter sdf = DateTimeFormat.forPattern("yyyyMMddHHmmss");
            DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
            for (int i = model.getRowCount() -1; i >= 0; i--) {
                String timestampInicio = (String) model.getValueAt(i, 0);
                DateTime fechaInicio = sdf.parseDateTime(timestampInicio);
                DateTime fechaActual = DateTime.now();
                Integer seconds = Seconds.secondsBetween(fechaInicio, fechaActual).getSeconds();
                String transcurrido = LocalTime.MIN.plusSeconds(seconds).format(DateTimeFormatter.ISO_LOCAL_TIME);
                model.setValueAt(transcurrido, i, 4);
            }   
        }
    }
}
