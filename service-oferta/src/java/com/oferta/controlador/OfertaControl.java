/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.controlador;

import com.oferta.modelo.Oferta;
import com.oferta.servicio.ServicioOferta;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ESTUDIANTE
 */

@WebServlet("/publicar/*")
public class OfertaControl extends HttpServlet {
   private void setCORSHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        // Lista de orígenes permitidos (ajusta según tus necesidades)
        List<String> allowedOrigins = List.of("http://localhost:3000", "http://localhost:5173");
        
        if (origin != null && allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600"); // Cache preflight por 1 hora
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configurar CORS primero
        setCORSHeaders(request, response);
        
        // Si es una solicitud OPTIONS (preflight), terminar aquí
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        
        try {
            // Leer el cuerpo JSON de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            JSONObject json = new JSONObject(sb.toString());
            String horarioInicio = formatTime(json.getString("horarioInicio"));
            String horarioFin = formatTime(json.getString("horarioFin"));
            int servicioId = json.getInt("servicioId");
            double precioOferta = json.getDouble("precioOferta");
            int cuposDisponibles = json.getInt("cuposDisponibles");
            String descripcionOferta = json.getString("descripcionOferta");
            int idPrestador = json.getInt("idPrestador");
            
            // Convertir días disponibles de JSONArray a lista
            JSONArray diasArray = json.getJSONArray("diasDisponibles");
            List<String> diasDisponibles = new ArrayList<>();
            for (int i = 0; i < diasArray.length(); i++) {
                diasDisponibles.add(diasArray.getString(i));
            }
            
            // Crear y guardar la oferta
            Oferta oferta = new Oferta(servicioId, precioOferta, horarioInicio, horarioFin, 
                                      diasDisponibles, cuposDisponibles, descripcionOferta, idPrestador);
            
            ServicioOferta ser = new ServicioOferta();
            boolean exito = ser.publicarOferta(oferta);
            
            // Respuesta
            JSONObject respuesta = new JSONObject();
            if (exito) {
                respuesta.put("mensaje", "Oferta publicada correctamente");
                respuesta.put("ofertaId", oferta.getId());
            } else {
                respuesta.put("mensaje", "Error al publicar la oferta");
            }
            response.getWriter().write(respuesta.toString());
            
        } catch (Exception e) {
            // Asegurar que los headers CORS se envíen incluso en errores
            setCORSHeaders(request, response);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        setCORSHeaders(request, response);

        try {
            ServicioOferta servO = new ServicioOferta();
            List<Oferta> ofertas = servO.ofertasActivas();

            JSONArray jsonArray = new JSONArray();
            for (Oferta oferta : ofertas) {
                JSONObject json = new JSONObject();
                json.put("id", oferta.getId());
                json.put("servicio_id", oferta.getServicioId()); // Solo el ID
                json.put("prestador_id", oferta.getIdPrestador()); // Solo el ID
                json.put("precio_oferta", oferta.getPrecioOferta());
                json.put("horario_inicio", oferta.getHorarioInicio());
                json.put("horario_fin", oferta.getHorarioFin());
                json.put("dias_disponibles", oferta.getDiasAsString());
                json.put("descripcion", oferta.getDescripcionOferta());
                json.put("cupos_disponibles", oferta.getCuposDisponibles());

                jsonArray.put(json);
            }

            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Error al obtener ofertas: " + e.getMessage());
        }
    }
    private String formatTime(String timeString) {
        try {
            // Asume formato HH:mm (ej. "14:30")
            if (timeString.matches("^\\d{2}:\\d{2}$")) {
                return timeString + ":00"; // Agrega segundos para formato completo HH:mm:ss
            }
            return timeString; // Si ya tiene formato correcto
        } catch (Exception e) {
            return "00:00:00"; // Valor por defecto si hay error
        }
    }
}



   

