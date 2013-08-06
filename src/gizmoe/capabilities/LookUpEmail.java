package gizmoe.capabilities;

import gizmoe.demoui.DemoUi;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JTextArea;


public class LookUpEmail extends DemoBaseCapability{

	private final String tag = "LookUpEmail, thread"+this.hashCode()+":: ";
	private ConcurrentHashMap<String, Object> ioMap;
	public void run() {
		String name = null, email = null;
		Boolean found = false;
		if(ioMap.containsKey("name")){
			/*
			 * Input Section
			 */
			name = (String) ioMap.get("name");
			form.setArea(tag+"Trying to see if "+name+" is an advisor in the database");
//			System.out.println(tag+"Received input name = "+name);
			
			/*
			 * Operation Section
			 */
			InputStream stream = LookUpEmail.class.getResourceAsStream("lookup.txt");
//			if(stream == null){
//				outputs.put("noDBConn", true);
//			}
			Scanner in = new Scanner(stream);
			while(in.hasNext()){
				String[] line = in.nextLine().split(";");
				String candidate = line[0];
				if(candidate.equalsIgnoreCase(name)){
					email = line[1];
					found = true;
					break;
				}
			}
		}else{
			System.err.println(tag+"Input name not found");
		}
		
		//Simulate operation
		if(seconds > 0){
			try {
				Thread.sleep((seconds+1) * 1000);
			} catch (Exception e) {
				return;
			}
		}
		/*
		 * Output Section
		 */
		ioMap.clear();
		if(found == false){
//			System.out.println(tag+"Sending output emailNotFound = "+!found);
			ioMap.put("emailNotFound",!found);		
		}else{
			form.setArea(tag+name+" is a known advisor with email id "+email);
//			System.out.println(tag+"Sending output email = "+email);
			ioMap.put("email",email);		
		}
		return;
	}
	
	public LookUpEmail(ConcurrentHashMap<String, Object> inputs, DemoUi area) {
		this.ioMap = inputs;
                this.form = area;
	}

}
