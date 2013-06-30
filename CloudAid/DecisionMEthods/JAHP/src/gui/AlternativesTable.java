// Graphical User Interface
package gui;

//imports
import javax.swing.*;          //This is the final package name.
//import com.sun.java.swing.*; //Used by JDK 1.2 Beta 4 and all
//Swing releases before Swing 1.1 Beta 3.
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import Jama.*;


// Abstract Data Type
import adt.*;


/**
 * <code>AlternativesTable</code> the custom  swing.Table
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version April 14, 2003
 */
public class AlternativesTable extends JTable implements ListSelectionListener{

  //ATTRIBUTS
  private Hierarchy h; // The decision Hierarchy
  private JAHP window; // the main JAHP window
  private AlternativesModel am; // the model
  private String SaveDirectory;
  private String filename;
  
  /**
   * Creates a new  <code>AlternativesTable</code> instance.
   * @param Hierarchy h
   * @param JAHP window the main window
   */
    public AlternativesTable(Hierarchy h, JAHP window, String source, String dest) {
        super();
	this.setPreferredScrollableViewportSize(new Dimension(150, 150));
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


	this.h=h;
	this.window=window;
	this.am=new AlternativesModel(h,this,window);
	this.setModel(am);
	this.filename = source;
	this.SaveDirectory = dest;
	

	//Listen for when the selection changes.
	this.setColumnSelectionAllowed(false); 
	this.setRowSelectionAllowed(false); 
	getSelectionModel().addListSelectionListener(this);


	// Look and Feel
        //DefaultCellRenderer renderer = new DefaultCellRenderer(red,new ColorRenderer(true));
        //setCellRenderer(renderer);
    }


  /**
   * the <code>valueChanged</code> overide method here.
   *
   * @param ListSelectionModel 
   * @see ListSelectionListener
   *
   */

  public void valueChanged(ListSelectionEvent e) {
    //Systemout.println("Alternatives valueChanged");    
    ListSelectionModel lsm = getSelectionModel();
    if (e.getValueIsAdjusting()) return;
    if (lsm.isSelectionEmpty()) {
      //no rows are selected
    } else {
      int selectedRow = lsm.getMinSelectionIndex();
      Alternative node = (Alternative) (h.getAlternatives()).get(selectedRow);
      // show the criterium in the Main Panel
      //Systemout.println("New Alternative value :"+node.toString());    
      //TO DO
      //System.out.println("value changed"+node.getName());
      window.updateSHOWALTERNATIVE(node);

    }
  }


  /**
   * the <code>delNode</code> method to delete a node in this table..
   *
   */  

  public void delNode() {
    //Systemout.println("Alternatives valueChanged");    
    ListSelectionModel lsm = getSelectionModel();
    if (lsm.isSelectionEmpty()) {
      //no rows are selected
    } else {
      int selectedRow = lsm.getMinSelectionIndex();
      Alternative node = (Alternative) (h.getAlternatives()).get(selectedRow);
      // show the criterium in the Main Panel
      //Systemout.println("New Alternative value :"+node.toString());    
      am.removeRow(node);
      
    }
  }

  /**
   * the <code>addNode</code> method to add a node in this table..
   *
   */  
  public void addNode() {
    am.addRow();
  }
  
  /**
   * the <code>addNode</code> method to add a node in this table..
   *
   */  
  public void saveAlternatives() {
	  System.out.println("get the alternatives values");
	  
	  window.dispose();
	  postResults(h);
	  
	  
	  
  }

  /**
   * the <code>updateALTERNATIVE</code> method to update this table.
   *
   */  
  public void updateALTERNATIVE(){
    //Systemout.println("AlternativesTable update alt");
    this.am=new AlternativesModel(h,this,window);
    this.setModel(am);
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
  
  private void postResults(Hierarchy h){
		XMCDAConverter converter = new XMCDAConverter(); //the xmcda converter
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		HashMap<String, String> params;
		
		
		XMCDA xmcda = converter.getFromFile(this.filename);
		
		params = converter.getMethodParameters(xmcda);
		String time = params.get("FileTimestamp");
		String compId = params.get("ComponentID");
		System.out.println(time);
		System.out.println(compId);
		
		files.add(converter.attachCompTimestamp(time, compId));
		files.add(converter.createPerformance(h));
		
		converter.export(converter.append(files), generateFileName(time));
	}
	
	private String generateFileName(String time){
		
		return "/XMCDA_Decision_" + time + ".xml"; 
		
	}

}
