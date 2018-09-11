/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

import com.alvarogalia.Client.Obj.DetalleListaBlanca;
import com.alvarogalia.Client.Obj.DetalleListaNegra;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Alvaro
 */
public class FrameDetalleListaBlanca extends javax.swing.JFrame {

    private FirebaseDatabase database;
    String ubicacion;
    /**
     * Creates new form FrameDetalleListaBlanca
     */
    public FrameDetalleListaBlanca(FirebaseDatabase pDatabase) {
        initComponents();
        
        DatePickerSettings dateSettingsDesde = new DatePickerSettings();
        dateSettingsDesde.setFormatForDatesCommonEra("yyyyMMdd");
        dateSettingsDesde.setFormatForDatesBeforeCommonEra("uuuuMMdd");
        dateSettingsDesde.setAllowKeyboardEditing(false);
        dateSettingsDesde.setAllowEmptyDates(false);
        dateSettingsDesde.setFirstDayOfWeek(DayOfWeek.MONDAY);
        pickerDesde.datePicker.setSettings(dateSettingsDesde);
        pickerDesde.datePicker.setDateToToday();
        DatePickerSettings dateSettingsHasta = new DatePickerSettings();
        dateSettingsHasta.setFormatForDatesCommonEra("yyyyMMdd");
        dateSettingsHasta.setFormatForDatesBeforeCommonEra("uuuuMMdd");
        dateSettingsHasta.setAllowKeyboardEditing(false);
        dateSettingsHasta.setAllowEmptyDates(true);
        dateSettingsHasta.setFirstDayOfWeek(DayOfWeek.MONDAY);
        pickerHasta.datePicker.setSettings(dateSettingsHasta);
        
        database = pDatabase;
        ubicacion = "Brasil";
        
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtPPU = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        pickerDesde = new com.github.lgooddatepicker.components.DateTimePicker();
        jLabel4 = new javax.swing.JLabel();
        pickerHasta = new com.github.lgooddatepicker.components.DateTimePicker();
        btnCancelar = new javax.swing.JButton();
        btnGrabar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Detalle lista blanca");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setText("Patente vehiculo");

        jLabel3.setText("Vigencia desde");

        jLabel4.setText("Vigencia hasta");

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnGrabar.setText("Grabar");
        btnGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGrabarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPPU)
                            .addComponent(pickerDesde, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .addComponent(pickerHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCancelar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGrabar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPPU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(pickerDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(pickerHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnGrabar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGrabarActionPerformed
        DetalleListaBlanca detalle = new DetalleListaBlanca();
        detalle.setTimestampRegistro(Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Timestamp(System.currentTimeMillis()))));
        System.out.println(pickerDesde.datePicker.getText() + " " + pickerDesde.timePicker.getText());
        System.out.println(pickerHasta.datePicker.getText() + " " + pickerHasta.timePicker.getText());
        
        long fechaDesde = Util.StringToLong(pickerDesde.datePicker.getText() + pickerDesde.timePicker.getText());
        long fechaHasta = Util.StringToLong(pickerHasta.datePicker.getText() + pickerHasta.timePicker.getText());
        detalle.setTimestampVigenciaDesde(fechaDesde);
        detalle.setTimestampVigenciaHasta(fechaHasta);
        
        Map<String, Object> map = new HashMap<>();
        map.put(txtPPU.getText(), detalle);
        DatabaseReference refListaNegra = database.getReference("listaNegra/" + ubicacion);
        refListaNegra.updateChildrenAsync(map);
    }//GEN-LAST:event_btnGrabarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGrabar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private com.github.lgooddatepicker.components.DateTimePicker pickerDesde;
    private com.github.lgooddatepicker.components.DateTimePicker pickerHasta;
    private javax.swing.JTextField txtPPU;
    // End of variables declaration//GEN-END:variables
}
