/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.servicio;

import com.oferta.modelo.dao.ServicioDaoPostgres;
import com.oferta.modelo.Servicio;
import com.oferta.modelo.dao.IServicioDao;
import com.oferta.modelo.dao.ServicioDaoFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 *
 * @author ESTUDIANTE
 */
public class ServicioServicio {

    private IServicioDao serv;

    public ServicioServicio() {

        try {

            serv =  ServicioDaoFactory.getDao("postgres");

        } catch (SQLException | NamingException ex) {

            Logger.getLogger(ServicioServicio.class.getName()).log(Level.SEVERE, "Error al inicializar ServicioDao", ex);

            throw new RuntimeException("No se pudo inicializar ServicioDao", ex); // Falla rápido

        }

    }
 

    public boolean CrearServicio(Servicio servicio) {
        return serv.crearServicio(servicio);
    }

    public List<Servicio> getAllServicios() {
        return serv.findAllServicios();
    }

    public List<Servicio> getAllServiciosID(int idPrestador) {
        return serv.getServiciosPorPrestador(idPrestador);
    }

    public Servicio getServicio(int id) {
        for (Servicio servicio : getAllServicios()) {
            if (servicio.getId() == id) {
                return servicio;
            }
        }
        return null;
    }

    public boolean eliminarServicioId(int id) {
      return serv.eliminarServicio(id);
    }

    public boolean actualizarServicio(int id, String serviceName, String serviceDescription, int idCategoria) {
    return serv.actualizarServicio(id, serviceName, serviceDescription, idCategoria);
    }
}
