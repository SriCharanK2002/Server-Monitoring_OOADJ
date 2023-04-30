package com.SM.model;

import com.SM.model.Server;

import java.sql.*;

public class UpdateSQL {
    private String driver = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/SM";
    private static final String USERNAME = "karan";
    private static final String PASSWORD = "password";
    // private static final String TABLE_NAME = "employees";
    public UpdateSQL() throws ClassNotFoundException{
        Class.forName(driver);
    }
    public void UpdateDatabase(Server server){
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME,PASSWORD);
            System.out.println("Connected to database!");

            String sql = "INSERT INTO " + server.GetHostName() + " (CPU, MEM,DISK, Ipaddress, TS) VALUES (?, ?, ?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Double.toString(server.getCpuUsage()));
            pstmt.setString(2,Double.toString(server.getMemUsage()));
            pstmt.setString(3,Double.toString(server.getDiskUsage()));
            pstmt.setString(4, server.getIPAddress());
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) affected");

            pstmt.close();
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }

    }

    public ResultSet GetData(Server server){

        try {

            Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            System.out.println("Connected to database!");

            String sql = "SELECT * FROM " + server.GetHostName();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery(sql);

            // while (rs.next()) {
            //     String CPU =rs.getString("cpu");
            //     String MEM =rs.getString("mem");
            //     String DISK =rs.getString("disk");
            //     String IP = rs.getString("ipaddress");
            //     String createdAt = rs.getTimestamp("TS").toString();
            //     System.out.println(CPU+ MEM+DISK+IP+createdAt);
            // }
            // stmt.close();
            // conn.close();
            // System.out.println("Connection closed.");
            return rs;



        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
        return null;


    }
}

