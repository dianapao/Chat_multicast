/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatMulticast;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 *
 * @author Diana Paola
 */
public class Client extends Thread {

    public static final String MCAST_ADDR = "230.1.1.1";
    public static final int MCAST_PORT = 4000;
    public static final int DGRAM_BUF_LEN = 2048;
    Ventana v = new Ventana(0);
    
    CEnvia clFile = new CEnvia();

    public void run() {
        InetAddress group = null;
        try {
            group = InetAddress.getByName(MCAST_ADDR);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        boolean salta = true;

        try {
            MulticastSocket socket = new MulticastSocket(MCAST_PORT);
            socket.joinGroup(group);
            DatagramPacket contacto = new DatagramPacket(("<inicio>" + v.getNombre()).getBytes(), ("<inicio>" + v.getNombre()).length(), group, MCAST_PORT);
            socket.send(contacto);
            while (salta) {
                //System.out.println("entro");
                if (v.getStatus() == 0) {     //Lectura
                    //System.out.println("entro lectura");
                    socket.setSoTimeout(100);
                    try {
                        byte[] buf = new byte[DGRAM_BUF_LEN];
                        DatagramPacket recv = new DatagramPacket(buf, buf.length);
                        socket.receive(recv);
                        byte[] data = recv.getData();
                        String mensaje = new String(data);
                        System.out.println("Datos recibidos: " + mensaje);
                        v.setNewMessage(mensaje);
                    } catch (Exception e) {
                    }
                } else if (v.getStatus() == 1) {   //Escritura
                    //System.out.println("entro Escritura");
                    String mensaje = "";
                    
                    if(v.getSalida() == 1){
                        mensaje = "<salida>" + v.getNombre();
                    }else{
                        if(v.getActiveTab() != 0){
                            mensaje = "C<msj><privado><" + v.getNombre() + "><" + v.getContactosChat(v.getActiveTab()) + ">" + v.getActiveMessage();
                        }else if(v.getActiveTab() == 0){
                            mensaje = "C<msj><" + v.getNombre() + ">" + v.getActiveMessage();
                        }
                    }
                    DatagramPacket packet = new DatagramPacket(mensaje.getBytes(), mensaje.length(), group, MCAST_PORT);
                    System.out.println("Enviando: " + mensaje + "  con un TTL= " + socket.getTimeToLive());
                    socket.send(packet);
                    v.setStatus(0);
                } else if (v.getStatus() == 2){ //enviar archivo                    
                    File f = v.getActiveFile();
                    System.out.println("File rev");
                    
                    String mensaje = "";
                    if(v.getActiveTab() != 0){  //chat privado
                         mensaje = "C<file><privado><" + v.getNombre() + "><" + v.getContactosChat(v.getActiveTab()) + ">" + v.getActiveFile();
                         clFile.transferirArchivo(f, v.getNombre(), v.getContactosChat(v.getActiveTab()) );
                    }else if (v.getActiveTab() == 0){   //chat general
                        mensaje = "C<file><" + v.getNombre() + ">" + v.getActiveFile();
                        clFile.transferirArchivo(f, v.getContactosChat());
                        System.out.println("C<> Archivo transferido a todos :D ");
                    
                    }
                    v.setStatus(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }

    }//run

    public static void main(String[] args) {

        try {
            Client cliente = new Client();
            cliente.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//main
}//class
