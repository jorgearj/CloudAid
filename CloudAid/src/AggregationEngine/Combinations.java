/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Combinations.java, Project: CloudGen, 17 Apr 2013 Author: Jorge Araújo
*/
package AggregationEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import Controller.CloudAid;

import data.csadata.CSAData;
import data.csadata.Requirement;
import data.csadata.Result;
import data.csadata.ServiceTemplate;
import data.servicedata.Offering;
import data.simulation.AlgorithmSimulation;
import data.simulation.TemplateSim;
import data.simulation.Test;

/**
 * @author Jorge
 *
 */
public class Combinations {
	
	private ArrayList<ArrayList<Result>> offeringsCombinations;
	private ArrayList<ArrayList<Result>> admissableCombinations;
	private CSAData data;
	//incomparability delta
	private double incDelta;
	
	//Algorithms
	public final static int BFS_NOINC = 0;
	public final static int BFS_INC = 1;
	
	
	
	//testing variables
	long algorithmTime = 0;
	double avgDegree = 0.0;
	double avgDepth = 0.0;
	int visitedNodes = 0;

	public Combinations(CSAData data, double incDelta){
		this.offeringsCombinations = new ArrayList<ArrayList<Result>>();	
		this.admissableCombinations = new ArrayList<ArrayList<Result>>();	
		this.data = data;
		this.incDelta = incDelta;
		//System.out.println(this.incDelta);
	}
	
	//algorithm = 0 -> no comparations algorithm // algorithm = 1 -> Comparations algorithm 
	public ArrayList<ArrayList<Result>> computeCombinations(int algorithm){
		
		System.out.println("Computing alternative combinations...");
		//combinations for the service
		ArrayList<ArrayList<Result>> templateAlternatives = new ArrayList<ArrayList<Result>>();
		
		for(ServiceTemplate template : data.getServiceTemplates()){
			//get all possible options for this attribute
			ArrayList<Result> alternatives = template.getResultList();
			
			templateAlternatives.add(alternatives);		
		}
		
		//System.out.println("OPTIONS");
		//this.printResult(templateAlternatives);
		ArrayList<ArrayList<Integer>> tested = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> admissable = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> indexes = new ArrayList<Integer>(Collections.nCopies(templateAlternatives.size(), 0));
		long start = 0;
		long end = 0;
		//decides which algorithm to use
		if(algorithm == Combinations.BFS_INC ){
			start = System.currentTimeMillis();
			//BFS algorithm with support for comparations
			admissable = this.permuteBFS_inc2(templateAlternatives, indexes, tested, admissable);
			end = System.currentTimeMillis();
		}else if(algorithm == Combinations.BFS_NOINC){
			start = System.currentTimeMillis();
			//BFS algorithm
			admissable = this.permuteBFS_noInc(templateAlternatives, indexes, tested, admissable);
			end = System.currentTimeMillis();
		}
		

		this.convertIndexes(templateAlternatives, admissable);
		//System.out.println();
		//System.out.println("Tested Solutions: " + tested.size());
		//System.out.println("Admissable Solutions");
		//this.printResult(admissableCombinations);
		//System.out.println("Number of Admissable Solutions: " + this.admissableCombinations.size());
		
		this.visitedNodes = tested.size();
		this.algorithmTime = end-start;
		
		return this.admissableCombinations;
	}

	
	public ArrayList<ArrayList<Result>> getServiceCombinations() {
		return offeringsCombinations;
	}

	public void setServiceCombinations(ArrayList<ArrayList<Result>> serviceCombinations) {
		this.offeringsCombinations = serviceCombinations;
	}

	public ArrayList<ArrayList<Result>> getOfferingsCombinations() {
		return offeringsCombinations;
	}

	public void setOfferingsCombinations(
			ArrayList<ArrayList<Result>> offeringsCombinations) {
		this.offeringsCombinations = offeringsCombinations;
	}
	
