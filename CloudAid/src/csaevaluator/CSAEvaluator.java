/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: CSAEvaluator.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package csaevaluator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import Controller.CloudAid;

import searchEngine.SearchCore;
import org.apache.commons.beanutils.*;

import data.csadata.CSAData;
import data.csadata.ServiceTemplate;
import data.csadata.Criterion;
import data.csadata.Requirement;
import data.csadata.Result;
import data.servicedata.Offering;
import decisionEngine.DecisionCore;


//class for evaluation and contructing the search queries
public class CSAEvaluator {
	
	private CSAData data;
	public static final int OK = 0;
	public static final int ERROR_1 = 1; // no components in the CSA
	
	private int methodID;
		
	public CSAEvaluator(int methodID){
		this.methodID = methodID;
	}

	public CSAData getData() {
		return data;
	}

	public void setData(CSAData data) {
		this.data = data;
	}
	
	public CSAData evaluator(CSAData csa){
		String[] msg = {"STATUS: Evaluating CSA Data"};
		CloudAid.askData(CloudAid.PROMPT, msg, null);
			
		try{
			csa = this.checkData(csa);
			if(csa.getEvalResult() == 0){
				//evaluation ok
				this.data = csa;
				this.convertReqsInCriterions();
				this.generalizeCSARequirements();
				this.generalizeCSACriteria();
				if(this.methodID == CloudAid.SAW){
					this.normalizeServiceTemplateWeights();
					this.normalizeCriteriaWeights();
				}
				return this.data;
			}else{
				return csa;
			}
		}catch(NullPointerException ex){
			//System.out.println("No data do evaluate.");
			return null;
		}
	}
	
