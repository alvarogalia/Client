/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client.Obj;

/**
 *
 * @author GENESYS
 */
public class Historial {
    private long timestamp;
    private String ppu;

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the ppu
     */
    public String getPpu() {
        return ppu;
    }

    /**
     * @param ppu the ppu to set
     */
    public void setPpu(String ppu) {
        this.ppu = ppu;
    }
}
