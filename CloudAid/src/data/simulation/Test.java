/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Test.java, Project: CloudAid, 8 Jun 2013 Author: Jorge Araújo
*/
package data.simulation;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Jorge
 *
 */
public class Test {

	private double priceLimit;
	private ArrayList<TemplateSim> templates;
	
	public Test(double priceLimit, ArrayList<TemplateSim> templates){
		this.priceLimit = priceLimit;
		this.templates = templates;
	}
	
	public double getPriceLimit() {
		return priceLimit;
	}
	public void setPriceLimit(double priceLimit) {
		this.priceLimit = priceLimit;
	}

	public ArrayList<TemplateSim> getTemplates() {
		return templates;
	}

	public void setTemplates(ArrayList<TemplateSim> templates) {
		this.templates = templates;
	}

	
	public void printTestData(PrintWriter out) {
		out.println("Price limit: " + this.priceLimit);
		out.println("#Templates = " + this.templates.size());
		out.println("Templates: ");
		int i=0;
		int combin = 1;
		for(TemplateSim template : this.templates){
			out.println(" - Template " + (i++)+":");
			out.println("   - #Alternatives: "+ template.getnAlternatives());
			combin = combin * template.getnAlternatives();
			out.println("   - Weight: "+ template.getWeight());
			out.println("   - Prices: "+ template.getPrices());
			out.println("   - Performances: "+ template.getPerformances());
		}
		out.println("Number of Combinations: "+ combin);
	}
	
	
	
}
