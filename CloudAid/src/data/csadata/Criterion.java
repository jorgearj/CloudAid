/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Criterion.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.csadata;

public class Criterion{
	
	public static final int NUMERICAL = 0;
	public static final int BINARY = 1;
	public static final int NON_NUMERICAL = 2;
	
	private static int criterionCount = 0;
	
	private final String id;
	private String name;
	private double weight;
	private String preferenceDirection;
	private String preference;
	private int type = -1;
	
	
	public Criterion(){
		this.id = "Crit"+criterionCount++;
	}
	
	public Criterion(String name){
		this.id = "Crit"+criterionCount++;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double wheight) {
		this.weight = wheight;
	}
	

	public String getPreferenceDirection() {
		return preferenceDirection;
	}

	public void setPreferenceDirection(String preferenceDirection) {
		this.preferenceDirection = preferenceDirection;
	}

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Criterion [id=" + id + ", name=" + name + ", wheight="
				+ weight + ", preferenceDirection=" + preferenceDirection
				+ ", preference=" + preference + ", type=" + type + "]";
	}

	public boolean isNumerical(){
		if(this.type == Criterion.NUMERICAL)
			return true;
		
		return false;
	}
	
	public boolean hasPreference(){
		if(this.preference != null)
			return true;
		
		return false;
	}

}
