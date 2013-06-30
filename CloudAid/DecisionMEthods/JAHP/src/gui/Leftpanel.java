// Graphical User Interface
package gui;

//imports
import javax.swing.*;          //This is the final package name.
//import com.sun.java.swing.*; //Used by JDK 1.2 Beta 4 and all
//Swing releases before Swing 1.1 Beta 3.
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import Jama.*;


// Abstract Data Type
import adt.*;


/**
 * <code>Leftpanel</code> is the panel with HierarchyTree, AlternativeList and button to add/del Alternatives/Criterium
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 26, 2003 initial version
 */
public class Leftpanel extends JPanel {

  //ATTRIBUTS
  private Hierarchy h;
  private JAHP window;
  private CriteriaPanel csp;
  private AlternativesPanel asp;
  private ShowAlternativeDataPanel sadp;

  JSplitPane jsp2;

  public Leftpanel(Hierarchy h,CriteriaPanel csp, AlternativesPanel asp,JAHP window, ShowAlternativeDataPanel sadp) {
    super(new BorderLayout());
    this.csp=csp;
    this.asp=asp;
    this.window=window;
    this.sadp = sadp;
    this.add("Center",csp);
    this.add("South",asp); 
    this.add(sadp);
 
    jsp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,csp,sadp);
    jsp2.setOneTouchExpandable(true);
    jsp2.setDividerLocation(150);
    this.add(jsp2);
    
    
  }


  public Dimension getpreferredSize(){
    return new Dimension(400,600);  
  }

  public Dimension getMinimumSize(){
    return new Dimension(350,500);  
  }


  public void updateSHOWALTERNATIVE(Alternative alt){
	  this.sadp.update(alt);
  }
  
  public void updateALTERNATIVE(){
    //Systemout.println("Leftpanel update alt");
    //this.remove(asp);
    //asp=new AlternativesPanel(h,window);
    //this.add("South",asp);
    //return asp;
    asp.updateALTERNATIVE();
  }
}
