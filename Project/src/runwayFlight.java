
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
@WebServlet("/runwayFlight")
public class runwayFlight extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public runwayFlight() {
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
			String query = "select runway.runwayNumber, aircraft, SGDT from runway left join (select runwayNumber, aircraft, SGDT from flight where runwayNumber is not null and (origin = ? and status = ?) or (destination = ? and status = ?) order by flight.runwayNumber asc) as runwayFlight on runway.runwayNumber = runwayFlight.runwayNumber where runway.IATA = ? ";
			String json = DbHelper.executeQueryJson(query, 
	   				new DbHelper.ParamType[] {DbHelper.ParamType.STRING,DbHelper.ParamType.STRING,DbHelper.ParamType.STRING,DbHelper.ParamType.STRING,DbHelper.ParamType.STRING},
	   				new Object[] {airport,"RUNWAY",airport,"LANDING",airport});
			System.out.println(json);
			response.getWriter().print(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public static void main(String[] args) throws ServletException, IOException {
		new runwayFlight().doGet(null, null);
	}

}