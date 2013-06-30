/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: UserInput.java, Project: CloudAid, 19 Apr 2013 Author: Jorge Araújo
*/
package gui;

import java.util.ArrayList;
import java.util.Scanner;

import Controller.CloudAid;

import data.csadata.CSAData;
import data.csadata.ServiceTemplate;
import data.csadata.Criterion;
import data.csadata.Requirement;
import data.servicedata.Enumerator.QUAL_FEATURE;
import data.servicedata.Enumerator.QUANT_FEATURE;

/**
 * @author Jorge
 *
 */
public class UserInput {
	private int methodID;
	
	public UserInput(int methodID){
		this.methodID = methodID;
	}
	
	public CSAData newCSA(){
		CSAData data = new CSAData();
		ArrayList<ServiceTemplate> components = new ArrayList<ServiceTemplate>();
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		ArrayList<Criterion> criteria = new ArrayList<Criterion>();
		
		while(true){
			System.out.println("CSA DATA:");
			System.out.println("1 - New Service Template");
			System.out.println("2 - New Requirement");
			System.out.println("3 - New Criterion");
			System.out.println("0 - DONE!!!");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			try {
				int opt = Integer.parseInt(s);
				switch (opt) {
					case 0:					
						data.setServiceTemplates(components);
						data.setRequirements(reqs);
						data.setCriteria(criteria);
						
						return data;
		            case 1:
		            	components.add(this.newComponent());
		            	break;
		            case 2:
		            	System.out.println("You are adding a global Requirement. These requirements must be fulfilled by all ServiceTemplates.");
		            	Requirement req = this.newRequirement();
		            	if(req != null)
		            		reqs.add(req);
		            	break;
		            case 3:
		            	System.out.println("You are adding a global Criterion. These criteria will be used by all ServiceTemplates.");
		            	criteria.add(this.newCriterion());
		            	break;
		            default: break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Choose one of the options!!!");
			}
		}
	}
	
	private ServiceTemplate newComponent(){
		ServiceTemplate comp = new ServiceTemplate();
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		ArrayList<Criterion> criteria = new ArrayList<Criterion>();
		while(true){
			System.out.println("SERVICE TEMPLATE DATA:");
			System.out.println("1 - Insert Service Template Data");
			System.out.println("2 - New Requirement");
			System.out.println("3 - New Criterion");
			System.out.println("0 - DONE!!!");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			try {
				int opt = Integer.parseInt(s);
				switch (opt) {
					case 0:
						comp.setRequirements(reqs);
						comp.setCriteria(criteria);
						
						return comp;
		            case 1:
		            	//ask for component data
		            	System.out.println("Please specify the Service Template Type: ");
		            	
		            	in = new Scanner(System.in);
		    			String type = in.nextLine();
		    			comp.setType(type);
		    			System.out.println("Please specify the Service Template Description: ");
		    			
		            	in = new Scanner(System.in);
		    			String description = in.nextLine();
		    			comp.setDescription(description);
		    			if(this.methodID == CloudAid.SAW){
							while(true){
								System.out.println("Please specify the Service Template decision weight:");
								String weight = in.nextLine();
								try {
									Float weightValue = Float.parseFloat(weight);
									comp.setWeight(weightValue);
									break;
								} catch (NumberFormatException e) {
									System.out.println("Please insert only numerical values!");
								}
							}
						}
		            	break;
		            case 2:
		            	Requirement req = this.newRequirement();
		            	if(req != null)
		            		reqs.add(req);
		            	break;
		            case 3:
		            	criteria.add(this.newCriterion());
		            	break;
		            default: break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Choose one of the options!!!");
			}
		}
	}
	
	private Requirement newRequirement(){
		Requirement req = new Requirement();
		Scanner in;
		
		//ask for requirement data
		while(true){
	    	System.out.println("Please specify the Requirement Type from the list of Cloud Concepts, or write 'Price' for a Price requirement: ");
	    	in = new Scanner(System.in);
			String type = in.nextLine();
			int res = this.checkCloudConcept(type);
			if(res != -1){
				//add the concept
				if(res == 0){
					//it is a qualitative concept
					req.setQualType(QUAL_FEATURE.get(type));
					while(true){
						System.out.println("Do you want to specify a particular value for this service feature? (Y/N)");
						String needed = in.nextLine();
						if(needed.equalsIgnoreCase("y")){
							System.out.println("Please specify the value:");
							String qualValue = in.nextLine();
							req.setQualValue(qualValue);
							break;
						}else if (needed.equalsIgnoreCase("n")){
							break;
						}else{
							System.out.println("Please answere only with Y/N");
						}
					}
					while(true){
						System.out.println("Is this a feature that if not present excludes the Service? (Y/N)");
						String needed = in.nextLine();
						if(needed.equalsIgnoreCase("y")){
							req.setNeeded(true);
							while(true){
								System.out.println("Is this a feature that you want the service to have (Y) or you want the service to don't have(N)? (Y/N)");
								String positive = in.nextLine();
								if(needed.equalsIgnoreCase("y")){
									req.setPositive(true);
									break;
								}else if (needed.equalsIgnoreCase("n")){
									req.setPositive(false);
									break;
								}else{
									System.out.println("Please answere only with Y/N");
								}
							}
							break;
						}else if (needed.equalsIgnoreCase("n")){
							req.setPositive(false);
							break;
						}else{
							System.out.println("Please answere only with Y/N");
						}
					}
				}else{
					//it is a quantitative concept
					if(res == 1)
						//not a price requirement
						req.setQuantType(QUANT_FEATURE.get(type));
					while(true){
						System.out.println("Does this requirement has a limit value? (Y/N)");
						String s = in.nextLine();
						if(s.equalsIgnoreCase("y")){
							while(true){
								System.out.println("Please specify the limit:");
								String limit = in.nextLine();
								try {
									Float limitValue = Float.parseFloat(limit);
									while(true){
										System.out.println("Is it a minimum or maximum limit? (min/max)");
										String limitType = in.nextLine();
										if(limitType.equalsIgnoreCase("min")){
											req.setMin(limitValue);
											req.setExclusivityMax(false);
											break;
										}else if (limitType.equalsIgnoreCase("max")){
											req.setMax(limitValue);
											req.setExclusivityMax(true);
											break;
										}else{
											System.out.println("Please answere only with min/max");
										}
									}
									break;
								} catch (NumberFormatException e) {
									System.out.println("Please insert only numerical value!");
								}
							}
							break;
						}else if (s.equalsIgnoreCase("n")){
							while(true){
								System.out.println("Is this a feature that if not present excludes the Service? (Y/N)");
								String needed = in.nextLine();
								if(needed.equalsIgnoreCase("y")){
									req.setNeeded(true);
									break;
								}else if (needed.equalsIgnoreCase("n")){
									req.setPositive(false);
									break;
								}else{
									System.out.println("Please answere only with Y/N");
								}
							}
							break;
						}else{
							System.out.println("Please answere only with Y/N");
						}
					}
				}
				System.out.println("Please specify a requirement description: ");
		    	in = new Scanner(System.in);
				String description = in.nextLine();
				req.setDescription(description);
				while(true){
					System.out.println("Will this requirement also be decision criterion? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						req.setCriterion(true);
						break;
					}else if (s.equalsIgnoreCase("n")){
						req.setCriterion(false);
						break;
					}else{
						System.out.println("Please answere only with Y/N");
					}
				}
				return req;
			}else{
				while(true){
					System.out.println("The type you specified is not recognized! Want to try again? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						break;
					}else if (s.equalsIgnoreCase("n")){			
						return null;
					}else{
						System.out.println("Please answere only with Y/N");
					}
				}
			}
			
		}
	}
	
	private Criterion newCriterion(){
		Criterion crit = new Criterion();
		Scanner in;
		while(true){
	    	System.out.println("Please specify the Criterion Type from the list of Cloud Concepts: ");
	    	in = new Scanner(System.in);
			String type = in.nextLine();
			
			if(this.checkCloudConcept(type) != -1){
				crit.setName(type);
				if(this.methodID == CloudAid.SAW){
					while(true){
						System.out.println("Please specify the criterion decision weight:");
						String weight = in.nextLine();
						try {
							Float weightValue = Float.parseFloat(weight);
							crit.setWeight(weightValue);
							break;
						} catch (NumberFormatException e) {
							System.out.println("Please insert only numerical values!");
						}
					}
				}
				while(true){
					System.out.println("Do you want to maximize the Criterion value? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						crit.setPreferenceDirection("max");
						return crit;
					}else if (s.equalsIgnoreCase("n")){
						crit.setPreferenceDirection("min");
						return crit;
					}else{
						System.out.println("Please answere only with Y/N");
					}
				}
			}else{
				while(true){
					System.out.println("The type you specified is not recognized! Want to try again? (Y/N)");
					String s = in.nextLine();
					if(s.equalsIgnoreCase("y")){
						break;
					}else if (s.equalsIgnoreCase("n")){
						
						return null;
					}else{
						System.out.println("Please answere only with Y/N");
					}
				}
			}
			
		}
	}
	
	//returns -1 if no type fount, 0 if it is a qualitative feature and 1 if it is a Quantitative feature, or 2 if it is  price concept
	private int checkCloudConcept(String concept){
		if(QUAL_FEATURE.get(concept) != null){
			return 0;
		}else if(QUANT_FEATURE.get(concept) != null){
			return 1;
		}else if(concept.equalsIgnoreCase("price")){
			return 2;
		}else
			return -1;
	}

}
