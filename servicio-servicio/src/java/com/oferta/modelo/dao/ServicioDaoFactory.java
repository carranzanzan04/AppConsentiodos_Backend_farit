/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.modelo.dao;

import java.sql.SQLException;
import javax.naming.NamingException;

public class ServicioDaoFactory {
 
    public static IServicioDao getDao(String tipoBD) throws SQLException, NamingException {

        switch (tipoBD.toLowerCase()) {
            case "mysql":
                return new ServicioDaoMySQL();
            case "postgres":
                return new ServicioDaoPostgres();
            default:
                throw new IllegalArgumentException("Tipo de base de datos no soportado: " + tipoBD);

        }

    }

}
 
