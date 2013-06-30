// Graphical User Interface
package gui;

//imports
import javax.swing.*;          //This is the final package name.
//import com.sun.java.swing.*; //Used by JDK 1.2 Beta 4 and all
//Swing releases before Swing 1.1 Beta 3.
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import Jama.*;


// needs Data Type
import adt.*;

/**
 * <code>AlternativePanel</code> the  Pane to show  the best alternative  
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 26, 2003
 */
public class AlternativePanel extends JPanel implements DocumentListener{
  private Hierarchy h;
  private Alternative alt;
  private JAHP window;

  private JTextArea tf_comment;
  private PlainDocument whatsup;

  /**
   * Creates a new  <code>AlternativePanel</code> instance.
   * @param the Decision <code>Hierarchy</code> 
   * @param the <code>Alternative</code> to show
   * @param the main <code>JAHP</code> window
   */
  public AlternativePanel(Hierarchy h,Alternative alt,JAHP window) {
    super(new GridLayout(0,1));    
    this.h=h;
    this.window=window;
    this.alt=alt;
    if (alt!=null){
    this.add(new JLabel("Comment :"));
    whatsup=new PlainDocument();
    try{ whatsup.insertString(0,alt.getComment(), null); }
    catch (BadLocationException e) {System.err.println("Bad Location Exception "+e);}
    tf_comment=new JTextArea(whatsup,null,20,20);
    tf_comment.setCaretPosition(tf_comment.getText().length());
    JScrollPane scrollPane = new JScrollPane(tf_comment);
    whatsup.addDocumentListener(this);
    this.add(scrollPane);
    }  
  }

  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
    public Dimension getPreferredSize(){
      return new Dimension(800,100);
    }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
    return new Dimension(500,100);
}


  /**
   * <code>updateComment</code> method invoked when comment is changed
   *
   * @param e a <code>DocumentEvent</code> value
   */
  public void updateComment(DocumentEvent e) {
    //JTextField source = (JTextField)e.getSource();
    String text=new String() ;
    try {text=whatsup.getText(0,whatsup.getLength());}
    catch (BadLocationException f) {System.out.println("Bad Location Exception "+f);}
    alt.setComment(text);
    
  }

  /**Gives notification that there was an insert into the document.*/
  public void insertUpdate(DocumentEvent e){    
    updateComment(e);
  }

  /**Gives notification that a portion of the document has been removed.*/
  public void removeUpdate(DocumentEvent e){
    updateComment(e);
  }

  /** Gives notification that an attribute or set of attributes changed. */
  public void changedUpdate(DocumentEvent e){
    updateComment(e);
  }
  
}

