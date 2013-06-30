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


/**
 * Criterium class
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version Fev 13, 2003
 * @version March 24, 2003  final one
 * @version July 21, 2003
 */
public class Criterium extends Activity implements Serializable,Cloneable {

  //ATTRIBUTS

  //useful to have always a new criterium with a new name 
 private static int newCriteriumSuffix = 1;

  // Is this criterium is a goal ?
  private boolean goal;
  // Is this criterium is in the lowest level?
  private boolean ll;

  //link with other Objects
  private Criterium father;
  private Vector sons ;
  private int nb_sons ;
  private String preference;

  // The associated PairwiseComparisonMatrix
  private PairwiseComparisonMatrix p;

  
  /**
   * Get the value of ll.
   * @return value of ll.
   */
  public boolean isLl() {
    return ll;
  }
  
  /**
   * Set the value of ll.
   * @param v  Value to assign to ll.
   */
  public void setLl(boolean  v) {
    this.ll = v;
  }
  
  /**
   * Get the value of goal.
   * @return value of goal.
   */
  public boolean isGoal() {
    return goal;
  }
  
  /**
   * Set the value of goal.
   * @param v  Value to assign to goal.
   */
  public void setGoal(boolean  v) {
    this.goal = v;
  }
  
  
  /**
   * Get the value of father.
   * @return value of father.
   */
  public Criterium getFather() {
    return father;
  }
  
  /**
   * Set the value of father.
   * @param v  Value to assign to father.
   */
  public void setFather(Criterium  v) {
    this.father = v;
  }
  
  /**
   * Gets the value of nb_sons
   *
   * @return the value of nb_sons
   */
  public int getNb_sons() {
    return this.nb_sons;
  }

  /**
   * Sets the value of nb_sons
   *
   * @param argNb_sons Value to assign to this.nb_sons
   */
  public void setNb_sons(int argNb_sons){
    this.nb_sons = argNb_sons;
  }
    
  /**
   * Get the value of sons.
   * @return value of sons.
   */
  public Vector getSons() {
    return sons;
  }
  
  /**
   * Set the value of sons.
   * @param v  Value to assign to sons.
   */
  public void setSons(Vector  v) {
    this.sons = v;
  }
  
  /**
   * Gets the value of p
   *
   * @return the value of p
   */
  public PairwiseComparisonMatrix getP() {
    return this.p;
  }
  
  /**
   * Sets the value of p
   *
   * @param argP Value to assign to this.p
   */
  public void setP(PairwiseComparisonMatrix argP){
    this.p = argP;
  }
  
  


  public String getPreference() {
	return preference;
}

public void setPreference(String preference) {
	this.preference = preference;
}

/**
   * <code>print</code> Returns a string representation of this Criterium, containing the String representation of each element.
   * Useful to debug
   * @return a <code>String</code> value
   */
  public String print(){
    String s=new String();

    s=s+"Name of this criterium                  : "+getName()+"\n";
    s=s+"URL of this criterium                   : "+getUrl().toString()+"\n";
    s=s+"Is this criterium in the lowest level ? : "+isLl()+"\n";
    s=s+"Is this criterium a goal ?              : "+isGoal()+"\n";
    if (!isGoal()) 
      s=s+"The name of his father                  : "+getFather().getName()+"\n";    //     getFather();
    s=s+"Number of sons                            : "+getNb_sons()+"\n";
    s=s+"PairwiseComparisonMatrix                  : "+"\n"+getP().toString();
    s=s+"sons                  : \n";

    Vector v=new Vector();
    v=getSons();

    if (isLl()) { // a vector of alternatives
      for(int i=0;i<getNb_sons();i++){
	Alternative alt = (Alternative) v.get(i);
	s=s+alt.print(); }
    }
    else{// a vector of criteria
      for(int i=0;i<getNb_sons();i++){
	Criterium c= (Criterium) v.get(i); 
	s=s+c.print();
      }
      
    }
    return s;
  }

  /**
   * <code>getSubcriteriumAt</code> Returns the ith subcriterium
   * @param a <code>int</code> value : the index
   * @return a <code>Criterium</code> value 
   * @exception IllegalArgumentException e "not found..."
   */
  public Criterium getSubcriteriumAt(int i) {
    if (this.isLl()) throw new IllegalArgumentException("The ith subcriterium of a criterium in the least level can not be found");
    return (Criterium)sons.elementAt(i);
    }

