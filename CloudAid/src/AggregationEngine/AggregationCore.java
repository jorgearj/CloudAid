/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: AggregationCore.java, Project: CloudAid, 23 Apr 2013 Author: Jorge Araújo
*/
package AggregationEngine;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import Controller.CloudAid;

import data.csadata.CSAData;
import data.csadata.Result;
import data.csadata.ServiceTemplate;
import data.simulation.AlgorithmSimulation;
import data.simulation.TemplateSim;
import data.simulation.Test;
import data.simulation.TestGenerator;

/**
 * @author Jorge
 *
 */
public class AggregationCore {
	
	private XMCDAConverter converter;
	private final String source = "./Decision/";
	private final String destination = "./TO_Decide/";
	private final String methodDest[] = {"SAW","AHP" };
	
	//testing variables
	long algorithmTime;
	double avgDegree;
	double avgDepth;
	int visitedNodes;
	PrintWriter out;
	

	public AggregationCore(PrintWriter out){
		this.converter = new XMCDAConverter();
		this.out = out; 
	}
	
	public CSAData computeAggregation(CSAData data, int method, double incDelta, int algorithm){
		CSAData result = new CSAData();
		ArrayList<ArrayList<Result>> admissables = new ArrayList<ArrayList<Result>>();
		ArrayList<Result> solution = new ArrayList<Result>();
		FileChecker checker = new FileChecker(source+methodDest[method]);
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		System.out.println("STATUS: Starting Altenatives aggregation...");
		
		//get service templates weights
		if(method == CloudAid.AHP){
			files = ahpXMCDA(data.getServiceTemplates());
			postXMCDA(files, method);
			
			//wait for the results
			System.out.println("STATUS: Waiting for a decision...");
			String filename = checker.listen();
			
			data = converter.getPerformance(converter.getFromFile(filename), data);
		}
		
		Combinations comb = new Combinations(data, incDelta);
		
		String[] msg = {"Computing Aggregation Solutions..."};
		CloudAid.askData(CloudAid.PROMPT, msg, null);
		admissables = comb.computeCombinations(algorithm);
		out.println("Number of Admissible Solutions: "+ admissables.size());
		
		if(admissables.size() > 0){
			if(admissables.size() > 1){
				//compare admissables with wieghts
				solution = this.compareAdmissables(data, admissables, method);
			}else{
				//there is only one solution
				solution = admissables.get(0);
			}
			
			result = this.storeResults(solution, data);
		}else{
			String[] msg2 = {"No aggregation solutions available for the specified requirements."};
			CloudAid.askData(CloudAid.PROMPT, msg2, null);
		}
		
		this.algorithmTime = comb.algorithmTime;
		this.visitedNodes = comb.visitedNodes;
		this.avgDegree = comb.avgDegree;
		this.avgDepth = comb.avgDepth;
		return result;
	}
	
	
	private CSAData storeResults(ArrayList<Result> solution, CSAData data){		
		CSAData result = new CSAData();
		result.setServiceTemplates((ArrayList<ServiceTemplate>) data.getServiceTemplates().clone());
		
		for(int i = 0; i < result.getServiceTemplates().size(); i++){
			result.getServiceTemplates().get(i).getResultList().removeAll(result.getServiceTemplates().get(i).getResultList());
			result.getServiceTemplates().get(i).addResult(solution.get(i));
		}
		return result;
	}
	
	private ArrayList<Result> compareAdmissables(CSAData data, ArrayList<ArrayList<Result>> admissables, int method){
		ArrayList<Result> solution = new ArrayList<Result>();
		
		//make the comparations
		float max = -1;
		
		for(ArrayList<Result> admissable : admissables){
			//get the solution total value with wieghts
			float total = 0;
			for(int i = 0; i < data.getServiceTemplates().size(); i++){
				float weight = data.getServiceTemplates().get(i).getWeight();
				double templateVal = weight * admissable.get(i).getPerformance(); 
				total = total + (float) templateVal;
			}
			System.out.println("SYSTEM: Total = " +total);
			
			if(total > max){
				max = total;
				solution = admissable;
			}
		}
		
		return solution;
	}
	
	private ArrayList<XMCDA> ahpXMCDA(ArrayList<ServiceTemplate> templates){
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		
		XMCDA alte = converter.createAlternatives(templates);
		files.add(alte);
		
		return files;
	}
	
	private void postXMCDA(ArrayList<XMCDA> files, int method){
		//attach timestamp in the method messages
		long time = System.currentTimeMillis();
		XMCDA timeStamp = converter.attachCompTimestamp(time);
		files.add(timeStamp);
		
		XMCDA result = converter.append(files);

		converter.export(result, getDestination(time, method));
	}
	
	private String getDestination(long time, int method){
		return this.destination + this.methodDest[method] + "/XMCDA_To_Decide_" + time + ".xml";
	}
	
	public void printResult(ArrayList<ArrayList<Result>> result, PrintWriter out){
		out.println("--------------");
		for(ArrayList<Result> list : result){
			for(Result res : list){
				out.print(" -  Offering: "+ res.getService().getName() + " = "+ res.getPerformance());
			}
			out.println("");
		}
		out.println("--------------");
	}
	
