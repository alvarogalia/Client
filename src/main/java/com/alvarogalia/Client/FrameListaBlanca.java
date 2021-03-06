/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

import com.alvarogalia.Client.Obj.DetalleListaBlanca;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alvaro
 */
public class FrameListaBlanca extends javax.swing.JFrame {

    /**
     * Creates new form FrameListaBlanca
     */
    FirebaseDatabase database;
    public Map<String, DetalleListaBlanca> arrListaBlanca = new HashMap<>();
    public boolean hayCambios = false;
    private String ubicacion;
    DefaultTableModel model;
    FrameDetalleListaBlanca frameDetalleListaBlanca;
    
    public FrameListaBlanca(FirebaseDatabase pDatabase) {
        initComponents();
        database = pDatabase;
        ubicacion = "Brasil";
        model = (DefaultTableModel)tblListaBlanca.getModel();
        frameDetalleListaBlanca = new FrameDetalleListaBlanca(pDatabase);
        
        Query qListaNegra = database.getReference("listaBlanca/" + ubicacion);
        qListaNegra.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                DetalleListaBlanca detalle = ds.getValue(DetalleListaBlanca.class);
                arrListaBlanca.put(ds.getKey(), detalle);
                model.insertRow(0, new Object[]{ds.getKey(),Util.longToDate(detalle.getTimestampRegistro()),Util.longToDate(detalle.getTimestampVigenciaDesde()),Util.longToDate(detalle.getTimestampVigenciaHasta())});
                hayCambios = true;
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {
                DetalleListaBlanca detalle = ds.getValue(DetalleListaBlanca.class);
                arrListaBlanca.replace(ds.getKey(), detalle);
                for(int i = 0; i < model.getRowCount(); i++){
                   if(model.getValueAt(i, 0).equals(ds.getKey())){
                       model.removeRow(i);
                       model.insertRow(i, new Object[]{ds.getKey(),Util.longToDate(detalle.getTimestampRegistro()),Util.longToDate(detalle.getTimestampVigenciaDesde()),Util.longToDate(detalle.getTimestampVigenciaHasta())});
                   }
                }
                hayCambios = true;
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                DetalleListaBlanca detalle = ds.getValue(DetalleListaBlanca.class);
                arrListaBlanca.replace(ds.getKey(), detalle);
                for(int i = 0; i < model.getRowCount(); i++){
                   if(model.getValueAt(i, 0).equals(ds.getKey())){
                       model.removeRow(i);
                   }
                }
                hayCambios = true;
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {
            }

            @Override
            public void onCancelled(DatabaseError de) {
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
        tblListaBlanca = new javax.swing.JTable();
        btnVolver = new javax.swing.JButton();
        btnRegistroNuevo = new javax.swing.JButton();
        btnEliminarRegistro = new javax.swing.JButton();
        btnActualizarRegistro = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblListaBlanca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PPU", "Fecha Registro", "Vigencia Desde", "Vigencia Hasta"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        jScrollPane1.setViewportView(tblListaBlanca);
        if (tblListaBlanca.getColumnModel().getColumnCount() > 0) {
            tblListaBlanca.getColumnModel().getColumn(0).setResizable(false);
            tblListaBlanca.getColumnModel().getColumn(1).setResizable(false);
            tblListaBlanca.getColumnModel().getColumn(2).setResizable(false);
            tblListaBlanca.getColumnModel().getColumn(3).setResizable(false);
        }

        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        btnRegistroNuevo.setText("Nuevo Registro");
        btnRegistroNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistroNuevoActionPerformed(evt);
            }
        });

        btnEliminarRegistro.setText("Eliminar Registro");

        btnActualizarRegistro.setText("Actualizar Registro");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVolver)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnActualizarRegistro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarRegistro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRegistroNuevo)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolver)
                    .addComponent(btnRegistroNuevo)
                    .addComponent(btnEliminarRegistro)
                    .addComponent(btnActualizarRegistro))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistroNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistroNuevoActionPerformed
        frameDetalleListaBlanca.setLocationRelativeTo(null);
        frameDetalleListaBlanca.setVisible(true);
        frameDetalleListaBlanca.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }//GEN-LAST:event_btnRegistroNuevoActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        setVisible(false);
        frameDetalleListaBlanca.setVisible(false);
    }//GEN-LAST:event_btnVolverActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarRegistro;
    private javax.swing.JButton btnEliminarRegistro;
    private javax.swing.JButton btnRegistroNuevo;
    private javax.swing.JButton btnVolver;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblListaBlanca;
    // End of variables declaration//GEN-END:variables
}
