package saw;

import java.io.File;
import java.util.ArrayList;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import xmcda.XMCDAConverter;

public class SawStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<File> folders = new ArrayList<File>();
		File decisionDir = new File("./Decision");
		File to_DecideDir = new File("./TO_Decide");
		File decisionAHPDir = new File("./Decision/SAW");
		File to_DecideAHPDir = new File("./TO_Decide/SAW");
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
		
	}

}
