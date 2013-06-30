/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.servicedata;

import java.util.ArrayList;
import java.util.HashMap;

public class Offering {
	private String id;
	private String name;
	private String description;
	private String URL;
	private HashMap<String, String> attributes;
	private HashMap<String, Double> weightedAttributes;
	private ArrayList<QuantitativeFeature> quantitativeFeatures;
	private ArrayList<QualitativeFeature> qualitativeFeatures;
	
	public Offering(String name, String description){
		this.name = name;
		this.description = description;
		this.attributes = new HashMap();
		this.weightedAttributes = new HashMap();
		this.qualitativeFeatures = new ArrayList<QualitativeFeature>();
		this.quantitativeFeatures = new ArrayList<QuantitativeFeature>();
	}
	
	public Offering(){
		this.attributes = new HashMap();
		this.weightedAttributes = new HashMap();
		this.qualitativeFeatures = new ArrayList<QualitativeFeature>();
		this.quantitativeFeatures = new ArrayList<QuantitativeFeature>();
	}

	public void addQualitativeFeature(QualitativeFeature f){
		this.qualitativeFeatures.add(f);
	}
	
	public void addQuantitativeFeature(QuantitativeFeature f){
		this.quantitativeFeatures.add(f);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	
	public HashMap getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap attributes) {
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public ArrayList<QuantitativeFeature> getQuantitativeFeatures() {
		return quantitativeFeatures;
	}

	public void setQuantitativeFeatures(
			ArrayList<QuantitativeFeature> quantitativeFeatures) {
		this.quantitativeFeatures = quantitativeFeatures;
	}

	public ArrayList<QualitativeFeature> getQualitativeFeatures() {
		return qualitativeFeatures;
	}

	public void setQualitativeFeatures(
			ArrayList<QualitativeFeature> qualitativeFeatures) {
		this.qualitativeFeatures = qualitativeFeatures;
	}
	
	

	public HashMap<String, Double> getWeightedAttributes() {
		return weightedAttributes;
	}

	public void setWeightedAttributes(HashMap<String, Double> weightedAttributes) {
		this.weightedAttributes = weightedAttributes;
	}

	@Override
	public String toString() {
		return "Service [id=" + id + ", name=" + name + ", description="
				+ description + ", URL=" + URL + ", attributes=" + attributes
				+ ", quantitativeFeatures=" + quantitativeFeatures
				+ ", qualitativeFeatures=" + qualitativeFeatures + "]";
	}

	

}