	private ArrayList<ArrayList<Integer>> permuteBFS_inc2(ArrayList<ArrayList<Result>> array, ArrayList<Integer> indexes, ArrayList<ArrayList<Integer>> tested, ArrayList<ArrayList<Integer>> admissable){
		System.out.println("SYSTEM: Using Incomparability algorithm.");
		//testing variables
		double totalDegree = 0.0;
		double totalDepth = 0.0;
		int level = 0;
		int levelNodes = 1;
		int nextLevelNodes = 0;
		
		Queue<ArrayList<Integer>> nodes = (Queue<ArrayList<Integer>>) new LinkedList<ArrayList<Integer>>();
		nodes.add(indexes);
		while(!nodes.isEmpty()){	
			//System.out.println();
			//System.out.println(nodes.toString());
			indexes = nodes.poll();
			printIndexes(indexes);
			tested.add((ArrayList<Integer>) indexes.clone());
			if(levelNodes == 0){
				level++;
				levelNodes = nextLevelNodes;
				nextLevelNodes = 0;
			}
			totalDepth = totalDepth + level;
			levelNodes--;
			ArrayList<Result> solution = new ArrayList<Result>();
			for(int i = 0; i < indexes.size(); i++){
				solution.add(array.get(i).get(indexes.get(i)));
				//System.out.print(solution.get(i).getService().getId() + "-");
			}
			if(AggChecker.checkAdmissability(solution, data.getRequirements())){
				//System.out.println(" - Admissable!!!");
	    		if(checkIncomparabilityInAdmissable(indexes, admissable, array)){
	    			//System.out.println("incomparable with some admissable");
	    			admissable.add((ArrayList<Integer>) indexes.clone());
	    			
	    			//get sons to check incomparability
		    		for(int i = 0; i < indexes.size(); i++){
		    			ArrayList<Integer> newIndexes = (ArrayList<Integer>) indexes.clone(); 			
		    			int index = newIndexes.get(i);
		    			newIndexes.set(i, Integer.valueOf(++index));
		    			//printIndexes(newIndexes);
		    			if( newIndexes.get(i) < array.get(i).size()){
		    				if(!tested.contains(newIndexes) && !nodes.contains(newIndexes)){
			    				if(checkIncomparable(indexes, array, newIndexes)){
			    					nodes.add(newIndexes);
			    					totalDegree++;
			    					nextLevelNodes++;
			    					//System.out.println("incomparable");
			    				}//else
			    				//System.out.println("dominated");
		    				}//else
		    				//System.out.println("already tested");
		    			}//else
		    			//System.out.println("Not possible");
		    		}
	    		}
			}else{
				//System.out.println(" - Not admissable!!!");
				for(int i = 0; i < indexes.size(); i++){
	    			ArrayList<Integer> newIndexes = (ArrayList<Integer>) indexes.clone(); 			
	    			int index = newIndexes.get(i);
	    			newIndexes.set(i, Integer.valueOf(++index));
	    			//printIndexes(newIndexes);
	    			if( newIndexes.get(i) < array.get(i).size()){
	    				if(!tested.contains(newIndexes) && !nodes.contains(newIndexes)){
		    				if(!checkInAdmissable(newIndexes, admissable)){
		    					nodes.add(newIndexes);
		    					totalDegree++;
		    					nextLevelNodes++;
		    					//System.out.println("not in admissable");
		    				}//else
		    					//System.out.println("bigger than admissable");
	    				}//else
	    				//System.out.println("already tested");
	    			}//else
	    			//System.out.println("Not possible");
	    		}
			}
		}
		
		this.avgDepth = totalDepth/tested.size();
		this.avgDegree = totalDegree/tested.size();
		return admissable;
	}
	
	
	//algorithm without the incomparability feature
	private ArrayList<ArrayList<Integer>> permuteBFS_noInc(ArrayList<ArrayList<Result>> array, ArrayList<Integer> indexes, ArrayList<ArrayList<Integer>> tested, ArrayList<ArrayList<Integer>> admissable){
		System.out.println("SYSTEM: Using default algorithm.");
		//testing variables
		double totalDegree = 0.0;
		double totalDepth = 0.0;
		int level = 0;
		int levelNodes = 1;
		int nextLevelNodes = 0;
		
		Queue<ArrayList<Integer>> nodes = (Queue<ArrayList<Integer>>) new LinkedList<ArrayList<Integer>>();
		nodes.add(indexes);
		while(!nodes.isEmpty()){	
			//System.out.println();
			//System.out.println(nodes.toString());
			indexes = nodes.poll();
			printIndexes(indexes);
			tested.add((ArrayList<Integer>) indexes.clone());
			if(levelNodes == 0){
				level++;
				levelNodes = nextLevelNodes;
				nextLevelNodes = 0;
			}
			totalDepth = totalDepth + level;
			levelNodes--;
			ArrayList<Result> solution = new ArrayList<Result>();
			for(int i = 0; i < indexes.size(); i++){
				solution.add(array.get(i).get(indexes.get(i)));
				//System.out.print(solution.get(i).getService().getId() + "-");
			}
			if(AggChecker.checkAdmissability(solution, data.getRequirements())){
	    		//System.out.println(" - Admissable!!!");
	    		if(!checkInAdmissable(indexes, admissable)){
	    			//System.out.println("not in admissable");
	    			admissable.add((ArrayList<Integer>) indexes.clone());
	    			//System.out.println("Added");
	    		}
			}else{
	    		//System.out.println(" - Not admissable!!!");
	    		for(int i = 0; i < indexes.size(); i++){
	    			ArrayList<Integer> newIndexes = (ArrayList<Integer>) indexes.clone(); 			
	    			int index = newIndexes.get(i);
	    			newIndexes.set(i, Integer.valueOf(++index));
	    			//printIndexes(newIndexes);
	    			if( newIndexes.get(i) < array.get(i).size()){
	    				if(!tested.contains(newIndexes) && !nodes.contains(newIndexes)){
		    				if(!checkInAdmissable(newIndexes, admissable)){
		    					nodes.add(newIndexes);
		    					totalDegree++;
		    					nextLevelNodes++;
		    					//System.out.println("not in admissable");
		    				}//else
		    					//System.out.println("dominated");
	    				}//else
	    					//System.out.println("already tested");
	    			}//else
	    				//System.out.println("Not possible");
	    		}
			}
		}
		this.avgDepth = totalDepth/tested.size();
		this.avgDegree = totalDegree/tested.size();
		return admissable;
	}
	
	
	private void convertIndexes(ArrayList<ArrayList<Result>> array, ArrayList<ArrayList<Integer>> admissable){
		for(ArrayList<Integer> indexes : admissable){
			ArrayList<Result> solution = new ArrayList<Result>();
			for(int i = 0; i < indexes.size(); i++){
				solution.add(array.get(i).get(indexes.get(i)));
				//System.out.print(solution.get(i).getService().getId() + "-");
			}
			//System.out.println();
			this.admissableCombinations.add((ArrayList<Result>) solution.clone());
		}
	}
	
