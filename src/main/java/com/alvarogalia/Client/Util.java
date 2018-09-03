/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client;

/**
 *
 * @author GENESYS
 */
public class Util {
    public static String longToDate(long pLong){
        //2018-00-00 00:00:00
        //2018-00-00 00:00:00
        String strLong = String.valueOf(pLong);
        strLong = new StringBuilder(strLong)
                .insert(4, "-")
                .insert(7, "-")
                .insert(10, " ")
                .insert(13, ":")
                .insert(16, ":").toString();
        return strLong;
    }
    public static long StringToLong(String pString){
        String limpio = pString.replace("-", "").replace(" ", "").replace(":", "");
        return Long.parseLong(limpio);
    }
}
