/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.servicedata;

import data.servicedata.Enumerator.QUANT_FEATURE;
import data.servicedata.Enumerator.UNIT_OF_MEASUREMENT;

public class QuantitativeFeature {
	private String name;
	private String description;
	private float valueFloat;
	private Enumerator.UNIT_OF_MEASUREMENT unitOfMeasurement;
	private Enumerator.QUANT_FEATURE type;
	
	
	public Enumerator.QUANT_FEATURE getType() {
		return type;
	}
	public void setType(Enumerator.QUANT_FEATURE type) {
		this.type = type;
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
	public float getValueFloat() {
		return valueFloat;
	}
	public void setValueFloat(float valueFloat) {
		this.valueFloat = valueFloat;
	}
	
	public Enumerator.UNIT_OF_MEASUREMENT getUnitOfMeasurement() {
		return unitOfMeasurement;
	}
	public void setUnitOfMeasurement(Enumerator.UNIT_OF_MEASUREMENT unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}
	@Override
	public String toString() {
		return "QuantitativeFeature [name=" + name + ", description="
				+ description + ", valueFloat=" + valueFloat
				+ ", unitOfMeasurement=" + unitOfMeasurement + ", type=" + type
				+ "]";
	}

	
	

}
