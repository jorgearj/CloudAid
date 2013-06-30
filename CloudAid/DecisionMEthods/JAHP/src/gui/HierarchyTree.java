// Graphical User Interface
package gui;

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
 * <code>HierarchyTree</code> the custom  swing.Tree
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 8, 2003
 * @version March 26, 2003
 */
public class HierarchyTree extends JTree implements TreeSelectionListener{

  //ATTRIBUTS
  private Hierarchy h;
  private JAHP window;
  private HierarchyModel hm;


  /**
   * Creates a new  <code>HierarchyTree</code> instance.
   * @param the decision <code>Hierarchy</code> 
   * @param the main <code>JAHP</code> window
   */
    public HierarchyTree(Hierarchy h,JAHP window) {
        super();
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	this.setEditable(true);

	this.h=h;
	this.window=window;
	this.hm=new HierarchyModel(h,this,window);
	this.setModel(hm);

	//Listen for when the selection changes.
	this.addTreeSelectionListener(this);

	// Look and Feel
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        Icon personIcon = null;
        renderer.setLeafIcon(personIcon);//Icon
        renderer.setClosedIcon(personIcon);//Icon
        renderer.setOpenIcon(personIcon);//Icon
        setCellRenderer(renderer);
    }


  ////////////////////////Events
  
  /**
   * <code>valueChanged</code> method invoked when url a new element in JTree is selected
   *
   * @param e a <code>ActionEvent</code> value
   */
  public void valueChanged(TreeSelectionEvent e) {
    //Systemout.println("HierarchyTree valueChanged");    
    Criterium node = (Criterium) getLastSelectedPathComponent();
    if (node == null) return;	      	      
    // show the criterium in the Main Panel
        //Systemout.println("New Hierarchy value :"+node.toString());    
    window.updateSHOWCRITERIUM(node);
    //System.out.println("value changed"+node.getName());
  }

  /*
   * Delete a node in the JTree
   */
  public void delNode() {
    Criterium currentNode = null;
    Criterium parentNode = null;
    TreePath currentSelection = this.getSelectionPath();
    if (currentSelection == null) {
      //Systemout.println("parentPatth null");
    } else {      
      currentNode= (Criterium) currentSelection.getLastPathComponent();
      //parentNode= (Criterium) (currentNode.getFather());
      if (!currentNode.isGoal()){
	hm.removeNodeFromParent(currentNode);

      }      
    }
  }

  /*
   * Add a node in the JTree
   */
  public void addNode() {
    Criterium parentNode = null;
    Criterium childNode = null;
    TreePath parentPath = this.getSelectionPath();
    if (parentPath == null) {
      parentNode = h.getGoal();
    } 
    else {
      parentNode = (Criterium) parentPath.getLastPathComponent();
    }

    childNode=hm.addPath(parentNode);
    this.scrollPathToVisible(parentPath.pathByAddingChild(childNode));
  }
  
  // NOT YET IMPLEMENTED
  public void addNode(Criterium parent,boolean shouldBeVisible) {
  
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

}
