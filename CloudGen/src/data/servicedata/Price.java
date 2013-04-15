/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Price.java, Project: CloudGen, 13 Apr 2013 Author: Jorge Araújo
*/

package data.servicedata;

import java.util.ArrayList;

import uk.co.jemos.podam.api.RandomDataProviderStrategy;

public class Price {
	
	private final float FEATURE_PRICE_MIN = (float) 1.5;
	private final float FEATURE_PRICE_MAX = (float) 2.5;
	
	private final String currency = "EUR";
	
	private ArrayList<QualitativeFeature> freeQualitativeFeatures;
	private ArrayList<QuantitativeFeature> freeQuantitativeFeatures;
	private ArrayList<GroupedPrice> prices;
	
	public Price(){
		this.freeQualitativeFeatures = new ArrayList<QualitativeFeature>();
		this.freeQuantitativeFeatures = new ArrayList<QuantitativeFeature>();
		prices = new ArrayList<GroupedPrice>();
	}
	
	
	
	public ArrayList<QualitativeFeature> getFreeQualitativeFeatures() {
		return freeQualitativeFeatures;
	}



	public void setFreeQualitativeFeatures(
			ArrayList<QualitativeFeature> freeQualitativeFeatures) {
		this.freeQualitativeFeatures = freeQualitativeFeatures;
	}



	public ArrayList<QuantitativeFeature> getFreeQuantitativeFeatures() {
		return freeQuantitativeFeatures;
	}



	public void setFreeQuantitativeFeatures(
			ArrayList<QuantitativeFeature> freeQuantitativeFeatures) {
		this.freeQuantitativeFeatures = freeQuantitativeFeatures;
	}



	public ArrayList<GroupedPrice> getPrices() {
		return prices;
	}



	public void setPrices(ArrayList<GroupedPrice> prices) {
		this.prices = prices;
	}



	public String getCurrency() {
		return currency;
	}



	//generates random free features from the features list of the service random prices for each feature
	public void generateRandomIndividualPrices(ServiceData service){
		for(QualitativeFeature feature : service.getQualitativeFeatures()){
			int free = RandomDataProviderStrategy.getInstance().getIntegerInRange(0, 100, null);
			if(free > 50){
				this.freeQualitativeFeatures.add(feature);
			}else{
				ArrayList<QualitativeFeature> qualFeat = new ArrayList<QualitativeFeature>();
				ArrayList<QuantitativeFeature> quantFeat = new ArrayList<QuantitativeFeature>();
				qualFeat.add(feature);
				float price = RandomDataProviderStrategy.getInstance().getFloatInRange(FEATURE_PRICE_MIN, FEATURE_PRICE_MAX, null);
				GroupedPrice gp = new GroupedPrice(quantFeat, qualFeat, price);
				this.prices.add(gp);
			}
		}
		
		for(QuantitativeFeature feature : service.getQuantitativeFeatures()){
			int free = RandomDataProviderStrategy.getInstance().getIntegerInRange(0, 100, null);
			if(free > 50){
				this.freeQuantitativeFeatures.add(feature);
			}else{
				ArrayList<QualitativeFeature> qualFeat = new ArrayList<QualitativeFeature>();
				ArrayList<QuantitativeFeature> quantFeat = new ArrayList<QuantitativeFeature>();
				quantFeat.add(feature);
				float price = RandomDataProviderStrategy.getInstance().getFloatInRange(FEATURE_PRICE_MIN, FEATURE_PRICE_MAX, null);
				GroupedPrice gp = new GroupedPrice(quantFeat, qualFeat, price);
				this.prices.add(gp);
			}
		}
		

	}
	
	public void printPrice(){
		int count = 0;
		System.out.println("Free Qualitative Features:");
		for(QualitativeFeature qf : this.freeQualitativeFeatures){
			System.out.println("   - "+ qf.toString());
		}
		System.out.println("Free Quantitative Features:");
		for(QuantitativeFeature qf : this.freeQuantitativeFeatures){
			System.out.println("   - "+ qf.toString());
		}
		System.out.println("Paid Features:");
		for(GroupedPrice gp : this.prices){
			System.out.println("Group "+ count++ +":");
			gp.print();
		}
		
	}

}
