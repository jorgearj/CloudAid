// Abstract Data Type
package adt ;

//imports
import javax.swing.*;          //This is the final package name.
//import com.sun.java.swing.*; //Used by JDK 1.2 Beta 4 and all
//Swing releases before Swing 1.1 Beta 3.
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import Jama.*;



/**
 * Decision <code>Hierarchy</code> class
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version Fev 13, 2003
 * @version March 24, 2003  final one
 */
public class Hierarchy implements Cloneable,Serializable{


  // ATTRBUTS
  private Criterium goal;
  private Vector alternatives ;
  private int nb_alternatives ;

  /**
   * Gets the value of goal
   *
   * @return the value of goal
   */
  public Criterium getGoal() {
    return this.goal;
  }

  /**
   * Sets the value of goal
   *
   * @param argGoal Value to assign to this.goal
   */
  public void setGoal(Criterium argGoal){
    this.goal = argGoal;
  }

  /**
   * Gets the value of alternatives
   *
   * @return the value of alternatives
   */
  public Vector getAlternatives() {
    return this.alternatives;
  }

  /**
   * Sets the value of alternatives
   *
   * @param argAlternatives Value to assign to this.alternatives
   */
  public void setAlternatives(Vector argAlternatives){
    this.alternatives = argAlternatives;
  }


  /**
   * Gets the value of nb_alternatives
   *
   * @return the value of nb_alternatives
   */
  public int getNb_alternatives() {
    return this.nb_alternatives;
  }
  
  /**
   * Sets the value of nb_alternatives
   *
   * @param argNb_alternatives Value to assign to this.nb_alternatives
   */
  public void setNb_alternatives(int argNb_alternatives){
    this.nb_alternatives = argNb_alternatives;
  }


  /**
   * Creates a new  <code>Hierarchy</code> instance.
   * a minimal hierarchy is composed by 1 goal : "My goal" and 2 alternatives : "Alternative 1" and "Alternative 2"
   */
  public Hierarchy(){

    //Specification of the alternatives
    alternatives =new Vector();
    setNb_alternatives(0);
    //Specification of the goal
    goal =new Criterium();
    goal.setId("goal");
    goal.setName("My goal");
    try{
      goal.setUrl(new URL("http://messel.emse.fr/~mmorge/JAHP/goal.html"));}
    catch(MalformedURLException e){
      System.err.println("Error : MalformedURLException"+e);
      System.exit(-1);
    }

    goal.setLl(true);
    goal.setGoal(true);
    goal.setFather(null);
    goal.setSons(alternatives);
    goal.setNb_sons(nb_alternatives);
    //insert PairwiseComparisonMatrix in Criterium
    goal.setP(new PairwiseComparisonMatrix(alternatives.size()));

  }



  /**
   * <code>print</code> Returns a string representation of this Hierarchy, containing the String representation of each elements to debug.
   *
   * @return a <code>String</code> value
   */
  public String print(){
    String s=new String();
    s="Nb of alternatives          : "+nb_alternatives+"\n";
    s=s+"Alternatives of hierarchy : \n" ;
    Vector v=new Vector();
    v=getAlternatives();
    for(int i=0;i<getNb_alternatives();i++){
      s=s+"Alternative "+i+"\n";
      Alternative alt =(Alternative) v.get(i);
      alt.print();
    }
    s=s+"Goal of the hierarchy       : \n"+getGoal().print();
    return s;
  }


  /**
   * <code>toString</code> Returns a short string representation of this Hierarchy
   *
   * @return a <code>String</code> value
   */
  public String toString(){
    String s=new String();
    s="Goal of the hierarchy       : \n"+getGoal().toString();
    s=s+"Nb of alternatives          : "+nb_alternatives+"\n";
    return s;
  }


  /**
   * <code>addAlternative</code> method here.
   *
   * @param alt <code>Alternative</code> value
   * @exception IllegalArgumentException "Out of bounded..."
   * @return int index of the alternatives 
   */
  public int indexAlt(Alternative alt){
    int index=0;
    if (isNew(alt)) throw new IllegalArgumentException("out of bounded alternatives");
    for(int i=0;i<nb_alternatives;i++){
      Alternative current_alt=(Alternative) alternatives.get(i);
      if (current_alt.equals(alt)){
	index=i;
	return index;
      }
    }
    return -1;
  }



