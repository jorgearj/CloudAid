package xmcda;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeOnCriteriaPerformances;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternatives;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativesValues;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteria;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteriaValues;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriterion;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriterionValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMethodParameters;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XParameter;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPerformanceTable;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPreferenceDirection;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XQuantitative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XScale;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XValues;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAReadUtils;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAWriteUtils;

import saw.Alternative;
import saw.Criterion;


import com.google.common.io.Files;

public class XMCDAConverter {
	
	private final String ALTERNATIVE_PREFIX = "alt";
	private final String CRITERION_PREFIX = "cri";
	
	public XMCDAConverter(){
		
	}
	
	public XMCDA getFromFile(String source){
		File f = new File(source);
		XMCDAReadUtils reader = new XMCDAReadUtils();
		XMCDADoc doc = null;
		try {
			doc = reader.getXMCDADoc(Files.newInputStreamSupplier(f));
		} catch (IOException | XmlException e) {
			System.err.println("Unable to retrieve XMCDA data from file!!!");
			e.printStackTrace();
		}
		return doc.getXMCDA();
	}
	
	public ArrayList<Alternative> getAlternatives(XMCDA xmcda){
		ArrayList<Alternative> res = new ArrayList<Alternative>();
		
		List<XAlternatives> XAlternatives = xmcda.getAlternativesList();
		List<XAlternativesValues> topValues = xmcda.getAlternativesValuesList();
		
		for(XAlternatives list : XAlternatives){
			List<XAlternative> alternatives = list.getAlternativeList();
			for(XAlternative alt : alternatives){
				Alternative a = new Alternative();
			    a.setId(alt.getId());
			    a.setName(alt.getName());
			    
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
			    			a.setAttributes(attr);
			    		}
			    		
			    	}
			    }
			    
			    res.add(a);
			}
		}
		
		//print data
		//System.out.println(res.toString());
		
		return res;
	}
	
	public ArrayList<Criterion> getCriteria(XMCDA xmcda){
		ArrayList<Criterion> criteria = new ArrayList<Criterion>();
		
		List<XCriteria> list = xmcda.getCriteriaList();
		List<XCriteriaValues> criteriaValues = xmcda.getCriteriaValuesList();
		
		for(int i=0; i <list.size(); i++){
			List<XCriterion> criterion = list.get(i).getCriterionList();
			for(XCriterion crit : criterion){
			    Criterion c1 = new Criterion(crit.getName());
			    c1.setId(crit.getId());
			    
			    //get the preference direction
			    List<XScale> scales = crit.getScaleList();
			    for(XScale scale : scales){
			    	
			    	if(scale.getMcdaConcept().equalsIgnoreCase("Preference"))
			    		c1.setPreference(scale.getQualitative().getRankedLabelList().get(0).getLabel());
			    }
			    
			    //get the criterion weight
			    
			    for(XCriteriaValues critValues : criteriaValues){
			    	List<XCriterionValue> values = critValues.getCriterionValueList();
			    	for(XCriterionValue val : values){
				    	if(val.getCriterionID().equalsIgnoreCase(crit.getId())){
				    		List<XValue> v = val.getValueList();
					    	c1.setWheight(v.get(0).getReal());
				    	}	    	
				    }
			    }
			    
			    criteria.add(c1);
			    
			}
		}
		
		//print data
		//System.out.println(criteria.toString());
				
		return criteria;
	}
	
	public HashMap<String, String> getMethodParameters(XMCDA xmcda){
		HashMap<String, String> param = new HashMap<String, String>(); 
		
		List<XMethodParameters> methodParameters = xmcda.getMethodParametersList();
		List<XParameter> parametersList = methodParameters.get(0).getParameterList();
		
		for(XParameter parameter : parametersList){
			param.put(parameter.getName(), parameter.getValue().getLabel());
		}
		
		return param;
	}
	
	public XMCDA attachCompTimestamp(String time, String compId){
		XMCDA XMCDATime = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMethodParameters parameters = XMCDATime.addNewMethodParameters();
		XParameter p1 = parameters.addNewParameter();
		p1.setName("FileTimestamp");
		XValue val1 = p1.addNewValue();
		val1.setLabel(time);
		XParameter p2 = parameters.addNewParameter();
		p2.setName("ComponentID");
		XValue val2 = p2.addNewValue();
		val2.setLabel(compId);
		
		return XMCDATime;
		
	}
	
	public XMCDA createPerformance(ArrayList<Alternative> alternatives){
		//create the XMCDA file
		XMCDA XMCDAPerformance = XMCDADoc.XMCDA.Factory.newInstance();
		
		XPerformanceTable performanceTable = XMCDAPerformance.addNewPerformanceTable();
		for(int i=0; i<alternatives.size(); i++){
			XAlternativeOnCriteriaPerformances performances = performanceTable.addNewAlternativePerformances();
			Alternative alt = alternatives.get(i);
			performances.setAlternativeID(alt.getId());
			XAlternativeOnCriteriaPerformances.Performance p = performances.addNewPerformance();
			p.setCriterionID("TopGoal");
			XValue v = p.addNewValue();
			Double res = alt.getValue();
			v.setReal(res.floatValue());
			System.out.print(alt.getName()+" -- ");
			System.out.println(res);
		}
	    
		//System.out.println(XMCDAPerformance.toString());
		
		return XMCDAPerformance;
	}
	
	
	public XMCDA append(ArrayList<XMCDA> list){
		XMCDA xmcda = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMCDAWriteUtils writer = new XMCDAWriteUtils();
		
		writer.appendTo(list, xmcda);
		
		//print data
		//System.out.println(xmcda.toString());
		
		return xmcda;
		
	}
	
	public void export(XMCDA xmcda, String dest){
		XMCDAWriteUtils writer = new XMCDAWriteUtils();
		XMCDADoc file = XMCDADoc.Factory.newInstance();
		
		File result = new File(dest);
		
		file.setXMCDA(xmcda);
		try {
			writer.write(file, Files.newOutputStreamSupplier(result));
		} catch (IOException e) {
			System.out.println("Unable to create file!!!");
			e.printStackTrace();
		}
	}

}
