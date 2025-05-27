/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.servicio.reserva.dao;

import com.servicio.reserva.model.Reserva;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author ESTUDIANTE
 */
public class ReservaDAOPostgres implements ReservaDAO {
    private final DataSource dataSource;

    public ReservaDAOPostgres() throws SQLException, NamingException{
        try {
            InitialContext ctx = new InitialContext();
            dataSource =  (DataSource) ctx.lookup("java:/comp/env/jdbc/IPSDS");      
        } catch (NamingException ex) {
            throw new RuntimeException("Error de configuración JNDI", ex);
        }   
    }

    @Override
    public Reserva crear(Reserva reserva) {
        String sql = "INSERT INTO reservas (id_servicio, id_mascota, fecha_reserva) VALUES (?, ?, ?) RETURNING id_reserva";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reserva.getIdServicio());
            stmt.setInt(2, reserva.getIdMascota());
            stmt.setDate(3, new java.sql.Date(reserva.getFechaReserva().getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reserva.setId(rs.getInt("id_reserva"));
                }
            }
            return reserva;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear reserva", e);
        }
    }

    @Override
    public Reserva obtenerPorId(Integer id) {
        String sql = "SELECT * FROM reservas WHERE id_reserva = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearReserva(rs);
                }
                return null;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener reserva por ID", e);
        }
    }

    @Override
    public List<Reserva> obtenerTodas() {
        String sql = "SELECT * FROM reservas";
        List<Reserva> reservas = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservas.add(mapearReserva(rs));
            }
            return reservas;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las reservas", e);
        }
    }

    @Override
    public boolean actualizar(Reserva reserva) {
        String sql = "UPDATE reservas SET id_servicio = ?, id_mascota = ?, fecha_reserva = ? WHERE id_reserva = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reserva.getIdServicio());
            stmt.setInt(2, reserva.getIdMascota());
            stmt.setDate(3, new java.sql.Date(reserva.getFechaReserva().getTime()));
            stmt.setInt(4, reserva.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar reserva", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        // En tu esquema no hay campo 'estado', así que implemento como eliminación
        String sql = "DELETE FROM reservas WHERE id_reserva = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar reserva", e);
        }
    }

    @Override
    public List<Reserva> obtenerPorCliente(Integer idCliente) {
        // Asumiendo que hay una relación cliente-mascota que no está en el esquema
        // Esta implementación es un placeholder - necesitarías ajustarla según tu modelo real
        String sql = "SELECT r.* FROM reservas r JOIN mascotas m ON r.id_mascota = m.id WHERE m.id_cliente = ?";
        List<Reserva> reservas = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearReserva(rs));
                }
                return reservas;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener reservas por cliente", e);
        }
    }

    @Override
    public List<Reserva> obtenerPorServicio(Integer idServicio) {
        String sql = "SELECT * FROM reservas WHERE id_servicio = ?";
        List<Reserva> reservas = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idServicio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearReserva(rs));
                }
                return reservas;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener reservas por servicio", e);
        }
    }

    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getInt("id_reserva"));
        reserva.setIdServicio(rs.getInt("id_servicio"));
        reserva.setIdMascota(rs.getInt("id_mascota"));
        reserva.setFechaReserva(rs.getDate("fecha_reserva"));
        return reserva;
    }

    @Override
    public boolean cambiarEstado(Integer id, String estado) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Reserva> obtenerPorServicios(List<Integer> idsServicios) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
    
