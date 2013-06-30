/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: TemplateSim.java, Project: CloudAid, 30 May 2013 Author: Jorge Araújo
*/
package data.simulation;

import java.util.ArrayList;

/**
 * @author Jorge
 *
 */
public class TemplateSim {
	private int nAlternatives;
	private double weight;
	private ArrayList<Double> performances;
	private ArrayList<Double> prices;

	public TemplateSim(int n, double w, ArrayList<Double> performances, ArrayList<Double> prices){
		this.nAlternatives = n;
		this.weight = w;
		this.performances = performances;
		this.prices = prices;
	}

	public int getnAlternatives() {
		return nAlternatives;
	}

	public void setnAlternatives(int nAlternatives) {
		this.nAlternatives = nAlternatives;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public ArrayList<Double> getPerformances() {
		return performances;
	}

	public void setPerformances(ArrayList<Double> performances) {
		this.performances = performances;
	}

	public ArrayList<Double> getPrices() {
		return prices;
	}

	public void setPrices(ArrayList<Double> prices) {
		this.prices = prices;
	}
	
	
	
}
