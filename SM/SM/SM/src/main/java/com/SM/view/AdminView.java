package com.SM.view;

import com.SM.controller.Monitor;
import com.SM.model.Server;
import com.SM.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

@Route("admin")
@PageTitle("Admin Page")
public class AdminView extends VerticalLayout {
    SecurityService securityService;
    public AdminView(SecurityService securityService) {
        Admin admin = new Admin("Admin","admin@email.com","admin");
        this.securityService = securityService;
        Monitor monitor = admin.create_monitor();
        createHeader();
        Label lb = new Label("Enter the Server name to add or remove it");
        add(lb);
        TextField server_name = new TextField("Server name");
        TextField ip_address = new TextField("IP address");
        server_name.setRequiredIndicatorVisible(true);
        ip_address.setRequiredIndicatorVisible(true);
        Button addServer = new Button("Add Server",e -> {
            String serv_name = server_name.getValue();
            String ip_addr = ip_address.getValue();
            Server s = new Server(ip_addr,serv_name);
            boolean res = monitor.addServer(s);
            if(res == true){
                Notification.show("Server has been added");
            }
            else{
                Notification.show("Server with given name or IP address already exists");
            }
        });

        Button removeServer = new Button("Remove Server",e -> {
            String serv_name = server_name.getValue();
            String ip_adder = ip_address.getValue();
            int flag=0;
            for (Server s: monitor.servers){
                if(s.GetHostName().equals(serv_name) && s.getIPAddress().equals(ip_adder)){
                    flag=1;
                    monitor.removeServer(s);
                    break;
                }
            }
            if(flag==1){
                Notification.show("Server has been removed");
            }
            else{
                Notification.show("Server with given name or IP address doesn't exist");
            }
        });

        Label outputLabel = new Label();

        Button showServersButton = new Button("Show Servers");
        showServersButton.addClickListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Server server : monitor.servers) {
                sb.append(server.GetHostName()).append(" - ").append(server.getIPAddress()).append(" | | ");
            }
            outputLabel.setText(sb.toString());
        });
        Button serializeButton = new Button("Download Monitor");
        serializeButton.addClickListener(event -> {
            try {
                FileOutputStream fileOut = new FileOutputStream("monitor.cfg");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(monitor);
                out.close();
                fileOut.close();
                Notification.show("Object serialized to monitor.cgf");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        HorizontalLayout h1 = new HorizontalLayout();
        h1.add(server_name,ip_address);
        HorizontalLayout h2 = new HorizontalLayout();
        h2.add(addServer,removeServer);
        add(h1,h2,showServersButton,outputLabel,serializeButton);
    }
    private void createHeader(){
        H2 head = new H2("Admin Page");
        head.addClassNames("text-l","m-n");
        Button logout = new Button("Log Out", e -> securityService.logout());
        HorizontalLayout header = new HorizontalLayout(head,logout);

        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(head);
        header.setSizeFull();

        add(header);
    }
}
