package paa.locker.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import paa.locker.business.JPAParcelService;
import paa.locker.business.ParcelServiceException;
import paa.locker.model.Locker;
import paa.locker.model.Parcel;

/**
 * Servlet implementation class LockerServer
 */
@WebServlet("/LockerServer")
public class LockerServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private JPAParcelService ps;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LockerServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
    public void init(ServletConfig config) throws ServletException {
    	 super.init(config);
    	 final String absoluteDiskPath = getServletContext().getRealPath("./WEB-INF/bdatos");
    	 try{
    	 ps = new JPAParcelService(absoluteDiskPath);
    	 } catch(Exception e) {
    	 System.out.println("Error al instanciar JPAParcelService.\n");
    	 e.printStackTrace();
    	 throw new ServletException(e);
    	 }
    	 System.out.println(getServletContext().getRealPath(absoluteDiskPath));
    } 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		String accion = request.getParameter("accion");
		
		if (accion != null) {
			List<String> listaParametros = new ArrayList<String>();
        
			switch (accion) {
			case "createLocker":
				try {
					String[] nom_parametros = {"name", "address", "longitude", "latitude", "largeCompartments", "smallCompartments"};
					ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
					listaParametros = handleRequest(request, response, nom_parametros);
					
					if (listaParametros.size() == nom_parametros.length) {
						Locker l = ps.createLocker(listaParametros.get(0), listaParametros.get(1), Double.parseDouble(listaParametros.get(2)), 
						Double.parseDouble(listaParametros.get(3)), Integer.parseInt(listaParametros.get(4)), Integer.parseInt(listaParametros.get(5)));
				
						String json = objm.writeValueAsString(l);
						out.println(json);
		
					} else {
						response.addHeader("msg", "No estan todos los parámetros para realizar cierta acción");
						response.sendError(400, "No estan todos los parámetros para realizar cierta acción");
					}
			
				} catch (ParcelServiceException e) {
					response.addHeader("msg", e.getMessage());
					response.sendError(400, e.getMessage());
				
				} catch (NumberFormatException e1) {
					response.addHeader("msg", "El formato de algún parámetro numérico es incorrecto. Recuerde que longitude,"
							+ " latitude, largeCompartments y smallCompartments deben ser números");
					response.sendError(400, "El formato de algún parámetro numérico es incorrecto. Recuerde que longitude,"
						+ " latitude, largeCompartments y smallCompartments deben ser números");
					
				} catch (NullPointerException ex) {
					response.addHeader("msg", "Falta un parámetro obligatorio por introducir");
					response.sendError(400, "Falta un parámetro obligatorio por introducir");
				}

				break;
			case "findLocker":
				try  {
					String[] nom_parametros = {"lockerCode"};
					ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
			
					listaParametros = handleRequest(request, response, nom_parametros);
			
					if (listaParametros.size() == nom_parametros.length) {
						Locker l = ps.findLocker(Long.parseLong(listaParametros.get(0)));
			
						String json = objm.writeValueAsString(l);
						out.println(json);
			
					} else {
						response.sendError(400, "No estan todos los parámetros para realizar cierta acción");
						response.addHeader("msg", "No estan todos los parámetros para realizar cierta acción");
					}
		
				} catch (ParcelServiceException e) {
					response.addHeader("msg", e.getMessage());
					response.sendError(400, e.getMessage());
				
				} catch (NullPointerException ex) {
					response.addHeader("msg", "Falta un parámetro obligatorio por introducir");
					response.sendError(400, "Falta un parámetro obligatorio por introducir");
				}
				break;
			case "findAllLockers":
				try {
					List<Locker> lista = ps.findAllLockers();
					ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
					String json = objm.writeValueAsString(lista);
					out.println(json);
				
				} catch (ParcelServiceException e) {
					response.addHeader("msg", e.getMessage());
					response.sendError(400, e.getMessage());
				
				} catch (NullPointerException ex) {
					response.addHeader("msg", "Falta un parámetro obligatorio por introducir");
					response.sendError(400, "Falta un parámetro obligatorio por introducir");
					
				}
				break;
			case "aviableCompartments":
				try {
					String[] nom_parametros2 = {"lockerCode", "date", "parcelWeight"};
					ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
				
					listaParametros = handleRequest(request, response, nom_parametros2);
				
					if (listaParametros.size() == nom_parametros2.length) {
						Integer n = ps.availableCompartments(Long.parseLong(listaParametros.get(0)), LocalDate.parse(listaParametros.get(1)), 
								Float.parseFloat(listaParametros.get(2)));
				
						String json = objm.writeValueAsString(n);
						out.println(json);
				
					} else {
						response.addHeader("msg", "No estan todos los parámetros para realizar cierta acción");
						response.sendError(400, "No estan todos los parámetros para realizar cierta acción");	
					}
				} catch (ParcelServiceException e) {
					response.addHeader("msg", e.getMessage());
					response.sendError(400, e.getMessage());
				
				} catch (NumberFormatException e1) {
					response.addHeader("msg", "El formato de algún parámetro numérico es incorrecto. Recuerde que parcelWeight debe ser un números");
					response.sendError(400, "El formato de algún parámetro numérico es incorrecto. Recuerde que parcelWeight debe ser un números");	
				
				} catch (DateTimeParseException e2) {
					response.addHeader("msg", "El formato del parámetro arrivalDate es incorrecto. Recuerde que debe ser escrito"
							+ " de forma 2022-08-05");
					response.sendError(400, "El formato del parámetro arrivalDate es incorrecto. Recuerde que debe ser escrito"
							+ " de forma 2022-08-05");

				} catch (NullPointerException ex) {
					response.addHeader("msg", "Falta un parámetro obligatorio por introducir");
					response.sendError(400, "Falta un parámetro obligatorio por introducir");
				}
				break;
			case "deliverParcel":
				try  {
					String[] nom_parametros3 = {"lockerCode", "addressee", "weight", "arrivalDate"};
					ObjectMapper objm = new ObjectMapper().registerModule(new JavaTimeModule());
				
					listaParametros = handleRequest(request, response, nom_parametros3);
				
					if (listaParametros.size() == nom_parametros3.length) {
						Parcel p = ps.deliverParcel(Long.parseLong(listaParametros.get(0)), Integer.parseInt(listaParametros.get(1)), 
								Float.parseFloat(listaParametros.get(2)), LocalDate.parse(listaParametros.get(3)));
				
						String json = objm.writeValueAsString(p);
						out.println(json);
					} else {
						response.addHeader("msg", "No estan todos los parámetros para realizar cierta acción");
						response.sendError(400, "No estan todos los parámetros para realizar cierta acción");
					}
				} catch (ParcelServiceException e) {
					response.addHeader("msg", e.getMessage());
					response.sendError(400, e.getMessage());
				
				} catch (NumberFormatException e1) {
					response.addHeader("msg", "El formato de algún parámetro numérico es incorrecto. Recuerde que lockerCode, "
							+ "addressee y weight son números");
					response.sendError(400, "El formato de algún parámetro numérico es incorrecto. Recuerde que lockerCode, "
							+ "addressee y weight son números");
				
				} catch (DateTimeParseException e2) {
					response.addHeader("msg", "El formato del parámetro arrivalDate es incorrecto. Recuerde que debe ser escrito"
							+ " de forma 2022-08-05");
					response.sendError(400, "El formato del parámetro arrivalDate es incorrecto. Recuerde que debe ser escrito"
							+ " de forma 2022-08-05");
				} catch (NullPointerException ex) {
					response.addHeader("msg", "Falta un parámetro obligatorio por introducir");
					response.sendError(400, "Falta un parámetro obligatorio por introducir");
				}
				break;
			case "retrieveParcel":
				try {
					String[] nom_parametros4 = {"parcelCode"};
				
					listaParametros = handleRequest(request, response, nom_parametros4);
				
					if (listaParametros.size() == nom_parametros4.length) {
						ps.retrieveParcel(Long.parseLong(listaParametros.get(0)));
					} else {
						response.addHeader("msg", "No estan todos los parámetros para realizar cierta acción");
						response.sendError(400, "No estan todos los parámetros para realizar cierta acción");
					}
				} catch (ParcelServiceException e) {
					response.addHeader("msg", e.getMessage());
					response.sendError(400, e.getMessage());
				
				} catch (NullPointerException ex) {
					response.addHeader("msg", "Falta un parámetro obligatorio por introducir");
					response.sendError(400, "Falta un parámetro obligatorio por introducir");
				}
				break;
			default:
				response.addHeader("msg", "El parámetro accion no es válido. Indique que operación desea realizar de forma correcta");
				response.sendError(400, "El parámetro accion no es válido. Indique que operación desea realizar de forma correcta");
				break;
			}
		} else {
			response.addHeader("msg", "El parámetro accion no se encuentra. Es decir no ha indicado que operación desea realizar");
			response.sendError(400, "El parámetro accion no se encuentra. Es decir no ha indicado que operación desea realizar");
		}
	}
	
	public List<String> handleRequest(HttpServletRequest req, HttpServletResponse res, String[] nombre_parametros) throws IOException, NullPointerException {
		List<String> parameterValues = new ArrayList<String> ();
		for (String s: nombre_parametros) {
			String[] paramValues = req.getParameterValues(s);
			parameterValues.add(paramValues[paramValues.length-1]);
			}
		return parameterValues;
	}
}
