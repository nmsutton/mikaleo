/*
 * Author: Nate Sutton
 * 2017
 * 
 * References:
 * https://stackoverflow.com/questions/4231458/moving-the-cursor-in-java
 */

package mouse_controller;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class mouse_controller {	

	public static void main(String[] args) {
		  ServerSocket serverSocket = null;
		  Socket socket = null;
		  DataInputStream dataInputStream = null;
		  DataOutputStream dataOutputStream = null;
		  BufferedReader is = null;
		    String accel_x_str, accel_y_str, accel_z_str;
			String magnet_x_str, magnet_y_str, magnet_z_str = null;
			float accel_x, accel_y, accel_z;
			float magnet_x, magnet_y, magnet_z = 0;
			float mouse_x, mouse_y;
			float x_tracking_scaling = 0.0F;
			float x_tracking_offset = 3F, y_tracking_offset = -4F;			
			float mouse_x_scaling = 0.025F, mouse_y_scaling = 0.025F;
			float mouse_x_offset = 932F, mouse_y_offset = 533F;
			Boolean logging_active = false;
		
		// TODO Auto-generated method stub
		System.out.print("hello world\n\n");
		
		Robot robot;
		try {
		    // These coordinates are screen coordinates
		    int xCoord = 500;
		    int yCoord = 500;

		    // Move the cursor
		    robot = new Robot();
		    //robot.mouseMove(xCoord, yCoord);
 
		} catch (AWTException e) {
		} 
		
		try {
		   serverSocket = new ServerSocket(8888);
		   System.out.println("Listening :8888");
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		  
		  while(true){
		   try {
		    socket = serverSocket.accept();
		    dataInputStream = new DataInputStream(socket.getInputStream());
		    is = new BufferedReader(new InputStreamReader(dataInputStream));
		    dataOutputStream = new DataOutputStream(socket.getOutputStream());
		    if (logging_active) {
		    System.out.println("ip: " + socket.getInetAddress());
		    }
		    //System.out.println("message: " + dataInputStream.readUTF());
		    accel_x_str = is.readLine(); 
		    //System.out.println(accel_x_str);
		    accel_x = Float.valueOf(accel_x_str.split("\\.")[0].replaceAll("[^0-9-]", "")+"."+accel_x_str.split("\\.")[1].replaceAll("[^0-9]", ""));//is.readLine();//Float.valueOf(is.readLine().replaceAll("[^0-9.]", ""));//.replace('!', '+').replace(')', '+').replace('/', '+').replace('(', '+'));
		    accel_y_str = is.readLine(); 
		    accel_y = Float.valueOf(accel_y_str.split("\\.")[0].replaceAll("[^0-9]-", "")+"."+accel_y_str.split("\\.")[1].replaceAll("[^0-9]", ""));//		    accel_z = Float.valueOf(is.readLine().replace('!', '+'));
		    accel_z_str = is.readLine(); 
		    //System.out.println(accel_z_str);		    
		    accel_z = Float.valueOf(accel_z_str.split("\\.")[0].replaceAll("[^0-9]-", "")+"."+accel_z_str.split("\\.")[1].replaceAll("[^0-9]", ""));
		    magnet_x_str = is.readLine(); 
		    magnet_x = Float.valueOf(magnet_x_str.split("\\.")[0].replaceAll("[^0-9]-", "")+"."+magnet_x_str.split("\\.")[1].replaceAll("[^0-9]", ""));
		    magnet_y_str = is.readLine(); 
		    magnet_y = Float.valueOf(magnet_y_str.split("\\.")[0].replaceAll("[^0-9]-", "")+"."+magnet_y_str.split("\\.")[1].replaceAll("[^0-9]", ""));
		    magnet_z_str = is.readLine();
		    //if (magnet_z_str != null) {} else { 
		    if (magnet_z_str != null && magnet_z_str.split("\\.")[0] != (null) && magnet_z_str.split("\\.")[1] != (null)) {
		    magnet_z = Float.valueOf(magnet_z_str.split("\\.")[0].replaceAll("[^0-9]-", "")+"."+magnet_z_str.split("\\.")[1].replaceAll("[^0-9]", ""));
		    }
		    //magnet_x = Float.valueOf(is.readLine().replace('!', '+'));
		    //magnet_y = Float.valueOf(is.readLine().replace('!', '+'));
		    //magnet_z = Float.valueOf(is.readLine().replace('!', '+'));
		    x_tracking_scaling = (14F / 45F)*.75F;
		    mouse_x = mouse_x_offset+(1080F*(mouse_x_scaling*(1-((x_tracking_offset+magnet_y+(x_tracking_scaling*45F))/(x_tracking_scaling*90F)))));
		    mouse_y = mouse_y_offset+(1920F*(mouse_y_scaling*(y_tracking_offset+accel_z+9.81F)/19.62F));
		    
		    if (mouse_x > 1919) {
		    	mouse_x = 1919;
		    }
		    if (mouse_x < 1) {
		    	mouse_x = 1;
		    }
		    if (mouse_y > 1079) {
		    	mouse_y = 1079;
		    	
		    }
		    if (mouse_y < 1) {
		    	mouse_y = 1;
		    }
		    if (logging_active) {
		    System.out.println(accel_x+"\t"+accel_y+"\t"+accel_z+"\t"+magnet_x+"\t"+magnet_y+"\t"+magnet_z+"\tx:\t"+mouse_x+"\ty:\t"+mouse_y);		    
		    }
		    
		    robot = new Robot();
		    robot.mouseMove((int) mouse_x, (int) mouse_y);
		    
		    //Sleep to avoid too much input
		    Thread.sleep(10);                 //1000 milliseconds is one second.
		    
		    //dataOutputStream.writeUTF("Hello!");
		   } catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   } catch (AWTException e) {
		   } catch(InterruptedException ex) {
		        Thread.currentThread().interrupt();
		    }
		   finally{
		    if( socket!= null){
		     try {
		      socket.close();
		     } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		     }
		    }
		    
		    if( dataInputStream!= null){
		     try {
		      dataInputStream.close();
		     } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		     }
		    }
		    
		    if( dataOutputStream!= null){
		     try {
		      dataOutputStream.close();
		     } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		     }
		    }
		   }
		   
			/*try {
			    // These coordinates are screen coordinates
			    int xCoord = 500;
			    int yCoord = 500;

			    // Move the cursor
			    robot = new Robot();
			    robot.mouseMove(xCoord, yCoord);
			} catch (AWTException e) {
			}*/
		  }
	}

}
