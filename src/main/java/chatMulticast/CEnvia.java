/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatMulticast;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Diana Paola
 */
public class CEnvia {
    private Socket cl;
    private int puerto;
    private String dir;
    private String destino;
    BufferedOutputStream bos;
    DataOutputStream dos;
    
    /*private String  nombre;
    private String path;
    private long tam;*/
    
    public CEnvia(){
        this.puerto = 1234;
        this.dir = "127.0.1.5";
        this.destino = "";
    }
    
    public void crearConexion() throws IOException{
        cl = new Socket(dir, puerto);
        System.out.println("Conexion establecida...\n ");        
    }
    
    public void transferirArchivo(File f, String remitente, String destinatario) throws IOException{
        
        ArrayList<String> contactos = new ArrayList();
        contactos.add(destinatario);
        contactos.add(remitente);
        
        transferirArchivo(f, contactos);
    }
    
    public void transferirArchivo(File f, ArrayList<String> contactos) throws IOException{
        
        
        String nombre = f.getName(); //nombre del archivo
        String path = f.getAbsolutePath();  //ruta del archivo
        long tam = f.length();  //tamanio del archivo
   
        int i=0;
        while(i < contactos.size()){
            crearConexion();
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            DataInputStream dis = new DataInputStream(new FileInputStream(path));
            System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes a: " +contactos.get(i)+"\n\n");
                
                
                dos.writeUTF(contactos.get(i));
                dos.flush();
                dos.writeUTF(nombre);   //escrito el nombre del archivo en el servidor
                dos.flush();    //me sercioro que los datos se manden 
                dos.writeLong(tam); //mando el tam del archivo al servidor p/saber la condicion de paro
                dos.flush();
                
                long enviados = 0;
                int l=0,porcentaje=0;   //l servira p/saber cuantos bytes he leido del archivo
                
                while(enviados<tam){    /*enviamos el contenido del archivo. En cada iteracion
                                        mandamos un pedazo del archivo xq quiza pesa mas q mi RAM*/
                    byte[] b = new byte[1500];  //mando pedazos de max 1500 bytes
                    l=dis.read(b);      //leo lo mas q se pueda entre 0 y 1500 bytes del archivo
                    System.out.println("enviados: "+l);
                    
                    dos.write(b,0,l);   /*los datos q lei en b se escriben en el socket 
                                        (de b desde 0 hasta lo q lei) en el dataOutputStream*/
                    dos.flush();    //nos aseguramos de enviar los datos
                    enviados = enviados + l;
                    porcentaje = (int)((enviados*100)/tam);
                    System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                    
                }//while
                
                System.out.println("\nArchivo enviado..");
                i++;
                
                dis.close();
                dos.close();
                cl.close();
        }
        
                
    }
    
    public static void main(String[] args){
         
    }//main
}