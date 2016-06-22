/*project name: BitTorrent P2P Network for File Downloading in Distributed System
 implement time:09/2015
 implementer:Xin Du
 All rights reserved.*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.net.ServerSocket;
import java.net.Socket;  
import java.net.UnknownHostException;  
import java.util.ArrayList;
import java.util.Random;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.io.*;

  
public class Client4 extends Thread {  
	public String filename;
	public String fileOwnerIp;
	public int fileOwnerPortNum;
	public String downloadNeighborIp;
	public int downloadNeighborPortNum;
	public String uploadNeighborIp;
	public int uploadNeighborPortNum;
	public static int chunkNum;
	public static ArrayList<byte[]> arraylist=new ArrayList<byte[]>();;
	public static ArrayList<Integer> clientmarkarray = new ArrayList<Integer>();
	
	public int tasktype;// 1 stands for download from server; 2 stands for downloads from neighbor; 3 stands for upload to neighbor; 
    public Client4(String Ip, int PortNum,int tasktype,String filename){        
    	switch(tasktype){
    	case 1:
    		this.fileOwnerIp=Ip;
    		this.fileOwnerPortNum=PortNum;
    		this.tasktype=tasktype;
    		this.filename=filename;
    		break;
    	case 2:
    		this.downloadNeighborIp=Ip;
    		this.downloadNeighborPortNum=PortNum;
    		this.tasktype=tasktype;
    		this.filename=filename;
    		break;
    	case 3:
    		this.uploadNeighborIp=Ip;
    		this.uploadNeighborPortNum=PortNum;
    		this.tasktype=tasktype;
    		this.filename=filename;
    		break;
    	default:
    		break;	
    	}
    }  
    public void run(){
    	switch(this.tasktype){
    	case 1:
    		this.downloadFromServer();
    		break;
    	case 2:
    		this.downloadFromNeighbor();
    		break;
    	case 3:
    		this.uploadtoNeighbor();
    		break;
    	default:
    		break;	
    	}
    }
    public void uploadtoNeighbor(){
    	try {
			sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	ServerSocket ss=null; 
    	DataOutputStream dos=null;  
        DataInputStream dis=null;  
        DataInputStream disSocket=null;  
        Socket socket=null;
        try{    
        	ss=new ServerSocket(this.uploadNeighborPortNum); 
        	System.out.println("Client4 is waiting for connection with uploadNeighbor:"); 
        	socket=ss.accept(); 
        	disSocket = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
        	dos=new DataOutputStream(socket.getOutputStream()); 
        	while(true){
        		if (disSocket.readInt()==-1){//1.
        			for(int j=0;j<clientmarkarray.size();j++){
        				dos.writeInt(clientmarkarray.get(j));//2.
        				dos.flush();
        			}
        		}
            int s=disSocket.readInt();
            if(s==-5){
                 int requestnumber=disSocket.readInt();//3.
                 if(requestnumber==-2){
                	 break;
                 }
                 System.out.println("File request number from uploadNeighbor:"+requestnumber);
                 String filename=requestnumber+"_"+this.filename;
                 File file=new File(filename); 
                 dis=new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
                 byte[]bufArray=new byte[(int)file.length()];
                 dos.writeInt((int)file.length());//4.
                 dos.flush();
                 dis.read(bufArray); 
                 dos.write(bufArray,0,(int)file.length());//5.
                 System.out.println("Send file chunk"+requestnumber+" to uploadNeighbor.");
               	 dos.flush();
               	 if(disSocket.readInt()==-2){
              	     break;
                }  
             }
    	 }
        }catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {   
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
                   System.out.println("Connection between Client4 and uploadNeighbor is closed now.");
              } catch (IOException e) {   
              }   
            } 
    }

    public void downloadFromNeighbor(){
    	String savePathsmall=null;
    	Socket socket=null;
    	DataInputStream dis=null;  
        DataOutputStream dos=null; 
        try {
			sleep(1500);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
    	try {   
            socket = new Socket(this.downloadNeighborIp,this.downloadNeighborPortNum); 
            System.out.println("Connected to downloadNeighbor.");
          } catch (UnknownHostException e1) {   
            //e1.printStackTrace(); 
        	  System.out.println("Cannot connect to download neighbor, will try to reconnect in 2 seconds.");
              try {
				sleep(2000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
              this.downloadFromNeighbor();
              return;
          } catch (IOException e1) {   
        	  System.out.println("Cannot connect to download neighbor, will try to reconnect in 2 seconds.");
              try {
				sleep(2000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
              this.downloadFromNeighbor(); 
              return;
          }  
    	try {   
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
            dos = new DataOutputStream(socket.getOutputStream()); 
          } catch (IOException e1) {   
            e1.printStackTrace();   

             
          } 
    	try{ 
    		int c=0;
        while(c<clientmarkarray.size()){
            dos.writeInt(-1);
     	    ArrayList<Integer> downloadNeighbormarkarray=new ArrayList<Integer>(clientmarkarray.size());
     	    for(int i=0;i<clientmarkarray.size();i++){	   
     	    	downloadNeighbormarkarray.add(dis.readInt());  
     	    }
     	    System.out.println("Already has:" + clientmarkarray);
     	    System.out.println("DownloadNeighbor has: " + downloadNeighbormarkarray);
     	    ArrayList<Integer> clientLackArray=new ArrayList<Integer>();
     	    for(int i=0;i<clientmarkarray.size();i++){
     		    if((downloadNeighbormarkarray.get(i)-clientmarkarray.get(i))==1){
     			    clientLackArray.add(i);
     		    }
     	    }
     	    if (clientLackArray.isEmpty()){
     		    int counter=0;
    		    for (int i=0;i<clientmarkarray.size();i++){
    			    if(clientmarkarray.get(i)==1){
    				    counter++;
    			    }
    		    }
    		    c=counter;
    		    if (c==clientmarkarray.size()){
      			    dos.writeInt(-5);
      			    dos.writeInt(-2);
      			    dos.flush(); 
      			  System.out.println("Download completed. Prepare to disconnect from downloadneighbor.");
      		   	} 
    		    else{ 
    		    	dos.writeInt(-4);
    		    	System.out.println("Downloadneighbor does not have any chunk that I need.");
    		    	sleep(1000);
      		   	}
     		    continue;
     	    }
     	    else{
     		    dos.writeInt(-5);
     		    int requestnum;
     		    int rnd = new Random().nextInt(clientLackArray.size());
     		    requestnum = clientLackArray.get(rnd);
     		    dos.writeInt(requestnum);
     		    dos.flush();
     		    System.out.println("Send requestnum " + requestnum+" to downloadNeighbor");
     		    int readChunkSize=dis.readInt();
     		    byte[] buf = new byte[readChunkSize];
     		    dis.read(buf);
                System.out.println("Recieve chunk " +requestnum +" from downloadNeighbor.");
     		    arraylist.add(buf);
     		    savePathsmall =requestnum+"_"+this.filename;
     		    DataOutputStream fileOut = new DataOutputStream(   
                    new BufferedOutputStream(new BufferedOutputStream(   
                        new FileOutputStream(savePathsmall))));
     		    fileOut.write(arraylist.get(arraylist.size()-1)); 
     		    
     		    fileOut.close();
                try{
                    sleep(1000);}
                catch(Exception ee){
                    ;
                }
     		    clientmarkarray.set(requestnum, 1); 
     		    int counter=0;
     		    for (int i=0;i<clientmarkarray.size();i++){
     			    if(clientmarkarray.get(i)==1){
     				    counter++;
     			    }
     		    }
     		    c=counter;
     		    if (c==clientmarkarray.size()){
     			    dos.writeInt(-2);
     			    dos.flush(); 
     			    System.out.println("Download completed. Prepare to disconnect from downloadneighbor.");
     		   	}
     		    else{
     			    dos.writeInt(-3);
     			    dos.flush(); 
     		    }
     	      }
     	    }
    	  }catch (Exception e) {   
               e.printStackTrace();
               
               return;   
          }  
          finally{
  			//Close connections
  			try{
  				dis.close();
  				dos.close();
  			    socket.close();
  			    System.out.println("Connection between Client4 and downloadNeighbor is closed now.");
  			    this.mergeFile(this.chunkNum);
  			}
  			catch(IOException ioException){
  				ioException.printStackTrace();
  			}
  		}
    }   

    public void downloadFromServer(){ 
    	String savePathsmall=null;
        Socket socket=null; 
        DataInputStream dis=null;  
        DataOutputStream dos=null; 
        try {   
            socket = new Socket(this.fileOwnerIp,this.fileOwnerPortNum);   
          } catch (UnknownHostException e1) {   
            e1.printStackTrace();   
          } catch (IOException e1) {   
            e1.printStackTrace();   
          }   
        try {   
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
            dos = new DataOutputStream(socket.getOutputStream()); 
          } catch (IOException e1) {   
            e1.printStackTrace();   
          }    
        long len = 0;   
       try{  
           dos.writeUTF(this.filename);
    	   dos.flush();
    	   len = dis.readLong();   
    	   int chunknum=dis.readInt();
    	   this.chunkNum = chunknum;
    	   System.out.println("Begin to receive the file: "+this.filename); 
           System.out.println("Total size of file " + len + "B"); 
           System.out.println("The total chunknum is " +chunknum);
           for(int i=0;i<chunknum;i++){	   
        	   clientmarkarray.add(0);    
           }
           int c=0;
           while(c<clientmarkarray.size()){
               dos.writeInt(-1);
               dos.flush();
        	   ArrayList<Integer> servermarkarray=new ArrayList<Integer>(chunknum);
        	   for(int i=0;i<chunknum;i++){	   
        		   servermarkarray.add(dis.readInt());    
        	   }
        	   System.out.println("Already has:" + clientmarkarray);
        	   System.out.println("Fileowner has:" + servermarkarray);
        	   ArrayList<Integer> clientLackArray=new ArrayList<Integer>();
        	   for(int i=0;i<chunknum;i++){
        		   if((servermarkarray.get(i)-clientmarkarray.get(i))==1){
        			   clientLackArray.add(i);
        		   }
        	   }
        	   if(clientLackArray.size()==0){
        		   dos.writeInt(-6);
        		   break;
        	   }
        	   int requestnum;
        	   int rnd = new Random().nextInt(clientLackArray.size());
        	   requestnum = clientLackArray.get(rnd);
        	   dos.writeInt(requestnum);
        	   dos.flush();
        	   System.out.println("send requestnum " + requestnum+" to server");
        	   int readChunkSize=dis.readInt();
        	   byte[] buf = new byte[readChunkSize];
        	   dis.read(buf); 
        	   arraylist.add(buf);
        	   savePathsmall =requestnum+"_"+this.filename;
    	       DataOutputStream fileOut = new DataOutputStream(   
                       new BufferedOutputStream(new BufferedOutputStream(   
                           new FileOutputStream(savePathsmall))));
    	       fileOut.write(arraylist.get(arraylist.size()-1)); 
    	       System.out.println("Recieve chunk"+requestnum+" from server!");
    	       fileOut.close(); 
    	       clientmarkarray.set(requestnum, 1);
    	       int counter=0;
    	       for (int i=0;i<clientmarkarray.size();i++){
    	    	   if(clientmarkarray.get(i)==1){
    	    		   counter++;
    	    	   }
    	       }
    	       c=counter;
    	       if (c==clientmarkarray.size()){
    	    	   dos.writeInt(-2);
    	    	   dos.flush();
	           }
    	       else{
    	    	   dos.writeInt(-3);
    	    	   dos.flush(); 
    	       }
    	       try{
    	    	   Thread.sleep(500);//1000ms = 1sec
    	    	}catch(InterruptedException e){
    	    	}
               }
        } catch (Exception e) {   
          e.printStackTrace();   
          return;   
        }  
        finally{
			//Close connections
			try{
				dis.close();
				dos.close();
				socket.close();
				System.out.println("Connection between Client4 and server is closed now." );
				this.mergeFile(this.chunkNum);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
    }  

    public void mergeFile(int chunknum) {
    	try{
    	String tempPath = "merged_" + this.filename;
    	FileOutputStream fileoutputStream = new FileOutputStream(tempPath);
    	int num=0;
    	String chunkFileName=null;
    	while(num<chunknum){
    		chunkFileName=num+"_"+this.filename;
    		File file=new File(chunkFileName); 
    		FileInputStream fileinputstream = new FileInputStream(chunkFileName);
    		byte[] chunkbuffer= new byte[(int)file.length()];
    		fileinputstream.read(chunkbuffer, 0, (int)file.length());
    		fileoutputStream.write(chunkbuffer);
			fileoutputStream.flush();
			fileinputstream.close();
			num++;
    	}
    	fileoutputStream.close();
    	System.out.println("Merge file completed. The savepath is " +tempPath);
    } catch (FileNotFoundException e) {  
        e.printStackTrace();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }
    }
    
    public static void main(String[] args) {
        String[] temp=null;
        try{
            FileInputStream fis = new FileInputStream("config");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            temp= line.split(" ");
            br.close();
        } catch (Exception e){
            ;
        }
        Client4 c41= new Client4("localhost", Integer.parseInt(temp[1]) ,1,"1.pdf");
        Client4 c42= new Client4("localhost", Integer.parseInt(temp[2]) ,2,"1.pdf");
        Client4 c43= new Client4("localhost", Integer.parseInt(temp[3]) ,3,"1.pdf");
        c41.start();
        c42.start();
        c43.start();
      }    
} 
