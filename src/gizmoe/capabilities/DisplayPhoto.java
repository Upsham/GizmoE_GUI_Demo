package gizmoe.capabilities;

import gizmoe.demoui.DemoUi;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class DisplayPhoto extends DemoBaseCapability{

	private static class ShowImage extends Panel{
		private static final long serialVersionUID = 1L;

		  private BufferedImage image;

		  public ShowImage(String filename) {
		    try {
		      image = ImageIO.read(new File(filename));
		    } catch (IOException ie) {
		      ie.printStackTrace();
		    }
		  }

		  public void paint(Graphics g) {
		    g.drawImage(image, 0, 0, null);
		  }

		  static public boolean doShow(String args, long secs) throws Exception {
		    JFrame frame = new JFrame("Photo");
                    frame.setSize(350, 350);
		    Panel panel = new ShowImage(args);
                    JLabel label = new JLabel();
                    ImageIcon myImgIcon = new ImageIcon();
                    JLabel imageLbl = new JLabel(myImgIcon);
                    frame.add(imageLbl, BorderLayout.CENTER);
		    frame.getContentPane().add(panel);
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
                    Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
                    int x = (int) rect.getMaxX() - frame.getWidth();
                    int y = 0;
                    frame.setLocation(x, y);
		    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    frame.setVisible(true);
		    //Simulate operation
			if(secs > 0){
				try {
					Thread.sleep(secs * 1000);
				} catch (Exception e) {
				    frame.dispose();
					return true;
				}
			}else{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					frame.dispose();
					return true;
				}
			}
		    frame.dispose();
                    return false;
		  }
	}
	private final String tag = "DisplayPhoto, thread"+this.hashCode()+":: ";
	ConcurrentHashMap<String, Object> ioMap;

	public void run() {
		String handle = null;
                Boolean killed = false;
		if(ioMap.containsKey("queryHandle")){
			/*
			 * Input Section
			 */
			handle = (String) ioMap.get("queryHandle");
                        form.setArea(tag+"Displaying "+handle+"'s photo now");
			//System.out.println(tag+"Received input queryHandle = "+handle);
			
			/*
			 * Operation Section
			 */
			try {
				killed = ShowImage.doShow(handle, seconds);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else{
			System.err.println(tag+"Input queryHandle not found");
		}
                if(killed == false){
                    form.setArea(tag+"Done displaying "+handle+"'s photo");
                }

		ioMap.clear();
}
	
	public DisplayPhoto(ConcurrentHashMap<String, Object> inputs, DemoUi ar) {
		this.ioMap = inputs;
                this.form = ar;
	}

}
