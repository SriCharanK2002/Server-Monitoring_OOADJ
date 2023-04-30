package com.SM.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import com.SM.model.Server;
import com.SM.controller.Monitor;
import com.SM.security.SecurityService;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;


@Route("technician")
@PageTitle("Technician")
public class TechnicianView extends VerticalLayout {
    Monitor mon;
    int bflag=0;
    SecurityService securityService;
    Image img;

    public TechnicianView(SecurityService securityService){
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        this.securityService = securityService;
        createHeader();

//        Grid<ArrayList> grid = new Grid<>();
//        grid.addColumn(String::toString).setHeader("String");
        Label layout1 = new Label();
        layout1.getStyle().set("white-space","pre-wrap");


        Technician tech = new Technician("Technician","Tech@email.com","Tech");
        TextField mon_name = new TextField("Enter Monitor Name");
        Button deserializeButton = new Button("Upload Monitor",e -> {
            try {
                FileInputStream fileIn = new FileInputStream(mon_name.getValue());
                ObjectInputStream in = new ObjectInputStream(fileIn);
                mon = (Monitor) in.readObject();
                in.close();
                fileIn.close();
                Notification.show("Monitor deserialized from monitor.cfg");
            } catch (Exception exp) {
                exp.printStackTrace();
                Notification.show("Monitor does not exist");
            }
        });
        Label vr = new Label("Click the following button to see the Current Status of the servers ");
        Button viewRep = new Button("View Report",e -> {
            try {
                ArrayList<String> ans = tech.viewReport(mon);
                System.out.println(ans);
//                grid.setItems(ans);
                for(String s : ans) {
                    Label label = new Label(s);
                    layout1.add(label);
                }
            } catch (ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        TextField server_name = new TextField("Enter Server Name");
        Button getLog = new Button("Generate Log file",e -> {
            String serv_name = server_name.getValue();
            Server s = mon.getServer(serv_name);
            try {
                tech.generateLogs(s);
                Notification.show("Log file created");
            } catch (SQLException | IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button genGraph = new Button("Generate Chart",e -> {
            InputStreamFactory inputStreamFactory = new InputStreamFactory()
            {
                @Override
                public InputStream createInputStream() {
                    try {
                        return new FileInputStream("chart.png");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            String serv_name = server_name.getValue();
            Server s = mon.getServer(serv_name);
            try {
                tech.generateGraph(s);
            } catch (IOException | ClassNotFoundException | SQLException ex) {
                throw new RuntimeException(ex);
            }
            StreamResource resource = new StreamResource("chart.png", inputStreamFactory);
            if(bflag==1) {
                remove(img);
            }
            bflag=1;
            img=new Image(resource, "Image not found");
           add(img);
        });
        add(mon_name,deserializeButton,vr,viewRep,layout1,server_name,getLog,genGraph);
    }



    public void createHeader(){
        H2 head = new H2("Technician Page");
        head.addClassNames("text-l","m-n");
        Button logout = new Button("Log Out", e -> securityService.logout());
        HorizontalLayout header = new HorizontalLayout(head,logout);

        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(head);
        header.setSizeFull();

        add(header);
    }
}
