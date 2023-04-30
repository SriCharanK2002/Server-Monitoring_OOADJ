package com.SM.controller;
import com.SM.model.Server;
import com.SM.model.UpdateSQL;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Monitor implements Serializable {
    public List<Server> servers;

    public Monitor() {
        this.servers = new ArrayList<Server>();
    }

    public boolean addServer(Server server) {
        boolean ret = false;
        int flag = 1;
        for(Server s: servers){
            if(s.GetHostName().equals(server.GetHostName()) || s.getIPAddress().equals(server.getIPAddress()))
                flag = -1;
        }
        if(flag == 1){
            servers.add(server);
            ret = true;
        }
        return ret;
    }

    public void removeServer(Server server) {
//        boolean ret = false;

        servers.remove(server);

//                break;

//        if(ret == true)
//            servers.remove(server);
//        return ret;
    }

    public Server getServer(String name) {
        for (Server server : servers) {
            if (server.GetHostName().equals(name)) {
                return server;
            }
        }
        return null;
    }

    public void monitorServers() {
        for (Server server : servers) {
            int flag=0;
            if (server.getCpuUsage() > 90) {
                flag=1;
                notify(server, "CPU usage is high");
            }
            if (server.getMemUsage() > 90) {
                notify(server, "Memory usage is high");
                flag=1;
            }
            if (server.getDiskUsage() > 90) {
                notify(server, "Disk usage is high");
                flag=1;
            }
            if(flag==0){
                System.out.println("Normal");
                // notify(server, "Normal");
            }
        }
    }

    private void notify(Server server, String eventType) {
        System.out.println("Server " + server.GetHostName() + ": " + eventType+
                " \nIP address: " + server.getIPAddress() + "\n"
                + "CPU usage: " + server.getCpuUsage() + "%\n"
                + "Memory usage: " + server.getMemUsage() + "MB\n"
                + "Disk usage: " + server.getDiskUsage() + "%");
    }


    public ArrayList generateReport() throws ClassNotFoundException, SQLException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("Report Generate at: " + currentDateTime);
        ArrayList<String> res = new ArrayList<>();
        String ans = "";
        for (Server server : servers) {
            UpdateSQL uSql = new UpdateSQL();
            ResultSet rs=uSql.GetData(server);
            rs.last();
            String CPU =rs.getString("cpu");
            String MEM =rs.getString("mem");
            String DISK =rs.getString("disk");
            String IP = rs.getString("ipaddress");
            String createdAt = rs.getTimestamp("TS").toString();
            ans = "Server " + server.GetHostName() + ": at " +createdAt+
                    " \nIP address: " + IP + "\n"
                    + "CPU usage: " + CPU + "%\n"
                    + "Memory usage: " + MEM + "MB\n"
                    + "Disk usage: " + DISK + "%\n\n";
            rs.close();
            res.add(ans);
        }
        return res;
    }
}