package com.SM.view;

import com.SM.model.Server;
import com.SM.model.UpdateSQL;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import com.SM.controller.Monitor;
import org.jfree.chart.ChartPanel;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.chart.ChartFactory;
import java.awt.image.BufferedImage;
import java.io.File;


import javax.imageio.ImageIO;

class Technician extends Personnel{
    public Technician(String username,String email, String password){
        this.username=username;
        this.email=email;
        this.password=password;

    }
    ArrayList viewReport(Monitor mon) throws ClassNotFoundException, SQLException {
        //Gives logs for all servers in a monitor
        return mon.generateReport();
    }

    void fixServer(Server ser){
        // String descriptionString;
        // descriptionString taken input from UI.
        // Information based function
    }


    void generateLogs(Server server) throws SQLException, ClassNotFoundException, IOException {
        //Gives logs for one specific server
        UpdateSQL usql=new UpdateSQL();
        ResultSet rs = usql.GetData(server);
        String logEntry;
        String filename = server.GetHostName()+".log";
        FileWriter fw = new FileWriter(filename,true);
        while(rs.next()){
            String CPU =rs.getString("cpu");
            String MEM =rs.getString("mem");
            String DISK =rs.getString("disk");
            String IP = rs.getString("ipaddress");
            String createdAt = rs.getTimestamp("TS").toString();
            logEntry = ": at " +createdAt+"\n"
                    + " IP address: " + IP + "\n"
                    + "CPU usage: " + CPU + "%\n"
                    + "Memory usage: " + MEM + "MB\n"
                    + "Disk usage: " + DISK + "%\n\n";
            fw.write(logEntry);
        }
        fw.close();
        rs.close();
    }
    void generateGraph(Server server) throws ClassNotFoundException, SQLException, IOException {
        UpdateSQL uSQL = new UpdateSQL();
        ResultSet rs = uSQL.GetData(server);
        ArrayList<Float> cpuUsage = new ArrayList<>();
        ArrayList<Timestamp> times = new ArrayList<>();
        while(rs.next()) {
            float CPU = Float.parseFloat(rs.getString("cpu"));
            cpuUsage.add(CPU);
            Timestamp createdAt = rs.getTimestamp("TS");
            times.add(createdAt);
        }

        TimeSeries series = new TimeSeries("CPU Usage");
        for (int i = 0; i < cpuUsage.size(); i++) {
            series.addOrUpdate(new Minute(times.get(i)), cpuUsage.get(i));
        }
        // Create a chart with the time series
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "CPU Usage",
                "Time",
                "CPU Usage",
                new TimeSeriesCollection(series),
                true,
                true,
                false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        BufferedImage image = chartPanel.getChart().createBufferedImage(500,300);
        File graphOut = new File("chart.png");
        ImageIO.write(image,"png",graphOut);
    }
}
