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
public class DetalleListaBlanca {
    private long timestampRegistro;
    private long timestampVigenciaDesde;
    private long timestampVigenciaHasta;

    /**
     * @return the timestampRegistro
     */
    public long getTimestampRegistro() {
        return timestampRegistro;
    }

    /**
     * @param timestampRegistro the timestampRegistro to set
     */
    public void setTimestampRegistro(long timestampRegistro) {
        this.timestampRegistro = timestampRegistro;
    }

    /**
     * @return the timestampVigenciaDesde
     */
    public long getTimestampVigenciaDesde() {
        return timestampVigenciaDesde;
    }

    /**
     * @param timestampVigenciaDesde the timestampVigenciaDesde to set
     */
    public void setTimestampVigenciaDesde(long timestampVigenciaDesde) {
        this.timestampVigenciaDesde = timestampVigenciaDesde;
    }

    /**
     * @return the timestampVigenciaHasta
     */
    public long getTimestampVigenciaHasta() {
        return timestampVigenciaHasta;
    }

    /**
     * @param timestampVigenciaHasta the timestampVigenciaHasta to set
     */
    public void setTimestampVigenciaHasta(long timestampVigenciaHasta) {
        this.timestampVigenciaHasta = timestampVigenciaHasta;
    }
}
