/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.servicio.reserva.dao;

import com.servicio.reserva.model.Reserva;
import java.util.List;

/**
 *
 * @author ESTUDIANTE
 */
public interface ReservaDAO {
    Reserva crear(Reserva reserva);
    Reserva obtenerPorId(Integer id);
    List<Reserva> obtenerTodas();
    boolean actualizar(Reserva reserva);
    boolean eliminar(Integer id);
    List<Reserva> obtenerPorCliente(Integer idCliente);
    List<Reserva> obtenerPorServicio(Integer idServicio);
    boolean cambiarEstado(Integer id, String estado);
    public List<Reserva> obtenerPorServicios(List<Integer> idsServicios);
    
}
