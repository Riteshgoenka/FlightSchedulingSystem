import java.sql.Connection;
import java.util.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.concurrent.TimeUnit;
class flightStruct {

    public String aircraft;
    public String sgdt;
    public Integer passengers;
    flightStruct(String Aircraft,String Sgdt,Integer no)
    {
    	aircraft = Aircraft;
    	sgdt  = Sgdt;
    	passengers = no;
    }
}
class SortbyPass implements Comparator<flightStruct> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(flightStruct a, flightStruct b) 
    { 
        return b.passengers-a.passengers; 
    } 
} 
public class gateScheduling {
	
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
	    	
		    	try (Connection conn = DriverManager.getConnection(config.url, config.username , config.password))
		        {
		            conn.setAutoCommit(false);
		            try(
//		            		PreparedStatement stmt_get_flights = 
//		            				conn.prepareStatement("select * from flight where (origin = 'LAS') and (SGDT between ? and ?) and status = 'NOGATE'");
		            		PreparedStatement stmt_get_flights = 
            					conn.prepareStatement("select * from flight where (origin = 'LAS') and status = 'NOGATE'");
//		            		PreparedStatement stmt_get_arr_flights = 
//		            				conn.prepareStatement("select * from flight where (destination = 'LAS') and (SGAT between ? and ?) and status = 'LANDED'");
		            		PreparedStatement stmt_get_arr_flights = 
		            				conn.prepareStatement("select * from flight where (destination = 'LAS') and status = 'LANDED'");
		            		PreparedStatement stmt_gates = conn.prepareStatement("select * from gate where IATA = 'LAS' and status = 0");
		            		PreparedStatement stmt_gates_update = 
		            				conn.prepareStatement("update gate set aircraft = ? , sgdt = ?,status = 1 where IATA = 'LAS' and gateNumber = ?");
		            		PreparedStatement stmt_flights_update = 
		            				conn.prepareStatement("update flight set status = 'GATE' , gateNo = ? where aircraft = ? and sgdt = ?");
		            	)
		            {
//		            	stmt_get_flights.setString(1, "2008-01-10 00:00:00");
//		            	stmt_get_flights.setString(2, "2008-01-11 00:00:00");
		            	ResultSet rs_flights = stmt_get_flights.executeQuery();
//		            	stmt_get_arr_flights.setString(1, "2008-01-10 00:00:00");
//		            	stmt_get_arr_flights.setString(2, "2008-01-11 00:00:00");
		            	ResultSet rs_arr_flights = stmt_get_arr_flights.executeQuery();
		            	
		                List<Integer> free_gates=new ArrayList<Integer>();
		                ResultSet rs_gates = stmt_gates.executeQuery();
		                List<flightStruct> flightList = new ArrayList<flightStruct>();
		                while(rs_gates.next() )
		                {
		                	free_gates.add(rs_gates.getInt(2));
		                }
	
		                Integer counter = 0;
		                while(rs_flights.next()) {
		                	counter++;
		                	String sgdt = rs_flights.getString(5);
		                	Date dt = (Date) dtf.parse(sgdt);
		                	System.out.println(ts);
		                	if(dt.getTime() < ts.getTime() + 3600000)
		                	{
		                		System.out.println(1);
		                		flightList.add(new flightStruct(rs_flights.getString(1),rs_flights.getString(5),rs_flights.getInt(15)));
		                	}
		                }
		                System.out.println(counter);
		                
		                while(rs_arr_flights.next()) {
		                	String sgdt = rs_flights.getString(8);
		                	Date dt = (Date) dtf.parse(sgdt);
		                	if(dt.getTime() < ts.getTime())
		                	{
		                		flightList.add(new flightStruct(rs_arr_flights.getString(1),rs_arr_flights.getString(5),rs_arr_flights.getInt(15)));
		                	}
		                	
		                }
		                
		                Collections.sort(flightList,new SortbyPass());
		                Integer free_ite = 0;
		                
		                for (int i=0; i<flightList.size(); i++) {
		                	if(free_ite >= free_gates.size())
		                		break;
		                	
		                	stmt_flights_update.setInt(1,free_gates.get(free_ite));
		                	stmt_flights_update.setString(2,flightList.get(i).aircraft);
		                	stmt_flights_update.setString(3,flightList.get(i).sgdt);
		                	stmt_flights_update.executeUpdate();
		                	
		                	stmt_gates_update.setString(1,flightList.get(i).aircraft);
		                	stmt_gates_update.setString(2,flightList.get(i).sgdt);
		                	stmt_gates_update.setInt(3,free_gates.get(free_ite));
		                	
		                	stmt_gates_update.executeUpdate();
		                	
		                	free_ite++;
		                    System.out.println(flightList.get(i).passengers);
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
	    	
	    	try {
				TimeUnit.SECONDS.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    } 
	   }
}
