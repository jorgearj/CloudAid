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
 * <code>JAHP</code> the main JFrame to show and modify a Decisionnal Hierarchy
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 8, 2003 initial idea
 * @version March 26, 2003
 */
public class JAHP extends JFrame implements ActionListener{

  JMenu mFile,mHelp; // Menus
  JMenuItem miLoad,miSave,miQuit,miAbout; //MenuItems
  File home; //home directory
  File home_icons; // directory where icons could be found
  File home_example; //directory where examples could be found 
  File default_file; //default file loaded
  File file_mail; //mail icon
  File file_edit; //mail icon
  File imageFile1; //photo author

  // data
  private Hierarchy h;
  private Criterium current_criterium;
  private Alternative current_alternative;
  private String filename;
  private String dest;
  // Panels
  JSplitPane jsp;
  JSplitPane jsp2;

  
  private Rightpanel rp;
  private AlternativesPanel asp;
  private AlternativePanel ap;

  
  private Leftpanel lp;
  private CriteriaPanel csp;
  private CriteriumPanel cp;
  private ShowAlternativeDataPanel sadp;

  
  /*
   *Method to show a new Criterium
   */
  public void updateSHOWCRITERIUM(Criterium c){
    current_criterium=c;
    jsp.remove(rp);
    rp.updateSHOWCRITERIUM(c);
    jsp.setRightComponent(rp);
  }

  /*
   *Method to show a new Alternative
   */
  public void updateSHOWALTERNATIVE(Alternative alt){
    current_alternative=alt;
    jsp.remove(rp);
    rp.updateSHOWALTERNATIVE(alt);
    lp.updateSHOWALTERNATIVE(alt);
    jsp.setRightComponent(rp);
  }

  /*
   *Method to show when a new Alternative is added
   */
  public void updateafteraddALTERNATIVE(Alternative alt){
    jsp.remove(rp);
    rp.updateafteraddALTERNATIVE(alt);
    jsp.setRightComponent(rp);
    if (current_criterium.isLl()) updateSHOWCRITERIUM(current_criterium);
  }

  /*
   *Method to show when a new Alternative is deleted
   */  
  public void updateafterdelALTERNATIVE(){
    jsp.remove(rp);
    rp.updateafterdelALTERNATIVE();
    jsp.setRightComponent(rp);
    if (current_criterium.isLl()) updateSHOWCRITERIUM(current_criterium);
  }

  /*
   *Method to show when a new Criterium is added
   */
  public void updateafteraddCRITERIUM(Criterium c){
    current_criterium=c;
    jsp.remove(rp);
    rp.updateafteraddCRITERIUM(c);
    jsp.setRightComponent(rp);
  }

  /*
   *Method to show when a new Criterium is deleted
   */
  public void updateafterdelCRITERIUM(){
    jsp.remove(rp);
    rp.updateafterdelCRITERIUM();
    jsp.setRightComponent(rp);
  }

  /*
   *Method to show a new Alternative
   */
  public void updateALTERNATIVE(){
    //jsp.remove(lp);
    lp.updateALTERNATIVE();
    //jsp.setLeftComponent(lp);
    //Systemout.println("JAHP update alt");
   
  }


  /*
   *Method to show when a  Criterium is modified
   */
  public void updateaftermodifyCRITERIUM(){
    updateSHOWCRITERIUM(current_criterium);
  }

  /*
   *Method to show when an Alternative is modified
   */
  public void updateaftermodifyALTERNATIVE(){
    if (current_criterium.isLl()) updateSHOWCRITERIUM(current_criterium);
  }


