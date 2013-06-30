/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package Controller;

import gui.ShellUI;
import gui.UserInput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import AggregationEngine.AggregationCore;
import AggregationEngine.Combinations;

import searchEngine.SearchCore;

import csaevaluator.CSAEvaluator;

import data.csadata.CSAData;
import data.csadata.ServiceTemplate;
import data.csadata.Criterion;
import data.csadata.Requirement;
import data.csadata.Result;
import data.servicedata.Offering;
import data.simulation.DataSimulator;
import decisionEngine.DecisionCore;

public class CloudAid {
	//User Interface Data Codes
	public static final int GET_WEIGHT = 100;
	public static final int GET_PREFERENCE_DIRECTION = 101;
	public static final int GET_PREFERENCE_VALUE = 102;
	public static final int GET_DISTANCE_VALUE = 103;
	public static final int GET_YESNO_ANSWER = 200;
	public static final int PROMPT = 300;
	public static final int PRINTCSA = 301;
	public static final int PRINTALTDATA = 302;
	public static final int PRINTRESULTLIST = 303;
	
	
	private SearchCore searchEngine;
	private DecisionCore decisionEngine;
	private AggregationCore aggregatorEngine;
	private ShellUI shell;
	private int mode; //0 - automatic / 1 - shell mode 
	
	//execution modes
	public static final int AUTO = 0;
	public static final int SHELL = 1;
	
	//decision Method codes
	public static final int SAW = 0;
	public static final int AHP = 1;
	
