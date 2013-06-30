// Abstract Data Type
package adt ;


//imports
import javax.swing.*;          //This is the final package name.
//import com.sun.java.swing.*; //Used by JDK 1.2 Beta 4 and all
//Swing releases before Swing 1.1 Beta 3.
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import Jama.*;


/**
 * Alternative class
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version Fev 13, 2003
 * @version March 26, 2003 final one
 * @version July 21, 2003
 */
public class Alternative extends Activity implements Serializable,Cloneable{
  
  //ATTRIBUTS

  //useful to have always a new criterium with a new name 
 private static int newAlternativeSuffix = 1;
 private static String id;
 private HashMap<String, String> attributes;
 private HashMap<String, String> preferences;


  /**
   * Creates a new  <code>Alternative</code> instance.
   * 
   */
  public Alternative(){
    super();
    this.setId("alternative"+newAlternativeSuffix++);
    this.setName("My alternative"+newAlternativeSuffix++);
    try{
      this.setUrl(new URL("http://messel.emse.fr/~mmorge"));}
    catch(MalformedURLException e){
      System.err.println("Error : MalformedURLException"+e);
      System.exit(-1);
    }

  }
  
  

  public HashMap<String, String> getAttributes() {
	return attributes;
}



public void setAttributes(HashMap<String, String> attributes) {
	this.attributes = attributes;
}



public HashMap<String, String> getPreferences() {
	return preferences;
}



public void setPreferences(HashMap<String, String> preferences) {
	this.preferences = preferences;
}



/**
   * <code>print</code> Returns a string representation of this Alternative, containing the String representation of each element.
   *
   * @return a <code>String</code> value
   */
  public String print(){
    String s=new String();
    s=s+"Name of this alternative                  : "+getName()+"\n";
    s=s+"URL of this alternative                   : "+getUrl().toString()+"\n";
    return s;
  }


/**
   * <code>equals</code> method here.
   * @return boolean
   * @param Alternative alt which should be compared 
   *
   */
    public boolean equals(Alternative alt){
    return name.equals(alt.getName());
    }


  //************************************
  //
  //Method to calculate solutions
  // 
  // 
  //************************************* 

  /**
   * <code>S</code> method here.
   * @param Criterium c :  the index of the alternative
   * @param Hierarchy h :  the reference hierarchy 
   * @return double  : the influence of the criterium on the alternative according to the hierarchy
   * @exception IllegalArgumentException
   */
  public double S(Criterium c,Hierarchy h){
    if (c.equals(h.getGoal())) 
      throw new IllegalArgumentException("the influence of the goal on the alternative according to the hierarchy can not be calculated");
    int index_alt=0;
      Vector alts=h.getAlternatives();
    for(int i=0;i<h.getNb_alternatives();i++){
      Alternative alt=(Alternative) alts.get(i);      
      if (this.equals(alt)) index_alt=i;
    }
    return(c.Jstar(index_alt)*h.V(c)/h.Pi(index_alt)  );
  }
  
  public String printData(){
	  String data;
	  String html = "<html>";
	  String endHtml = "</html";
	  String newLine = "<br>";
	  String header = "Alternative Data: ";
	  
	  
	  data = html + header + this.name + newLine;
	  if(this.attributes != null){
		  Iterator it = this.attributes.entrySet().iterator();
		  data = data + newLine + "Attributes:" + newLine;
		  while (it.hasNext()) {
			  Map.Entry pairs = (Map.Entry)it.next();
			  data = data + pairs.getKey() + " = " + pairs.getValue() + " ("+this.preferences.get(pairs.getKey())+")"+ newLine;
		  }
	  }

	  return data+endHtml;
  }

  /**
   * <code>main</code> method to test this class.
   *
   * @param args[] a <code>String</code> value : command line
   */
  public static void main(String args[]){
    Alternative a1=new Alternative();
    Alternative a2=new Alternative();
    //Systemout.println("a1 : "+a1.print());
    //Systemout.println("a2 : "+a2.print());
    
  }
}


  

