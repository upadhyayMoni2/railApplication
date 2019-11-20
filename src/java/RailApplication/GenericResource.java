/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RailApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Monika Upadhyay
 */
@Path("mad311")
public class GenericResource {

    ResultSet rs1 = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;
    Connection conn = null;
    Statement stm = null;
    Statement stm2 = null;
    Statement stm3 = null;
    ResultSet rs = null;
    String sql, sql2, sql3;
   
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }
    
      
    public Connection getConnection() {

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "mad311team2", "anypw");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    
    public void closeConnection() {


        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
               System.out.print(e.getMessage());
            
            }
        }
        if (stm != null) {
            try {
                stm.close();
            } catch (SQLException e) {

                 System.out.print(e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                 System.out.print(e.getMessage());
            
            }
        }
    }
    
    @GET
    @Path("viewTrainSchedulesDetails")
    @Produces(MediaType.TEXT_PLAIN)
    public String viewTrainSchedulesDetails() {
        
        
        JSONObject mainObj = new JSONObject();
        JSONArray scheduleArr = new JSONArray();
        JSONObject scheduleObj = new JSONObject();
        JSONArray routeArr = new JSONArray();
        JSONObject routeObj = new JSONObject();
        
        String train_name ,train_type ,arrival_time ,depart_time,source ,destination ,date_of_route;
        int train_id ,route_id;
        
        conn = getConnection();
       
        sql = "SELECT TRAIN_ID,TRAIN_NAME,TRAIN_TYPE,ARRIVAL_TIME,DEPART_TIME FROM TRAINS";
        
          if(conn != null){
          
           try {
              
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
           
            mainObj.accumulate("status", "ok");
            mainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
            
            while(rs.next()){
            train_id = rs.getInt("TRAIN_ID");
            train_name = rs.getString("TRAIN_NAME");
            train_type = rs.getString("TRAIN_TYPE");
            arrival_time = rs.getString("ARRIVAL_TIME");
            depart_time = rs.getString("DEPART_TIME");
            
            scheduleObj.accumulate("train_id",train_id);
            scheduleObj.accumulate("train_name",train_name);
            scheduleObj.accumulate("train_type",train_type );
            scheduleObj.accumulate("arrival_time",arrival_time );
            scheduleObj.accumulate("depart_time",depart_time );
        
            sql2 = "SELECT * FROM ROUTES WHERE TRAIN_ID = '"+ train_id +"'";
            stm2 = conn.createStatement();
            rs2 = stm2.executeQuery(sql2);
            
            while(rs2.next()){
            route_id =rs2.getInt("ROUTE_ID");
            source = rs2.getString("SOURCE");
            destination = rs2.getString("DESTINATION");
            date_of_route =rs2.getString("DATE_OF_ROUTE");
            
           routeObj.accumulate("ROUTE_ID", route_id);
           routeObj.accumulate("SOURCE", source);
           routeObj.accumulate("DESTINATION", destination);
           routeObj.accumulate("DATE_OF_ROUTE", date_of_route);
           
            routeArr.add(routeObj);
            routeObj.clear();
            }
            if(!routeArr.isEmpty()){
                scheduleObj.accumulate("Routes",routeArr);
                routeArr.clear();
            }
            scheduleArr.add(scheduleObj);
            scheduleObj.clear();
            }
            mainObj.accumulate("Schedules",scheduleArr);
         } catch(Exception e){
             System.out.print(e);
          }
          }else{
            mainObj.accumulate("status", "error");
           mainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
           mainObj.accumulate("message", "Something went wrong!! No Schedule Data found");
                     
          } 
          return mainObj.toString();
    }
    
    @GET
    @Path("viewCorrespondingStations&{train_id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String viewCorrespondingStations(@PathParam("train_id") int train_id) {
        
        
        JSONObject mainObj = new JSONObject();
        JSONArray stationArr = new JSONArray();
        JSONObject stationObj = new JSONObject();
        JSONArray routeArr = new JSONArray();
        JSONObject routeObj = new JSONObject();
        
        String train_name ,train_type ,arrival_time ,depart_time,source ,destination ,date_of_route,station_name;
        int route_id,station_id;
        double contact_number;
        
        conn = getConnection();
       
        sql = "SELECT TRAIN_ID,TRAIN_NAME,TRAIN_TYPE,ARRIVAL_TIME,DEPART_TIME FROM TRAINS WHERE TRAIN_ID = '"+ train_id +"'";
        
          if(conn != null){
          
           try {
              
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
           
            mainObj.accumulate("status", "ok");
            mainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
            
            while(rs.next()){
            train_id = rs.getInt("TRAIN_ID");
            train_name = rs.getString("TRAIN_NAME");
            train_type = rs.getString("TRAIN_TYPE");
            arrival_time = rs.getString("ARRIVAL_TIME");
            depart_time = rs.getString("DEPART_TIME");
            
            mainObj.accumulate("train_id",train_id);
            mainObj.accumulate("train_name",train_name);
            mainObj.accumulate("train_type",train_type );
            mainObj.accumulate("arrival_time",arrival_time );
            mainObj.accumulate("depart_time",depart_time );
        
            sql2 = "SELECT * FROM ROUTES WHERE TRAIN_ID = '"+ train_id +"'";
            stm2 = conn.createStatement();
            rs2 = stm2.executeQuery(sql2);
            
            while(rs2.next()){
            route_id =rs2.getInt("ROUTE_ID");
            source = rs2.getString("SOURCE");
            destination = rs2.getString("DESTINATION");
            date_of_route =rs2.getString("DATE_OF_ROUTE");
            
            routeObj.accumulate("ROUTE_ID", route_id);
            routeObj.accumulate("SOURCE", source);
            routeObj.accumulate("DESTINATION", destination);
            routeObj.accumulate("DATE_OF_ROUTE", date_of_route);
            
            sql3 = "SELECT STATIONS.STATION_ID,STATION_NAME,CONTACT_NUMBER FROM STATIONS,STATION_ROUTE,ROUTES WHERE ROUTES.ROUTE_ID = STATION_ROUTE.ROUTE_ID AND STATIONS.STATION_ID = STATION_ROUTE.STATION_ID AND ROUTES.ROUTE_ID = '"+ route_id +"'";
                    
            stm3 = conn.createStatement();
            rs3 = stm3.executeQuery(sql3);
            
            while(rs3.next()){
                
                station_id = rs3.getInt("STATION_ID");
                station_name = rs3.getString("STATION_NAME");
                contact_number = rs3.getDouble("CONTACT_NUMBER");
                
                stationObj.accumulate("Station_id", station_id);
                stationObj.accumulate("Station_name", station_name);
                stationObj.accumulate("Contact_number", contact_number);
                
                stationArr.add(stationObj);
                stationObj.clear();
            }
            routeObj.accumulate("Corresponding_stations", stationArr);
            stationArr.clear();
            routeArr.add(routeObj);
            routeObj.clear();
            }
            if(!routeArr.isEmpty()){
                mainObj.accumulate("Routes",routeArr);
                routeArr.clear();
            }
            
            }
            
            } catch(Exception e){
             System.out.print(e);
          }
          }else{
           mainObj.accumulate("status", "error");
           mainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
           mainObj.accumulate("message", "Something went wrong!! No Corresponding Data found");
          
          } 
          return mainObj.toString();
    }

    @GET
    @Path("modifyStationDetails&{station_id}&{station_name}&{contact_number}&{number_of_plateforms}")
    @Produces(MediaType.TEXT_PLAIN)
    public String modifyStationDetails(@PathParam("station_id") int station_Id, @PathParam("station_name") String station_Name ,@PathParam("contact_number") int contact_Number ,@PathParam("Number_of_plateforms") int number_of_plateforms) {
        
        JSONObject modifyStationObj = new JSONObject();
        conn = getConnection();
        if (conn != null) {
            String sql = "update stations set station_name='" + station_Name + "' ,contact_number ='"+contact_Number+"' , number_of_plateforms ='"+number_of_plateforms+"' where station_id= '" + station_Id + "'";

            try {
                stm = conn.createStatement();
                        
                int i = stm.executeUpdate(sql);

                if (i > 0) {

                    modifyStationObj.accumulate("Status", "OK");
                    modifyStationObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

                    modifyStationObj.accumulate("message", "Station suceeesfully modified");
                    System.out.println(modifyStationObj);

                } else {
                    modifyStationObj.accumulate("message", "Something went wrong!! Station not modified");
                    System.out.println(modifyStationObj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            modifyStationObj.accumulate("Status", "Error");
            modifyStationObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

            modifyStationObj.accumulate("message", "ConnectionError");
        }
        return modifyStationObj.toString();
    }


    @GET
    @Path("registerTrain&{train_name}&{number_of_coaches}&{train_type}&{arrival_time}&{depart_time}")
    @Produces(MediaType.TEXT_PLAIN)
    public String registerTrain(@PathParam("train_name") String train_Name , @PathParam("number_of_coaches") int number_of_coaches ,@PathParam("train_type") String train_Type , @PathParam("arrival_time") String arrival_Time ,@PathParam("depart_time") String depart_Time) {

        conn = getConnection();
        JSONObject regTrainObj = new JSONObject();
                
                
        if (conn != null) {
            String sql = "insert into TRAINS(train_id , train_name , number_of_coaches ,train_type, arrival_time , depart_time) values(AutoNumber.NEXTVAL,'" + train_Name + "' ,'" + number_of_coaches + "' ,'" + train_Type + "','" + arrival_Time + "','" + depart_Time + "') ";
             
          
            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);
                   
                if (i > 0) {
                      
                    regTrainObj.accumulate("Status", "OK");
                    regTrainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
                    regTrainObj.accumulate("message", "Train suceesfully registred");
                    System.out.println(regTrainObj);

                } else {
                    regTrainObj.accumulate("message", "Something went wrong!! Train  not registered");
                    System.out.println(regTrainObj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
                //System.out.println(ex);
            } finally {
                closeConnection();
            }
        } else {
            regTrainObj.accumulate("Status", "Error");
            regTrainObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

            regTrainObj.accumulate("message", "ConnectionError");
        }
        return regTrainObj.toString();
    }


    @GET
    @Path("viewPassengersDetails")
    @Produces(MediaType.TEXT_PLAIN)
    public String viewPassengersDetails() {

        JSONObject viewPassengersList = new JSONObject();
        JSONObject singlePassenger = new JSONObject();
        JSONArray arrPassengers = new JSONArray();
        
        int passengerId;
        double phone;
        String first_name ,last_name,address;
        conn = getConnection();
        if (conn != null) {
            try {
                String sql = "select * from passengers";
                stm = conn.createStatement();
                rs = stm.executeQuery(sql);
                
                viewPassengersList.put("Status", "OK");
                viewPassengersList.put("TimeStamp", System.currentTimeMillis() / 1000);

                if(rs.next() == false)
                {
                   viewPassengersList.put("Status", "Empty record");
                    viewPassengersList.put("TimeStamp", System.currentTimeMillis() / 1000);
                }
                else{
                    
                    do{
                    passengerId = rs.getInt("PASSENGER_ID");
                    first_name = rs.getString("FIRST_NAME");
                    last_name = rs.getString("LAST_NAME");
                    phone = rs.getDouble("PHONE");
                    address = rs.getString("ADDRESS");
                    singlePassenger.put("passenger_id", passengerId);
                    singlePassenger.put("first_name", first_name);
                    singlePassenger.put("last_name", last_name);
                    singlePassenger.put("address", address);
                    singlePassenger.put("phone", phone);

                    arrPassengers.add(singlePassenger);

                    singlePassenger.clear();
                    }while (rs.next());
                }
                viewPassengersList.accumulate("Passengers", arrPassengers);
            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            viewPassengersList.accumulate("Status", "Error");
            viewPassengersList.accumulate("TimeStamp", System.currentTimeMillis() / 1000);
            viewPassengersList.accumulate("Message", "Connection Error");
        }

        return viewPassengersList.toString();
    }
    @GET
    @Path("cancelTicket&{ticketid}")
    @Produces(MediaType.TEXT_PLAIN)
    public String cancelTicket(@PathParam("ticketid") int ticketId) {

        conn = getConnection();

        JSONObject cancelticketObj = new JSONObject();
        if (conn != null) {
            String sql = "Delete from TICKETS where TICKET_ID = '" + ticketId + "'";

            try {
                stm = conn.createStatement();
                int i = stm.executeUpdate(sql);
                if (i > 0) {
                    cancelticketObj.accumulate("Status", "OK");
                    cancelticketObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

                    cancelticketObj.accumulate("message", "Ticket Successfully cancelled");
                    //System.out.println(cancelticketObj);
                } else {
                     cancelticketObj.accumulate("Status", "WRONG");
                    cancelticketObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

                    cancelticketObj.accumulate("message", "Something went wrong!! Ticket not cancelled");
                   // System.out.println(cancelticketObj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                closeConnection();
            }
        } else {
            cancelticketObj.accumulate("Status", "Error");
            cancelticketObj.accumulate("TimeStamp", System.currentTimeMillis() / 1000);

            cancelticketObj.accumulate("message", "ConnectionEroor");
        }
        return cancelticketObj.toString();
    }
}
