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

    public VideoCamera(VideoCapture cam){
        camera  = cam; 
    }

    @Override
    protected void paintComponent(Graphics g){
        String country = "eu", configfile = "openalpr.conf", runtimeDataDir = "runtime_data";
        super.paintComponent(g);
        Mat mat = new Mat();
        if( camera.read(mat))
        {
            MatOfRect objects = new MatOfRect();
            CascadeClassifier classifier =  new CascadeClassifier("data/cascade.xml");
            int minHeight = mat.rows()/15;
            int minWidth = mat.cols()/15;
            classifier.detectMultiScale(mat, objects, 1.1, 8,0, new Size(minWidth,minHeight));
            
            SimpleDateFormat formatLong = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            
            if(!objects.empty()){
                for(int i = 0; i < objects.toList().size(); i++){
                    Rect rect = objects.toList().get(i);
                    Mat subMat = mat.submat(rect);
                    if(subMat.cols()>=100 && subMat.rows()>= 36){
//                        Imgcodecs.imwrite("/media/pi/NUEVO VOL/plates/"+ formatLong.format(timestamp) + "_" + i +".jpg", subMat);
//                        try {
//                            Alpr alpr = new Alpr(country, configfile, runtimeDataDir);
//                            alpr.setTopN(1);
//                            alpr.setDefaultRegion("cl");
//                            MatOfByte matOfByte = new MatOfByte();
//                            
//                            Imgcodecs.imencode("*.jpg", subMat, matOfByte);
//                            AlprResults response = alpr.recognize(matOfByte.toArray());
//                            alpr.unload();
//                            if(response.getPlates().size() > 0){
//                                String ppu = response.getPlates().get(0).getBestPlate().getCharacters();
//                                image = Util.drawPlate(image, rect, ppu);
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
                    }
                }
            }

            Scalar Detect_Color = new Scalar(0, 255, 0, 255);
            if(!objects.empty()){
                for(int i = 0; i < objects.toList().size(); i++){
                    Rect rect = objects.toList().get(i);
                    Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), Detect_Color, 5);
                }
            }
            Imgproc.putText(mat, mat.cols()+"x"+mat.rows(),  new Point(30, 30),Core.FONT_HERSHEY_PLAIN , 1 , Detect_Color, 1);
            BufferedImage image = Util.Mat2BufferedImage(mat);
//            Imgcodecs.imwrite("/media/pi/NUEVO VOL/video/"+ formatLong.format(timestamp) +".jpg", mat);
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