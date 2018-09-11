package com.alvarogalia.Client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;



@SuppressWarnings("serial")
public class VideoCamera extends JPanel
{
    VideoCapture camera; 

    public VideoCamera(VideoCapture cam){
        camera  = cam; 
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Mat mat = new Mat();
        if( camera.read(mat))
        {
            BufferedImage image = Util.Mat2BufferedImage(mat);
            double relation = 640.0/480.0;
            int finalWidth = this.getBounds().width-12;
            int finalHeight = (int)((finalWidth)/relation);
            int finalTopMargin = (int) (this.getBounds().height - finalHeight) / 2;
            g.drawImage(image,0,finalTopMargin,finalWidth, finalHeight, null);
        }else{
            camera.release();
        }
    }
}