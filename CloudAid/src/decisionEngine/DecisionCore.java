/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package decisionEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import Controller.CloudAid;

import data.csadata.CSAData;
import data.csadata.ServiceTemplate;
import data.csadata.Result;
import data.servicedata.Offering;

public class DecisionCore {
	
	private XMCDAConverter converter;
	private Normalizer normalizer;
	private final String source = "./Decision/";
	private final String destination = "./TO_Decide/";
	private final String methodDest[] = {"SAW","AHP" };
	
	public DecisionCore(){
		this.converter = new XMCDAConverter();
		this.normalizer = new Normalizer();
		
	}
	
	public ArrayList<Result> decide(ServiceTemplate comp, ArrayList<Offering> alternatives, int method){
		ArrayList<Result> resultList = new ArrayList<Result>();
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		System.out.println("STATUS: Decision for Service Template: "+ comp.getType());
		FileChecker checker = new FileChecker(source+methodDest[method]);
		
		if(method == CloudAid.SAW){
			files = sawXMCDA(comp, alternatives);
		}else if(method == CloudAid.AHP){
			files = ahpXMCDA(comp, alternatives);
		}else{
			return null;
		}
		
		postXMCDA(files, comp, method);

		//wait for the results
		System.out.println("STATUS: Waiting for a decision...");
		String filename = checker.listen();
		
		resultList = converter.getPerformance(converter.getFromFile(filename), alternatives);
		String[] msg = {"Decision Results for Service Template: "+ comp.getId()};
		CloudAid.askData(CloudAid.PRINTRESULTLIST, msg, resultList);
		
		//sort results;
		Collections.sort(resultList);
		Collections.reverse(resultList);
		String[] msg2 = {"Ordered Decision Results for Service Template: "+ comp.getId()};
		CloudAid.askData(CloudAid.PRINTRESULTLIST, msg2, resultList);
		return resultList;
		
	}
	
	private String getDestination(long time, int method){
		return this.destination + this.methodDest[method] + "/XMCDA_To_Decide_" + time + ".xml";
	}
	
	private void postXMCDA(ArrayList<XMCDA> files, ServiceTemplate comp, int method){
		//attach timestamp in the method messages
		long time = System.currentTimeMillis();
		XMCDA timeStamp = converter.attachCompTimestamp(time, comp);
		files.add(timeStamp);
		
		XMCDA result = converter.append(files);

		converter.export(result, getDestination(time, method));
	}
	
	private ArrayList<XMCDA> sawXMCDA(ServiceTemplate comp, ArrayList<Offering> alternatives){
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		//normalize alternatives
		this.normalizer.normalize(alternatives, comp.getCriteria());
		System.out.println("SYSTEM: Creating XMCDA");
		XMCDA alte = converter.createAlternatives(alternatives);
		files.add(alte);
		XMCDA crit = converter.createCriteria(comp);
		files.add(crit);
		XMCDA weights = converter.createWeights(comp);
		files.add(weights);
		XMCDA alternativeValues = converter.createAlternativeValues(alternatives, CloudAid.SAW);
		files.add(alternativeValues);
		
		return files;
	}
	
	private ArrayList<XMCDA> ahpXMCDA(ServiceTemplate comp, ArrayList<Offering> alternatives){
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		this.normalizer.addPreferences(alternatives, comp.getCriteria());
		System.out.println("SYSTEM: Creating XMCDA");
		XMCDA alte = converter.createAlternatives(alternatives);
		files.add(alte);
		XMCDA crit = converter.createCriteria(comp);
		files.add(crit);
		XMCDA alternativeValues = converter.createAlternativeValues(alternatives, CloudAid.AHP);
		files.add(alternativeValues);
		
		return files;
	}
}
