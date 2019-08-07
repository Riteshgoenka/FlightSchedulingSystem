import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.lang.Math;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
class flightStruct1 {

    public String aircraft;
    public String sgdt;
    public Integer passengers;
    public Integer resources_req;
    public Integer resources_alloc;
    public Integer gate;
    public Integer type;
    public Integer copy;
    flightStruct1(String Aircraft,String Sgdt,Integer no,Integer no1,Integer gate_no,Integer ty)
    {
    	aircraft = Aircraft;
    	sgdt  = Sgdt;
    	passengers = no;
    	resources_req = no1;
    	resources_alloc = 0;
    	gate = gate_no;
    	type = ty;
    	copy = no1;
    }
}
class SortbyPass1 implements Comparator<flightStruct1> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(flightStruct1 a, flightStruct1 b) 
    { 
        return b.passengers-a.passengers; 
    } 
} 
public class loadScheduling {
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
	    double slot_time = 5;
	    while(true)
	    {
	    	Date date = new Date();
	    	Timestamp ts = new Timestamp(date.getTime());
	    	
	    		
		    	try (Connection conn = DriverManager.getConnection(config.url, config.username , config.password))
		        {
		            conn.setAutoCommit(false);
		            try(
//		            		PreparedStatement stmt_get_flights = 
//		            			conn.prepareStatement("select * from flight where (origin = 'LAS') and (SGDT between ? and ?) and status = 'GATE'");
//		            		PreparedStatement stmt_get_arr_flights = 
//			            			conn.prepareStatement("select * from flight where (destination = 'LAS') and (SGAT between ? and ?) and status = 'GATE'");
		            		PreparedStatement stmt_get_flights = 
	            					conn.prepareStatement("select * from flight where (origin = 'LAS') and status = 'GATE'");
		            		PreparedStatement stmt_get_arr_flights = 
		            			conn.prepareStatement("select * from flight where (destination = 'LAS') and status = 'GATE'");
		            		PreparedStatement stmt_get_resources = 
			            			conn.prepareStatement("select humanResources from airport where IATA = 'LAS'");
		            		PreparedStatement stmt_get_runways = 
			            			conn.prepareStatement("select * from runway where IATA = 'LAS'");
		            		PreparedStatement stmt_update_runway = 
			            			conn.prepareStatement("update runway set queueLen = queueLen + 1 where IATA = 'LAS' and runwayNumber = ?");
		            		PreparedStatement stmt_update1_flights = 
			            			conn.prepareStatement("update flight set resources_req = ?,resources_alloc = ? where aircraft = ? and sgdt = ?");
		            		PreparedStatement stmt_update2_flights = 
			            			conn.prepareStatement("update flight set resources_req = NULL,resources_alloc = NULL ,status = 'RUNWAY',gateNo = NULL,"
			            					+ "runwayNumber = ?,enterQueueNumber=?,currentQueueNumber=? where aircraft = ? and sgdt = ?");
		            		PreparedStatement stmt_update_arr_flights = 
			            			conn.prepareStatement("update flight set resources_req = NULL,resources_alloc = NULL ,status = 'PARKED',gateNo = NULL,"
			            					+ "runwayNumber = NULL,enterQueueNumber=NULL,currentQueueNumber=NULL where aircraft = ? and sgdt = ?");
		            		PreparedStatement stmt_update_alloc_flights = 
			            			conn.prepareStatement("update flight set resources_alloc = 0");
		            		
		            		PreparedStatement stmt_gates_update = 
		            				conn.prepareStatement("update gate set aircraft = NULL , sgdt = NULL,status = 0 where IATA = 'LAS' and gateNumber = ?");
		            		PreparedStatement stmt_times_update = 
		            				conn.prepareStatement("update flight set  EGDT= ? + interval ? minute where aircraft = ? and sgdt = ?");
		            		PreparedStatement stmt_times_update_check = 
		            				conn.prepareStatement("update flight set  EGDT= SGDT where EGDT < SGDT");
		            		PreparedStatement stmt_times_update_erdt = 
		            				conn.prepareStatement("update flight set  ERDT= ? + interval ? minute where aircraft = ? and sgdt = ?");
		            		PreparedStatement stmt_times_update_erat = 
		            				conn.prepareStatement("update flight set  ERAT= DATE_ADD(EGDT, INTERVAL duration MINUTE) where aircraft = ? and sgdt = ?");
		            		
		            	)
		            {	
		            	stmt_update_alloc_flights.executeUpdate();
//		            	stmt_get_flights.setString(1, "2008-01-10 00:00:00");
//		            	stmt_get_flights.setString(2, "2008-01-11 00:00:00");
		            	ResultSet rs_flights = stmt_get_flights.executeQuery();
		            	
//		            	stmt_get_arr_flights.setString(1, "2008-01-10 00:00:00");
//		            	stmt_get_arr_flights.setString(2, "2008-01-11 00:00:00");
		            	ResultSet rs_arr_flights = stmt_get_arr_flights.executeQuery();
	
		            	ResultSet rs_resources = stmt_get_resources.executeQuery();
		            	Integer total_resources = 0;
		            	if(rs_resources.next())
		            	{
		            		total_resources = rs_resources.getInt(1);
		            	}
		            	System.out.println(total_resources);
		                List<flightStruct1> flightList = new ArrayList<flightStruct1>();
		                
		                Integer counter = 0;
		                double sum = 0;
		                while(rs_flights.next()) {
		                	String sgdt = rs_flights.getString(8);
		                	Date dt = (Date) dtf.parse(sgdt);
		                	if(rs_flights.getInt(19) == 0)
		                	{
		                		int runwayNo=0,enterQueueNumber =0,currentQueueNumber = 0;
		                		ResultSet rs_runways = stmt_get_runways.executeQuery();
		                		int min = 100;
		                		while(rs_runways.next())
		                		{
		                			int q = rs_runways.getInt(3);
		                			int r = rs_runways.getInt(2);
		                			if(q < min)
		                			{
		                				min = q;
		                				runwayNo = r;
		                			}
		                		}
		                		enterQueueNumber = min;
		                		currentQueueNumber = min + 1;
		                		
		                		stmt_update_runway.setInt(1, runwayNo);
		                		stmt_update_runway.executeUpdate();
		                		conn.commit();
		                		Date date1 = new Date();
		            	    	Timestamp ts1 = new Timestamp(date1.getTime());
		            	    	String date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts1);
		                		stmt_times_update_erdt.setString(1,date2);
		                		stmt_times_update_erdt.setInt(2,2*enterQueueNumber);
		                		stmt_times_update_erdt.setString(3, rs_flights.getString(1));
		                		stmt_times_update_erdt.setString(4, rs_flights.getString(5));
		                			
		                		stmt_times_update_erdt.executeUpdate();
		                		
		                		stmt_times_update_erat.setString(1, rs_flights.getString(1));
		                		stmt_times_update_erat.setString(2, rs_flights.getString(5));
		                		
		                		stmt_times_update_erat.executeUpdate();
		                		
		                		conn.commit();
		                		stmt_update2_flights.setString(4, rs_flights.getString(1));
		                		stmt_update2_flights.setString(5, rs_flights.getString(5));
		                		stmt_update2_flights.setInt(1, runwayNo);
		                		stmt_update2_flights.setInt(2, enterQueueNumber);
		                		stmt_update2_flights.setInt(3, currentQueueNumber);
		                		stmt_update2_flights.executeUpdate();
		                		
		                		stmt_gates_update.setInt(1, rs_flights.getInt(17));
		                		stmt_gates_update.executeUpdate();
		                		conn.commit();
		                	}
		                	else
		                	{
		                		counter++;
			                	flightList.add(new flightStruct1(rs_flights.getString(1),rs_flights.getString(5),rs_flights.getInt(15),rs_flights.getInt(19),rs_flights.getInt(17),0));
			                	sum += Math.sqrt(rs_flights.getInt(15)*rs_flights.getInt(19));
		                	}
		                	
		                }
		                counter = 0;
		                while(rs_arr_flights.next()) {
		                	if(rs_arr_flights.getInt(19)==0) {
		                		stmt_update_arr_flights.setString(1, rs_arr_flights.getString(1));
		                		stmt_update_arr_flights.setString(2, rs_arr_flights.getString(5));
		                		stmt_update_arr_flights.executeUpdate();
		                		
		                		stmt_gates_update.setInt(1, rs_arr_flights.getInt(17));
		                		stmt_gates_update.executeUpdate();
		                		conn.commit();
		                	}
		                	else
		                	{
		                		counter++;
			                	flightList.add(new flightStruct1(rs_arr_flights.getString(1),rs_arr_flights.getString(5),rs_arr_flights.getInt(15),rs_arr_flights.getInt(19),rs_arr_flights.getInt(17),1));
			                	sum += Math.sqrt(rs_arr_flights.getInt(15)*rs_arr_flights.getInt(19));
		                	}
		                }
		                System.out.println(sum);
		                System.out.println(counter);
		                
		                
		                Collections.sort(flightList,new SortbyPass1());
	    
		                Integer total_alloc = 0;
		                Integer nonzeroes = flightList.size();
		                for (int i=0; i<flightList.size(); i++)
		                {
		                	flightList.get(i).copy=flightList.get(i).resources_req;
		                }
		                while(nonzeroes>0 &&  total_alloc < total_resources )
		                {
		                	nonzeroes = 0;
		                	for (int i=0; i<flightList.size(); i++) {
		                		if(flightList.get(i).resources_req != 0)
		                		{
		                			double current = Math.sqrt(flightList.get(i).resources_req*flightList.get(i).passengers);
				                	int current_alloc1  = (int)Math.ceil((current/sum)*total_resources);
				                	int current_alloc2  = Math.min(current_alloc1,flightList.get(i).resources_req );
				                	int remaining = total_resources - total_alloc;
				                	if(remaining == 0)
				                		break;
				                	int current_alloc = Math.min(current_alloc2,remaining);
				                	flightList.get(i).resources_req -= current_alloc;
				                	flightList.get(i).resources_alloc += current_alloc;
				                	if(flightList.get(i).resources_req != 0)
				                		nonzeroes += 1;
				                	
				                	total_alloc+=current_alloc;
		                		}
			                }
		                }
		                for (int i=0; i<flightList.size(); i++)
		                {
		                	int x1 = flightList.get(i).copy;
		                	int x2 = flightList.get(i).resources_alloc;
		                	if(x2!=0) {
		                		Date date1 = new Date();
		            	    	Timestamp ts1 = new Timestamp(date1.getTime());
		            	    	String date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts1);
//		            	    	System.out.println(date2);
		                		stmt_times_update.setString(1,date2);
		                		int minutes = (int)slot_time * (int)Math.ceil((x1*1.0)/x2);
		                		stmt_times_update.setInt(2, minutes);
		                		stmt_times_update.setString(3,flightList.get(i).aircraft);
		                		stmt_times_update.setString(4,flightList.get(i).sgdt);
		                		stmt_times_update.executeUpdate();
		                		conn.commit();
		                		stmt_times_update_check.executeUpdate();
		                		conn.commit();
		                	}
	                		stmt_update1_flights.setInt(1, flightList.get(i).resources_req);
	                		stmt_update1_flights.setInt(2, flightList.get(i).resources_alloc);
	                		stmt_update1_flights.setString(3, flightList.get(i).aircraft);
	                		stmt_update1_flights.setString(4, flightList.get(i).sgdt);
	                		
	                		stmt_update1_flights.executeUpdate();
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
					TimeUnit.SECONDS.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }
	      
	   }
}
