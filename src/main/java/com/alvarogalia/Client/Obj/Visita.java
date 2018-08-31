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
public class Visita {
    private String ppu;
    private String rut;
    private String dv;
    private String nombre;
    private long timestampEntrada;
    private long timestampSalida;
    private int duracion;
    
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

    /**
     * @return the rut
     */
    public String getRut() {
        return rut;
    }

    /**
     * @param rut the rut to set
     */
    public void setRut(String rut) {
        this.rut = rut;
    }

    /**
     * @return the dv
     */
    public String getDv() {
        return dv;
    }

    /**
     * @param dv the dv to set
     */
    public void setDv(String dv) {
        this.dv = dv;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the timestampEntrada
     */
    public long getTimestampEntrada() {
        return timestampEntrada;
    }

    /**
     * @param timestampEntrada the timestampEntrada to set
     */
    public void setTimestampEntrada(long timestampEntrada) {
        this.timestampEntrada = timestampEntrada;
    }

    /**
     * @return the timestampSalida
     */
    public long getTimestampSalida() {
        return timestampSalida;
    }

    /**
     * @param timestampSalida the timestampSalida to set
     */
    public void setTimestampSalida(long timestampSalida) {
        this.timestampSalida = timestampSalida;
    }

    /**
     * @return the duracion
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * @param duracion the duracion to set
     */
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
}
