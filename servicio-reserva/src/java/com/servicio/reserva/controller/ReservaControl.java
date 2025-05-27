/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.servicio.reserva.controller;

import com.servicio.reserva.dto.ReservaDTO;
import com.servicio.reserva.model.Reserva;
import com.servicio.reserva.service.ReservaServicio;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ESTUDIANTE
 */
@WebServlet("/api/reservas/*")
public class ReservaControl extends HttpServlet {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private ReservaServicio reservaServicio;

    @Override
    public void init() throws ServletException {
        try {
            reservaServicio = new ReservaServicio();
        } catch (Exception e) {
            throw new ServletException("Error al inicializar ReservaService", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setCORSHeaders(request, response);

        try {
            // Leer el cuerpo de la solicitud
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            JSONObject json = new JSONObject(requestBody);

            // Crear DTO desde JSON
            ReservaDTO reservaDTO = new ReservaDTO();
            reservaDTO.setIdServicio(json.getInt("idServicio"));
            reservaDTO.setIdMascota(json.getInt("idMascota"));
            reservaDTO.setIdCliente(json.getInt("idCliente"));

            // Parsear fecha en formato ISO 8601 (ej: "2023-12-31T15:30:00" o "2023-12-31T15:30:00Z")
            String fechaStr = json.getString("fechaReserva");

            // Versión más robusta para parsear la fecha
            Instant instant;
            try {
                // Primero intenta parsear directamente como Instant (para formatos con Z)
                instant = Instant.parse(fechaStr);
            } catch (DateTimeParseException e) {
                // Si falla, intenta parsear como LocalDateTime y convertir
                LocalDateTime localDateTime = LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            }

            reservaDTO.setFechaReserva(Date.from(instant));

            // Crear reserva
            Reserva reservaCreada = reservaServicio.crearReserva(reservaDTO);
            JSONObject respuesta = new JSONObject();
            respuesta.put("id", reservaCreada.getId());
            respuesta.put("mensaje", "Reserva creada exitosamente");
            respuesta.put("fechaReserva", fechaStr); // Para verificación

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(respuesta.toString());

        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, "Formato de fecha inválido. Use formato ISO 8601 (ej: 2023-12-31T15:30:00 o 2023-12-31T15:30:00Z)");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            enviarError(response, "Error interno del servidor: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setCORSHeaders(request, response);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            String pathInfo = request.getPathInfo();

            // Caso 1: Obtener todas las reservas
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Reserva> reservas = reservaServicio.obtenerTodasLasReservas();
                enviarRespuesta(reservas, response);

                // Caso 2: Obtener por servicio
            } else if (pathInfo.startsWith("/servicio/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length != 3) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    enviarError(response, "Formato de URL inválido. Use /api/reservas/servicio/{id} o /api/reservas/servicio/{id1,id2,id3}");
                    return;
                }

                String idsParam = parts[2];
                if (idsParam == null || idsParam.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    enviarError(response, "Se requiere al menos un ID de servicio");
                    return;
                }

                String[] idsArray = idsParam.split(",");
                List<Integer> idsServicios = new ArrayList<>();

                for (String idStr : idsArray) {
                    try {
                        String cleanedId = idStr.trim();
                        if (!cleanedId.isEmpty()) {
                            idsServicios.add(Integer.valueOf(cleanedId));
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        enviarError(response, "ID de servicio no válido: '" + idStr + "'. Solo se permiten números enteros.");
                        return;
                    }
                }

                if (idsServicios.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    enviarError(response, "No se proporcionaron IDs de servicio válidos");
                    return;
                }

                List<Reserva> reservas;
                if (idsServicios.size() == 1) {
                    reservas = reservaServicio.obtenerReservasPorServicio(idsServicios.get(0));
                } else {
                    reservas = reservaServicio.obtenerReservasPorServicios(idsServicios);
                }

                enviarRespuesta(reservas, response);

                // Caso 3: Obtener por cliente (/api/reservas/mascota/{id})
            } else if (pathInfo.startsWith("/mascota/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length != 3) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    enviarError(response, "Formato de URL inválido. Use /api/reservas/mascota/{id}");
                    return;
                }

                Integer idCliente = Integer.valueOf(parts[2]);
                List<Reserva> reservas = reservaServicio.obtenerReservasPorCliente(idCliente);
                enviarRespuesta(reservas, response);

                // Caso 4: Obtener reserva específica (/api/reservas/{id})
            } else if (pathInfo.matches("/\\d+$")) {
                Integer idReserva = Integer.valueOf(pathInfo.substring(1));
                Reserva reserva = reservaServicio.obtenerReservaPorId(idReserva);

                if (reserva != null) {
                    JSONObject reservaJson = crearJsonReserva(reserva);
                    response.getWriter().write(reservaJson.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    enviarError(response, "Reserva no encontrada");
                }

                // Caso inválido
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                enviarError(response, "Ruta no válida. Rutas soportadas: "
                        + "/api/reservas, /api/reservas/servicio/{id}, /api/reservas/mascota/{id}, /api/reservas/{id}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, "El ID debe ser un número válido");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            enviarError(response, "Error interno del servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setCORSHeaders(request, response);

        try {
            String pathInfo = request.getPathInfo();

            // Verificar que la URL tenga el formato correcto: /api/reservas/{id}/estado
            if (pathInfo == null || !pathInfo.matches("/\\d+/estado")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                enviarError(response, "Formato de URL inválido. Use /api/reservas/{id}/estado");
                return;
            }

            // Extraer el ID de la reserva
            String[] parts = pathInfo.split("/");
            Integer idReserva = Integer.valueOf(parts[1]);

            // Leer el nuevo estado del cuerpo de la solicitud
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            JSONObject json = new JSONObject(requestBody);

            if (!json.has("estado")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                enviarError(response, "El campo 'estado' es requerido en el cuerpo de la solicitud");
                return;
            }

            String nuevoEstado = json.getString("estado");

            // Validar que el estado sea uno de los permitidos (opcional)
            if (!esEstadoValido(nuevoEstado)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                enviarError(response, "Estado no válido. Los estados permitidos son: PENDIENTE, CONFIRMADA, CANCELADA");
                return;
            }

            // Actualizar el estado
            boolean actualizado = reservaServicio.actualizarEstado(idReserva, nuevoEstado);

            if (actualizado) {
                JSONObject respuesta = new JSONObject();
                respuesta.put("mensaje", "Estado de reserva actualizado exitosamente");
                respuesta.put("idReserva", idReserva);
                respuesta.put("nuevoEstado", nuevoEstado);
                response.getWriter().write(respuesta.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                enviarError(response, "Reserva no encontrada o estado no modificado");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, "El ID debe ser un número válido");
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, "Formato JSON inválido en el cuerpo de la solicitud");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            enviarError(response, "Error interno del servidor: " + e.getMessage());
        }
    }

    private boolean esEstadoValido(String estado) {
        if (estado == null) {
            return false;
        }
        return estado.equals("PENDIENTE")
                || estado.equals("CONFIRMADA")
                || estado.equals("CANCELADA");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setCORSHeaders(request, response);

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || !pathInfo.matches("/\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                enviarError(response, "Se requiere ID de reserva");
                return;
            }

            Integer idReserva = Integer.valueOf(pathInfo.substring(1));
            boolean exito = reservaServicio.cancelarReserva(idReserva);

            if (exito) {
                JSONObject respuesta = new JSONObject();
                respuesta.put("mensaje", "Reserva cancelada exitosamente");
                response.getWriter().write(respuesta.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                enviarError(response, "Reserva no encontrada o ya cancelada");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, "ID debe ser un número");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            enviarError(response, e.getMessage());
        } catch (IOException | JSONException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            enviarError(response, "Error interno del servidor");
        }
    }

    // Métodos auxiliares
    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", mensaje);
        response.getWriter().write(error.toString());
    }

    private void setCORSHeaders(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // Método auxiliar para enviar listas de reservas
    private void enviarRespuesta(List<Reserva> reservas, HttpServletResponse response)
            throws IOException, JSONException {

        JSONArray reservasJson = new JSONArray();
        for (Reserva reserva : reservas) {
            reservasJson.put(crearJsonReserva(reserva));
        }
        response.getWriter().write(reservasJson.toString());
    }

    // Método auxiliar para crear JSON de una reserva
    private JSONObject crearJsonReserva(Reserva reserva) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", reserva.getId());
        json.put("idServicio", reserva.getIdServicio());
        json.put("idMascota", reserva.getIdMascota());
        json.put("idCliente", reserva.getIdCliente());
        if (reserva.getFechaReserva() != null) {
            json.put("fechaReserva", dateFormat.format(reserva.getFechaReserva()));
        } else {
            json.put("fechaReserva", JSONObject.NULL);
        }

        json.put("estado", reserva.getEstado());
        return json;
    }

}
