/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.servicio.reserva.model;

import java.util.Date;

/**
 *
 * @author ESTUDIANTE
 */
public class Reserva {
    private Integer id;
    private Integer idServicio;
    private Integer idMascota;
    private Integer idCliente;
    private Date fechaReserva;
    private String estado;
    private Date fechaCreacion;

    public Reserva() {
    }

    public Reserva(Integer id, Integer idServicio, Integer idMascota, Integer idCliente, Date fechaReserva, String estado) {
        this.id = id;
        this.idServicio = idServicio;
        this.idMascota = idMascota;
        this.idCliente = idCliente;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.fechaCreacion = new Date();
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the idServicio
     */
    public Integer getIdServicio() {
        return idServicio;
    }

    /**
     * @param idServicio the idServicio to set
     */
    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    /**
     * @return the idMascota
     */
    public Integer getIdMascota() {
        return idMascota;
    }

    /**
     * @param idMascota the idMascota to set
     */
    public void setIdMascota(Integer idMascota) {
        this.idMascota = idMascota;
    }

    /**
     * @return the idCliente
     */
    public Integer getIdCliente() {
        return idCliente;
    }

    /**
     * @param idCliente the idCliente to set
     */
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * @return the fechaReserva
     */
    public Date getFechaReserva() {
        return fechaReserva;
    }

    /**
     * @param fechaReserva the fechaReserva to set
     */
    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the fechaCreacion
     */
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * @param fechaCreacion the fechaCreacion to set
     */
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    
    
}
