/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatMulticast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Diana Paola
 */
public class SRecibe {
     public static void main(String[] args){
      try{
          int pto = 1234;
          ServerSocket s = new ServerSocket(pto);
          s.setReuseAddress(true); //si cerramos la ejecucion no esperamos de 15-25s para desasociar puerto
          System.out.println("Servidor iniciado esperando por archivos..");
          
          File f = new File(""); //al hacer "" apunta automaticamente a la ruta de la raiz de mi proyecto
          String ruta = f.getAbsolutePath();    //con esta instrucción guardo esa ruta anterior
          
          for(;;){  //inf xq no se cuantos clientes se van a conectar
              Socket cl = s.accept();   //acepto la conexion cuando se solicite
              System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.getPort());
              
              /* a la conexion le asociamos un flujo de lectura con dataInputStream xq leere diferentes 
              tipos de datos. p.e: String:nombre archivo, long:tam archivo, arr de bytes:contenido */
              DataInputStream dis = new DataInputStream(cl.getInputStream());
              
              String contact = dis.readUTF();
              
              String ruta_archivos = ruta+"\\contactos\\"+contact+"\\";   //string con la ruta del proyecto + una carpeta
                System.out.println("ruta creada: "+ruta_archivos); 
                File f2 = new File(ruta_archivos);    //genero otra instancia File asociandolo a la ruta con la
                f2.mkdirs();                      //carpeta. con mkdirs Creo la carpeta desde cero
                f2.setWritable(true); //doy permiso de escritura a la carpeta xq ahi iran los archivos
              
              
              String nombre = dis.readUTF(); //nos preparamos p/ leer el name del file y lo hacemos con readUTF
              long tam = dis.readLong();    //leemos el tam del archivo con readLong (xq es de tipo long)
              System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
              
            
              DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
              long recibidos=0; //acumulador para ver que tanto he recibido del tam del archivo
              int l=0, porcentaje=0; //l-> para saber cuantos bytes se leyeron desde el socket
              
              while(recibidos<tam){ //hasta que lea todo
                  
                  byte[] b = new byte[1500]; //aqui copiamos temporalmente los datos que llegan del socket
                  l = dis.read(b);      //leo lo de 1500 bytes o hasta donde llegue 
                  
                  System.out.println("leidos: "+l);
                  dos.write(b,0,l); //escribo en mi nuevo archivo lo que leí y almacené en b
                  dos.flush();  //
                  
                  recibidos = recibidos + l;
                  porcentaje = (int)((recibidos*100)/tam);
                  System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
              }//while
              
              System.out.println("Archivo recibido..");
              dos.close();
              dis.close();
              cl.close();
          }//for
          
      }catch(Exception e){
          e.printStackTrace();
      }  
    }//main
}
