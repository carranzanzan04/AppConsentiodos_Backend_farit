/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.modelo.dao;

import com.oferta.modelo.Servicio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author ESTUDIANTE
 */
public class ServicioDaoPostgres implements  IServicioDao{

    
    private DataSource dataSource;

    public ServicioDaoPostgres() throws SQLException, NamingException {
        try {
            InitialContext ctx = new InitialContext();
            dataSource =  (DataSource) ctx.lookup("java:/comp/env/jdbc/IPSDS");
            
        } catch (NamingException ex) {
            throw new RuntimeException("Error de configuración JNDI", ex);
        }
    }

    @Override
    public boolean crearServicio(Servicio servicio) {
        try (Connection conexion=dataSource.getConnection() ){
            String sql = "INSERT INTO servicios (nombre, descripcion,  image_path, id_usuario,id_categoria) VALUES (?, ?, ?, ?, ?)";
    
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, servicio.getNombre());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setString(3, servicio.getImagePath());
            stmt.setInt(4, servicio.getIdPrestador());
            stmt.setInt(5, servicio.getIdCategoria());
            
            
            int rs = stmt.executeUpdate();


            return rs > 0;

        } catch (SQLException ex) {
            Logger.getLogger(ServicioDaoPostgres.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(" " + ex.getMessage());
        }
        return false;
    }

    @Override
    public List<Servicio> findAllServicios() {
        List<Servicio> lista = new LinkedList<>();
        String sql = "SELECT * FROM servicios ";
        try(Connection conexion=dataSource.getConnection() ) {
           
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                int id = Integer.parseInt(rs.getString("id"));
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String image = rs.getString("image_path");
                 int idCategoria = rs.getInt("id_categoria");
                 int idPrestador= rs.getInt("id_usuario");
                Servicio serv = new Servicio(id, nombre, descripcion, idCategoria,image,idPrestador);
                lista.add(serv);
            }

            System.out.println("" + lista);

            

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" " + e.getMessage());
        }
        return lista;
    }
    
    @Override
    public List<Servicio> getServiciosPorPrestador(int idPrestador) {
        List<Servicio> servicios = new LinkedList<>();
        String sql = "SELECT * FROM servicios WHERE id_usuario = ?";

        try (Connection conexion=dataSource.getConnection() ){
            
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idPrestador);
            
            ResultSet rs = stmt.executeQuery();
          
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int idCategoria = rs.getInt("id_categoria");
                String image = rs.getString("image_path");

                Servicio serv = new Servicio(id, nombre, descripcion, idCategoria,image,idPrestador);
                servicios.add(serv);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return servicios;
    }
    
    @Override
    public Servicio obtenerServicioId(int idServicio) {
        String sql = "SELECT * FROM servicios WHERE id = ?";
        Servicio serv = null;
        try(Connection conexion=dataSource.getConnection() ) {
            
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idServicio);
            
            ResultSet rs = stmt.executeQuery();
          
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            String descripcion = rs.getString("descripcion");
            int idCategoria = rs.getInt("id_categoria");
            String image = rs.getString("image_path");
            int idPrestador = rs.getInt("id_prestador");

            serv = new Servicio(id, nombre, descripcion, idCategoria , image,idPrestador);
            
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return serv;
    }

    @Override
    public boolean eliminarServicio(int id) {
        boolean exito = false;
        try (Connection conexion=dataSource.getConnection() ){
            String query = "DELETE FROM servicios WHERE id = ?";
            
            PreparedStatement statement = conexion.prepareStatement(query);
            statement.setInt(1, id);

            exito = statement.executeUpdate() > 0; // Retorna true si se eliminó algún registro
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }
      
    
    @Override
    public boolean actualizarServicio(int id, String serviceName, String serviceDescription, int idCategoria ) {
        boolean exito = false;
        try (Connection conexion=dataSource.getConnection() ){
            String query = "UPDATE servicios SET nombre = ?, descripcion = ?, categoria = ?, costo = ? WHERE id = ?";
   
            PreparedStatement statement = conexion.prepareStatement(query);
            statement.setString(1, serviceName);
            statement.setString(2, serviceDescription);
            statement.setInt(3, idCategoria);
            

            exito = statement.executeUpdate() > 0; // Retorna true si se actualizó algún registro
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }
}
