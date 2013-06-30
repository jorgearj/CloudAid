/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: AlgorithmSimulation.java, Project: CloudAid, 30 May 2013 Author: Jorge Araújo
*/
package data.simulation;

import java.util.ArrayList;
import java.util.Collections;

import Controller.CloudAid;

import data.csadata.CSAData;
import data.csadata.Criterion;
import data.csadata.Requirement;
import data.csadata.Result;
import data.csadata.ServiceTemplate;
import data.servicedata.Offering;

/**
 * @author Jorge
 *
 */
public class AlgorithmSimulation {
	
	String[] alph= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	public AlgorithmSimulation(){
		
	}
	
	public CSAData generateData(Test test){
		CSAData simData = new CSAData();
		int countTemp = 0;
		
		for(TemplateSim n : test.getTemplates()){
			ServiceTemplate template = new ServiceTemplate("comp"+countTemp++, "comp"+countTemp, "comp"+countTemp + "description");		
			ArrayList<Result> resultList = new ArrayList<Result>();
			
			for(int i= 0; i < n.getnAlternatives(); i++){
				Offering off = new Offering();
				off.setId(alph[countTemp-1]+(i+1));
				off.setName(alph[countTemp-1]+(i+1));
				off.getAttributes().put("price", Double.toString(n.getPrices().get(i)));
				resultList.add(new Result(off, n.getPerformances().get(i)));
			}
			template.setWeight((float)n.getWeight());
			template.setResultList(resultList);
			simData.addComponent(template);
		}
		
		Requirement auxReq = new Requirement();
		auxReq.setDescription("price must be bellow "+ test.getPriceLimit());
		auxReq.setMax((float)test.getPriceLimit());
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true);
		simData.addReq(auxReq);
		
		this.normalize(simData);
		//this.printData(simData);
		
		return simData;
	}
	
	private void normalize(CSAData data){
		float total = 0;
		
		for(ServiceTemplate template :data.getServiceTemplates()){
			total = total + template.getWeight();
		}
		
		for(ServiceTemplate template :data.getServiceTemplates()){
			template.setWeight(template.getWeight()/total);
		}
	}
	
	private void printData(CSAData data){
		ArrayList<ServiceTemplate> comps;
		ArrayList<Requirement> reqs;
		ArrayList<Criterion> crit;
		
		comps = data.getServiceTemplates();
		System.out.println("CSA SERVICE TEMPLATEs: ");
		for(int i=0; i < comps.size(); i++){
			System.out.println(comps.get(i).toString());
		}
		
		reqs = data.getRequirements();
		System.out.println("CSA GENERAL REQUIREMENTS: ");
		for(int i=0; i < reqs.size(); i++){
			System.out.println(reqs.get(i).toString());
		}
		
		crit = data.getCriteria();
		System.out.println("CSA CRITERIA: ");
		for(int i=0; i < crit.size(); i++){
			System.out.println(crit.get(i).toString());
		}
		
	}
	
	private void teste(){
		ArrayList<Result> a = new ArrayList<Result>();
		Offering off1 = new Offering();
		off1.setId("A1");
		off1.getAttributes().put("price", "50");
		a.add(new Result(off1, 1));
		
		Offering off2 = new Offering();
		off2.setId("A2");
		off2.getAttributes().put("price", "100");
		a.add(new Result(off2, 1));
		
		Offering off3 = new Offering();
		off3.setId("A3");
		off3.getAttributes().put("price", "200");
		a.add(new Result(off3, 1));
		
		Offering off4 = new Offering();
		off4.setId("A4");
		off4.getAttributes().put("price", "400");
		a.add(new Result(off4, 1));
		
		Offering off5 = new Offering();
		off5.setId("A5");
		off5.getAttributes().put("price", "800");
		a.add(new Result(off5, 1));
		
		Offering off6 = new Offering();
		off6.setId("A6");
		off6.getAttributes().put("price", "1600");
		a.add(new Result(off6, 1));
		
		Offering off10 = new Offering();
		off10.setId("A7");
		off10.getAttributes().put("price", "3200");
		//a.add(new Result(off10, 1));
		
		ArrayList<Result> b = new ArrayList<Result>();
		
		Offering off7 = new Offering();
		off7.setId("B1");
		off7.getAttributes().put("price", "1600");
		b.add(new Result(off7, 1));
		
		Offering off11 = new Offering();
		off11.setId("A2");
		off11.getAttributes().put("price", "3200");
		a.add(new Result(off11, 1));
		
		ArrayList<Result> c = new ArrayList<Result>();
		
		Offering off12 = new Offering();
		off12.setId("C1");
		off12.getAttributes().put("price", "3200");
		c.add(new Result(off12, 1));
		
		Offering off8 = new Offering();
		off8.setId("C2");
		off8.getAttributes().put("price", "1600");
		c.add(new Result(off8, 1));
		
		Offering off9 = new Offering();
		off9.setId("C3");
		off9.getAttributes().put("price", "800");
		c.add(new Result(off9, 1));
		
		ArrayList<ArrayList<Result>> teste = new ArrayList<ArrayList<Result>>();
		teste.add(a);
		teste.add(b);
		teste.add(c);
		
		ArrayList<Requirement> reqs = new ArrayList<Requirement>();
		Requirement auxReq = new Requirement();
		auxReq.setDescription("price must be bellow 8000");
		auxReq.setMax(3000);
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true);
		reqs.add(auxReq);
		
		
		//ArrayList<ArrayList<Result>> tested = new ArrayList<ArrayList<Result>>();
		//ArrayList<ArrayList<Result>> admissable = new ArrayList<ArrayList<Result>>();
		//permute2(teste, 0, new ArrayList<Result>(),  tested,  admissable, reqs);
		
		ArrayList<ArrayList<Integer>> tested = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> admissable = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> list = new ArrayList<Integer>(Collections.nCopies(teste.size(), 0));
		//permuteDFS(teste, list, tested, admissable, reqs);
		//permuteBFS2(teste, list, tested, admissable, reqs);
		System.out.println();
		System.out.println("RESULTS");
		System.out.println("Tested: " + tested.size());
		//printResults(tested);
		System.out.println();
		System.out.println();
		System.out.println("Admissable: " + admissable.size());
		//printResults(admissable);
		
		if(tested.contains(tested.get(0)))
			System.out.println("Encontrou");
	}
}
