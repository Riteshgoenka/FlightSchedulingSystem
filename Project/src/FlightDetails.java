import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AllConversations
 */
@WebServlet("/FlightDetails")
public class FlightDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FlightDetails() {
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
		String origin = request.getParameter("origin");
		String destination = request.getParameter("destination");
		String date = request.getParameter("date");
		//System.out.println(date);
		String query = "select aircraft, origin, destination, SGDT, SGAT, EGDT, EGAT, status, duration from flight where origin = ? and destination = ? and DATE(SGDT) = ?";
		String res = DbHelper.executeQueryJson(query, 
				new DbHelper.ParamType[] {DbHelper.ParamType.STRING,
						DbHelper.ParamType.STRING,DbHelper.ParamType.STRING
						}, 
				new String[] {origin,destination,date});
		
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
		new FlightDetails().doGet(null, null);
	}

}
