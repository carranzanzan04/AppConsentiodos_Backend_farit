/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.modelo;

import java.util.Date;
import java.util.List;

/**
 *
 * @author ESTUDIANTE
 */
public class Oferta {
    private int id;
    private int servicioId;
    private double precioOferta;
    private String horarioInicio;
    private String horarioFin;
    private List<String> diasDisponibles;
    private int cuposDisponibles;
    private String descripcionOferta;
    private boolean activa;
    private Date fechaPublicacion;
    private int idPrestador;

    public Oferta(int servicioId, double precioOferta, String horarioInicio, String horarioFin, List<String> diasDisponibles, int cuposDisponibles, String descripcionOferta, boolean activa, Date fechaPublicacion, int idPrestador) {
        this.servicioId = servicioId;
        this.precioOferta = precioOferta;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.diasDisponibles = diasDisponibles;
        this.cuposDisponibles = cuposDisponibles;
        this.descripcionOferta = descripcionOferta;
        this.idPrestador = idPrestador;
    }

    public Oferta(int servicioId, double precioOferta, String horarioInicio, String horarioFin, List<String> diasDisponibles, int cuposDisponibles, String descripcionOferta, int idPrestador) {
        this.servicioId = servicioId;
        this.precioOferta = precioOferta;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.diasDisponibles = diasDisponibles;
        this.cuposDisponibles = cuposDisponibles;
        this.descripcionOferta = descripcionOferta;
        this.idPrestador = idPrestador;
    }
    
    

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the servicioId
     */
    public int getServicioId() {
        return servicioId;
    }

    /**
     * @param servicioId the servicioId to set
     */
    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    /**
     * @return the precioOferta
     */
    public double getPrecioOferta() {
        return precioOferta;
    }

    /**
     * @param precioOferta the precioOferta to set
     */
    public void setPrecioOferta(double precioOferta) {
        this.precioOferta = precioOferta;
    }

    /**
     * @return the horarioInicio
     */
    public String getHorarioInicio() {
        return horarioInicio;
    }

    /**
     * @param horarioInicio the horarioInicio to set
     */
    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    /**
     * @return the horarioFin
     */
    public String getHorarioFin() {
        return horarioFin;
    }

    /**
     * @param horarioFin the horarioFin to set
     */
    public void setHorarioFin(String horarioFin) {
        this.horarioFin = horarioFin;
    }

    /**
     * @return the diasDisponibles
     */
    public List<String> getDiasDisponibles() {
        return diasDisponibles;
    }

    /**
     * @param diasDisponibles the diasDisponibles to set
     */
    public void setDiasDisponibles(List<String> diasDisponibles) {
        this.diasDisponibles = diasDisponibles;
    }

    /**
     * @return the cuposDisponibles
     */
    public int getCuposDisponibles() {
        return cuposDisponibles;
    }

    /**
     * @param cuposDisponibles the cuposDisponibles to set
     */
    public void setCuposDisponibles(int cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }

    /**
     * @return the descripcionOferta
     */
    public String getDescripcionOferta() {
        return descripcionOferta;
    }

    /**
     * @param descripcionOferta the descripcionOferta to set
     */
    public void setDescripcionOferta(String descripcionOferta) {
        this.descripcionOferta = descripcionOferta;
    }

    /**
     * @return the activa
     */
    public boolean isActiva() {
        return activa;
    }

    /**
     * @param activa the activa to set
     */
    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    /**
     * @return the fechaPublicacion
     */
    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    /**
     * @param fechaPublicacion the fechaPublicacion to set
     */
    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    /**
     * @return the idPrestador
     */
    public int getIdPrestador() {
        return idPrestador;
    }

    /**
     * @param idPrestador the idPrestador to set
     */
    public void setIdPrestador(int idPrestador) {
        this.idPrestador = idPrestador;
    }
    
    // Método para convertir días a String (para almacenar en BD)
    public String getDiasAsString() {
        return String.join(",", diasDisponibles);
    }
     
}