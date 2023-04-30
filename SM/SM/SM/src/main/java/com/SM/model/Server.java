package com.SM.model;

import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;

public class Server implements Serializable {
    private String ipAddress;
    private String hostname;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;

    public Server(String ipAddress, String hostname) {
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.cpuUsage = 0.0;
        this.memoryUsage = 0.0;
        this.diskUsage = 0.0;
    }

    public void updateStats() {
        // Code to get current system stats for this server
        double currentCpuUsage = getCurrentCpuUsage();
        double currentMemoryUsage = getCurrentMemoryUsage();
        double currentDiskUsage = getCurrentDiskUsage();
        // System.out.println(currentCpuUsage);

        // Update server stats
        setCpuUsage(currentCpuUsage);
        setMemoryUsage(currentMemoryUsage);
        setDiskUsage(currentDiskUsage);
    }

    // Getters and setters for ipAddress, hostname, cpuUsage, memoryUsage, and diskUsage

    private void setDiskUsage(double currentDiskUsage) {
        if (currentDiskUsage < 0) {
            throw new IllegalArgumentException("Disk usage cannot be negative.");
        }
        this.diskUsage = currentDiskUsage;
    }

    private void setCpuUsage(double currentCpuUsage) {
        if (currentCpuUsage < 0 || currentCpuUsage > 100) {
            throw new IllegalArgumentException("CPU usage must be between 0 and 100.");
        }
        this.cpuUsage = currentCpuUsage;
    }

    private void setMemoryUsage(double currentMemoryUsage) {
        if (currentMemoryUsage < 0) {
            throw new IllegalArgumentException("Memory usage cannot be negative.");
        }
        this.memoryUsage = currentMemoryUsage;
    }
    //Getters
    public String getIPAddress() {
        return ipAddress;
    }
    public String GetHostName() {
        return hostname;
    }
    public double getCpuUsage() {
        return cpuUsage;
    }
    public double getMemUsage() {
        return memoryUsage;
    }
    public double getDiskUsage() {
        return diskUsage;
    }

    // Private helper methods to get current system stats for this server
    private double getCurrentCpuUsage() {
        double cpuUsage = 0;
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            double loadAvg = osBean.getSystemLoadAverage();
            int processors = osBean.getAvailableProcessors();
            cpuUsage = loadAvg / processors * 100;
        } catch (Exception e) {
            // Handle the exception appropriately
        }
        return cpuUsage;
    }


    private double getCurrentMemoryUsage() {
        double memoryUsage = 0;
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                    OperatingSystemMXBean.class);
            long freeBytes = osBean.getFreePhysicalMemorySize();
            long totalBytes = osBean.getTotalPhysicalMemorySize();
            memoryUsage = totalBytes - freeBytes;

        } catch (Exception e) {
            // Handle the exception appropriately
        }
        return memoryUsage;
    }

    private double getCurrentDiskUsage() {
        double diskUsage = 0;
        try {
            File root = File.listRoots()[0];
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            diskUsage = (double) usedSpace / (double) totalSpace * 100;
        } catch (Exception e) {
            // Handle the exception appropriately
        }
        return diskUsage;
    }

    // Method to serialize the Server object to a file
    public void serialize(String fileName) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        }
    }

    // Static method to deserialize a Server object from a file
    public static Server deserialize(String fileName) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Server) in.readObject();
        }
    }
    public void displayStats() {
        System.out.println("Server Name: " + hostname);
        System.out.println("IP Address: " + ipAddress);
        // System.out.println("Port Number: " + port);
        System.out.println("CPU Usage: " + cpuUsage + "%");
        System.out.println("Memory Usage: " + (memoryUsage/1024)/1024 + " MB");
        System.out.println("Disk Usage: " + diskUsage + " %");
    }
    //Methods to update Server IP if interface changes
    private String getIPAddress(String interfaceName) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            InetAddress networkAddress = address.getAddress();
            if (networkAddress != null && networkAddress instanceof Inet4Address) {
                return networkAddress.getHostAddress();
            }
        }
        throw new RuntimeException("Unable to get network address for interface " + interfaceName);
    }
    public void setIPAddress(String iface) throws SocketException{
        this.ipAddress=getIPAddress(iface);


    }
//    public static void main(String args[]) throws SocketException, ClassNotFoundException
//    {
//        Server myServer = new Server("192.168.1.100","MYSERVER");
//        myServer.setIPAddress("enp0s3");
//
//
//        while(true){
//            myServer.updateStats();
//            UpdateSQL connection=new UpdateSQL();
//            connection.UpdateDatabase(myServer);
//
//            try {
//                myServer.serialize(myServer.GetHostName()+".cfg");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                Server server = Server.deserialize(myServer.GetHostName()+".cfg");
//                server.displayStats();
//            } catch (ClassNotFoundException | IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                Thread.sleep(5000); // pause for 5 second
//            } catch (InterruptedException e) {
//                // handle the exception if the thread is interrupted while sleeping
//            }
//
//        }
//    }
}

