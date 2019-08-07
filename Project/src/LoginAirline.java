import java.sql.*;
import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/LoginAirline")
public class LoginAirline extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginAirline() {
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String userid = request.getParameter("airline");
		String password = request.getParameter("password");
		
		String query = "select password from airlineUsers where userid = ?";
		List<List<Object>> res;
		try {
			res = DbHelper.executeQueryList(query, 
					new DbHelper.ParamType[] {DbHelper.ParamType.STRING}, 
					new Object[] {userid});
			String dbPass = res.isEmpty()? null : (String)res.get(0).get(0);
			if(dbPass != null && dbPass.equals(password)) {
				session.setAttribute("id", userid);
				response.sendRedirect("p6.html");
			}
			else {
		    	response.sendRedirect("p5.html");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
