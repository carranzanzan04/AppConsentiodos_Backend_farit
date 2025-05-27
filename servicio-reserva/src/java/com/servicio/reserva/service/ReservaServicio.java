/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.servicio.reserva.service;

import com.servicio.reserva.dao.ReservaDAO;
import com.servicio.reserva.dto.ReservaDTO;
import com.servicio.reserva.factory.FactoryDAO;
import com.servicio.reserva.model.Reserva;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 *
 * @author ESTUDIANTE
 */

public class ReservaServicio {
    private ReservaDAO reservaDAO;

    public ReservaServicio() {
        try {
            reservaDAO = FactoryDAO.getDao("mysql");
        } catch (SQLException | NamingException ex) {
            Logger.getLogger(ReservaServicio.class.getName()).log(Level.SEVERE, "Error al inicializar ReservaDAO", ex);
            throw new RuntimeException("No se pudo inicializar ReservaDAO", ex);
        }
    }

    // Método para crear reserva (ya existente)
    public Reserva crearReserva(ReservaDTO reservaDTO) {
        validarReservaDTO(reservaDTO);
        Reserva reserva = convertirDTOaEntidad(reservaDTO);
        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(new Date());
        return reservaDAO.crear(reserva);
    }

    public boolean cancelarReserva(Integer idReserva) {
        return reservaDAO.eliminar(idReserva);
    }

    // Método para obtener reservas por servicio
    public List<Reserva> obtenerReservasPorServicio(Integer idServicio) {
        if (idServicio == null || idServicio <= 0) {
            throw new IllegalArgumentException("ID de servicio inválido");
        }
        return reservaDAO.obtenerPorServicio(idServicio);
    }
    
     public List<Reserva> obtenerReservasPorServicios(List<Integer> idsServicios) {
        return reservaDAO.obtenerPorServicios(idsServicios);
    }

    // Método para obtener reserva por ID
    public Reserva obtenerReservaPorId(Integer idReserva) {
        if (idReserva == null || idReserva <= 0) {
            throw new IllegalArgumentException("ID de reserva inválido");
        }
        return reservaDAO.obtenerPorId(idReserva);
    }

    // Método para obtener todas las reservas
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaDAO.obtenerTodas();
    }

    public ReservaServicio(ReservaDAO reservaDAO) {
        this.reservaDAO = reservaDAO;
    }
    
    public List<Reserva> obtenerReservasPorCliente(Integer IdDueño) {
        return reservaDAO.obtenerPorCliente(IdDueño);
    }
 
    // Métodos auxiliares de validación
    private void validarReservaDTO(ReservaDTO reservaDTO) {
        if (reservaDTO == null) {
            throw new IllegalArgumentException("El DTO de reserva no puede ser nulo");
        }
        
        if (reservaDTO.getIdServicio() == null || reservaDTO.getIdMascota() == null || 
            reservaDTO.getIdCliente() == null || reservaDTO.getFechaReserva() == null) {
            throw new IllegalArgumentException("Todos los campos de la reserva son requeridos");
        }
        
        if (reservaDTO.getFechaReserva().before(new Date())) {
            throw new IllegalArgumentException("La fecha de reserva no puede ser en el pasado");
        }
    }

    // Métodos de conversión
    private Reserva convertirDTOaEntidad(ReservaDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setIdServicio(dto.getIdServicio());
        reserva.setIdMascota(dto.getIdMascota());
        reserva.setIdCliente(dto.getIdCliente());
        reserva.setFechaReserva(dto.getFechaReserva());
        return reserva;
    }

    private ReservaDTO convertirEnt1idadADTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setIdServicio(reserva.getIdServicio());
        dto.setIdMascota(reserva.getIdMascota());
        dto.setIdCliente(reserva.getIdCliente());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setEstado(reserva.getEstado());
        return dto;
    }

    public boolean actualizarEstado(int id_oferta, String nuevoEstado) {
        return reservaDAO.cambiarEstado(id_oferta, nuevoEstado);
    }
}