	private static boolean checkInAdmissable(ArrayList<Integer> toTest, ArrayList<ArrayList<Integer>> admissable){
		boolean dominated = true;
		
		if(admissable.size() == 0)
			return false;
		else{
			//System.out.println("TOTEST");
			printIndexes(toTest);
			//System.out.println("Testing with:");
			for(ArrayList<Integer> indexes : admissable){
				printIndexes(indexes);
				for(int i = 0; i < toTest.size(); i++){
					if(toTest.get(i) < indexes.get(i)){
						dominated = false;
						//System.out.print("|"+toTest.get(i)+" < "+indexes.get(i)+" ->"+dominated);
						break;
					}
					dominated = true;
					//System.out.print("|"+toTest.get(i)+" < "+indexes.get(i)+" ->"+dominated);
				}
				if(dominated)
					return true;
			}
			return dominated;
		}
	}
	
	private boolean checkIncomparable(ArrayList<Integer> indexes, ArrayList<ArrayList<Result>> array, ArrayList<Integer> newIndexes){
		ArrayList<Result> solution = new ArrayList<Result>();
		ArrayList<Result> incSolution = new ArrayList<Result>();
		
		for(int i = 0; i < indexes.size(); i++){
			System.out.print("index: "+i+" - ");
			if(newIndexes.get(i) > indexes.get(i) ){
				double delta = array.get(i).get(indexes.get(i)).getPerformance() - array.get(i).get(newIndexes.get(i)).getPerformance();
				if(delta < this.incDelta){
					System.out.println(array.get(i).get(indexes.get(i)).getPerformance() +"->"+array.get(i).get(newIndexes.get(i)).getPerformance());
					return true;
				}	
			}
		}
		return false;
	}
	
