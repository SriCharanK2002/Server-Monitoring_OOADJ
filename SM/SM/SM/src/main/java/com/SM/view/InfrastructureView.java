package com.SM.view;

import com.SM.controller.Monitor;
import com.SM.model.Server;
import com.SM.security.SecurityService;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.SQLException;

@Route("infra")
@PageTitle("Infra Manager")
    public class InfrastructureView extends VerticalLayout {
        Monitor mon;

        SecurityService securityService;
        public InfrastructureView(SecurityService securityService) {
            createHeader();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
            InfraStructureManager infra = new InfraStructureManager("Infra","Infra@email.com","Infra");
            this.securityService = securityService;
            TextField mon_name = new TextField("Enter Monitor Name");
            Button deserializeButton = new Button("Upload Monitor");
            deserializeButton.addClickListener(event -> {
                try {
                    FileInputStream fileIn = new FileInputStream(mon_name.getValue());
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    mon = (Monitor) in.readObject();
                    in.close();
                    fileIn.close();
                    Notification.show("Monitor deserialized from monitor.cfg");
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show("Monitor does not exist");
                }
            });
            Label outputLabel = new Label();
            Button showServersButton = new Button("Show Servers");
            showServersButton.addClickListener(e -> {


                StringBuilder sb = new StringBuilder();
                for (Server server : mon.servers) {
                    sb.append(server.GetHostName()).append(" - ").append(server.getIPAddress()).append(" | | ");
                }
                outputLabel.setText(sb.toString());
            });
            add(mon_name,deserializeButton);
            H4 heading4 = new H4("View List of Servers in the Monitor");
            add(heading4);
            add(outputLabel);
            add(showServersButton);
//            add(deserializeButton);
            TextField server_name = new TextField("Enter Server Name");
            Label lb = new Label();
            Button ViewServer = new Button("View Server Info",buttonClickEvent -> {
                lb.getStyle().set("white-space","pre-wrap");
                    int flag=0;
                    String serv_name = server_name.getValue();
                    for (Server s: mon.servers){
                        if(s.GetHostName().equals(serv_name)){
                            try {
                                String info=infra.viewServerInfo(s);
                                lb.setText(info);

                                flag=1;
                            } catch (ClassNotFoundException | SQLException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                    }
                    if(flag==1){
                        Notification.show("Server info displayed");
                    }
                    else {
                        Notification.show("Server with given name doesn't exist");
                    }
            });
            H4 heading = new H4("View Server Info");
            add(heading);
            add(server_name,ViewServer,lb);
            TextField updateS=new TextField("Enter server name you want to upgrade");
            TextField infor=new TextField("Enter upgrade info ");
            updateS.setWidth("300px");
            Button updateSe=new Button("Update Server",buttonClickEvent -> {
                int flag=0;
                for (Server s: mon.servers){
                    if(s.GetHostName().equals(updateS.getValue())){
                        flag=1;
                        infra.updateServer(s,infor.getValue());
                        break;
                    }
                }
                if(flag==1){
                    Notification.show("Server Log updated");
                }
                else{
                    Notification.show("Server with given name doesn't exist");
                }
            });
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
            H4 heading1 = new H4("Log Up-gradation Details");
            add(heading1);
            add(updateS,infor,updateSe);
        }
        private void createHeader(){
            H2 head = new H2("Infrastructure Manager Page");
            head.addClassNames("text-l","m-n");
            Button logout = new Button("Log Out", e -> securityService.logout());
            HorizontalLayout header = new HorizontalLayout(head,logout);

            header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            header.expand(head);
            header.setSizeFull();

            add(header);

        }
    }

