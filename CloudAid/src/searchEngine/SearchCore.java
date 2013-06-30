/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package searchEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import Controller.CloudAid;

import data.csadata.ServiceTemplate;
import data.csadata.Criterion;
import data.csadata.Requirement;
import data.servicedata.Enumerator;
import data.servicedata.Offering;
import data.servicedata.QualitativeFeature;
import data.servicedata.QuantitativeFeature;
import data.simulation.DataSimulator;

public class SearchCore {
	
	private JENAEngine jena;
	
	//testing variables
	long searchExecutionTime = 0;
	
	
	public SearchCore(){
		this.jena = new JENAEngine();
		
	}
	
	public ArrayList<Offering> searchAlternatives(ServiceTemplate c){
		ArrayList<Offering> alternatives = new ArrayList<Offering>();
		ArrayList<Offering> tempAlt = new ArrayList<Offering>();
		System.out.println("STATUS: Search for Service Template: "+ c.getType());
		
		//for testing purposes generates hardcoded alternatives
		//DataSimulator gen = new DataSimulator();
		//alternatives = gen.generateAlternatives();
		
		//get exclusive requirements
		ArrayList<Requirement> exReqs = this.getExclusiveReqs(c);
		//print reqs
		System.out.println("SYSTEM: Exclusive Requirements:");
		for(Requirement r : exReqs){
			System.out.println("SYSTEM: "+ r.toString());
		}
		
		if(exReqs.size() > 0){
			long start = System.nanoTime();
			tempAlt = jena.getOfferingByExcludedReqs(exReqs);
			long end = System.nanoTime();
			this.searchExecutionTime = end-start;
		}
		else{
			System.out.println("SYSTEM: No exclusive requirements.");
		}
		
		if(tempAlt.size() == 0 || tempAlt == null){
			String[] msg = {"No alternatives found for the requirements specified. Please remake your requirements list!!!"};
			CloudAid.askData(CloudAid.PROMPT, msg, null);
			return null;
		}else{
		
			//set the alternatives attributes
			for(Offering off : tempAlt){
				off = this.setAttributes(off, c.getCriteria());
				alternatives.add(off);
			}
			
			CloudAid.askData(CloudAid.PRINTALTDATA, null, tempAlt);
			System.out.println("SYSTEM: Search Execution Time: "+ this.searchExecutionTime);
			//System.out.println("Alternatives:");
			//for(Offering s : alternatives){
				//System.out.println(s.toString());
			//}
			
			return alternatives;
		}
	}
	
	
	private ArrayList<Requirement> getExclusiveReqs(ServiceTemplate c){
		ArrayList<Requirement> exReqs = new ArrayList<Requirement>();
		
		for(Requirement r : c.getRequirements()){
			if(!r.isCriterion() || r.isExclusive() || r.isNeeded() ){
				exReqs.add(r);
			}
		}
		
		return exReqs;
	}
	
	private Offering setAttributes(Offering offering, ArrayList<Criterion> criteria){
		HashMap<String,String> attributes = new HashMap<String, String>();
		attributes = offering.getAttributes();

		System.out.println("SYSTEM: "+offering.getName());
		for(Criterion crit : criteria){
			String value;
			if(Enumerator.QUAL_FEATURE.get(crit.getName()) != null){
				//it is a Qualitative feature
				value = this.getQualFeatureValue(offering, Enumerator.QUAL_FEATURE.get(crit.getName()));
				System.out.println("SYSTEM: "+crit.getName() + " - "+value);
				attributes.put(crit.getName(), value);
			}else if(Enumerator.QUANT_FEATURE.get(crit.getName()) != null) {
				//it is a Quantitative feature
				value = this.getQuantFeatureValue(offering, Enumerator.QUANT_FEATURE.get(crit.getName()));
				System.out.println("SYSTEM: "+crit.getName() + " - "+value);
				attributes.put(crit.getName(), value);
			}
		}
		
		offering.setAttributes(attributes);
		
		return offering;
	}
	
	private String getQualFeatureValue(Offering offering, Enumerator.QUAL_FEATURE feat){
		for(QualitativeFeature qf : offering.getQualitativeFeatures()){
			if(qf.getType() == feat){
				return qf.getName();
			}
		}
		
		return "N/A";
	}
	
	private String getQuantFeatureValue(Offering offering, Enumerator.QUANT_FEATURE feat){
		for(QuantitativeFeature qf : offering.getQuantitativeFeatures()){
			if(qf.getType() == feat){
				return Float.toString(qf.getValueFloat());
			}
		}
		
		return "N/A";
	}
	
	public static void main(String[] args) {
		SearchCore test = new SearchCore();
		
		ArrayList<String> serviceNames = test.jena.getOfferingNames();
		System.out.println(serviceNames.size());
		System.out.println("Offering Names:");
		for(String name : serviceNames){
			System.out.println("   - " + name);
		}
		
		ArrayList<Offering> services = new ArrayList<Offering>();
		
		while(true){
			System.out.println("Insert offering name: ");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			if(s.equalsIgnoreCase("exit")){		
				break;
			}else{
				services = test.jena.getOfferingByName(s);	
				for(Offering serv : services){
					System.out.println(serv.toString());
				}	
			}
			
		}
		
		/*System.out.println("Files List:");
		for(String file : fileNames){
			System.out.println(file);
			//get the directory name for the file
			Path path = Paths.get(file);
			System.out.println(path.getParent().getFileName().toString());
		}*/
		
	}

}
