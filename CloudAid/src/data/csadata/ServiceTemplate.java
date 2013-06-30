/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Component.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.csadata;

import java.util.ArrayList;

public class ServiceTemplate {
	
	private static int componentCount = 0;
	private final String id;
	private String type;
	private String description;
	private ArrayList<Requirement> requirements;
	private ArrayList<Result> resultList;
	private ArrayList<Criterion> criteria;
	private float weight;
	
	
	public ServiceTemplate(){
		this.id = "Comp"+componentCount++;
		requirements = new ArrayList<Requirement>();
		criteria = new ArrayList<Criterion>();
	}
	
	public ServiceTemplate(String id, String type, String description) {
		super();
		requirements = new ArrayList<Requirement>();
		criteria = new ArrayList<Criterion>();
		this.id = "Comp"+componentCount++;
		this.type = type;
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<Requirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(ArrayList<Requirement> requirements) {
		this.requirements = requirements;
	} 
	
	public void addReq(Requirement req){
		this.requirements.add(req);
	}
	
	public ArrayList<Result> getResultList() {
		return resultList;
	}
	public void setResultList(ArrayList<Result> resultList) {
		this.resultList = resultList;
	}
	
	public void addResult(Result res){
		this.resultList.add(res);
	}
	
	public ArrayList<Criterion> getCriteria() {
		return criteria;
	}
	public void setCriteria(ArrayList<Criterion> criteria) {
		this.criteria = criteria;
	}
	
	public void addCrit(Criterion crit){
		this.criteria.add(crit);
	}
	public String getId() {
		return id;
	}
	
	
	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "ServiceTemplate [id=" + id + ", type=" + type
				+ ", description=" + description + ", requirements="
				+ requirements + ", resultList=" + resultList + ", criteria="
				+ criteria + ", weight=" + weight + "]";
	}

	

}
