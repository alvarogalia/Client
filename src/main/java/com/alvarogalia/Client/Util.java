/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author GENESYS
 */
public class Util {
    public static String longToDate(long pLong){
        //2018-00-00 00:00:00
        //2018-00-00 00:00:00
        String strLong = String.valueOf(pLong);
        try{
            strLong = new StringBuilder(strLong)
                .insert(4, "-")
                .insert(7, "-")
                .insert(10, " ")
                .insert(13, ":")
                .insert(16, ":").toString();
        return strLong;
        }catch(Exception  e){
            return strLong;
        }
        
    }
    public static long StringToLong(String pString){
        String limpio = pString.replace("-", "").replace(" ", "").replace(":", "");
        return Long.parseLong(limpio);
    }
    
    static class ColorTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ColorTableModel model = (ColorTableModel) table.getModel();
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(model.getRowColour(row));
            return c;
        }
    }
    
    static class ColorTableModel extends DefaultTableModel {

        List<Color> rowColours = Arrays.asList(
            Color.RED,
            Color.GREEN,
            Color.CYAN
        );

        public void setRowColour(int row, Color c) {
            rowColours.set(row, c);
            fireTableRowsUpdated(row, row);
        }

        public Color getRowColour(int row) {
            return rowColours.get(row);
        }

        @Override
        public Object getValueAt(int row, int column) {
            return String.format("%d %d", row, column);
        }
    }
}

