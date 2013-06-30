package adt;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import gui.JAHP;

public class Main {
	
	private static void test(String filename, String dest){
		LoadXMCDA load = new LoadXMCDA();
	    Hierarchy h =new Hierarchy();
	    h=load.getHierarchy(filename);
	    JAHP mainFrame = new JAHP(h, filename, dest);
	    mainFrame.pack();
	    mainFrame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
		String path = location.getFile();
        File root = new File(path);
        path = root.getParentFile().getParent();
        System.out.println(path);*/
		
		ArrayList<File> folders = new ArrayList<File>();
		File decisionDir = new File("./Decision");
		File to_DecideDir = new File("./TO_Decide");
		File decisionAHPDir = new File("./Decision/AHP");
		File to_DecideAHPDir = new File("./TO_Decide/AHP");
		folders.add(decisionDir);
		folders.add(to_DecideDir);
		folders.add(decisionAHPDir);
		folders.add(to_DecideAHPDir);
		
        
		
		
		for(File folder : folders){
			System.out.println(folder.getAbsolutePath());
		  // if the directory does not exist, create it
		  if (!folder.exists()){
		    System.out.println("creating directory: " + folder.getName());
		    boolean result = folder.mkdir();  
		    if(result){    
		       System.out.println("DIR created");  
		     }
		  }else{
			  System.out.println("Folder exists"); 
		  }
		}
		
		String source = to_DecideAHPDir.getAbsolutePath();
		String dest = decisionAHPDir.getAbsolutePath();
		
		FileChecker checker = new FileChecker(source, dest);
		checker.init();
		//test("/Users/Jorge/Desktop/SMS/CODE/SMS_v0.1/TO_Decide/AHP/XMCDA_To_Decide_1361992099570.xml");

	}

}
