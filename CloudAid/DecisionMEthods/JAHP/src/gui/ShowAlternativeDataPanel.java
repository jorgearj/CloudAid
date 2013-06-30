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
 * <code>CriteriumPanel</code> the  Pane to modify the comparisons in a criterium of the  Decisionnal Hierarchy
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 9, 2003
 */
public class ShowAlternativeDataPanel extends JPanel{

  //ATTRIBUTS
  private Hierarchy h; // the decision Hierarchy
  private Alternative alt; // the alternative to show
  private JAHP window; // the main JAHP window
  private JLabel data; // inconsistency ration view


  /**
   * <code>updateALTERNATIVE</code>  method to update the Panel and subpanel
   * @param Criterium c to show
   */   
  public void update(Alternative a){
    this.alt=a;
    this.data.setText(a.printData());

  }

  /**
   * Creates a new  <code>CriteriumPanel</code> instance.
   * @param Criterium c
   * @param Hierarchy h
   * @param JAHP main window   
   */
  public ShowAlternativeDataPanel(Alternative a, Hierarchy h, JAHP window) {
    super(new BorderLayout());
    this.alt=a;
    this.h=h;
    this.data = new JLabel();

    this.window=window;
    this.add("North",new JLabel("Alternative data:"));
    this.add("North",data);
    }


  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
    public Dimension getPreferredSize(){
      return new Dimension(200,500);
    }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
    return new Dimension(150,400);
  }
}
