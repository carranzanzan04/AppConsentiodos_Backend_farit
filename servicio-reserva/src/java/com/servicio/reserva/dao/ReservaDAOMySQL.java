/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.servicio.reserva.dao;

import com.servicio.reserva.model.Reserva;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author ESTUDIANTE
 */
public class ReservaDAOMySQL implements ReservaDAO {

    private final DataSource dataSource;

    public ReservaDAOMySQL() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/comp/env/jdbc/MySQLDS");
        } catch (NamingException ex) {
            throw new RuntimeException("Error de configuración JNDI", ex);
        }
    }

    @Override
    public Reserva crear(Reserva reserva) {
        String sql = "INSERT INTO reservas (id_servicio, id_mascota, fecha_reserva, estado, id_dueno) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Validación de datos básica
            if (reserva.getFechaReserva() == null) {
                throw new IllegalArgumentException("La fecha de reserva no puede ser nula");
            }

            // Configurar parámetros
            stmt.setInt(1, reserva.getIdServicio());
            stmt.setInt(2, reserva.getIdMascota());

            // Conversión robusta de fecha
            Timestamp timestamp = new Timestamp(reserva.getFechaReserva().getTime());
            stmt.setTimestamp(3, timestamp);
            stmt.setString(4, reserva.getEstado());
            stmt.setInt(5, reserva.getIdCliente());

            // Ejecutar inserción
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación de reserva falló, no se insertaron filas.");
            }

            // Obtener ID generado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    reserva.setId(idGenerado);
                    System.out.println("Reserva creada con ID: " + idGenerado); // Log diagnóstico
                } else {
                    throw new SQLException("La creación de reserva falló, no se obtuvo ID.");
                }
            }

            return reserva;

        } catch (SQLException e) {

            System.err.println("Error SQL al crear reserva:");
            System.err.println(" - Estado SQL: " + e.getSQLState());
            System.err.println(" - Código de error: " + e.getErrorCode());
            System.err.println(" - Mensaje: " + e.getMessage());

            throw new RuntimeException("Error al crear reserva: " + e.getMessage(), e);
        }
    }

    @Override
    public Reserva obtenerPorId(Integer id) {
        String sql = "SELECT * FROM reservas WHERE id_reserva = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT id_reserva, id_servicio, id_mascota, fecha_reserva, estado, id_dueno FROM reservas";
        List<Reserva> reservas = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reserva reserva = new Reserva();

                // Mapeo explícito de cada campo
                reserva.setId(rs.getInt("id_reserva"));
                reserva.setIdServicio(rs.getInt("id_servicio"));
                reserva.setIdMascota(rs.getInt("id_mascota"));
                reserva.setEstado(rs.getString("estado"));
                reserva.setIdCliente(rs.getInt("id_dueno"));

                // Manejo seguro de la fecha
                Timestamp timestamp = rs.getTimestamp("fecha_reserva");
                if (!rs.wasNull()) {
                    reserva.setFechaReserva(new java.util.Date(timestamp.getTime()));
                } else {
                    reserva.setFechaReserva(null);
                }

                reservas.add(reserva);
            }

            return reservas;

        } catch (SQLException e) {
            // Log detallado del error
            System.err.println("Error SQL en obtenerTodas():");
            System.err.println(" - SQL State: " + e.getSQLState());
            System.err.println(" - Error Code: " + e.getErrorCode());
            System.err.println(" - Message: " + e.getMessage());

            throw new RuntimeException("Error al obtener todas las reservas: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Reserva reserva) {
        String sql = "UPDATE reservas SET id_servicio = ?, id_mascota = ?, fecha_reserva = ? WHERE id_reserva = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        // En MySQL, implementamos como eliminación ya que no hay campo de estado
        String sql = "DELETE FROM reservas WHERE id_reserva = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar reserva", e);
        }
    }

    @Override
    public List<Reserva> obtenerPorCliente(Integer idDueno) {
        String sql = "SELECT * "
                + "FROM reservas WHERE id_dueno = ?";
        List<Reserva> reservas = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("ID: "+idDueno);
            stmt.setInt(1, idDueno);

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

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    public boolean cambiarEstado(Integer idReserva, String nuevoEstado) {
        String sql = "UPDATE reservas SET estado = ? WHERE id_reserva = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReserva);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado de reserva", e);
        }
    }

    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getInt("id_reserva"));
        reserva.setIdServicio(rs.getInt("id_servicio"));
        reserva.setIdMascota(rs.getInt("id_mascota"));
        reserva.setFechaReserva(rs.getDate("fecha_reserva"));
        reserva.setEstado(rs.getString("estado"));
        reserva.setIdCliente(rs.getInt("id_dueno"));
        return reserva;
    }

    @Override
    public List<Reserva> obtenerPorServicios(List<Integer> idsServicios) {
        if (idsServicios == null || idsServicios.isEmpty()) {
            throw new IllegalArgumentException("La lista de IDs no puede estar vacía");
        }

        // Crear la parte IN de la consulta con parámetros preparados
        String placeholders = idsServicios.stream()
                                       .map(id -> "?")
                                       .collect(Collectors.joining(","));
        
        String sql = "SELECT * FROM reservas WHERE id_servicio IN (" + placeholders + ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Establecer parámetros
            for (int i = 0; i < idsServicios.size(); i++) {
                stmt.setInt(i + 1, idsServicios.get(i));
            }

            // Ejecutar consulta
            try (ResultSet rs = stmt.executeQuery()) {
                List<Reserva> reservas = new ArrayList<>();
                while (rs.next()) {
                    reservas.add(mapearReserva(rs));
                }
                return reservas;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservaDAOMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Reserva> reservas = null;
        return reservas;
    }
    
}