  /**
   * <code>addAlternative</code> method here.
   *
   * @param alt <code>Alternative</code> value
   * @exception IllegalArgumentException "Out of bounded"
   */
  public void addAlternative(Alternative alt){
    if (!isNew(alt)) throw new IllegalArgumentException("A alternative with the same name is always in this hierarchy");
    nb_alternatives++;
    alternatives.add(alt);
    goal.addAlternative(alt,this);
  }

  /**
   * <code>addSubcriterium</code> method here.
   *
   * @param Criterium c which should be the father
   * @param Criterium subc which should be added
   * @param Vector alternatives
   * @param int nb_alternatives
   * @exception IllegalArgumentException
   */
  public void addSubcriterium(Criterium c,Criterium subc,Vector alternatives,int nb_alternatives){

    if (!goal.isNew(subc)) throw new IllegalArgumentException("A criterium with the same name is always in this hierarchy");
    subc.setFather(c);
    subc.setGoal(false);
    subc.setLl(true);
    subc.setSons((Vector) alternatives.clone());
    subc.setNb_sons(nb_alternatives);
    subc.setP(new PairwiseComparisonMatrix(nb_alternatives));
    goal.addSubcriterium(c,subc);
  }


  /**
   * <code>isNew</code> method here.
   *
   * @param Alternatives alt
   * @return boolean
   */
  public boolean isNew(Alternative alt){
    for(int i=0;i<nb_alternatives;i++){
      Alternative current_alt=(Alternative) alternatives.get(i);
      if (current_alt.equals(alt)) return false;
    }
    return true;
  }

  //************************************
  //
  //Method to calculate solutions
  // 
  // 
  //************************************* 
  
  /**
   * <code>best_alternative</code> method here.
   *
   * @return an int : the index of the best alternative 
   */
  public int bestAlternative(){
    int index=0;
    double value=0.0;
    for(int i=0;i<nb_alternatives;i++){
      if (Pi(i)>value){
	index=i;
	value=Pi(i);
      }
  }
    return index;
  }


  /**
   * <code>Pi</code> method here.
   * @param i :  the index of the alternative
   * @return double  : the value of the alternative according to the hierarchy
   */
  public double Pi(int i){
    return goal.Jstar(i);
  }

  /**
   * <code>V</code> method here.
   * @param Criterium c according to the hierarchy is evaluated
   * @return double  : the value of the criterium according to the hierarchy
   * @return IllegalArgumentException
   */
  public double V(Criterium c){
    if (goal.equals(c)) throw new IllegalArgumentException("the value of the criterium according to the hierarchy can not be calculated");
    return(goal.I(c));
  }


  //************************************
  //
  //Method to modify hierarchy
  // 
  // 
  //************************************* 

  /**
   * <code>delAlternative</code> method here.
   * @param Alternative :  the alternative which should be deleted
   * @exception IllegalArgumentException
   */
  public void delAlternative(Alternative alt){
    Vector old_alts=(Vector)alternatives.clone();
    int old_nb_alts=nb_alternatives;
    Vector new_alts=new Vector();
    int index=0;
    if (nb_alternatives==1) throw new IllegalArgumentException("alternative can not be deleted : a hierarchy must have even a alternative");
    for(int i=0;i<nb_alternatives;i++){
      Alternative current_alt=(Alternative) old_alts.get(i);
      if (current_alt.equals(alt)){
	index=i;
      }
      else new_alts.add(current_alt);
    }
    setAlternatives(new_alts);
    nb_alternatives--;
    goal.delAlternative(index);
  }

  /**
   * <code>delCriterium</code> method here.
   * @param Criterium :  the criterium which should be deleted
   * @exception IllegalArgumentException
   */
  public void delCriterium(Criterium c){
    goal.delCriterium(c,this);
  }


  /////////////////////
  //
  // Methods to load and save a hierarchy
  // NOT Yet Implemented
  /////////////////////


}


  

