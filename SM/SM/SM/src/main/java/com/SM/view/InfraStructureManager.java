package com.SM.view;

import com.SM.model.Server;
import com.SM.model.UpdateSQL;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class InfraStructureManager extends Personnel {
    public InfraStructureManager(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String viewServerInfo(Server server) throws ClassNotFoundException, SQLException {
        //input will be monitor.getserver()
        //prints out most recent info of server
        UpdateSQL usql = new UpdateSQL();
        ResultSet rs = usql.GetData(server);
        //     while (rs.next()) {
        // }
        rs.last();
        String CPU = rs.getString("cpu");
        String MEM = rs.getString("mem");
        String DISK = rs.getString("disk");
        String IP = rs.getString("ipaddress");
        String createdAt = rs.getTimestamp("TS").toString();
        String data = "Server " + server.GetHostName() + ": at " + createdAt +
                " \nIP address: " + IP + "\n"
                + "CPU usage: " + CPU + "%\n"
                + "Memory usage: " + MEM + "MB\n"
                + "Disk usage: " + DISK + "%\n\n";

        rs.close();
        return data;
    }

    public int updateServer(Server server, String Update) {
        //This is an information based function in which any hardware updates are taken
        // as input and recorded in a history file.

        try {
            FileWriter writer = new FileWriter(server.GetHostName() +".log",true);
            writer.write("\n\nTime: " + LocalTime.now()+"\n"+Update+"\n\n");
            writer.close();
            return 1;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return 0;
    }
}
