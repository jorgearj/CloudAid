// Abstract Data Type
package adt ;

import java.util.*;
import java.io.*;
import java.net.*;
//import org.exolab.castor.xml.*;
//import org.apache.xml.serialize.*;

import org.apache.xmlbeans.XmlException;
import org.decisiondeck.jmcda.persist.xmcda2.utils.*;
import org.decisiondeck.jmcda.persist.xmcda2.generated.*;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import com.google.common.io.Files;



/**
 * <code>OwnTest</code> : a useful class to build a complex hierarchy and test data type and algorithms
 * @author  Maxime MORGE <A HREF="mailto:morge@emse.fr">morge@emse.fr</A> 
 * @version March 8, 2003
 * @version March 26, 2003 final version
 */
public class LoadXMCDA
{

  /**
   *  Constructs a complex hierarchy  used to test.
   */
  public Hierarchy getHierarchy(String source) {
	  
	// build a tested hierarchy H={G,A1,A2}
	    Hierarchy h = new Hierarchy();
	    //System.out.println("\t \t Hierarchy h:\n"+h.toString());
	    
	    HashMap<String, String> preferences = new HashMap<String, String>();
	
	    File f = new File(source);
		XMCDAReadUtils reader = new XMCDAReadUtils();
		
		try {
			XMCDADoc doc = reader.getXMCDADoc(Files.newInputStreamSupplier(f));
			XMCDA xmcda = doc.getXMCDA();
			
			
			List<XCriteria> list = xmcda.getCriteriaList();
			
			
			for(int i=0; i <list.size(); i++){
				//System.out.println(list.get(i).toString());
				List<XCriterion> criterion = list.get(i).getCriterionList();
				//System.out.println(criterion.size());
				for(int j=0; j <criterion.size(); j++){
					//System.out.println(criterion.get(i).toString());
					System.out.println(criterion.get(j).getName());
					//build and add subcriteria C1,C2,C3,C4,C5,C7 
				    Criterium c1=new Criterium();
				    c1.setId(criterion.get(j).getId());
				    c1.setName(criterion.get(j).getName());
				    //get preference value
				    List<XScale> scaleList = criterion.get(j).getScaleList();
				    System.out.println(scaleList.size());
				    for(XScale scale : scaleList){
				    	
				    	if(scale.getMcdaConcept().equalsIgnoreCase("Preference"))
				    		c1.setPreference(scale.getQualitative().getRankedLabelList().get(0).getLabel());
				    }
				    
				    preferences.put(c1.getName(), c1.getPreference());
				    //c1.setComment("The first criterium");
				    try{
				      c1.setUrl(new URL("http://messel.emse.fr/~mmorge/1criterium.html"));}
				    catch(MalformedURLException e){
				      System.err.println("Error : MalformedURLException"+e);
				      System.exit(-1);
				    }	  
				    //Every criterium should contain alternatives...
				    //It's easier to addSubcriterium
				    //and to calculate J(a_i|c_j); J* I I* \pi
				    //I()
				    h.addSubcriterium(h.getGoal(),c1,h.getAlternatives(),h.getNb_alternatives());
				    //h.addCriterium(c) ;
				    //System.out.println("\t \t Hierarchy h + alt +c1:\n"+h.toString());
				    //System.out.println("************************************************************************************************\n");
				    
				}
			}
			
			List<XAlternatives> listAlt = xmcda.getAlternativesList();
			List<XAlternativesValues> topValues = xmcda.getAlternativesValuesList();
			
			for(int i=0; i <listAlt.size(); i++){
				//System.out.println(list.get(i).toString());
				List<XAlternative> alternatives = listAlt.get(i).getAlternativeList();
				System.out.println(alternatives.size());
				for(int j=0; j <alternatives.size(); j++){
					//System.out.println(criterion.get(i).toString());
					System.out.println(alternatives.get(j).getName());
					//build and add a third  alternative A3
				    Alternative alt=new Alternative();
				    alt.setId(alternatives.get(j).getId());
				    alt.setName(alternatives.get(j).getName());
				    alt.setPreferences(preferences);
				    //alt.setComment("The third alternative");
				    
					  //get the alternative values
					    for(XAlternativesValues alternativesValues : topValues){
					    	List<XAlternativeValue> altValues = alternativesValues.getAlternativeValueList();
					    	for(XAlternativeValue alternativeValue : altValues){
					    		if(alternativeValue.getAlternativeID().equalsIgnoreCase(alt.getId())){
					    			List<XValues> values = alternativeValue.getValuesList();
					    			HashMap<String, String> attr = new HashMap<String, String>(); 
					    			for(XValue val : values.get(0).getValueList()){
					    				attr.put(val.getName(), val.getLabel());
					    			}
					    			alt.setAttributes(attr);
					    			
					    		}
					    		
					    	}
					    }
				    
				    try{
				      alt.setUrl(new URL("http://messel.emse.fr/~mmorge/3alternative.html"));}
				    catch(MalformedURLException e){
				      System.err.println("Error : MalformedURLException"+e);
				      System.exit(-1);
				    }
				    //System.out.println("\t \t Alternative alt:\n"+alt.toString());
				    h.addAlternative(alt);
				    if(alt.getAttributes() != null)
				    	System.out.println(alt.getAttributes().toString());
				    //System.out.println("Hierarchy h + Alternative alt:\n"+h.toString());
				}
			}
			
			
			
		} catch (IOException | XmlException e) {
			System.err.println("ERRO!!!");
			e.printStackTrace();
		}
    
    
    
    return h;

  }
  
  public static void main(String[] args) {
	  String source = "/Users/Jorge/Desktop/SMS/CODE/SMS_v0.1/TO_Decide/AHP/XMCDA_To_Decide_1363691881335.xml";
	  LoadXMCDA test = new LoadXMCDA();
	  test.getHierarchy(source);
  }

}

