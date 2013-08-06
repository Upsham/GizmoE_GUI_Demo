package gizmoe.capabilities;

import gizmoe.demoui.DemoUi;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class SkypeWithPerson extends DemoBaseCapability{

	private final String tag = "SkypeWithPerson, thread"+this.hashCode()+":: ";
	ConcurrentHashMap<String, Object> ioMap;
	public SkypeWithPerson(ConcurrentHashMap<String, Object> inputs , DemoUi area) {
		this.ioMap = inputs;
                this.form = area;
	}
        
        public JFrame skype(String args){
            JFrame frame = new JFrame("Skype");
                    frame.setSize(330, 330);
                    ImageIcon imageIcon = new ImageIcon(getClass().getResource("skype.png"));
                    JLabel label = new JLabel("Skyping with "+args);
                    frame.add(label);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setIcon(imageIcon);
                    label.setHorizontalTextPosition(JLabel.CENTER);
                    label.setVerticalTextPosition(JLabel.BOTTOM);
                    label.setVisible(true);
                    frame.setLocation(0, 0);
                    frame.setVisible(true);
                    return frame;
        }
	public void run() {
		String name = null;
		if(ioMap.containsKey("name")){
			/*
			 * Input Section
			 */
			name = (String) ioMap.get("name");
//			System.out.println(tag+"Received input name = "+name);
                        form.setArea(tag+"Now Skyping with "+name);
                        JFrame frame = skype(name);
			//Simulate operation
			if(seconds > 0){
				try {
					Thread.sleep(seconds * 1000);
				} catch (Exception e) {
                                    frame.dispose();
                                        form.setArea(tag+"Killing Skype Session with "+name);
					return;
				}
			}else{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
                                        frame.dispose();
                                        form.setArea(tag+"Killing Skype Session with "+name);
					return;
				}
			}
                        frame.dispose();

			/*
			 * Operation Section
			 */
			//Go to location sim
		}else{
			System.err.println(tag+"Input name not found");
		}
//		System.out.println(tag+"Skyped with = "+name);
                form.setArea(tag+"Finished skyping with "+name);
                ioMap.clear();
	}

}
