/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package AggregationEngine;

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

import com.google.common.io.Files;

import data.csadata.CSAData;
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
	
	
	public XMCDA attachCompTimestamp(long time){
		XMCDA XMCDATime = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMethodParameters parameters = XMCDATime.addNewMethodParameters();
		XParameter p1 = parameters.addNewParameter();
		p1.setName("FileTimestamp");
		XValue val1 = p1.addNewValue();
		val1.setLabel(Long.toString(time));
		XParameter p2 = parameters.addNewParameter();
		p2.setName("ComponentID");
		XValue val2 = p2.addNewValue();
		val2.setLabel("Aggregation_Data");
		
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
	
	public XMCDA createAlternatives(ArrayList<ServiceTemplate> templates){
		//variable for prefixing alternatives
		int prefix = 1;
		
		//create the XMCDA file
		XMCDA XMCDAalternatives = XMCDADoc.XMCDA.Factory.newInstance();
		
		XAlternatives top = XMCDAalternatives.addNewAlternatives();
		for(ServiceTemplate template : templates){
			XAlternative alt = top.addNewAlternative();
			alt.setId(template.getId());
			alt.setName(template.getType());
		}
		
		//print data
		//System.out.println(XMCDAalternatives.toString());
		
		return XMCDAalternatives;
	}
	
	public CSAData getPerformance(XMCDA xmcda, CSAData data){

		for(XPerformanceTable performanceTable : xmcda.getPerformanceTableList()){
			for(XAlternativeOnCriteriaPerformances altPerformances : performanceTable.getAlternativePerformancesList()){
				//get Correct service alternative information
				for(ServiceTemplate template : data.getServiceTemplates()){
					if(template.getId().equalsIgnoreCase(altPerformances.getAlternativeID())){
						double perf = new Double(altPerformances.getPerformanceList().get(0).getValue().getReal());
						template.setWeight((float)perf);
					}
				}
			}
		
		}
		return data;
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
