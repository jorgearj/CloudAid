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

// Data Type
import adt.*;

/**
 * <code>ComparisonPane</code> the  Pane to show  and modify a comparision   
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 18, 2003
 * @version March 26, 2003
 */
public class ComparisonPane extends JPanel implements ChangeListener{
  private Criterium c;
  private int i;
  private int j;
  private JSlider slide;
  private boolean last;

  private CriteriumShowPanel csp;

  private JAHP window;

  public ComparisonPane(Criterium c, int i, int j,boolean last,CriteriumShowPanel csp,JAHP window) {
      super(new BorderLayout());
      this.c=c;
      this.i=i;
      this.j=j;
      this.last=last;
      this.csp=csp;
      this.window=window;
      
      //Create the label table with title
      Hashtable labelTable = new Hashtable();
      labelTable.put( new Integer( 8 ), new JLabel("EXTREMELY") );
      labelTable.put( new Integer( 7 ), new JLabel("Intermediate between VERY STRONGLY and EXTREMELY") );
      labelTable.put( new Integer( 6 ), new JLabel("VERY STRONGLY") );
      labelTable.put( new Integer( 5 ), new JLabel("Intermediate between STRONGLY and VERY STRONGLY") );
      labelTable.put( new Integer( 4 ), new JLabel("STRONGLY") );
      labelTable.put( new Integer( 3 ), new JLabel("Intermediate between SLIGHTLY and STRONGLY") );
      labelTable.put( new Integer( 2 ), new JLabel("SLIGHTLY"));
      labelTable.put( new Integer( 1 ), new JLabel("Intermediate between EQUALLY and SLIGHTLY"));//up
      labelTable.put( new Integer( 0 ), new JLabel("EQUALLY") );
      labelTable.put( new Integer( -1 ), new JLabel("Intermediate between EQUALLY and SLIGHTLY"));//down
      labelTable.put( new Integer( -2 ), new JLabel("SLIGHTLY"));
      labelTable.put( new Integer( -3 ), new JLabel("Intermediate between SLIGHTLY and STRONGLY") );
      labelTable.put( new Integer( -4 ), new JLabel("STRONGLY") );
      labelTable.put( new Integer( -5 ), new JLabel("Intermediate between STRONGLY and VERY STRONGLY") );
      labelTable.put( new Integer( -6 ), new JLabel("VERY STRONGLY") );
      labelTable.put( new Integer( -7 ), new JLabel("Intermediate between VERY STRONGLY and EXTREMELY") );
      labelTable.put( new Integer( -8 ), new JLabel("EXTREMELY") );

      int n=c.getNb_sons();
      //System.out.println("NB SONS"+n);
      //System.out.println("i : "+i+"   j : "+j+"      n : "+n);	  
      
      slide= new JSlider(JSlider.VERTICAL,-8,8, (int) (c.getP()).get(i,j)   );
      slide.setLabelTable( labelTable );
      slide.addChangeListener(this);
      slide.setMajorTickSpacing(2);
      slide.setMinorTickSpacing(1);
      slide.setPaintTicks(true);
      if (last) {slide.setPaintLabels(true); }//Paint label only for the last JSlider
      else slide.setPaintLabels(false);
      slide.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	
      String top_title; 
      String down_title;
      if (c.isLl()) {
	top_title= new String((c.getAlternativeAt(i)).getName());
	down_title = new String((c.getAlternativeAt(j)).getName());
      }
      else{
	top_title= new String((c.getSubcriteriumAt(i)).getName());
	down_title = new String((c.getSubcriteriumAt(j)).getName());
      }
      

      
      this.add("North",new JLabel("<html><font color=red>"+top_title+"</font></html>"));
      this.add("Center",slide);
      this.add("South",new JLabel("<html><font color=red>"+down_title+"</font></html>"));
  }

  /**
   * Describe <code>getPreferredSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
    public Dimension getPreferredSize(){
      if (last) return new Dimension(400,350);
      return new Dimension(100,350);
    }

  /**
   * Describe <code>getMinimumSize</code> method here.
   *
   * @return a <code>Dimension</code> value
   * @see  <code>Container</code>
   */
  public Dimension getMinimumSize(){
      if (last) return new Dimension(400,350);
      return new Dimension(100,350);
  }



  /**
   * <code>stateChanged</code> method invoked when slider value is changed
   *
   * @param e a <code>ChangeEvent</code> value
   */
  public synchronized void stateChanged(ChangeEvent e) {
    JSlider source = (JSlider)e.getSource();
    if (!source.getValueIsAdjusting()) {
      int val = (int)source.getValue();
      //System.out.println("comparison value"+val);
      PairwiseComparisonMatrix p=c.getP();
      if (val==0){
	p.set(this.i,this.j,1);
	//System.out.println(p.toString());
      }else if (val<0){	
	p.set(this.i,this.j,1.0/(-val+1.0));	
	//System.out.println(p.toString());
      }else if (val>0){	
	p.set(this.i,this.j,(val+1));
	//System.out.println(p.toString());
    }
      //le repaint ...
      
      //System.out.println("updateTABLE");     
      csp.updateWEIGHT();
      //Systemout.println("state changed");
      window.updateALTERNATIVE();
    }
  }
}
