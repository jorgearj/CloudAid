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
 * <code>AlternativesPanel</code> the  Pane to show  all alternatives 
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 26, 2003
 */
public class AlternativesPanel extends JPanel implements ActionListener{

  // ATTRIBUTS
  private Hierarchy h; // the decision Hierarchy
  private JAHP window; // the main window
  private AlternativesTable at; // the table to show Alternatives
  private JButton add; // a button to add an Alternative
  private JButton del;// a button to del an Alternative
  private JButton save; //button to save alternatives values

  /**
   * Creates a new  <code>AlternativePanel</code> instance.
   * @param the decision <code>Hierarchy</code>
   * @param the main <code>JAHP</code> window
   */
  public AlternativesPanel(Hierarchy h,JAHP window, String source, String dest) {
    super(new GridLayout(0,1));

    this.h=h;
    this.window=window;


    //HierarchyTree
    at =new AlternativesTable(h,window, source, dest);
    
    JScrollPane toppane = new JScrollPane(at);
    this.add("Center",toppane);


    // JButton to add and delete criteria
    JPanel alternativemodifypanel =new JPanel(new GridLayout(0,1));
    add=new JButton("Add Alternative");
    del=new JButton("Delete Alternative");
    save=new JButton("Save Alternatives");
    alternativemodifypanel.add(add);
    alternativemodifypanel.add(del);
    alternativemodifypanel.add(save);
    this.add("South",alternativemodifypanel);

    //add ActionListener to the buttons
    add.addActionListener(this);
    del.addActionListener(this);
    save.addActionListener(this);
  }


  /**
   * a <code>actionPerformed</code> method here.
   * @param the <code>ActionEvent</code>
   */
  public void actionPerformed(ActionEvent e){
    if (e.getSource()==add){
      //Systemout.println("Add criterium");      
      at.addNode();
    }
    else if(e.getSource()==del){
      //Systemout.println("Del criterium");
      at.delNode();
    }
    else{
    	System.out.println("Save alternatives to XMCDA");
    	at.saveAlternatives();
    }
  }

  /**
   * a <code>updateALTERNATIVE</code> method to update this panel.
   * @param the <code>ActionEvent</code>
   */
  public void updateALTERNATIVE(){
    //Systemout.println("AlternativesPanel update alt");
    at.updateALTERNATIVE();
  }

  

  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
    public Dimension getPreferredSize(){
      return new Dimension(150,200);
    }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
    return new Dimension(150,200);
}

}