	public CloudAid(int mode, int scenario, int method, int algorithm){
		PrintWriter out;
		try {
			out = new PrintWriter("Tests.txt");
		
		
			System.out.println("SYSTEM: Initializing CloudAid Components...");
			this.shell = new ShellUI();
			
			this.shell.prompt("STATUS: ----Initializing ServiceSet");
			this.searchEngine = new SearchCore();
			this.shell.prompt("STATUS: ----Initializing DecisionEngine");
			this.decisionEngine = new DecisionCore();
			this.shell.prompt("STATUS: ----Initializing AggregationEngine");
			this.aggregatorEngine = new AggregationCore(out);
			
			this.mode = mode;
			this.init(method, scenario, algorithm);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init(int method, int scenario, int algorithm){
		CSAData data;
		
		if(this.mode == CloudAid.AUTO){
			//automatic mode
			data = this.automatic(scenario);
			this.start(data, method, scenario, algorithm);
		}else if(this.mode == CloudAid.SHELL){
			//UI mode
			method = this.shell.chooseMethod();
			data = this.shell.getCSAData(method);
			this.start(data, method, scenario, algorithm);
		}
	}
	
	private CSAData automatic(int scenario){
		
		
		DataSimulator sim =  new DataSimulator();
		CSAData data = sim.init(scenario);
		
		System.out.println(data.getServiceTemplates().size());
		
		//printData(data);
		
		return data;
	}
	
	private void start(CSAData data, int methodID, int scenario, int algorithm){ 
		boolean ok = true; //true if all service templates have alternatives false if not
		CSAEvaluator eval = new CSAEvaluator(methodID);
		data = eval.evaluator(data);
		this.shell.printData(data);
		
		//initiate evaluator
		if(data != null){
			if(data.getEvalResult() == CSAEvaluator.OK){
				try {
					ArrayList<ServiceTemplate> comps = new ArrayList<ServiceTemplate>() ;
					comps = data.getServiceTemplates();
					//System.out.println("Number of components: " + comps.size());
					for(ServiceTemplate c : comps){
						ArrayList<Offering> alternatives = this.search(c);
						//System.out.println("Number of alternatives: " + alternatives.size());
						if(alternatives != null){
							ArrayList<Result> res = this.decision(c, alternatives, methodID);
							c.setResultList(res);	
						}
						else{
							ok = false;
							break;
						}
						
					}
					if(ok){
						this.shell.printResults(data);
						CSAData result = this.aggregatorEngine.computeAggregation(data, methodID, 0.05, algorithm);
						this.shell.printResults(result);
					}
					
				} catch (NullPointerException e) {
					System.out.println(e.getMessage());
					this.init(methodID, scenario, algorithm);
				}
			}else if(data.getEvalResult() == CSAEvaluator.ERROR_1){
				this.shell.prompt("There are no Service Templates in your cloud architecture. Please reconcider your options.");
			}
			
			if(this.shell.propmtToExit())
				return;
			
			this.init(methodID, scenario, algorithm);
		}else{
			this.shell.prompt("No data to proceed! Please reconcider your options.");
			this.init(methodID, scenario, algorithm);
		}
		
			
	}
	
	//Search in the service marketplace for the alternatives that fullfil the requirements for a component
	private ArrayList<Offering> search(ServiceTemplate c){
		ArrayList<Offering> alternatives = new ArrayList<Offering>();
		alternatives = this.searchEngine.searchAlternatives(c);		
		return alternatives;
	}
		
	//ranks the alternatives based on the criteria for a component
	private ArrayList<Result> decision(ServiceTemplate c, ArrayList<Offering> alternatives, int methodID){
		ArrayList<Result> resultList = new ArrayList<Result>();
		resultList = this.decisionEngine.decide(c, alternatives, methodID);
		
		return resultList;
	}
	
	public static String askData(int code, String[] msg, Object data){
		ShellUI shell = new ShellUI();
		
		switch(code){
		case PROMPT:
			shell.prompt(msg[0]);
			return null;
		case PRINTCSA:
			shell.printResults((CSAData) data);
			return null;
		case GET_WEIGHT:
			return shell.askforCriterionWeight(msg[0], msg[1]);
		case GET_PREFERENCE_DIRECTION:
			return shell.askforCritPrefDirection(msg[0], msg[1]);
		case GET_YESNO_ANSWER:
			return shell.promptYesNo(msg[0]);
		case GET_PREFERENCE_VALUE:
			return shell.askforPreferenceValue(msg[0]);
		case GET_DISTANCE_VALUE:
			return shell.askforDistance(msg[0], msg[1]);
		case PRINTALTDATA:
			shell.printAlternativesData((ArrayList<Offering>)data);
			return null;
		case PRINTRESULTLIST:
			shell.printResultList(msg[0], (ArrayList<Result>) data);
			return null;
		default:
			System.out.println("ERROR: Unrecognized interface code!!!");
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] scenarios = {"heroku", "usecase", "reqs", "norm"};
		String[] methods = {"saw", "ahp"};
		int scenario = -1;
		int method = -1;
		int algorithm = -1;
		
		//check directories necessary
		ArrayList<File> folders = new ArrayList<File>();
		File decisionDir = new File("./Decision");
		File to_DecideDir = new File("./TO_Decide");
		File serviceDir = new File("./Services"); 
		folders.add(decisionDir);
		folders.add(to_DecideDir);
		folders.add(serviceDir);
		for(int i = 0; i < methods.length; i++){
			File methodDecisionDir = new File(decisionDir.getPath()+"/"+methods[i].toUpperCase());
			folders.add(methodDecisionDir);
			File methodTo_decideDir = new File(to_DecideDir.getPath()+"/"+methods[i].toUpperCase());
			folders.add(methodTo_decideDir);
		}
		
		
		for(File folder : folders){
			System.out.println(folder.getAbsolutePath());
		  // if the directory does not exist, create it
		  if (!folder.exists()){
		    System.out.println("creating directory: " + folder.getName());
		    boolean result = folder.mkdir();  
		    if(result){    
		       System.out.println("DIR created");  
		     }
		  }else{
			  System.out.println("Folder exists"); 
		  }
		}
		
		if(args.length == 0){
			CloudAid controller = new CloudAid(CloudAid.SHELL, DataSimulator.NONE ,CloudAid.SAW, Combinations.BFS_NOINC);
		}else if(args.length == 1){
			if(args[0].equalsIgnoreCase("INC")){
				CloudAid controller = new CloudAid(CloudAid.SHELL, DataSimulator.NONE ,CloudAid.SAW, Combinations.BFS_INC);
			}else if(args[0].equalsIgnoreCase("NOINC")){
				CloudAid controller = new CloudAid(CloudAid.SHELL, DataSimulator.NONE ,CloudAid.SAW, Combinations.BFS_NOINC);
			}else
				System.out.println("Invalid argument value...");
		}else if(args.length == 2){
			
			for(int i =0; i < scenarios.length; i++){
				if(args[0].equals(scenarios[i])){
					scenario = i;
					break;
				}
			}
			for(int i =0; i < methods.length; i++){
				if(args[1].equals(methods[i])){
					method = i;
					break;
				}
			}
			
			
			if((scenario > -1) && (method > -1)){
				CloudAid controller = new CloudAid(CloudAid.AUTO, scenario , method, Combinations.BFS_NOINC);
			}else{
				System.out.println("Invalid argument value...");
			}
		}else if(args.length == 3){
			for(int i =0; i < scenarios.length; i++){
				if(args[0].equals(scenarios[i])){
					scenario = i;
					break;
				}
			}
			for(int i =0; i < methods.length; i++){
				if(args[1].equals(methods[i])){
					method = i;
					break;
				}
			}
			if(args[2].equalsIgnoreCase("INC")){
				algorithm = Combinations.BFS_INC;
			}else if(args[2].equalsIgnoreCase("NOINC")){
				algorithm = Combinations.BFS_NOINC;
			}
			
			if((scenario > -1) && (method > -1) && (algorithm > -1)){
				CloudAid controller = new CloudAid(CloudAid.AUTO, scenario , method, algorithm);
			}else{
				System.out.println("Invalid argument value...");
			}
		}else{
			System.out.println("Invalid argument...");			
		}
		
	}

}