  /**
   * <code>getSubcriteriumAt</code> Returns the ith subcriterium
   * @param a <code>Criterium</code> value 
   * @return a <code>int</code> value : the index
   * @exception IllegalArgumentException e  "not found..."
   */
  public int  getIndexOfSubcriterium(Criterium c) {
    if (this.isLl()) throw new IllegalArgumentException("The ith subcriterium of a criterium in the least level can not be found");
    return sons.indexOf(c);
  }

  /**
   * <code>getAlternativeAt</code> Returns the ith Alternative
   * @param a <code>int</code> value : the index
   * @return a <code>Alternative</code> value
   * @exception IllegalArgumentException e  "nt found..."
   */
  public Alternative getAlternativeAt(int i) {
    if (!this.isLl()) throw new IllegalArgumentException("The ith alternative of a criterium not in the least level can not be found");
    return (Alternative) sons.elementAt(i);
    }

  /**
   * <code>getAlternativeAt</code> Returns the ith subcriterium
   * @param a <code>Alternative</code> value 
   * @return a <code>int</code> value : the index
   * @exception IllegalArgumentException e "not found..."
   */
  public int  getIndexOfAlternative(Alternative a) {
    if (this.isLl()) throw new IllegalArgumentException("The ith alternative of a criterium not in the least level can not be found");
    return sons.indexOf(a);
  }


  /**
   * Creates a new  <code>Criterium</code> instance.
   *
   */
  public Criterium(){
    super();
    this.setName("My criterium"+newCriteriumSuffix++);

    try{
      this.setUrl(new URL("http://messel.emse.fr/~mmorge"));}
    catch(MalformedURLException e){
      System.err.println("Error : MalformedURLException"+e);
      System.exit(-1);
    }
    //
    this.setGoal(true) ;
    this.setLl(true);
    this.setFather(null);
    this.setSons(null);
    this.setNb_sons(0);
    this.setP(new PairwiseComparisonMatrix(0));
    
  }


  //************************************
  //
  //Method to modify hierarchy
  // 
  // 
  //************************************* 

  /**
   * <code>addAlternative</code> method here.
   *
   * @param alt <code>Alternative</code> value
   * @param h <code>Hierarchy</code> value
   */
  public void addAlternative(Alternative alt,Hierarchy h){
    //pb with reference sons <=>  alternatives
    // nb_sons <=>nb_alternatives
    if (isLl()){//The PairwiseComparisonMatrix should be changed
      p.addElement();
      Vector s = h.getAlternatives();
     sons= (Vector) s.clone(); 
      nb_sons = h.getNb_alternatives();
      //setNb_sons(getNb_sons()+1);
      //getSons().add(alt);
    }
    else{//sons should be changed
      for(int i=0;i<getNb_sons();i++){
	Criterium subcriteria=(Criterium) (sons.get(i)) ;
	subcriteria.addAlternative(alt,h);
      }
    }
  }


  /**
   * <code>addSubcriterium</code> method here.
   *
   * @param Criterium c which is the father
   * @param Criterium subc which should be added
   */
  public void addSubcriterium(Criterium c,Criterium subc){
    if (this.equals(c)){
      if (isLl()){
	setLl(false);
      sons=new Vector();
      sons.add(subc);
      nb_sons=1;
      p=new PairwiseComparisonMatrix(1);
      }
      else{
	sons.add(subc);
	nb_sons++;
	p=new PairwiseComparisonMatrix(nb_sons);
      }
    }
      else { //recurcive call for all sons
	if (!isLl()){
	  for(int i=0;i<getNb_sons();i++){
	    Criterium son= (Criterium) sons.get(i) ;
	    son.addSubcriterium(c,subc);
	  }
	}
      }
  }

  /**
   * <code>equals</code> method here.
   * @return boolean
   * @param Criterium c
   *
   */
    public boolean equals(Criterium c){
    return name.equals(c.getName());
    }