	private boolean checkIncomparabilityInAdmissable(ArrayList<Integer> toTest, ArrayList<ArrayList<Integer>> admissable, ArrayList<ArrayList<Result>> array){
		boolean incomparable = true;
		
		if(admissable.size() == 0)
			return true;
		else{
			//System.out.println("TOTEST");
			//printIndexes(toTest);
			//System.out.println("Testing with:");
			for(ArrayList<Integer> indexes : admissable){
				//printIndexes(indexes);
				for(int i = 0; i < toTest.size(); i++){
					if(toTest.get(i) > indexes.get(i)){
						double delta = Math.abs(array.get(i).get(indexes.get(i)).getPerformance() - array.get(i).get(toTest.get(i)).getPerformance());
						if(delta < this.incDelta){
							incomparable = true;
							break;
						}
					}else if(toTest.get(i) < indexes.get(i)){
						incomparable = true;
						break;
					}
					
					incomparable = false;
				}
				if(!incomparable)
					return false;
			}
			return true;
		}
	}
	
	public void printResult(ArrayList<ArrayList<Result>> result){
		System.out.println("--------------");
		for(ArrayList<Result> list : result){
			for(Result res : list){
				System.out.print(" -  Offering: "+ res.getService().getName() + " = "+ res.getPerformance());
			}
			System.out.println("");
		}
		System.out.println("--------------");
	}
	
	private static void printResults(ArrayList<ArrayList<Integer>> list){
		for(ArrayList<Integer> indexes : list){
			printIndexes(indexes);
		}
	}
	
	private static void printIndexes(ArrayList<Integer> indexes){
		System.out.println("Indexes: ");
		for(int i : indexes){
			System.out.print(i);
		}
		System.out.println();
	}

	public static void main(String[] args) {
		ArrayList<ArrayList<Result>> admissableCombinations;
		ArrayList<TemplateSim> values = new ArrayList<TemplateSim>();
		
		ArrayList<Double> performanceA = new ArrayList<Double>();
		performanceA.add(1.0);
		performanceA.add(0.4);
		performanceA.add(0.33);
		performanceA.add(0.25);
		performanceA.add(0.05);
		ArrayList<Double> performanceB = new ArrayList<Double>();
		performanceB.add(0.45);
		performanceB.add(0.4);
		performanceB.add(0.33);
		performanceB.add(0.25);
		ArrayList<Double> priceA = new ArrayList<Double>();
		priceA.add(1100.0);
		priceA.add(1000.0);
		priceA.add(900.0);
		priceA.add(800.0);
		priceA.add(700.0);
		ArrayList<Double> priceB = new ArrayList<Double>();
		priceB.add(1100.0);
		priceB.add(1000.0);
		priceB.add(900.0);
		priceB.add(800.0);
		
		values.add(new TemplateSim(5, 4, performanceA, priceA));
		values.add(new TemplateSim(4, 5, performanceB, priceB));
		
		Test test = new Test(2050, values);
		AlgorithmSimulation sim = new AlgorithmSimulation();
		CSAData data = sim.generateData(test);
		Combinations comb = new Combinations(data, 0.1);
		admissableCombinations = comb.computeCombinations(Combinations.BFS_NOINC);
		System.out.println("Admissible Solutions");
		System.out.println();
		for(ArrayList<Result> solution : admissableCombinations){
			for(Result res : solution){
				System.out.print("["+res.getService().getId()+"-"+res.getPerformance()+"] - ");
			}
			System.out.println();
			
		}
		
		System.out.println("Visited: "+comb.visitedNodes);
		System.out.println("Degree: "+comb.avgDegree);
		System.out.println("Depth: "+comb.avgDepth);
		System.out.println("Time: "+comb.algorithmTime);
	}
}



