package gizmoe.capabilities;

import gizmoe.demoui.DemoUi;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class GotoLocation extends DemoBaseCapability{

	private static class MyImage extends JFrame {
	    
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		static int xPixel = 20/2;
	    static int yPixel = 40/2; 
	    
	    Image myImage, home, door, fire, offScreenImage;
	    Graphics offScreenGraphics;
	    String location;
	    public MyImage(String loc) {
	        location = loc;
	        xPixel = 20/2;
		    yPixel = 40/2;
		    try {
	          myImage = ImageIO.read(getClass().getResource("cobot.png"));
	          home = ImageIO.read(getClass().getResource("home.png"));
	          door = ImageIO.read(getClass().getResource("door.png"));
	          fire = ImageIO.read(getClass().getResource("fire.png"));
	        } 
	        catch(Exception e) {}
	        setSize(400,300);
	        setVisible(true);
	        if(!location.equals("Rm282")){
	        	try {
	        		Thread.sleep(1000);
	        	} catch (InterruptedException e) {
	        		e.printStackTrace();
	        	}
	        }
	        moveImage();
	        
	    }
	    
	    public void update(Graphics g) {
	        paint(g);
	    }
	    
	    public void paint(Graphics g) {
	        
	        int width  = getWidth();
	        int height = getHeight();
	        
	        if (offScreenImage == null) {
	            offScreenImage    = createImage(width, height);
	            offScreenGraphics = offScreenImage.getGraphics();
	        }
	        
	        // clear the off screen image
	        offScreenGraphics.clearRect(0, 0, width + 1, height + 1);
	        
	        // draw your image off screen
	        offScreenGraphics.drawImage(myImage, xPixel, yPixel, this);	        
	        if(location.equals("Rm282")){
		        offScreenGraphics.drawImage(fire, xPixel+65/2, yPixel+380/2, this);
	        	offScreenGraphics.drawImage(home, 590/2, 315/2, this);
	        }else{
	        	offScreenGraphics.drawImage(door, 500/2, 0, this);
	        	offScreenGraphics.drawString(location, (650-20)/2, 300/2);
	        }
	        // show the off screen image
	        g.drawImage(offScreenImage, 0, 0, this);
	        	   

	    }
	    
	    void moveImage() {
	    	if(location.equals("Rm282")){
	    		for ( int i = 0 ; i < 590/2 ; i++ ){

	    			xPixel +=1;
	    			yPixel = 40/2;
	    			repaint();

	    			// then sleep for a bit for your animation
	    			try { Thread.sleep(10); }   /* this will pause for 15 milliseconds */
	    			catch (InterruptedException e) { 
	    				this.dispose();
	    				return;	           
	    			}

	    		}
	    	}else{
	    		for ( int i = 0 ; i < 500/2 ; i++ ){

	    			xPixel +=1;
	    			yPixel = 40/2;
	    			repaint();

	    			// then sleep for a bit for your animation
	    			try { Thread.sleep(20); }   /* this will pause for 10 milliseconds */
	    			catch (InterruptedException e) { 
	    				this.dispose();
	    				return;	           
	    			}

	    		}
	    	}
	    }

	    public static void main(String location){
	    
	        MyImage me = new MyImage(location);	
	        me.dispose();
	    }
	    
	}
	
	private final String tag = "GotoLocation, thread"+this.hashCode()+":: ";
	ConcurrentHashMap<String, Object> ioMap;
	@Override
	public void run() {
		String location = null;
		if(ioMap.containsKey("location")){
			/*
			 * Input Section
			 */
			location = (String) ioMap.get("location");
//			System.out.println(tag+"Received input location = "+location);
                        form.setArea(tag+"Cobot3 is moving to "+location);
			MyImage.main(location);
			/*
			 * Operation Section
			 */
			//Go to location sim
		}else{
			System.err.println(tag+"Input location not found");
		}
                
                form.setArea(tag+"Cobot3 is now at "+location);

//		System.out.println(tag+"At location = "+location);
		ioMap.clear();
	}
	
	public GotoLocation(ConcurrentHashMap<String, Object> inputs, DemoUi area){
		this.ioMap = inputs;
                this.form = area;
	}

}