	//checks if the CSA has enough data for a decision
	private CSAData checkData(CSAData data){
		if(data !=null){
			if(data.getServiceTemplates().size() > 0){
				data.setEvalResult(OK);
				return data;
			}else{
				data.setEvalResult(ERROR_1);
				return data;
			}
		}else{
			return data;
		}
		
	}
		
	
	//Adds to each service template the general requirements for the CSA 
	//(if a part of the CSA does not fulfill the requirement the whole will not fullfill it either)
	private void generalizeCSARequirements(){
		ArrayList<ServiceTemplate> comps = new ArrayList<ServiceTemplate>() ;
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		
		comps = this.data.getServiceTemplates();
		reqs = this.data.getRequirements();
		
		for(ServiceTemplate c : comps){
			for(Requirement r : reqs){
				Requirement temp = new Requirement();
				try {
					BeanUtils.copyProperties(temp, r);
					//System.out.println(r.toString());
					//System.out.println(temp.toString());
					c.addReq(temp);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	//Adds to each Service template the general criteria for the CSA 
	//(each service template can have is own set of criteria, but if a global criteria is defined each component must use it.)
	private void generalizeCSACriteria(){
		ArrayList<ServiceTemplate> comps = new ArrayList<ServiceTemplate>() ;
		ArrayList<Criterion> crit = new ArrayList<Criterion>() ;
		
		comps = this.data.getServiceTemplates();
		crit = this.data.getCriteria();
		
		for(ServiceTemplate c : comps){
			for(Criterion cr : crit){
				try {
					Criterion temp = (Criterion) BeanUtils.cloneBean(cr);
					c.addCrit(temp);
				} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void convertReqsInCriterions(){
		//convert the general requirements
		ArrayList<Criterion> criteria = this.data.getCriteria();
		
		for(Requirement req : this.data.getRequirements()){
			if(req.isCriterion()){
				//create new criterion
				Criterion crit = new Criterion();
				if(req.getQuantType() != null)
					crit.setName(req.getQuantType().getValue());
				else if(req.getQualType() != null)
					crit.setName(req.getQualType().getValue());
				else
					//this is a price requirement
					crit.setName("price");
				
				String[] msg = {crit.getName(), "General"};
				if(this.methodID == CloudAid.SAW){
					Float weight = Float.parseFloat(CloudAid.askData(CloudAid.GET_WEIGHT, msg, null));
					crit.setWeight(weight);
				}
				//get the criterion preference direction
				String res = CloudAid.askData(CloudAid.GET_PREFERENCE_DIRECTION, msg, null);
				if(res.equalsIgnoreCase("y")){
					crit.setPreferenceDirection("max");
				}else if(res.equalsIgnoreCase("n")){
					crit.setPreferenceDirection("min");
				}
				
				criteria.add(crit);
			}
		}
		this.data.setCriteria(criteria);
		
		//convert each serpiceTemplate requirements
		for(ServiceTemplate template : this.data.getServiceTemplates()){
			ArrayList<Criterion> templateCriteria = template.getCriteria();
			for(Requirement req: template.getRequirements()){
				if(req.isCriterion()){
					//create new criterion
					Criterion crit = new Criterion();
					if(req.getQuantType() != null)
						crit.setName(req.getQuantType().getValue());
					else if(req.getQualType() != null)
						crit.setName(req.getQualType().getValue());
					else
						//this is a price requirement
						crit.setName("price");
					
					String[] msg = {crit.getName(), template.getType()};
					if(this.methodID == CloudAid.SAW){
						Float weight = Float.parseFloat(CloudAid.askData(CloudAid.GET_WEIGHT, msg, null));
						crit.setWeight(weight);
					}
					
					//get the criterion preference direction
					String res = CloudAid.askData(CloudAid.GET_PREFERENCE_DIRECTION, msg, null);
					if(res.equalsIgnoreCase("y")){
						crit.setPreferenceDirection("max");
					}else if(res.equalsIgnoreCase("n")){
						crit.setPreferenceDirection("min");
					}
					
					templateCriteria.add(crit);
				}
			}
			template.setCriteria(templateCriteria);
		}
	}
	
	private void normalizeServiceTemplateWeights(){
		
		//normalize the general criteria
		System.out.println("SYSTEM: Normalizing Service Template Weights");
		double total = 0;
		for(ServiceTemplate template : data.getServiceTemplates()){
			total = total + template.getWeight();
		}
		
		for(ServiceTemplate template : data.getServiceTemplates()){
			double res = template.getWeight() / total;
			System.out.println("SYSTEM: Total: "+ total +" || value: "+template.getWeight()+"|| res: "+ res);
			template.setWeight((float) res);
		}
	}
	
	private void normalizeCriteriaWeights(){
		
		//normalize the general criteria
		System.out.println("SYSTEM: Normalizing general criteria");
		double total = 0;
		for(Criterion crit : data.getCriteria()){
			total = total + crit.getWeight();
		}
		
		for(Criterion crit : data.getCriteria()){
			double res = crit.getWeight() / total;
			System.out.println("SYSTEM: Total: "+ total +" || value: "+crit.getWeight()+"|| res: "+ res);
			crit.setWeight(res);
		}
		
		//normalize each of the Service Templates
		for(ServiceTemplate template : data.getServiceTemplates()){
			System.out.println("SYSTEM: Normalizing criteria of the serviceTemplate: "+ template.getId());
			double templateTotal = 0;
			for(Criterion crit : template.getCriteria()){
				templateTotal = templateTotal + crit.getWeight();
			}
			
			for(Criterion crit : template.getCriteria()){
				double res = crit.getWeight() / templateTotal;
				System.out.println("SYSTEM: Total: "+ templateTotal +" || value: "+crit.getWeight()+"|| res: "+ res);
				crit.setWeight(res);
			}
		}
	}
	
	
	private double getMaximum(ArrayList<Double> values){
		double max = 0;
		
		for(Double val : values){
			if( val > max)
				max = val;
		}
		
		return max;
		
	}
	
	private double getMinimum(ArrayList<Double> values){
		double min = Double.MAX_VALUE;
		
		for(Double val : values){
			if( val < min)
				min = val;
		}
		
		return min;
		
	}
}
