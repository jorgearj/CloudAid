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
 * <code>CriteriaPanel</code> is the panel with HierarchyTree + add/del criterium
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 26, 2003 initial version
 */
public class CriteriaPanel extends JPanel implements ActionListener {

  //ATTRIBUTS
  private Hierarchy h; // The Decision hierarchy
  private HierarchyTree ht; // the Jtree 
  private JButton add; // a button to add a criterium
  private JButton del; // a button to delete a criterium
  private JAHP window;// the main JAHP window

  /**
   * Creates a new  <code>CriteriaPanel</code> instance.
   * @param the decision <code>Hierarchy</code>
   * @param the main <code>JAHP</code> window
   */
  public CriteriaPanel(Hierarchy h,JAHP window) {
    super(new BorderLayout());
    this.window=window;
    //HierarchyTree
    ht =new HierarchyTree(h,window);
    this.add("Center",ht);

    // JButton to add and delete criteria
    JPanel hierarchymodifypanel =new JPanel(new GridLayout(0,1));
    add=new JButton("Add Criterium");
    del=new JButton("Delete Criterium");
    hierarchymodifypanel.add(add);
    hierarchymodifypanel.add(del);
    add.addActionListener(this);
    del.addActionListener(this);

    this.add("South",hierarchymodifypanel);
        
  }
  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getpreferredSize(){
    return new Dimension(150,400);  
  }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
    return new Dimension(150,300);  
  }

  /**
   * <code>actionPerformed</code> method invoked when an Alternative is add/dell
   *
   * @param e a <code>ActionEvent</code> value
   */
  public void actionPerformed(ActionEvent e){
    if (e.getSource()==add){
      //Systemout.println("Add criterium");      
      ht.addNode();
      window.updateALTERNATIVE();
    }
    else{
      //Systemout.println("Del criterium");
      ht.delNode();
      window.updateALTERNATIVE();
    }
  }
}
