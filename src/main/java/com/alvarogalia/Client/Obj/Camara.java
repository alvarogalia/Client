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
public class Camara {
    private String PPU;
    private String lastSpotFecha;
    private String lastSpotHora;

    /**
     * @return the PPU
     */
    public String getPPU() {
        return PPU;
    }

    /**
     * @param PPU the PPU to set
     */
    public void setPPU(String PPU) {
        this.PPU = PPU;
    }
    
    /**
     * @return the lastSpotFecha
     */
    public String getLastSpotFecha() {
        return lastSpotFecha;
    }

    /**
     * @param lastSpotFecha the lastSpotFecha to set
     */
    public void setLastSpotFecha(String lastSpotFecha) {
        this.lastSpotFecha = lastSpotFecha;
    }

    /**
     * @return the lastSpotHora
     */
    public String getLastSpotHora() {
        return lastSpotHora;
    }

    /**
     * @param lastSpotHora the lastSpotHora to set
     */
    public void setLastSpotHora(String lastSpotHora) {
        this.lastSpotHora = lastSpotHora;
    }

    
}
