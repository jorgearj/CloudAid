/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package decisionEngine;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.decisiondeck.jmcda.persist.xmcda2.generated.XFunction;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMethodParameters;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XParameter;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XParameters;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPerformanceTable;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPreferenceDirection;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XQualitative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XQuantitative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XRankedLabel;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XScale;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XThresholds;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XValue;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XValues;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAReadUtils;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAWriteUtils;

import Controller.CloudAid;

import com.google.common.io.Files;

import data.csadata.ServiceTemplate;
import data.csadata.Criterion;
import data.csadata.Result;
import data.servicedata.Offering;

public class XMCDAConverter {
	
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
	
	public XMCDA createAlternatives(ArrayList<Offering> alternatives){
		//variable for prefixing alternatives
		int prefix = 1;
		
		//create the XMCDA file
		XMCDA XMCDAalternatives = XMCDADoc.XMCDA.Factory.newInstance();
		
		XAlternatives top = XMCDAalternatives.addNewAlternatives();
		for(Offering s : alternatives){
			XAlternative alt = top.addNewAlternative();
			alt.setId(s.getId());
			alt.setName(s.getName());
		}
		
		//print data
		//System.out.println(XMCDAalternatives.toString());
		
		return XMCDAalternatives;
	}
	
	public XMCDA createAlternativeValues(ArrayList<Offering> alternatives, int method){
		XMCDA XMCDAalternativesValues = XMCDADoc.XMCDA.Factory.newInstance();
		
		XAlternativesValues top = XMCDAalternativesValues.addNewAlternativesValues();
		for(Offering s : alternatives){
			XAlternativeValue altValue = top.addNewAlternativeValue();
			altValue.setAlternativeID(s.getId());
			XValues values = altValue.addNewValues();
			Iterator it;
			if(method == CloudAid.SAW){
				it = s.getWeightedAttributes().entrySet().iterator();
			}else{
				it = s.getAttributes().entrySet().iterator();
			}
		    while (it.hasNext()) {
		    	Map.Entry atribute = (Map.Entry)it.next();
				XValue val = values.addNewValue();
				val.setName((String) atribute.getKey());
				try {
					val.setLabel((String) atribute.getValue());
				} catch (ClassCastException e) {
					val.setLabel((String) Double.toString((double) atribute.getValue()));
				}
			}
			

		}
		
		//print data
		//System.out.println(XMCDAalternativesValues.toString());
		
		return XMCDAalternativesValues;
	}
	
	public XMCDA createCriteria(ServiceTemplate c){
		//variable for prefixing criteria
		int prefix = 1;
		
		//list of criteria
		ArrayList<Criterion> criteria = c.getCriteria();
		
		//create the XMCDA file
		XMCDA XMCDAcriteria = XMCDADoc.XMCDA.Factory.newInstance();
		
		XCriteria top = XMCDAcriteria.addNewCriteria();
		for(Criterion crit : criteria){
			XCriterion criterion = top.addNewCriterion();
			criterion.setId(crit.getId());
			criterion.setName(crit.getName());
			XScale scale = criterion.addNewScale();
			scale.setMcdaConcept("PreferenceDirection");
			XQuantitative quant = scale.addNewQuantitative();
			if(crit.getPreferenceDirection().equalsIgnoreCase("max"))
				quant.setPreferenceDirection(XPreferenceDirection.MAX);
			else if(crit.getPreferenceDirection().equalsIgnoreCase("min"))
				quant.setPreferenceDirection(XPreferenceDirection.MIN);
			
			XScale scalePref = criterion.addNewScale();
			scalePref.setMcdaConcept("Preference");
			XQualitative qualit = scalePref.addNewQualitative();
			XRankedLabel label = qualit.addNewRankedLabel();
			label.setRank(new BigInteger("1"));
			label.setLabel(crit.getPreference());
			
			
		}
		
		
		
		//print data
		//System.out.println(XMCDAcriteria.toString());
				
		return XMCDAcriteria;
	}
	
	public XMCDA createWeights(ServiceTemplate c){
		//list of criteria
		ArrayList<Criterion> criteria = c.getCriteria();
		
		//create the XMCDA file
		XMCDA XMCDAweights = XMCDADoc.XMCDA.Factory.newInstance();
		
		XCriteriaValues top = XMCDAweights.addNewCriteriaValues();
		for(Criterion crit : criteria){
			XCriterionValue criVal = top.addNewCriterionValue();
			criVal.setCriterionID(crit.getId());
			XValue val = criVal.addNewValue();
			Double weight = crit.getWeight();
			val.setReal(weight.floatValue());
		}

		//print data
		//System.out.println(XMCDAweights.toString());
		
		return XMCDAweights;
	}
	
	public XMCDA attachCompTimestamp(long time, ServiceTemplate c){
		XMCDA XMCDATime = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMethodParameters parameters = XMCDATime.addNewMethodParameters();
		XParameter p1 = parameters.addNewParameter();
		p1.setName("FileTimestamp");
		XValue val1 = p1.addNewValue();
		val1.setLabel(Long.toString(time));
		XParameter p2 = parameters.addNewParameter();
		p2.setName("ComponentID");
		XValue val2 = p2.addNewValue();
		val2.setLabel(c.getId());
		
		return XMCDATime;
		
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
	
	public ArrayList<Result> getPerformance(XMCDA xmcda, ArrayList<Offering> alternatives){
		ArrayList<Result> resultList = new ArrayList<Result>();
		for(XPerformanceTable performanceTable : xmcda.getPerformanceTableList()){
			for(XAlternativeOnCriteriaPerformances altPerformances : performanceTable.getAlternativePerformancesList()){
				//get Correct service alternative information
				for(Offering alt : alternatives){
					if(alt.getId().equalsIgnoreCase(altPerformances.getAlternativeID())){
						Double perf = new Double(altPerformances.getPerformanceList().get(0).getValue().getReal());
						Result res = new Result(alt, perf);
						resultList.add(res);
					}
				}
			}
		
		}
		return resultList;
	}
	
	public XMCDA append(ArrayList<XMCDA> list){
		XMCDA xmcda = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMCDAWriteUtils writer = new XMCDAWriteUtils();
		
		writer.appendTo(list, xmcda);
		
		//print data
		//System.out.println(xmcda.toString());
		
		return xmcda;
		
	}
	
	public void export (XMCDA xmcda, String dest){
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
