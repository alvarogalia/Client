package com.alvarogalia.Client;

import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprResults;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.opencv.core.Core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
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
    boolean detecting;
    boolean recording;
    String path;
    boolean reading;
    
    public VideoCamera(VideoCapture cam, boolean detecting, boolean recording, String path, boolean reading){
        this.camera  = cam; 
        this.detecting = detecting;
        this.recording = recording;
        this.path = path;
        this.reading = reading;
    }

    @Override
    protected void paintComponent(Graphics g){
        String country = "eu", configfile = "openalpr.conf", runtimeDataDir = "runtime_data";
        super.paintComponent(g);
        Mat mat = new Mat();
        int j = 0;
        while(j < 2){
            camera.grab();
            j++;
        }
        if( camera.retrieve(mat))
        {
            java.util.Date date = new java.util.Date();
            Timestamp timestamp1 = new Timestamp(date.getTime());
            
            MatOfRect objects = new MatOfRect();
            CascadeClassifier classifier =  new CascadeClassifier("data/cascade.xml");
            
            
            int minWidth = 90;
            int minHeight = 35;
            
            if(detecting){
                classifier.detectMultiScale(mat, objects, 1.2, 8,0, new Size(minWidth,minHeight));
            }
            
            SimpleDateFormat formatLong = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            
            if(!objects.empty()){
                for(int i = 0; i < objects.toList().size(); i++){
                    Rect rect = objects.toList().get(i);
                    Mat subMat = mat.submat(rect);
                    if(subMat.cols() >= minWidth && subMat.rows() >= minHeight){
                        Imgcodecs.imwrite(path + formatLong.format(timestamp) + "_" + i +".png", subMat);
                        if(reading){
                            try {
                                Alpr alpr = new Alpr(country, configfile, runtimeDataDir);
                                alpr.setTopN(1);
                                alpr.setDefaultRegion("cl");
                                MatOfByte matOfByte = new MatOfByte();

                                Imgcodecs.imencode("*.png", subMat, matOfByte);
                                AlprResults response = alpr.recognize(matOfByte.toArray());
                                alpr.unload();
                                if(response.getPlates().size() > 0){
                                    String ppu = response.getPlates().get(0).getBestPlate().getCharacters();
                                    Imgcodecs.imwrite(path + formatLong.format(timestamp) + "_" + i +"_" + ppu + ".png", subMat);
                                    System.out.println("Patente detectada: " + ppu);
                                }
                                camera.grab();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            Scalar Detect_Color = new Scalar(0, 255, 0, 255);
            if(!objects.empty()){
                for(int i = 0; i < objects.toList().size(); i++){
                    Rect rect = objects.toList().get(i);
                    Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), Detect_Color, 5);
                    Imgproc.putText(mat, rect.width+"x"+rect.height,  new Point(rect.x + rect.width, rect.y + rect.height),Core.FONT_HERSHEY_PLAIN , 2, Detect_Color, 5);
                }
            }
            java.util.Date date2 = new java.util.Date();
            Timestamp timestamp2 = new Timestamp(date2.getTime());
            long milliseconds = timestamp2.getTime() - timestamp1.getTime();
            long second = 1000;
            if(milliseconds==0){
                milliseconds=1;
            }
            double fps = second / milliseconds;
            
            Imgproc.putText(mat, (int)fps + "FPS " + mat.cols()+"x"+mat.rows(),  new Point(30, 30),Core.FONT_HERSHEY_PLAIN , 2 , Detect_Color, 5);
            BufferedImage image = Util.Mat2BufferedImage(mat);
            if(recording){
                Imgcodecs.imwrite(path + formatLong.format(timestamp) +".png", mat);
            }
            //double relation = mat.cols()/mat.rows();
            double relation = (double)mat.cols()/(double)mat.rows();
            int finalWidth = this.getBounds().width;
            int finalHeight = (int)((finalWidth)/relation);
            int finalTopMargin = (int) (this.getBounds().height - finalHeight) / 2;
            g.drawImage(image,0,finalTopMargin,finalWidth, finalHeight, null);
            
        }else{
            camera.release();
        }
    }
}