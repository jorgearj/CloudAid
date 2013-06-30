/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Requirement.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.csadata;

import data.servicedata.Enumerator;

public class Requirement{
	
	private static int count;
	
	private String id; 
	private int priority; // from 1 to 5, 1 being high priority and 5 being low priority
	private String description; 
	private Enumerator.QUAL_FEATURE qualType;
	private Enumerator.QUANT_FEATURE quantType;	
	private float min;
	private float max;
	private boolean needed; //true if the must exist this feature, false if the absense of this feature is not exclusive.
	private boolean positive = true; // true (default) if the user wants to have this feature in the service, false if he does not want this feature in the service.
	private String qualValue;
	private boolean criterion; // if true means that this requirement is also on criterion. If false means that this requirement is exclusive.
	private boolean exclusive = false; // this field is automatically set when one of <min, max, positive> fields are set
	private boolean exclusivityMax; //true if we want to use the max field, false if we want to use the min field
	
	public Requirement(){
		count++;
		id = "Req"+count;
	}

	public Requirement(String description, Enumerator.QUAL_FEATURE qualType, Enumerator.QUANT_FEATURE quantType){
		count++;
		id = "Req"+count;
		if(qualType != null && quantType != null){
			this.qualType = qualType;
			this.quantType = null;
		}
		else{
			this.qualType = qualType;
			this.quantType = quantType;
		}
		
	}
	
	public String getID(){
		return this.id;
	}
	
	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	public Enumerator.QUAL_FEATURE getQualType() {
		return qualType;
	}


	public void setQualType(Enumerator.QUAL_FEATURE qualType) {
		this.qualType = qualType;
		this.quantType = null;
	}


	public Enumerator.QUANT_FEATURE getQuantType() {
		return quantType;
	}


	public void setQuantType(Enumerator.QUANT_FEATURE quantType) {
		this.quantType = quantType;
		this.qualType = null;
	}


	public float getMin() {
		return min;
	}


	public void setMin(float min) {
		this.min = min;
		this.exclusive = true;
	}


	public float getMax() {
		return max;
	}


	public void setMax(float max) {
		this.max = max;
		this.exclusive = true;
	}


	public boolean isPositive() {
		return positive;
	}


	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public boolean isCriterion() {
		return criterion;
	}

	public void setCriterion(boolean criterion) {
		this.criterion = criterion;
	}

	public boolean isExclusive() {
		return exclusive;
	}
	
	public boolean isExclusivityMax(){
		return this.exclusivityMax;
	}

	public void setExclusivityMax(boolean exclusivityMax) {
		this.exclusivityMax = exclusivityMax;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public String getQualValue() {
		return qualValue;
	}

	public void setQualValue(String qualValue) {
		this.qualValue = qualValue;
	}

	public boolean isNeeded() {
		return needed;
	}

	public void setNeeded(boolean needed) {
		this.needed = needed;
	}

	@Override
	public String toString() {
		return "Requirement [id=" + id + ", priority=" + priority
				+ ", description=" + description + ", qualType=" + qualType
				+ ", quantType=" + quantType + ", exclusivityMax="
				+ exclusivityMax + ", min=" + min + ", max=" + max
				+ ", needed=" + positive + ", qualValue=" + qualValue
			    + ", criterion=" + criterion
				+ ", exclusive=" + exclusive + "]";
	}
}
