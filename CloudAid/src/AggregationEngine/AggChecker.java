/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: AggChecker.java, Project: CloudAid, 2 May 2013 Author: Jorge Araújo
*/
package AggregationEngine;

import java.util.ArrayList;

import data.csadata.Requirement;
import data.csadata.Result;
import data.csadata.ServiceTemplate;
import data.servicedata.Offering;

/**
 * @author Jorge
 *
 */
public class AggChecker {

	
	public AggChecker(){
		
	}
	
	public static boolean checkAdmissability(ArrayList<Result> offerings, ArrayList<Requirement> generalRequirements ){
		
		boolean admissable = false;
		
		for(Requirement req : generalRequirements){
			if(req.getQualType() != null){
				System.out.println("Checking a qualitative requirement");
			}else if(req.getQuantType() != null){
				System.out.println("Checking a quantitative requirement");
			}else{
				admissable = checkPrice(offerings, req);
			}
		}
		
		return admissable;
	}
	
	private static boolean checkRequirement(ArrayList<Result> offerings, Requirement req){
		return true;
	}
	
	private static boolean checkPrice(ArrayList<Result> offerings, Requirement req){
		double total = 0;
		
		//add all the price values
		for(Result off : offerings){
			total = total + Double.parseDouble((String) off.getService().getAttributes().get("price"));
			
		}
		
		//System.out.print(" price="+ total+" ");
		//check if the price is in the limit
		if(req.isExclusivityMax()){
			if(total <= req.getMax())
				return true;
		}else{
			if(total >= req.getMin())
				return true;
		}
		
		return false;
	}
}
