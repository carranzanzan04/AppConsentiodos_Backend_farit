/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.oferta.controlador;

import com.oferta.modelo.Servicio;
import com.oferta.servicio.ServicioServicio;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ESTUDIANTE
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)

@WebServlet("/servicio/*")
public class ServicioControl extends HttpServlet {

    ServicioServicio servicioS;

    @Override
    public void init() throws ServletException {
        try {
            servicioS = new ServicioServicio(); // Inicialización única
        } catch (Exception e) {
            throw new ServletException("Error al inicializar ServicioServicio", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        setCORSHeaders(request, response);

        try {
            // Leer los campos enviados
            int idPrestador = Integer.parseInt(request.getParameter("id"));
            String serviceName = request.getParameter("serviceName");
            String serviceDescription = request.getParameter("serviceDescription");
            int serviceCategory = Integer.parseInt(request.getParameter("idcategoria"));

            // Procesar el archivo
            Part filePart = request.getPart("serviceImage"); // El name del input file
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // Solo nombre de archivo

            // Ruta donde quieres guardar la imagen (por ejemplo en tu proyecto local)
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // Guardar la imagen en el servidor
            filePart.write(uploadPath + File.separator + fileName);

            // Ruta relativa para guardar en la base de datos
            String imagePath = "uploads/" + fileName;

            // Crear servicio con ruta de imagen
            Servicio servicio = new Servicio(serviceName, serviceDescription, serviceCategory, imagePath, idPrestador);

            boolean exito = servicioS.CrearServicio(servicio);

            // Respuesta
            JSONObject respuesta = new JSONObject();
            if (exito) {
                respuesta.put("mensaje", "El servicio se creó correctamente");
            } else {
                respuesta.put("mensaje", "Hubo un error en el registro del servicio");
            }
            response.getWriter().write(respuesta.toString());

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error procesando el formulario: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        setCORSHeaders(request, response);

        String pathInfo = request.getPathInfo(); // puede ser null, /{id}, /id/{id}

        if (pathInfo == null || pathInfo.equals("/")) {
            // Ruta: /servicio → todos los servicios
            List<Servicio> servicios = servicioS.getAllServicios();

            JSONArray serviciosJson = new JSONArray();
            for (Servicio servicio : servicios) {
                JSONObject servicioJson = new JSONObject();
                servicioJson.put("serviceId", servicio.getId());
                servicioJson.put("serviceName", servicio.getNombre());
                servicioJson.put("serviceDescription", servicio.getDescripcion());
                servicioJson.put("idcategoria", servicio.getIdCategoria());
                servicioJson.put("serviceImage", servicio.getImagePath());
                servicioJson.put("idprestador", servicio.getIdPrestador());
                serviciosJson.put(servicioJson);
            }

            response.setContentType("application/json");
            response.getWriter().write(serviciosJson.toString());

        } else {
            String[] partes = pathInfo.split("/");

            if (partes.length == 2) {
                // Ruta: /servicio/{id} → servicios de un prestador
                int id = Integer.parseInt(partes[1]);
                List<Servicio> servicios = servicioS.getAllServiciosID(id);

                JSONArray serviciosJson = new JSONArray();
                for (Servicio servicio : servicios) {
                    JSONObject servicioJson = new JSONObject();
                    servicioJson.put("serviceId", servicio.getId());
                    servicioJson.put("serviceName", servicio.getNombre());
                    servicioJson.put("serviceDescription", servicio.getDescripcion());
                    servicioJson.put("serviceCategory", servicio.getIdCategoria());
                    servicioJson.put("serviceImage", servicio.getImagePath());
                    serviciosJson.put(servicioJson);
                }

                response.setContentType("application/json");
                response.getWriter().write(serviciosJson.toString());

            } else if (partes.length == 3 && partes[1].equals("id")) {
                // Ruta: /servicio/id/{id} → un servicio específico
                int id = Integer.parseInt(partes[2]);
                Servicio servicio = servicioS.getServicio(id); // Asume que tienes este método

                if (servicio != null) {
                    JSONObject servicioJson = new JSONObject();
                    servicioJson.put("serviceId", servicio.getId());
                    servicioJson.put("serviceName", servicio.getNombre());
                    servicioJson.put("serviceDescription", servicio.getDescripcion());
                    servicioJson.put("idcategoria", servicio.getIdCategoria());
                    servicioJson.put("serviceImage", servicio.getImagePath());
                    servicioJson.put("idprestador", servicio.getIdPrestador());

                    response.setContentType("application/json");
                    response.getWriter().write(servicioJson.toString());
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Servicio no encontrado");
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta inválida");
            }
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        setCORSHeaders(request, response);

        try {
            // Obtener el ID del servicio de la URL
            String idStr = request.getPathInfo().substring(1); // /servicios/{id}
            int id = Integer.parseInt(idStr);

            // Llamar al DAO para eliminar el servicio
            boolean exito = servicioS.eliminarServicioId(id);

            // Responder al cliente
            JSONObject respuesta = new JSONObject();
            if (exito) {
                respuesta.put("mensaje", "El servicio se eliminó correctamente");
            } else {
                respuesta.put("mensaje", "No se pudo eliminar el servicio");
            }
            response.getWriter().write(respuesta.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error eliminando el servicio: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // Configurar headers CORS
        setCORSHeaders(request, response);

        try {
            // Obtener el ID del servicio de la URL
            String idStr = request.getPathInfo();
            if (idStr != null) {
                idStr = idStr.substring(1); // Remueve la barra inicial "/"
            }

            int id = Integer.parseInt(idStr);

            // Leer los datos actualizados del cuerpo de la solicitud
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }

            JSONObject json = new JSONObject(jsonBuffer.toString());
            String serviceName = json.getString("serviceName");
            String serviceDescription = json.getString("serviceDescription");
            int idCategoria = json.getInt("id_categoria");

            // Llamar al DAO para actualizar el servicio
            //servicioDao.actualizarServicio(id, serviceName, serviceDescription, idCategoria)
            boolean exito = servicioS.actualizarServicio(id, serviceName, serviceDescription, idCategoria);

            // Responder al cliente
            JSONObject respuesta = new JSONObject();
            if (exito) {
                respuesta.put("mensaje", "El servicio se actualizó correctamente");
            } else {
                respuesta.put("mensaje", "No se pudo actualizar el servicio");
            }
            response.getWriter().write(respuesta.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error actualizando el servicio: " + e.getMessage());
        }
    }

    private void setCORSHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        // Lista de orígenes permitidos (ajusta según tus necesidades)
        List<String> allowedOrigins = List.of("http://localhost:5173");

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
}