	public static void main(String[] args) {
		int iterations = 1;
		
		//different deltas
		ArrayList<Double> deltas = new ArrayList<Double>();
		//deltas.add(0.1);
		//deltas.add(0.05);
		//deltas.add(0.01);
		deltas.add(0.0); //means that we want to use the no incomaprability algorithm
		
		//testing counters
		long totalTestTime = 0;
		
		ArrayList<Long> totalAlgorithmTime = new ArrayList<Long>();
		ArrayList<Long> minExecTime  = new ArrayList<Long>();
		ArrayList<Long> maxExecTime  = new ArrayList<Long>();
		ArrayList<Integer> minTest  = new ArrayList<Integer>();
		ArrayList<Integer> maxTest  = new ArrayList<Integer>();
		
		for(int i = 0; i<deltas.size(); i++){
			totalAlgorithmTime.add((long) 0);
			minExecTime.add((long) Long.MAX_VALUE);
			maxExecTime.add((long) -1);
			minTest.add(0);
			maxTest.add(0);
		}
		
		//ArrayList<Test> testBatteries = TestGenerator.generate(5);
		
		//runs the test read from the input file.
		ArrayList<Test> testBatteries = TestGenerator.generateFromFile("TestsToRun.txt");
		
		try {
			PrintWriter out = new PrintWriter("Tests.txt");

			int testCount = 0;
			
			for(Test t : testBatteries){
				ArrayList<Integer> avgVisitedNodes = new ArrayList<Integer>();
				ArrayList<Long> avgExecutionTime = new ArrayList<Long>();
				ArrayList<Double> avgDepth = new ArrayList<Double>();
				ArrayList<Double> avgDegree = new ArrayList<Double>();
				
				for(int i = 0; i<deltas.size(); i++){
					avgVisitedNodes.add((Integer) 0);
					avgExecutionTime.add((long) 0);
					avgDepth.add((double) 0);
					avgDegree.add((double) 0);
				}
				
				testCount++;
				out.println("");
				out.println("-----------------------------------------------------");
				out.println("Test Number: " + testCount);
				System.out.println("Test Number: " + testCount);
				t.printTestData(out);
				for(int d = 0; d < deltas.size(); d++){
					for(int i = 0; i < iterations; i++){						
						ArrayList<ArrayList<Result>> admissableCombinations;
						AlgorithmSimulation sim = new AlgorithmSimulation();
						CSAData data = sim.generateData(t);
						AggregationCore aggreg = new AggregationCore(out);
						CSAData result;
						if(d == 0){
							 result = aggreg.computeAggregation(data, CloudAid.SAW, deltas.get(d), Combinations.BFS_NOINC);
						}else
							result = aggreg.computeAggregation(data, CloudAid.SAW, deltas.get(d), Combinations.BFS_INC);
						//CloudAid.askData(CloudAid.PRINTCSA, null, result);
						
						avgVisitedNodes.set(d,avgVisitedNodes.get(d) + aggreg.visitedNodes);
						avgDepth.set(d,avgDepth.get(d) + aggreg.avgDepth);
						avgDegree.set(d,avgDegree.get(d) + aggreg.avgDegree);
						avgExecutionTime.set(d,avgExecutionTime.get(d) + aggreg.algorithmTime);
						totalTestTime = totalTestTime + avgExecutionTime.get(d);
					}				
				
					avgDegree.set(d,avgDegree.get(d)/iterations);
					avgDepth.set(d,avgDepth.get(d)/iterations);
					avgVisitedNodes.set(d,avgVisitedNodes.get(d)/iterations);
					avgExecutionTime.set(d,avgExecutionTime.get(d)/iterations);
					totalAlgorithmTime.set(d,totalAlgorithmTime.get(d) + avgExecutionTime.get(d));
					
					if(avgExecutionTime.get(d) < minExecTime.get(d)){
						minExecTime.set(d,avgExecutionTime.get(d));
						minTest.set(d,testCount);
					}
					if(avgExecutionTime.get(d) > maxExecTime.get(d)){
						maxExecTime.set(d,avgExecutionTime.get(d));
						maxTest.set(d,testCount);
					}
					
					out.println("SYSTEM: TESTING RESULTS");
					out.println("SYSTEM: Number of iterations: " + iterations);
					out.println("SYSTEM: Average Degree (delta="+deltas.get(d)+"): " + avgDegree.get(d)/iterations);
					out.println("SYSTEM: Average Depth (delta="+deltas.get(d)+"): " + avgDepth.get(d)/iterations);
					out.println("SYSTEM: Average Visited Nodes (delta="+deltas.get(d)+"): " + avgVisitedNodes.get(d)/iterations);
					out.println("SYSTEM: Average Execution Time (delta="+deltas.get(d)+"): " + avgExecutionTime.get(d)/iterations+"ms");
					out.println("");
				}
			}
			
			out.println("---------------//-------------------------------");
			out.println("SYSTEM: OVERALL TEST RESULTS");
			out.println("SYSTEM: Executed " + testCount+ " test with a total time of: "+ totalTestTime+"ms");
			for(int i = 0; i<deltas.size(); i++){
				out.println("SYSTEM: Average Execution Time (delta="+deltas.get(i)+"): " + totalAlgorithmTime.get(i)/testBatteries.size());
				out.println("SYSTEM: Minimum Execution Time (delta="+deltas.get(i)+"): " + minExecTime.get(i)+ "ms in test: " + minTest.get(i));
				out.println("SYSTEM: Maximum Execution Time (delta="+deltas.get(i)+"): " + maxExecTime.get(i)+ "ms in test: " + maxTest.get(i));
			}
		
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
