/*project name: BitTorrent P2P Network for File Downloading in Distributed System
 implement time:09/2015
 implementer:Xin Du
 All rights reserved.*/
  
import java.io.BufferedInputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.util.ArrayList;
import java.io.*;
  
public class server extends Thread {  
    private ServerSocket ss=null; 
    private int port;
    private int chunkSize;
    public server(int port,int chunksize){     
    this.port=port;
    this.chunkSize=chunksize;
    }  
    public void run(){  
        DataOutputStream dos=null;  
        DataInputStream dis=null;  
        DataInputStream disSocket=null;  
        Socket socket=null;  
        try {  
            
            ss=new ServerSocket(this.port); 
            System.out.println("Server is waiting for connection:"); 
            socket=ss.accept(); 
            disSocket = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
            dos=new DataOutputStream(socket.getOutputStream()); 
            String filename=disSocket.readUTF();
            System.out.println("filename:"+filename); 
            File file=new File(filename);  
            dos.writeLong((long) file.length());  
            long filelength=(long) file.length();
            System.out.println("file length:"+filelength);    
            dos.flush();
            dis=new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));  
            ArrayList<byte[]> arraylist=new ArrayList<byte[]>();
            ArrayList<Integer> markarray=new ArrayList<Integer>();
            int remainlength=(int)filelength;
            while(remainlength>0){
            	if (remainlength<=this.chunkSize){
            		byte[]bufArray=new byte[remainlength];
            		dis.read(bufArray); 
            		remainlength=0;
            		arraylist.add(bufArray);
                    markarray.add(1);
            	}
            	else{
            		byte[]bufArray=new byte[this.chunkSize];
            		dis.read(bufArray); 
            		remainlength=remainlength-this.chunkSize;
            		arraylist.add(bufArray);
                    markarray.add(1);
            	}
            }
            System.out.println("chunksize:"+chunkSize);  
            System.out.println("Total chunk number: "+arraylist.size());
            System.out.println("FileOwner has "+markarray);
            dos.writeInt(markarray.size());//send chunknum to receiver
            dos.flush();
            while(true){
            if (disSocket.readInt()==-1){
                for(int j=0;j<markarray.size();j++){
                	//System.out.println(markarray.get(j));
            	    dos.writeInt(markarray.get(j));
            	    dos.flush();
                }
            }
            int a=disSocket.readInt();
            if(a==-6){
     		  break;
     		  }
                int requestnumber=a;
                  System.out.println("request file number from client:"+requestnumber);
                  dos.writeInt(arraylist.get(requestnumber).length);
                 // System.out.println("arraylist.get(requestnumber).length"+arraylist.get(requestnumber).length);
                  dos.write(arraylist.get(requestnumber), 0, arraylist.get(requestnumber).length);
               	  dos.flush();
               	  System.out.println("Send file chunk"+requestnumber+" to client.");
               	 
              if (disSocket.readInt()==-2){
            	  break;
              }  
            }
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {   
              // close all the connection  
              try {   
                if (dos != null)   
                  dos.close();   
              } catch (IOException e) {   
              }   
              try {   
                if (dis != null)   
                  dis.close();   
              } catch (IOException e) {   
              }   
              try {   
                if (socket != null) 
                  disSocket.close(); 
                  socket.close();   
              } catch (IOException e) {   
              }   
              try {   
                if (ss != null)   
                  ss.close();   
                System.out.println("Server is closed now.");
              } catch (IOException e) {   
              }   
            }   
    }  
    public static void main(String []args){
        String[] temp=null;
        try{
            FileInputStream fis = new FileInputStream("config");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            line = br.readLine();
            temp= line.split(" ");
            br.close();
        } catch (Exception e){
            ;
        }
     server s1=new server( Integer.parseInt(temp[1]),102400);
     s1.start();  
     server s2=new server( Integer.parseInt(temp[2]),102400);
     s2.start();  
     server s3=new server( Integer.parseInt(temp[3]),102400);
     s3.start();  
     server s4=new server( Integer.parseInt(temp[4]),102400);
     s4.start();  
     server s5=new server( Integer.parseInt(temp[5]),102400);
     s5.start(); 
    }  
}  