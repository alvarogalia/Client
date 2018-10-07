package com.alvarogalia.Client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
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
//            MatOfRect objects = new MatOfRect();
//            CascadeClassifier classifier =  new CascadeClassifier("data/cascade.xml");
//            classifier.detectMultiScale(mat, objects, 1.1, 3,0, new Size(36, 13));
//            if(!objects.empty()){
//                Scalar Detect_Color = new Scalar(0, 255, 0, 255);
//                for(int i = 0; i < objects.toList().size(); i++){
//                    Rect rect = objects.toList().get(i);
//                    Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), Detect_Color, 5);
//                }
//            }
            
            BufferedImage image = Util.Mat2BufferedImage(mat);
            SimpleDateFormat formatLong = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            
            //Imgcodecs.imwrite("/media/pi/NUEVO VOL/video/"+ formatLong.format(timestamp) +".jpg", mat);
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