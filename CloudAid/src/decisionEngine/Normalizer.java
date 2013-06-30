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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import Controller.CloudAid;

import data.csadata.Criterion;
import data.servicedata.Offering;

public class Normalizer {
	
	public Normalizer(){

	}
	
	public ArrayList<Offering> normalize(ArrayList<Offering> alt, ArrayList<Criterion> crit){
		ArrayList<Offering> normalized = alt;
		String preference;
		int type;
		
		for(Criterion criterion : crit){
			//check criterion type
			type = checkCriterionType(alt, criterion);
			
			//check if the criterion has a preference already defined, if not asks if it should have one
			if(!criterion.hasPreference())
				preference = this.checkPreference(alt, criterion);
			
			//normalize according to the criterion type
			if( type == 0){
				//numerical
				normalized = this.calculateDistances(normalized, criterion);
				normalized = this.normalizeNumerical(alt, criterion);
			}else if(type == 1){
				//binary
				normalized = this.normalizeBinary(alt, criterion);
			}else if(type == 2){
				//non-numerical
				//System.out.println(criterion.getName() + " ->non-numerical");
				normalized = this.normalizeNonNumerical(alt, criterion);
			}	
			this.printCriterion(criterion);
		}
		
		this.printData(normalized);
		
		return normalized;	
	}
	
	private String checkPreference(ArrayList<Offering> alternatives, Criterion criterion){
		double value;
		
		if(criterion.getType() == Criterion.NON_NUMERICAL || criterion.getType() == Criterion.BINARY){ //non-numerical and binary criteria must have a preference
			String[] msg = {criterion.getName()};
			String s3 = CloudAid.askData(CloudAid.GET_PREFERENCE_VALUE, msg, null);
			criterion.setPreference(s3);	
		}else if(criterion.getType() == Criterion.NUMERICAL){ //numerical criteria may or may not have a preference. If not the preference is later defined for the maximum value.
			String[] msg = {"Does criterion "+ criterion.getName() +" have a preferable value? (Y/N)"};
			String s = CloudAid.askData(CloudAid.GET_YESNO_ANSWER, msg, null);

			if(s.equalsIgnoreCase("y")){
				while(true){
					String[] msg1 = {criterion.getName()};
					String s2 = CloudAid.askData(CloudAid.GET_PREFERENCE_VALUE, msg1, null);
					try{
						value = Double.parseDouble(s2);
						criterion.setPreference(Double.toString(value));
						break;
					}catch(Exception ex){
						String[] msg2 = {"Please use only numerical values."};
						CloudAid.askData(CloudAid.PROMPT, msg2, null);
					}				
				}
			}else if(s.equalsIgnoreCase("n")){
				//no defined preference. Maximum value is set later (on the numerical normalization) as the preference.
				criterion.setPreference(criterion.getPreferenceDirection().toUpperCase());
			}
		}
		
		return criterion.getPreference();
		
	}
	
	//test if criteria is numerical // 0 if numerical, 1 if binary, 2 if non-numerical 
	private int checkCriterionType(ArrayList<Offering> alternatives, Criterion criterion){	
		for(Offering alt : alternatives){
			try {		
				double value = Double.parseDouble( (String) alt.getAttributes().get(criterion.getName()));
				criterion.setType(Criterion.NUMERICAL);
			} catch (Exception e) {
				String[] msg2 = {"The criterion "+ criterion.getName() + " is not numerical."};
				CloudAid.askData(CloudAid.PROMPT, msg2, null);
				//not numerical
				String[] msg = {"Is this a 2 option criterion? (Y/N)"};
				String s = CloudAid.askData(CloudAid.GET_YESNO_ANSWER, msg, null);			
				if(s.equalsIgnoreCase("y")){
					//binary criterion
					criterion.setType(Criterion.BINARY);
				}else if(s.equalsIgnoreCase("n")){
					//non-numerical criterion
					criterion.setType(Criterion.NON_NUMERICAL);
				}
				return criterion.getType();
			}
		}
		return criterion.getType();
	}
	
	//mode == 0 - get maximum from the original values || mode == 1 - get maximum from the already calculated weighted values
	private double getMaximum(ArrayList<Offering> alternatives, String criterionName, int mode){
		double max = 0;
		
		for(Offering alt : alternatives){
			double temp = 0;
			if(mode == 0){
				temp = Double.parseDouble((String) alt.getAttributes().get(criterionName));
			}else if(mode == 1){
				temp = alt.getWeightedAttributes().get(criterionName);
			}
				
			if( temp > max)
				max = temp;
		}
		
		return max;
		
	}
	
	//mode == 0 - get maximum from the original values || mode == 1 - get maximum from the already calculated weighted values
	private double getMinimum(ArrayList<Offering> alternatives, String criterionName, int mode){
		double min = Double.MAX_VALUE;
		
		for(Offering alt : alternatives){
			double temp = 0;
			if(mode == 0){
				temp = Double.parseDouble((String) alt.getAttributes().get(criterionName));
			}else if(mode == 1){
				temp = alt.getWeightedAttributes().get(criterionName);
			}
			if( temp < min)
				min = temp;
		}
		
		return min;
		
	}
	
