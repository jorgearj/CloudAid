
/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: CSAData.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.csadata;

import java.util.ArrayList;

public class CSAData {
	
	private ArrayList<ServiceTemplate> serviceTemplates;
	private ArrayList<Requirement> requirements;
	private ArrayList<Criterion> criteria;
	private int evalCode; // 0 = OK, 1 = Not enough components
	
	public CSAData(){
		this.serviceTemplates = new ArrayList<ServiceTemplate>();
		this.requirements = new ArrayList<Requirement>();
		this.criteria = new ArrayList<Criterion>();
	}
	
	public CSAData(ArrayList<ServiceTemplate> serviceTemplates, ArrayList<Requirement> requirements, ArrayList<Criterion> criteria) {
		super();
		this.serviceTemplates = serviceTemplates;
		this.requirements = requirements;
		this.criteria = criteria;
	}
	
	public ArrayList<ServiceTemplate> getServiceTemplates() {
		return serviceTemplates;
	}
	public void setServiceTemplates(ArrayList<ServiceTemplate> components) {
		this.serviceTemplates = components;
	}
	public ArrayList<Requirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(ArrayList<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	public ArrayList<Criterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(ArrayList<Criterion> criteria) {
		this.criteria = criteria;
	}
	
	public void addComponent(ServiceTemplate comp){
		this.serviceTemplates.add(comp);
	}
	
	public void addReq(Requirement req){
		this.requirements.add(req);
	}
	
	public void addCrit(Criterion cri){
		this.criteria.add(cri);
	}

	public int getEvalResult() {
		return evalCode;
	}

	public void setEvalResult(int evalCode) {
		this.evalCode = evalCode;
	}
	
	

}
