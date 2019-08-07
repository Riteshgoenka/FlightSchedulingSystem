
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jdk.nashorn.internal.parser.JSONParser;

/**
 * Servlet implementation class setresources
 */
@WebServlet("/addFlight")
public class addFlight extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addFlight() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(request.getSession().getAttribute("id") == null) { //not logged in
			response.sendRedirect("p5.html");
		}
		PrintWriter out = response.getWriter();
		String aircraft = request.getParameter("aircraft");
		String id = request.getParameter("id");
		String origin = request.getParameter("origin");
		String destination = request.getParameter("destination");
		String SGDT = request.getParameter("SGDT");
		String SGAT = request.getParameter("SGAT");
		String SRDT = request.getParameter("SRDT");
		String SRAT = request.getParameter("SRAT");
		String duration = request.getParameter("duration");
		String distance = request.getParameter("distance");
		String numPassengers = request.getParameter("numPassengers");
		int duration1 = Integer.parseInt(duration);
		int distance1 = Integer.parseInt(distance);
		int numPassengers1 = Integer.parseInt(numPassengers);
		String airline = (String) request.getSession(false).getAttribute("id");
	    String query = "insert into flight (aircraft,id,origin,destination,SGDT,SGAT,SRDT,SRAT,duration,distance,numPassengers,status) values (?,?,?,?,?,?,?,?,?,?,?)";
	    String json = DbHelper.executeUpdateJson(query, 
	   				new DbHelper.ParamType[] { DbHelper.ParamType.STRING,DbHelper.ParamType.STRING,DbHelper.ParamType.STRING,DbHelper.ParamType.STRING,DbHelper.ParamType.DATETIME,DbHelper.ParamType.DATETIME,DbHelper.ParamType.DATETIME,
	   						DbHelper.ParamType.DATETIME,DbHelper.ParamType.INT,DbHelper.ParamType.INT,DbHelper.ParamType.INT,DbHelper.ParamType.STRING},
	   				new Object[] {aircraft,id,origin,destination,Timestamp.valueOf(SGDT),Timestamp.valueOf(SGAT),Timestamp.valueOf(SRDT),Timestamp.valueOf(SRAT),duration1,distance1,numPassengers1,"NOGATE"});
		out.print(json);
		response.sendRedirect("p6.html");
		
	}
}


