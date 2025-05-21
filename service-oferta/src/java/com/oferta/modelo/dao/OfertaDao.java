/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.modelo.dao;

import com.oferta.modelo.Oferta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author ESTUDIANTE
 */

public class OfertaDao {
    private Connection conexion;
    private DataSource dataSource;

    public OfertaDao() throws SQLException, NamingException {
        try {
            InitialContext ctx = new InitialContext();
            dataSource =  (DataSource) ctx.lookup("java:/comp/env/jdbc/IPSDS");
            conexion = dataSource.getConnection();
        } catch (NamingException ex) {
            throw new RuntimeException("Error de configuración JNDI", ex);
        }
    }

    // Método para publicar una nueva oferta
    public boolean crearOferta(Oferta oferta) throws SQLException {
        String sql = "INSERT INTO ofertas (servicio_id, prestador_id, precio_oferta, " +
                     "horario_inicio, horario_fin, dias_disponibles, cupos_disponibles, " +
                     "descripcion) VALUES (?, ?, ?, ?::time, ?::time, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, oferta.getServicioId());
            stmt.setInt(2, oferta.getIdPrestador());
            stmt.setDouble(3, oferta.getPrecioOferta());
            stmt.setString(4, oferta.getHorarioInicio());
            stmt.setString(5, oferta.getHorarioFin());
            stmt.setString(6, oferta.getDiasAsString());
            stmt.setInt(7, oferta.getCuposDisponibles());
            stmt.setString(8, oferta.getDescripcionOferta());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        oferta.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // Método para obtener ofertas por prestador
    public List<Oferta> obtenerOfertasPorPrestador(int idPrestador) throws SQLException {
        List<Oferta> ofertas = new ArrayList<>();
        String sql = "SELECT * FROM ofertas WHERE prestador_id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPrestador);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ofertas.add(mapearOferta(rs));
                }
            }
        }
        return ofertas;
    }

    // Método para obtener ofertas activas
    public List<Oferta> obtenerOfertasActivas() throws SQLException {
        List<Oferta> ofertas = new ArrayList<>();
        String sql = "SELECT * FROM ofertas WHERE activa = TRUE";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ofertas.add(mapearOferta(rs));
            }
        }
        return ofertas;
    }

    // Método para actualizar una oferta
    public boolean actualizarOferta(Oferta oferta) throws SQLException {
        String sql = "UPDATE ofertas SET precio_oferta = ?, horario_inicio = ?, " +
                     "horario_fin = ?, dias_disponibles = ?, cupos_disponibles = ?, " +
                     "descripcion = ?, activa = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, oferta.getPrecioOferta());
            stmt.setString(2, oferta.getHorarioInicio());
            stmt.setString(3, oferta.getHorarioFin());
            stmt.setString(4, oferta.getDiasAsString());
            stmt.setInt(5, oferta.getCuposDisponibles());
            stmt.setString(6, oferta.getDescripcionOferta());
            stmt.setBoolean(7, oferta.isActiva());
            stmt.setInt(8, oferta.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Método para desactivar una oferta
    public boolean desactivarOferta(int idOferta) throws SQLException {
        String sql = "UPDATE ofertas SET activa = FALSE WHERE id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idOferta);
            return stmt.executeUpdate() > 0;
        }
    }

    // Método auxiliar para mapear ResultSet a objeto Oferta
    private Oferta mapearOferta(ResultSet rs) throws SQLException {
        Oferta oferta = new Oferta(
            rs.getInt("servicio_id"),
            rs.getDouble("precio_oferta"),
            rs.getString("horario_inicio"),
            rs.getString("horario_fin"),
            Arrays.asList(rs.getString("dias_disponibles").split(",")),
            rs.getInt("cupos_disponibles"),
            rs.getString("descripcion"),
            rs.getInt("prestador_id")
        );
        oferta.setId(rs.getInt("id"));
        oferta.setActiva(rs.getBoolean("activa"));
        oferta.setFechaPublicacion(rs.getTimestamp("fecha_publicacion"));
        return oferta;
    }
}