  /**
   * <code>isNew</code> method here.
   *
   * @param Criterium c
   */
  public boolean isNew(Criterium c){
    if (name.equals(c.getName())) return false;
    if (ll) return true;
    for (int i=0;i<nb_sons;i++) {
      Criterium son= (Criterium)sons.get(i);
      if (!son.isNew(c)) return false;
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
   * <code>Jstar</code> method here.
   *
   * @param index the index of the alternative 
   * @return double the global importance of the alternative according to the criterium
   */
  public double Jstar(int index){
    if (isLl()) return J(index);
    double sum=0.0;
    for(int i=0;i<nb_sons;i++){
      Criterium son=(Criterium) sons.get(i);
      sum+=son.Jstar(index)*p.getWeight(i);
    }
    return sum;
  }

  /**
   * <code>J</code> method here.
   *
   * @param index the index of the alternative 
   * @return double the global importance of the alternative according to the criterium in the lowest level
   * @exception IllegalArgumentException
   */
  public double J(int index){
    if (!isLl()) throw new IllegalArgumentException("J can not be calculated for a criterium which is not in the lowest level");
    return p.getWeight(index);
  }

  /**
   * <code>I</code> method here.
   * @param Criterium c :  the index of the alternative
   * @return double  : the value of the alternative according to the criterium
   */
  public double I(Criterium c){
    if (ll) return(0.0);
    for(int i=0;i <nb_sons;i++){
      Criterium son=(Criterium) sons.get(i);
      if (son.equals(c)) return p.getWeight(i);
    }
    double sum=0.0;
    for(int i=0;i <nb_sons;i++){
      Criterium son=(Criterium) sons.get(i);
      sum+=p.getWeight(i)*son.I(c);
    }
    return(sum);
  }


  //************************************
  //
  //Method to modify hierarchy
  // 
  // 
  //************************************* 


  /**
   * <code>delAlternative</code> method here.
   * @param int :  the index of the alternative which should be deleted
   */
  public void delAlternative(int index){

    if (ll){
      Vector new_sons=new Vector();
      for(int i=0;i<nb_sons;i++){
      Alternative current_alt=(Alternative) sons.get(i);
      if (index!=i) new_sons.add(current_alt);
      }
    setSons(new_sons);
    nb_sons--;
    p.delElement(index);
    }
    else{
      for(int i=0;i<nb_sons;i++){
	Criterium son= (Criterium) sons.get(i);
	son.delAlternative(index);
      }
    }
  }


  /**
   * <code>delCriterium</code> method here.
   * @param Criterium c :  the criterium which should be deleted
   * @exception IllegalArgumentException
   */
  public void delCriterium(Criterium c,Hierarchy h){    

    if (ll) throw new IllegalArgumentException("The criterium can not be deleted");

    if (isSon(c)){//son
      //find index
	int index=0;
	for(int i=0;i<nb_sons;i++){
	  Criterium current_c=(Criterium) sons.get(i);
	  if (current_c.equals(c)) index=i;
	}
	if (c.isLl()){// cut sons
	Vector new_sons=new Vector();
	for(int i=0;i<nb_sons;i++){
	  if (i!=index) new_sons.add((Criterium) sons.get(i));
	}
	//System.out.println("index : "+index+"\n");
	
	p.delElement(index);
	sons=new_sons;
	nb_sons--;
	if (nb_sons==0){//this criterium become in least level
	  this.setLl(true);
	  sons=h.getAlternatives();
	  nb_sons=h.getNb_alternatives();
	  this.setP(new PairwiseComparisonMatrix(nb_sons));
	}
	
	}
	else{//all sons must hill-climb
	  sons.remove(index);
	  for(int i=0;i<c.getNb_sons();i++){
	    Vector child_sons=c.getSons();
	    sons.add((Criterium) child_sons.get(i));
	  }
	  nb_sons+=c.getNb_sons()-1;
	  p=new PairwiseComparisonMatrix(nb_sons);
	}

    }
    else{//recursif call
      for(int i=0;i<nb_sons;i++){
	Criterium current_c=(Criterium) sons.get(i);
	if (current_c.isSubcriteria(c)) {
	  current_c.delCriterium(c,h);
	  break;      
	}
      }
    }
  }

  /**
   * <code>isSubcriteria</code> method here.
   * @param Criterium :  the criterium which should be a subcriteria
   * 
   */
  public boolean isSubcriteria(Criterium c){
    if (ll) return false;
    if (isSon(c)) return true;

    for(int i=0;i<nb_sons;i++){
      Criterium current_c=(Criterium) sons.get(i);
      if (current_c.isSubcriteria(c)) return true;
    }//c is not a subcriteria
    return false;
  }

  /**
   * <code>isSon</code> method here.
   * @param Criterium :  the criterium which should be a son
   * 
   */
  public boolean isSon(Criterium c){
    if (ll) return false;
    for(int i=0;i<nb_sons;i++){
      Criterium current_c=(Criterium) sons.get(i);
      if (c.equals(current_c)) return true;
    }//c is not a son
    return false;
  }



  /**
   * <code>main</code> method to test this class.
   * @param Criterium :  command line
   * 
   */
  public static void main(String[] argv) {
    Criterium c1=new Criterium();
    Criterium c2=new Criterium();
    //Systemout.println("C1 :"+c1.print());
    //Systemout.println("C2 :"+c2.print());
    

  }
  

}
