/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.modelo;

/**
 *
 * @author ESTUDIANTE
 */
public class Servicio {
    private int id;
    private String nombre;
    private String descripcion;
    private int idCategoria;
    private String imagePath;
    private int idPrestador;
    
    public Servicio(String nombre, String descripcion, int idCategoria, String imagePath, int idPrestador) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.imagePath = imagePath;
        this.idPrestador = idPrestador;
    }
    
    public Servicio(int id, String nombre, String descripcion, int idCategoria, String imagePath,int idPrestador) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.imagePath = imagePath;
          this.idPrestador = idPrestador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(int idPrestador) {
        this.idPrestador = idPrestador;
    }
     
}