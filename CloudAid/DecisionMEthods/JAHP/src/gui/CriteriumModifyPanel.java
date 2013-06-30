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
 * <code>CriteriumModifyPanel</code> the Pane to modify the comparisons in a criterium of the  Decisionnal Hierarchy
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 26, 2003
 */
public class CriteriumModifyPanel extends JScrollPane{
  private Hierarchy h;
  private Criterium c;
  private int size;

  private CriteriumShowPanel csp;

  ScrollPaneLayout spl;
  private JAHP window ;

  /**
   * Creates a new  <code>CriteriumModifyPanel</code> instance.
   * @param Criterium c
   * @param Hierarchy h
   * @param CriteriumShowPanel csp
   */
  public CriteriumModifyPanel(Criterium c, Hierarchy h,CriteriumShowPanel csp,JAHP window) {
    super();
    this.c=c;
    this.h=h;
    this.csp=csp;
    this.window=window;
   
    this.spl=new ScrollPaneLayout();
    spl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    spl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.setLayout(spl); 
    
    JPanel content=new JPanel(new FlowLayout());
    (getViewport()).add(content);

    //new BorderLayout()

    if (c!=null){
    int n=c.getNb_sons();
    this.size=n;
    //System.out.println("NB SONS"+n);
    n=n*(n-1)/2;
    int k=0;
    //System.out.println("NB PairwiseComparison"+n);
    for(int i=0;i<c.getNb_sons();i++){
      for(int j=i+1; j<c.getNb_sons();j++){
	//System.out.println("i : "+i+"   j : "+j);	  
	  ComparisonPane current_pane;
	  if (k!=(n-1)) {current_pane= new ComparisonPane(c,i,j,false,csp,window);}//Paint label only for the last JSlider
	  else current_pane= new ComparisonPane(c,i,j,true,csp,window);
	  content.add(current_pane);
	  k++;
	}
      }
    }
  }

  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
    public Dimension getPreferredSize(){
      return new Dimension(100*(size-1)+500,400);
    }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
    return new Dimension(100*(size-1)+500,350);
}
}
