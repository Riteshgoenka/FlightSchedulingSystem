import java.sql.*;
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
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
		PrintWriter out = response.getWriter();
		String id = request.getParameter("id");
		String pass = request.getParameter("pass");
	    int auth  = 0;
		
	    if(id == null || pass == null) { 
	    	response.sendRedirect("p3.html");
	    }
	    else {
			try 
			{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				java.sql.Connection conn = DriverManager.getConnection(config.url, config.username, config.password);
			       conn.setAutoCommit(false);
			       try(	   
			    		   PreparedStatement stmt = conn.prepareStatement("select count(*) as c from password where uid = ? and password = ?");
			    		   )
			       {	   stmt.setString(1, id);
			       		   stmt.setString(2, pass);
	               		   
	                       ResultSet rs = stmt.executeQuery(); // can cause SQLException
	                       if(rs.next()) {
	                    	   auth = rs.getInt("c");
	                       }
			               conn.commit();         // also can cause SQLException!
			       }
			       catch(Exception ex) {
			               conn.rollback();
			               throw ex; /* Other exception handling will be done at outer level */
			       } finally {
			               conn.setAutoCommit(true);
			       }
			}
			catch(Exception e) {
			       e.printStackTrace();
			}
			
			if(auth == 1) {
				HttpSession session = request.getSession();
				session.setAttribute("id", id);
				response.sendRedirect("p2.html");
			}
			else {
				out.println("<html>\n" +
						"<p>Invalid credentials</p>\n" +
			    		"<form action=\"Login\" method=\"get\">\n" + 
			    		"	ID: <input type=\"text\" name = \"id\">\n" +
			    		"	Password: <input type=\"text\" name = \"pass\">\n" +
			    		"	<input type=\"submit\" value = \"Submit\">\n" + 
			    		"</form>\n" +
			    		"</html>");
			}
	    }
	}

}