	private ArrayList<Offering> calculateDistances(ArrayList<Offering> alt, Criterion criterion){
		ArrayList<Offering> normalized = alt;
		double preference; // the value to compare
		
		//establish the preference depending on the preference direction
		if(criterion.getPreference().equalsIgnoreCase("max"))
			preference = this.getMaximum(normalized, criterion.getName(), 0); 
		else if(criterion.getPreference().equalsIgnoreCase("min"))
			preference = this.getMinimum(normalized, criterion.getName(), 0);
		else 
			preference = Double.parseDouble(criterion.getPreference());
		
		criterion.setPreference(Double.toString(preference));
		for(Offering alternative : normalized){
			double res = Math.abs(preference - Double.parseDouble((String) alternative.getAttributes().get(criterion.getName())));
			alternative.getWeightedAttributes().put(criterion.getName(), res);
			System.out.println("SYSTEM: "+alternative.getName() + " : " + criterion.getName() + "="+res);
		}
		
		return normalized;
	}
	
	private ArrayList<Offering> normalizeNumerical(ArrayList<Offering> alt, Criterion criterion){
		ArrayList<Offering> normalized = alt;
		
		double min = this.getMinimum(alt, criterion.getName(), 1);
		double max = this.getMaximum(alt, criterion.getName(), 1);
		
		/*
		if(min < 0){
			for(Service alternative : normalized){
				double res = (Double.parseDouble((String) alternative.getAttributes().get(criterion.getName())) + Math.abs(min));
				alternative.getAttributes().put(criterion.getName(), Double.toString(res));
				//System.out.println(alternative.getName() + " : " + criterion.getName() + "="+res);
			}
			
			max = max + Math.abs(min);
			min = min + Math.abs(min);
		}
		*/
		
		System.out.println("SYSTEM: NORMALIZING---");
		for(Offering alternative : normalized){
			double res = 0;
			if(max != min)
				res = (((alternative.getWeightedAttributes().get(criterion.getName())) - min) / (max-min));
			res = (res * -1) + 1; 
			System.out.println("SYSTEM: min: "+min+ "|| max: "+ max + "|| value: "+alternative.getWeightedAttributes().get(criterion.getName())+"|| res: "+ res);
			alternative.getWeightedAttributes().put(criterion.getName(), res);
			//System.out.println(alternative.getName() + " : " + criterion.getName() + "="+res);
		}
		
		return normalized;
	}
	
	private ArrayList<Offering> normalizeBinary(ArrayList<Offering> alternatives, Criterion criterion){
		ArrayList<Offering> normalized = alternatives;
		
		String best = criterion.getPreference();
		String worst = null;

		double res;
		for(Offering alternative : normalized){
			String value = (String)alternative.getAttributes().get(criterion.getName());
			
			if(value.equalsIgnoreCase(best)){
				res = 1;
			}else{
				res = 0;					
			}
			
			alternative.getWeightedAttributes().put(criterion.getName(), res);
			System.out.println(alternative.getName() + " : " + criterion.getName() + "="+res);
		}
		
		return normalized;
	}
	
	private ArrayList<Offering> normalizeNonNumerical(ArrayList<Offering> alternatives, Criterion criterion){
		ArrayList<Offering> normalized = alternatives;
		HashMap temps = new HashMap();
		Double tempValue;
		String preference = criterion.getPreference();
		
		for(Offering alt : normalized){			
			String tempName = (String) alt.getAttributes().get(criterion.getName());
			//System.out.println("Normalizing alternative: "+tempName);
			if(temps.containsKey(tempName)){
				//System.out.println("contains");
				tempValue = (Double)temps.get(tempName);
			}else{
				String[] msg = {tempName, preference};
				tempValue = Double.parseDouble(CloudAid.askData(CloudAid.GET_DISTANCE_VALUE, msg, null));
				temps.put(tempName, tempValue);
			}
			alt.getWeightedAttributes().put(criterion.getName(), tempValue);
			//System.out.println(alt.getName() + " : " + criterion.getName() + "="+tempValue);
		}
		
		normalized = this.normalizeNumerical(normalized, criterion);
		
		return normalized;
	}
	
	private void printData(ArrayList<Offering> normalized){
		
		for(Offering serv : normalized){
			System.out.println(serv.getName());
			Iterator it = serv.getWeightedAttributes().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        System.out.print("SYSTEM: "+pairs.getKey() + " = " + pairs.getValue()+"    ");
		    }
		    System.out.println();
		}
	}
	
	private void printCriterion(Criterion crit){
		System.out.println("SYSTEM: "+crit.toString());
		
	}
	
	public void addPreferences(ArrayList<Offering> alternatives, ArrayList<Criterion> crit){
		
		for(Criterion criterion : crit){
			this.checkCriterionType(alternatives, criterion);
			this.checkPreference(alternatives, criterion);
		}
		
		
		
	}

}
