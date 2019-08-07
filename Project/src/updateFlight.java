
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
@WebServlet("/updateFlight")
public class updateFlight extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public updateFlight() {
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
		if(request.getSession().getAttribute("aid") == null) { //not logged in
			response.sendRedirect("p1.html");
		}
		PrintWriter out = response.getWriter();
		String aircraft = request.getParameter("aircraft");
		String SGDT = request.getParameter("SGDT");
		String destination = request.getParameter("destination");
		String ERAT = request.getParameter("ERAT");

		String query1 = "update flight set destination = ? where aircraft = ? and SGDT = ? ";
		String query2 = "update flight set ERAT = ? where aircraft = ? and SGDT = ? ";
		
	    if(! destination.isEmpty()) {String json1 = DbHelper.executeUpdateJson(query1, 
	   				new DbHelper.ParamType[] { DbHelper.ParamType.STRING,
	   						DbHelper.ParamType.STRING,DbHelper.ParamType.DATETIME},
	   				new Object[] {destination, aircraft,Timestamp.valueOf(SGDT)});
		out.print(json1);}
	    
	    if(! ERAT.isEmpty()) {String json1 = DbHelper.executeUpdateJson(query2, 
   				new DbHelper.ParamType[] { DbHelper.ParamType.DATETIME,
   						DbHelper.ParamType.STRING,DbHelper.ParamType.DATETIME},
   				new Object[] {Timestamp.valueOf(ERAT), aircraft,Timestamp.valueOf(SGDT)});
	out.print(json1);}
	    
	    response.sendRedirect("p2.html");
		
	}
}


