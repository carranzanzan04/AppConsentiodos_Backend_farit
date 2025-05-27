/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.servicio.reserva.factory;

import com.servicio.reserva.dao.ReservaDAO;
import com.servicio.reserva.dao.ReservaDAOMySQL;
import com.servicio.reserva.dao.ReservaDAOPostgres;
import java.sql.SQLException;
import javax.naming.NamingException;

/**
 *
 * @author ESTUDIANTE
 */
public class FactoryDAO {
    
    public static ReservaDAO getDao(String tipoBD) throws SQLException, NamingException {
       switch (tipoBD.toLowerCase()) {
            case "mysql":
                return new ReservaDAOMySQL();
            case "postgres":
                return new ReservaDAOPostgres();
            default:
                throw new IllegalArgumentException("Tipo de base de datos no soportado: " + tipoBD);

        }
    }
    
}
