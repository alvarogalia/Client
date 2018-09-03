/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alvarogalia.Client.Obj;

/**
 *
 * @author Alvaro
 */
public class DetalleListaNegra {
    private long timestampRegistro;
    private String razon;
    private String contactoInformante;
    private String accion;

    /**
     * @return the timestampIngreso
     */
    public long getTimestampIngreso() {
        return timestampRegistro;
    }

    /**
     * @param timestampIngreso the timestampIngreso to set
     */
    public void setTimestampIngreso(long timestampIngreso) {
        this.timestampRegistro = timestampIngreso;
    }

    /**
     * @return the razon
     */
    public String getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(String razon) {
        this.razon = razon;
    }

    /**
     * @return the contactoInformante
     */
    public String getContactoInformante() {
        return contactoInformante;
    }

    /**
     * @param contactoInformante the contactoInformante to set
     */
    public void setContactoInformante(String contactoInformante) {
        this.contactoInformante = contactoInformante;
    }

    /**
     * @return the accion
     */
    public String getAccion() {
        return accion;
    }

    /**
     * @param accion the accion to set
     */
    public void setAccion(String accion) {
        this.accion = accion;
    }
    
}
