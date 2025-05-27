/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.servicio;

import com.oferta.modelo.dao.OfertaDao;
import com.oferta.modelo.Oferta;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 *
 * @author ESTUDIANTE
 */
public class ServicioOferta {
    private OfertaDao serv;

    public ServicioOferta() {
        try {
            serv = new OfertaDao();
        } catch (SQLException ex) {
            Logger.getLogger(Oferta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(Oferta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean publicarOferta(Oferta oferta) throws SQLException {
        return serv.crearOferta(oferta);
    }
    
    public List<Oferta> ofertasActivas(){
        try {
            return serv.obtenerOfertasActivas();
        } catch (SQLException ex) {
            Logger.getLogger(ServicioOferta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
}