  /**
   * Creates a new  <code>JAHP</code> instance.
   * @param the Decision <code>Hierarchy</code> 
   */
  public JAHP(Hierarchy h, String source, String dest) {
    super("Java Analytic Hierarchy Process");
    
    this.dest = dest;
    // window 
    addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.exit(0);
	}
      });
	
    this.h=h;
    this.filename = source;

    current_alternative=(Alternative) (h.getAlternatives()).get(0);
    current_criterium=h.getGoal();


    //File DATA
    home= new File("..");// export JAHP_PATH
    home_icons=new File(home,"icons");
    home_example=new File(home,"examples");
    default_file= new File(home_example,"essai.ahp");
    file_mail=new File(home_icons,"ComposeMail24.gif");
    file_edit=new File(home_icons,"Edit24.gif");
    imageFile1=new File(home_icons,"morge.png");

    // Make Menu
    JMenuBar menuBar = new JMenuBar();
    // File Menum
    mFile = new JMenu("File");
    miLoad = new JMenuItem("Load...  ");
    miLoad.addActionListener(this);
    mFile.add(miLoad);
    miSave = new JMenuItem("Save...  ");
    miSave.addActionListener(this);
    mFile.add(miSave);
    miQuit = new JMenuItem("Quit     ");
    miQuit.addActionListener(this);
    mFile.add(miQuit);
    menuBar.add(mFile);
    // Help Menu
    mHelp = new JMenu("Help");
    miAbout = new JMenuItem("About...  ");
    miAbout.addActionListener(this);
    mHelp.add(miAbout);
    menuBar.add(mHelp);
    this.setJMenuBar(menuBar);



    this.ap=new AlternativePanel(h,(Alternative) (h.getAlternatives()).get(0),this);
    this.cp=new CriteriumPanel(h.getGoal(),h,this);
    this.rp=new Rightpanel(h,cp,ap,this);
    this.asp=new AlternativesPanel(h,this, filename, dest);
    this.csp=new CriteriaPanel(h,this);
    this.sadp=new ShowAlternativeDataPanel((Alternative)h.getAlternatives().get(0), h, this);
    this.lp=new Leftpanel(h,csp,asp,this, this.sadp);   

    // construct the SpliPane = ContentPan        
    jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,lp,rp);

    jsp.setOneTouchExpandable(true);
    jsp.setDividerLocation(350);

    setContentPane(jsp);
  }


  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getPreferredSize(){
    return new Dimension(1200,650);  
  }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
    return new Dimension(800,550);  
  }


  /**
   * <code>actionPerformed</code> method to handle an action event.
   *
   * @param event an <code>ActionEvent</code> value
   */
  public void actionPerformed(ActionEvent event){
    Object source = event.getSource();
    if (source == miLoad) { //LOAD
      load();
    } else if (source == miSave) {//SAVE
      save();
    } else if (source == miQuit) {//QUIT
      System.exit(0);
    } else if (source == miAbout) {//ABOUT
      ShowAbout();
    }
  }


  /**
   * <code>ShowAbout</code> method to show a dialog frame (About...).
   *
   */
  void ShowAbout() {
    (new About(this,file_mail,imageFile1)).setVisible(true);
  }


  /**
   * <code>save</code> method to show a save dialog frame.
   *
   */
  void save() {
      JFileChooser JFC = new JFileChooser(home);
      // try to add a filter
      //FileFilter filter = new FileFilter();
      //filter.addExtension("ahp");
      //filter.setDescription("JAHP hierarchy");
      //JFC.setFileFilter(filter);
      int returnVal = JFC.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
	this.save(JFC.getSelectedFile());
      }
      
    }

  /**
   * <code>load</code> method to show a load dialog frame.
   *
   */
  void load() {
      JFileChooser JFC = new JFileChooser(home);
      //FileFilter filter = new FileFilter();
      //filter.addExtension("ahp");
      //filter.setDescription("JAHP hierarchy");
      //JFC.setFileFilter(filter);
      int returnVal = JFC.showOpenDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    this.load(JFC.getSelectedFile(), dest);
	}
	
    }


  /**
   * Describe <code>load</code> method to load a new node
   *
   * @param f a <code>java.io.File</code> value
   */
  public void load(java.io.File f, String dest){
    
    try{
      FileInputStream fis =new FileInputStream(f);
      ObjectInputStream o = new ObjectInputStream(fis);	
      this.h = (Hierarchy)o.readObject();
      o.close();
      fis.close();
    } catch (EOFException eofe) {
    } catch (IOException ioe) {
      System.err.println(ioe);
    } catch (ClassNotFoundException cnfe) {
      System.err.println(cnfe);
    }


    jsp.remove(lp);
    jsp.remove(rp);

    current_alternative=(Alternative)h.getAlternatives().get(0);
    current_criterium=h.getGoal();
    this.ap=new AlternativePanel(h,(Alternative) (Alternative) (h.getAlternatives()).get(0),this);
    this.cp=new CriteriumPanel(h.getGoal(),h,this);
    this.rp=new Rightpanel(h,cp,ap,this);
    this.asp=new AlternativesPanel(h,this, this.filename, dest);
    this.csp=new CriteriaPanel(h,this);
    this.sadp=new ShowAlternativeDataPanel((Alternative)h.getAlternatives().get(0), h, this);
    this.lp=new Leftpanel(h,csp,asp,this, this.sadp);    

    // construct the SpliPane = ContentPan        
    jsp.setLeftComponent(lp);
    jsp.setRightComponent(rp);
    
    
  }


  /**
   *  <code>save</code> method to save a hierarchy
   *
   * @param f a <code>java.io.File</code> value
   */
  public void save(java.io.File f){
    try{
      FileOutputStream fos = new FileOutputStream(f); 
      ObjectOutputStream o = new ObjectOutputStream(fos);
      o.writeObject(this.h);
      o.close();
      fos.close();
    } catch (IOException ioe) {
      System.err.println(ioe);
    }
  }
}
