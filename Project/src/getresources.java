
import java.io.IOException;
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
/**
 * Servlet implementation class ConversationDetail
 */
@WebServlet("/getresources")
public class getresources extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getresources() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
			HttpSession session = request.getSession();
			if(session.getAttribute("aid") == null) { //not logged in
				response.sendRedirect("LoginAirport");
			}
			String airport = (String) session.getAttribute("aid");
			String query = "select name, humanResources, runways, gates from airport where IATA = ?";
			String json = DbHelper.executeQueryJson(query, 
	   				new DbHelper.ParamType[] {DbHelper.ParamType.STRING},
	   				new Object[] {airport});
			//System.out.println(json);
			response.getWriter().println(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public static void main(String[] args) throws ServletException, IOException {
		new getresources().doGet(null, null);
	}

}