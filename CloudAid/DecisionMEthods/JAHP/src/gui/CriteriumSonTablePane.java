// Graphical User Interface
package gui;

//imports
import javax.swing.*;          //This is the final package name.
//import com.sun.java.swing.*; //Used by JDK 1.2 Beta 4 and all
//Swing releases before Swing 1.1 Beta 3.
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.JTable;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import Jama.*;


// Abstract Data Type
import adt.*;


/**
 * <code>CriteriumSonTablePane</code> the  Pane to show  sons of a criterium  and their weight in a JTable
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 19, 2003 initial version
 * @version March 26, 2003 final version
 */
public class CriteriumSonTablePane extends JPanel {

  // attributes
  private Hierarchy h;
  private Criterium c;

  // Container
  private JTable table;
  private JTableHeader tableheader;

  /**
   * Method to <code>update</code> this Panel if the criterium is changed  
   * 
   */
  public void update(){
    table.setModel(new CriteriumTableModel(c));
  }


  /**
   * Creates a new  <code>CriteriumSonTablePan</code> instance.
   * @param Criterium c
   * @param Hierarchy h
   */
  public CriteriumSonTablePane(Criterium c, Hierarchy h) {
    super(new GridLayout(2,1));
    this.c=c;
    this.h=h;
    CriteriumTableModel myModel = new CriteriumTableModel(c);
    table=new JTable(myModel);
    tableheader= table.getTableHeader();
    this.add(tableheader);
    this.add(table);

  }

  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
    public Dimension getPreferredSize(){
      return new Dimension(400,200);
    }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
    return new Dimension(300,100);
}

  // class to implement the   Table Model
  class CriteriumTableModel extends AbstractTableModel {
    //ATTRIBUTS
    private Vector columnNames ; // mames of columns
    private Vector data; // Data

  /**
   * <code>getColumnCount</code> overide method here.
   *
   * @return int the number of columns
   */
    public int getColumnCount() {
      return columnNames.size();
    }
    
  /**
   * <code>getRowCount</code> overide method here.
   *
   * @return int the number of rows
   */
    public int getRowCount() {
      return 1;
    }
    
  /**
   * <code>getColumnName</code> overide method here.
   *
   * @param int the index of the column
   * @return String the name of the column
   */
    public String getColumnName(int col) {
      return (String) columnNames.get(col);
    }

  /**
   * <code>getValueAt</code> overide method here.
   * @param int row index
   * @param int column index
   * @return int the number of columns
   */
    public Object getValueAt(int row, int col) {
      String value_s =new String();
      value_s= ((Double) data.get(col)).toString();
      if(value_s.length()>6){
	value_s = value_s.substring(0,5);}	    
      return (Object) value_s;

    }
    
  /**
   * Creates a new  <code>CriteriumTableModel</code> instance.
   * @param the <code>Criterium</code>
   */
    public CriteriumTableModel(Criterium c){
      columnNames= new Vector();
      data =new Vector();
      Vector sons= c.getSons();
      
      for(int i=0;i<c.getNb_sons();i++){
	if (c.isLl()){//sons are alternatives
	  Alternative current_alt=(Alternative)sons.get(i);
	  columnNames.add(current_alt.getName());
	  data.add(new Double((c.getP()).getWeight(i)));
	}
	else{//sons are criteria
	  Criterium current_subcriterium=(Criterium)sons.get(i);
	  columnNames.add(current_subcriterium.getName());
	  data.add(new Double ((c.getP()).getWeight(i)));
	}
	//System.out.println("ColumnName i :"+i+"     name ="+getColumnName(i));
	
      }
    }      
    

  }

}
