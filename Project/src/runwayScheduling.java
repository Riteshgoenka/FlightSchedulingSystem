import java.sql.Connection;
import java.util.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
class runwayStruct {

    public String airport;
    public Integer runwayNumber;
    public Integer queueLen;
    public Integer free;
    runwayStruct(String Airport,Integer r,Integer l)
    {
    	airport = Airport;
    	runwayNumber  = r;
    	queueLen = l;
    	free = 1;
    }
}
class SortbyqueueLen implements Comparator<runwayStruct> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(runwayStruct a, runwayStruct b) 
    { 
        return a.queueLen-b.queueLen; 
    } 
} 
public class runwayScheduling {
	
	public static void main(String []args) {
//		System.out.println("Hello World"); // prints Hello World
		SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
        Date current_time = null;
	    try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    double slot_time = 60;
	    while(true)
	    {
	    	Date date = new Date();
	    	Timestamp ts = new Timestamp(date.getTime());
	    	if(ts.getSeconds() == 0 && ts.getMinutes()%2==0)
	    	{
	    		try (Connection conn = DriverManager.getConnection(config.url, config.username , config.password))
		        {
		            conn.setAutoCommit(false);
		            try(
		            		PreparedStatement stmt_get_flights = 
		            				conn.prepareStatement("select * from flight where (origin = 'LAS') and (SGDT between ? and ?) and status = 'RUNWAY'"
		            						+ " and runwayNumber=?");
		            		PreparedStatement stmt_get_arr_flights = 
		            				conn.prepareStatement("select * from flight where (destination = 'LAS') and (SGAT between ? and ?) and status = 'TAKEOFF'");
		            		PreparedStatement stmt_update_land_flights = 
		            				conn.prepareStatement("update flight set status = 'LANDED',runwayNumber = NULL where (destination = 'LAS') and (SGAT between ? and ?) and status = 'LANDING'");
		            		PreparedStatement stmt_get_runways = 
			            			conn.prepareStatement("select * from runway where IATA = 'LAS'");
		            		PreparedStatement stmt_update_arr_flights = 
			            			conn.prepareStatement("update flight set status = 'LANDING', runwayNumber = ? where aircraft = ? and sgdt = ?");
		            		PreparedStatement stmt_update2_flights = 
			            			conn.prepareStatement("update flight set status = 'TAKEOFF',"
			            					+ "runwayNumber = NULL,enterQueueNumber=NULL,currentQueueNumber=NULL where aircraft = ? and sgdt = ?");
		            		PreparedStatement stmt_update3_flights = 
			            			conn.prepareStatement("update flight set currentQueueNumber=currentQueueNumber-1 where aircraft = ? and sgdt = ? and runwayNumber = ?");
		            	)
		            {
		            	stmt_update_land_flights.setString(1, "2008-01-10 00:00:00");
		            	stmt_update_land_flights.setString(2, "2008-01-11 00:00:00");
		            	stmt_update_land_flights.executeUpdate();
		            	conn.commit();
		            	stmt_get_flights.setString(1, "2008-01-10 00:00:00");
		            	stmt_get_flights.setString(2, "2008-01-11 00:00:00");
		            	
		            	ResultSet rs_runways = stmt_get_runways.executeQuery();
		            	List<runwayStruct> runwayList = new ArrayList<runwayStruct>();
		            	while(rs_runways.next())
		            	{
		            		runwayList.add(new runwayStruct(rs_runways.getString(1),rs_runways.getInt(2),rs_runways.getInt(3)));
		            	}
		            	Collections.sort(runwayList,new SortbyqueueLen());
		            	stmt_get_arr_flights.setString(1, "2008-01-10 00:00:00");
		            	stmt_get_arr_flights.setString(2, "2008-01-11 00:00:00");
		            	ResultSet rs_arr_flights = stmt_get_arr_flights.executeQuery();
		            	Integer free_ite = 0;
		            	while(rs_arr_flights.next())
		            	{
		            		if(free_ite < runwayList.size())
		            		{
		            			int r = runwayList.get(free_ite).runwayNumber;
		            			runwayList.get(free_ite).free = 0;
		            			stmt_update_arr_flights.setInt(1, r);
		            			stmt_update_arr_flights.setString(2, rs_arr_flights.getString(1));
		            			stmt_update_arr_flights.setString(3, rs_arr_flights.getString(5));
		            			stmt_update_arr_flights.executeUpdate();
		            			free_ite++;
		            		}
		            		else
		            		{
		            			break;
		            		}
		            	}
		            	conn.commit();
		            	for (int i=0; i<runwayList.size(); i++) {
		            		int r = runwayList.get(i).runwayNumber;
		            		int free = runwayList.get(i).free;
	            			stmt_get_flights.setInt(3, r);
		            		ResultSet rs_flights = stmt_get_flights.executeQuery();
		            		
		            		while(rs_flights.next()) {
		            			int cq = rs_flights.getInt(22);
		            			if(cq==0) {
		            				stmt_update2_flights.setString(1, rs_flights.getString(1));
		            				stmt_update2_flights.setString(2, rs_flights.getString(5));
		            				stmt_update2_flights.executeUpdate();
		            				conn.commit();
		            			}
		            			else {
		            				if(free == 1)
		            				{
		            					stmt_update3_flights.setString(1, rs_flights.getString(1));
			            				stmt_update3_flights.setString(2, rs_flights.getString(5));
			            				stmt_update3_flights.setInt(3, r);
			            				stmt_update3_flights.executeUpdate();
			            				conn.commit();
		            				}
		            				
		            			}
		            		}	
		            	}
		                conn.commit();
		            }
		            catch(Exception ex)
		            {
		                conn.rollback();
		                throw ex;
		            }
		            finally{
		                conn.setAutoCommit(true);
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
	    	}
	    	
	    }
	      
	   }
}
