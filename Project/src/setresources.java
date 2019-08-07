
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
@WebServlet("/setresources")
public class setresources extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public setresources() {
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
		String val1 = request.getParameter("value1");
		int value1 = -1;
		if(! val1.isEmpty())value1 = Integer.parseInt(val1);
		String airport = (String) request.getSession(false).getAttribute("aid");
	    String query1 = "update airport set humanResources = ? where IATA = ? ";
	    if(value1 > 0) {String json1 = DbHelper.executeUpdateJson(query1, 
	   				new DbHelper.ParamType[] { DbHelper.ParamType.INT,
	   						DbHelper.ParamType.STRING},
	   				new Object[] {value1, airport});
		out.print(json1);}
			response.sendRedirect("p2.html");
		
	}
}


