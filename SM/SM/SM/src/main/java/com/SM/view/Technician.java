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
        ArrayList<Float> memUsage = new ArrayList<>();
        ArrayList<Float> diskUsage = new ArrayList<>();
        ArrayList<Timestamp> times = new ArrayList<>();
        while(rs.next()) {
            float CPU = Float.parseFloat(rs.getString("cpu"));
            cpuUsage.add(CPU);
            float Mem = Float.parseFloat(rs.getString("mem"));
            memUsage.add(Mem);
            float disk = Float.parseFloat(rs.getString("disk"));
            diskUsage.add(disk);
            Timestamp createdAt = rs.getTimestamp("TS");
            times.add(createdAt);
        }
        String name = server.GetHostName();
        //CPU usage
        TimeSeries CPUseries = new TimeSeries("CPU Usage");
        for (int i = 0; i < cpuUsage.size(); i++) {
            CPUseries.addOrUpdate(new Minute(times.get(i)), cpuUsage.get(i));
        }
        JFreeChart CPUchart = ChartFactory.createTimeSeriesChart(
                "CPU Usage",
                "Time",
                "CPU Usage",
                new TimeSeriesCollection(CPUseries),
                true,
                true,
                false
        );
        ChartPanel CPUpanel = new ChartPanel(CPUchart);
        BufferedImage CPUimage = CPUpanel.getChart().createBufferedImage(500,300);
        File CPUgraph = new File(name+"_cpuUsage.png");
        ImageIO.write(CPUimage,"png",CPUgraph);

        TimeSeries Memseries = new TimeSeries("Memory Usage");
        for (int i = 0; i < cpuUsage.size(); i++) {
            Memseries.addOrUpdate(new Minute(times.get(i)), cpuUsage.get(i));
        }
        JFreeChart Memchart = ChartFactory.createTimeSeriesChart(
                "Memory Usage",
                "Time",
                "Mem Usage",
                new TimeSeriesCollection(CPUseries),
                true,
                true,
                false
        );
        ChartPanel Mempanel = new ChartPanel(Memchart);
        BufferedImage Memimage = CPUpanel.getChart().createBufferedImage(500,300);
        File Memgraph = new File(name+"_memUsage.png");
        ImageIO.write(Memimage,"png",Memgraph);

        TimeSeries Diskseries = new TimeSeries("Disk Usage");
        for (int i = 0; i < cpuUsage.size(); i++) {
            Diskseries.addOrUpdate(new Minute(times.get(i)), cpuUsage.get(i));
        }
        JFreeChart Diskchart = ChartFactory.createTimeSeriesChart(
                "Disk Usage",
                "Time",
                "Disk Usage",
                new TimeSeriesCollection(CPUseries),
                true,
                true,
                false
        );
        ChartPanel Diskpanel = new ChartPanel(Diskchart);
        BufferedImage Diskimage = Diskpanel.getChart().createBufferedImage(500,300);
        File Diskgraph = new File(name+"_diskUsage.png");
        ImageIO.write(Diskimage,"png",Diskgraph);
    }
}
