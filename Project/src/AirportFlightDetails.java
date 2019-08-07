import java.io.IOException;
import java.io.PrintWriter;
import java.security.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AllConversations
 */
@WebServlet("/AirportFlightDetails")
public class AirportFlightDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AirportFlightDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		HttpSession session = request.getSession();
//		if(session.getAttribute("id") == null) { //not logged in
//			response.getWriter().print(DbHelper.errorJson("Not logged in").toString());
//			return;
//		}
		
//		String userid = (String) session.getAttribute("id");
		HttpSession session = request.getSession();
		if(session.getAttribute("aid") == null) { //not logged in
			response.sendRedirect("p1.html");
		}
		String airport = (String) session.getAttribute("aid");
		long lSec = System.currentTimeMillis();
		long uSec = lSec +  36 * 100000 * 6;
		java.sql.Timestamp lTime = new java.sql.Timestamp(lSec) ;
		java.sql.Timestamp uTime = new java.sql.Timestamp(uSec) ;
		System.out.println(lTime);
		System.out.println(uTime);
		String query = "select * from flight where (origin = ? and EGDT > ? and EGDT < ?) or (destination = ? and EGAT > ? and EGAT < ?)";
		String res = DbHelper.executeQueryJson(query, 
				new DbHelper.ParamType[] {DbHelper.ParamType.STRING,DbHelper.ParamType.DATETIME,DbHelper.ParamType.DATETIME, 
						DbHelper.ParamType.STRING,DbHelper.ParamType.DATETIME,DbHelper.ParamType.DATETIME }, 
				new Object[] {airport,lTime,uTime,airport,lTime,uTime});
		
		PrintWriter out = response.getWriter();
		System.out.println(res);
		out.print(res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
	 * For testing other methods in this class.
	 */
	public static void main(String[] args) throws ServletException, IOException {
		new AirportFlightDetails().doGet(null, null);
	}

}
