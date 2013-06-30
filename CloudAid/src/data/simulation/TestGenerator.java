/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: TestGenerator.java, Project: CloudAid, 8 Jun 2013 Author: Jorge Araújo
*/
package data.simulation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.Charset;
import java.util.ArrayList;


import uk.co.jemos.podam.api.RandomDataProviderStrategy;

/**
 * @author Jorge
 *
 */
public class TestGenerator {

	public static ArrayList<Test> generate(int nTests){
		ArrayList<Test> tests = new ArrayList<Test>();
		double initialPrice = 1000.0;
		double initialPerformance = 1.0;
		
		for(int i=0; i < nTests; i++){
			int nTemplates = RandomDataProviderStrategy.getInstance().getIntegerInRange(6, 6, null);
			ArrayList<TemplateSim> templates = new ArrayList<TemplateSim>();
			
			//generate template data
			for(int t = 0; t < nTemplates; t++){
				int nAlternatives = RandomDataProviderStrategy.getInstance().getIntegerInRange(5, 5, null);
				double weight = RandomDataProviderStrategy.getInstance().getDoubleInRange(1.0, 5.0, null);
				ArrayList<Double> prices = new ArrayList<Double>();
				ArrayList<Double> performances = new ArrayList<Double>();
				
				double tempPerformance = initialPerformance;
				double tempPrice;
				for(int p =0; p < nAlternatives; p++){
					//performance
					performances.add(tempPerformance);
					tempPerformance = RandomDataProviderStrategy.getInstance().getDoubleInRange(0.0, tempPerformance, null);			
					//price
					tempPrice = RandomDataProviderStrategy.getInstance().getDoubleInRange(0.0, initialPrice, null);		
					prices.add(tempPrice);
				}
				TemplateSim template = new TemplateSim(nAlternatives,weight,performances,prices);
				templates.add(template);
			}
			
			double priceLimit = RandomDataProviderStrategy.getInstance().getDoubleInRange(0.0, (nTemplates)*initialPrice, null);
			Test test = new Test(priceLimit, templates);
			tests.add(test);
		}
		
		return tests;
	}
	
	public static ArrayList<Test> generateFromFile(String filename){
		ArrayList<Test> tests = new ArrayList<Test>();
		String line;
		
		try {
			FileInputStream fis = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			
			while ((line = br.readLine()) != null) {
			    System.out.println("Outer While: "+line);
				if(line.contains("Test Number:")){
					double priceLimit = -1;
					ArrayList<TemplateSim> templates = new ArrayList<TemplateSim>();
					while ((line = br.readLine()) != null) {				
						if(line.contains("--------")){
							tests.add(new Test(priceLimit, templates));
							break;
						}
						if(line.contains("Price limit")){
					    	System.out.println(line);
					    	String[] price = line.split("Price limit: ");
					    	priceLimit = Double.parseDouble(price[1]);
					    }else if(line.contains("- Template ")){
					    	int nAlternatives = -1;
					    	double weight = -1; 
					    	ArrayList<Double> performances = new ArrayList<Double>();
					    	ArrayList<Double> prices = new ArrayList<Double>();
					    	while ((line = br.readLine()) != null) {
					    		if(line.contains("- #Alternatives: ")){
					    			String[] tmpNAlt = line.split("- #Alternatives: ");
					    			nAlternatives = Integer.parseInt(tmpNAlt[1]);
					    		}else if(line.contains("- Weight: ")){
					    			String[] tmpWeight = line.split("- Weight: ");
					    			weight = Double.parseDouble(tmpWeight[1]);
					    		}else if(line.contains("- Prices: ")){
					    			String[] tmpPrices = line.split("- Prices: ");
					    			tmpPrices = tmpPrices[1].split(", ");
					    			for(int i = 0; i < tmpPrices.length; i++){
					    				String temp = tmpPrices[i].replace("[", "");
					    				temp = temp.replace("]", "");
					    				prices.add(Double.parseDouble(temp));
					    			}
					    		}else if(line.contains("- Performances: ")){
					    			String[] tmpPerformances = line.split("- Performances: ");
					    			tmpPerformances = tmpPerformances[1].split(", ");
					    			for(int i = 0; i < tmpPerformances.length; i++){
					    				String temp = tmpPerformances[i].replace("[", "");
					    				temp = temp.replace("]", "");
					    				performances.add(Double.parseDouble(temp));
					    			}
					    			templates.add(new TemplateSim(nAlternatives, weight, performances, prices));
					    			break;
					    		}
					    	}
					    }
					}
				}
			}

			// Done with the file
			br.close();
			System.out.println("TESTES: ");
			int count = 0;
			//tests.remove(0);
			for(Test t : tests){
				System.out.println(count++);
				TestGenerator.printTestData(t);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR: Unable to read test file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tests;
	}
	
	public static void printTestData(Test test) {
		System.out.println("Price limit: " + test.getPriceLimit());
		System.out.println("#Templates = " + test.getTemplates().size());
		System.out.println("Templates: ");
		int i=0;
		int combin = 1;
		for(TemplateSim template : test.getTemplates()){
			System.out.println(" - Template " + (i++)+":");
			System.out.println("   - #Alternatives: "+ template.getnAlternatives());
			combin = combin * template.getnAlternatives();
			System.out.println("   - Weight: "+ template.getWeight());
			System.out.println("   - Prices: "+ template.getPrices());
			System.out.println("   - Performances: "+ template.getPerformances());
		}
		System.out.println("Number of Combinations: "+ combin);
	}
	public static void main(String[] args) {
		TestGenerator.generateFromFile("TestsToRun.txt");
	}
}
