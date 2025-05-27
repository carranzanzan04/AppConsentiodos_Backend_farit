/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.oferta.modelo.dao;

import com.oferta.modelo.Servicio;
import java.util.List;

public interface IServicioDao {
    boolean crearServicio(Servicio servicio);
    List<Servicio> findAllServicios();
    List<Servicio> getServiciosPorPrestador(int idPrestador);
    Servicio obtenerServicioId(int idServicio);
    boolean eliminarServicio(int id);
    boolean actualizarServicio(int id, String serviceName, String serviceDescription, int idCategoria);
}
