/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

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
    public static BufferedImage Mat2BufferedImage(Mat m){

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1)
        {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage img = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return img;
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
    
    public static void addLibraryPath(String pathToAdd) throws Exception {
        Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        String[] paths = (String[]) usrPathsField.get(null);

        for (String path : paths)
            if (path.equals(pathToAdd))
                return;

        String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }
    
    public static BufferedImage drawPlate(BufferedImage image, Rect rect, String ppu){
        int plateWidth = rect.width;
        int plateHeight = rect.height;
        int fromX = rect.x;
        int fromY = rect.y;

        Graphics2D graphics2d = image.createGraphics();

        Image fondo = Toolkit.getDefaultToolkit().getImage("patente.png");
        graphics2d.drawImage(fondo, (int) fromX, (int) fromY, (int) plateWidth, (int) plateHeight, null);
        graphics2d.dispose();

        Font font = new Font("FE-Font", Font.PLAIN, (int) (plateHeight / 1.8));

        graphics2d = image.createGraphics();
        graphics2d.setFont(font);
        graphics2d.setColor(Color.DARK_GRAY);

        String text = String.valueOf(ppu.charAt(0));
        graphics2d.drawString(text, (int) (fromX + (plateWidth / 36) * 1), (int) (fromY + plateHeight / 1.95));
        text = String.valueOf(ppu.charAt(1));
        graphics2d.drawString(text, (int) (fromX + (plateWidth / 36) * 6), (int) (fromY + plateHeight / 1.95));
        text = String.valueOf(ppu.charAt(2));
        graphics2d.drawString(text, (int) (fromX + (plateWidth / 36) * 13), (int) (fromY + plateHeight / 1.95));
        text = String.valueOf(ppu.charAt(3));
        graphics2d.drawString(text, (int) (fromX + (plateWidth / 36) * 16.3), (int) (fromY + plateHeight / 1.95));
        text = String.valueOf(ppu.charAt(4));
        graphics2d.drawString(text, (int) (fromX + (plateWidth / 36) * 26.0), (int) (fromY + plateHeight / 1.95));
        text = String.valueOf(ppu.charAt(5));
        graphics2d.drawString(text, (int) (fromX + (plateWidth / 36) * 30.0), (int) (fromY + plateHeight / 1.95));
        graphics2d.dispose();
        
        return image;
    }
}

