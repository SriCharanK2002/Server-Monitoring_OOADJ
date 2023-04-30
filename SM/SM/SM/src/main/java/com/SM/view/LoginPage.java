package com.SM.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Login Page")
public class LoginPage extends VerticalLayout {
    public LoginPage() {
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Button loginbutton = new Button("Login");

        loginbutton.addClickListener(e -> {
            String enteredUsername = username.getValue();
            String enteredPassword = password.getValue();
            String isAuthentic = authenticateUser(enteredUsername, enteredPassword);
            if (isAuthentic.equals("Admin")) {
                UI.getCurrent().navigate("admin");
            } else if (isAuthentic.equals("tech")) {
                UI.getCurrent().navigate("technician");
            } else if (isAuthentic.equals("infra")) {
                UI.getCurrent().navigate("infra");
            } else {
                Notification.show("Incorrect username or password. Please try again.");
            }
        });
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(
                new H2("Remote Server Management"),
                username,
                password,
                loginbutton
        );
    }

    private String authenticateUser(String name, String password) {
        String val;
        if(name.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin"))
            val = "Admin";
        else if(name.equalsIgnoreCase("tech") && password.equalsIgnoreCase("tech"))
            val = "tech";
        else if(name.equalsIgnoreCase("infra") && password.equalsIgnoreCase("infra"))
            val = "infra";
        else val = "none";
        return val;
    }
}
