/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.simulation;

import java.util.ArrayList;

import data.csadata.CSAData;
import data.csadata.ServiceTemplate;
import data.csadata.Criterion;
import data.csadata.Requirement;
import data.servicedata.Enumerator;
import data.servicedata.Offering;

import searchEngine.SearchCore;

public class DataSimulator {
	
	private CSAData data;
	private final String ALTERNATIVE_PREFIX = "alt";
	private final String CRITERION_PREFIX = "cri";
	private final String COMPONENT_PREFIX = "comp";
	
	public static final int NONE = -1;
	public static final int HEROKU = 0;
	public static final int USECASE = 1;
	public static final int REQS = 2;
	public static final int NORM = 3;
	
	public DataSimulator(){
		
	}

	public CSAData init(int scenario){
		
		switch (scenario) {
	        case 0:  
	        	//for the default heroku simulation
	    		return this.herokuSimulation();
	        case 1:
	        	//for the UseCase real data simulation
	    		return this.useCaseSimulation();
	        case 2:
	        	//for the Req testing environment
	    		return this.reqsTesting();
	        case 3:
	        	//for the decision characteristics normalization testing
	    		return this.nomrTesting();
	        default: 
	        	System.out.println("SYSTEM: Invalid scenario!!!");
	            return null;
		}
	}
	
	private CSAData nomrTesting(){
		ArrayList<ServiceTemplate> comps = new ArrayList<ServiceTemplate>() ;
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		ArrayList<Criterion> criteria = new ArrayList<Criterion>() ;
		
		int componentPreffix = 1;
		//initiate the components and the requirements for each of them
		ServiceTemplate auxComp;
		Requirement auxReq;
		Criterion auxCrit;
		
		//GENERAL REQUIREMENTS & CRITERIA
		auxReq = new Requirement();
		auxReq.setDescription("The overall System price must be bellow 5000€");
		auxReq.setMax(5000);
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);
		
		//TEMPLATE
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "server", "a server for data processing");
		auxComp.setWeight(4);

		auxReq = new Requirement("min storage capacity > 150GB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(150);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min data transfer out > 40TB", null, Enumerator.QUANT_FEATURE.DATAOUTEXTERNAL);
		auxReq.setMin(40960);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have apache platform", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("Apache");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have 64bit", Enumerator.QUAL_FEATURE.BIT64, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have language Java", Enumerator.QUAL_FEATURE.LANGUAGE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("java");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
		auxReq.setMin((float)99.9);
		auxReq.setExclusivityMax(false);
		auxReq = new Requirement("Network performance", Enumerator.QUAL_FEATURE.PERFORMANCE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("Network");
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		
		
		auxCrit = new Criterion(Enumerator.QUAL_FEATURE.STORAGETYPE.getValue());
		auxCrit.setWeight(2);
		auxCrit.setPreferenceDirection("max");
		auxCrit.setType(1); //binary criterion
		auxComp.addCrit(auxCrit);
		
		//add component to the components list
		comps.add(auxComp);
		
		return new CSAData(comps, reqs, criteria);
	}
	
	private CSAData reqsTesting(){
		ArrayList<ServiceTemplate> comps = new ArrayList<ServiceTemplate>() ;
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		ArrayList<Criterion> criteria = new ArrayList<Criterion>() ;
		
		int componentPreffix = 1;
		//initiate the components and the requirements for each of them
		ServiceTemplate auxComp;
		Requirement auxReq;
		
		//GENERAL REQUIREMENTS & CRITERIA
		auxReq = new Requirement();
		auxReq.setDescription("The overall System price must be bellow 5000€");
		auxReq.setMax(5000);
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);
			
		
		//TEMPLATE1
				auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 5 requirements");
				auxComp.setWeight(4);

				auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
				auxReq.setMin(6204800);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
				auxReq.setPositive(true);
				auxReq.setQualValue("EU");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
				auxReq.setPositive(false);
				auxReq.setQualValue("MySQL");
				auxComp.addReq(auxReq);
				//add component to the components list
				comps.add(auxComp);
				
				
		//TEMPLATE1
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 5 requirements");
		auxComp.setWeight(4);

		auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(6204800);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(false);
		auxReq.setQualValue("MySQL");
		auxComp.addReq(auxReq);
		//add component to the components list
		comps.add(auxComp);
		
		//TEMPLATE2
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 6 requirements");
		auxComp.setWeight(4);

		auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(6204800);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(false);
		auxReq.setQualValue("MySQL");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
		auxReq.setMin((float)99.9);
		auxReq.setExclusivityMax(false);
		//add component to the components list
		comps.add(auxComp);
		
		//TEMPLATE3
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 7 requirements");
		auxComp.setWeight(4);

		auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(6204800);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(false);
		auxReq.setQualValue("MySQL");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
		auxReq.setMin((float)99.9);
		auxReq.setExclusivityMax(false);
		auxReq = new Requirement("min filesize > 5Gb", null, Enumerator.QUANT_FEATURE.FILESIZE);
		auxReq.setMin(5);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		//add component to the components list
		comps.add(auxComp);
		
		//TEMPLATE 4
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 8 requirements");
		auxComp.setWeight(4);

		auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(6204800);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(false);
		auxReq.setQualValue("MySQL");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
		auxReq.setMin((float)99.9);
		auxReq.setExclusivityMax(false);
		auxReq = new Requirement("min filesize > 5Gb", null, Enumerator.QUANT_FEATURE.FILESIZE);
		auxReq.setMin(5);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have security", Enumerator.QUAL_FEATURE.SECURITY, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have REST API", Enumerator.QUAL_FEATURE.API, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("REST");
		auxComp.addReq(auxReq);
		//add component to the components list
				comps.add(auxComp);		
				
		//TEMPLATE 5
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 10 requirements");
		auxComp.setWeight(4);

		auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(6204800);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(false);
		auxReq.setQualValue("MySQL");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
		auxReq.setMin((float)99.9);
		auxReq.setExclusivityMax(false);
		auxReq = new Requirement("min filesize > 5Gb", null, Enumerator.QUANT_FEATURE.FILESIZE);
		auxReq.setMin(5);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have security", Enumerator.QUAL_FEATURE.SECURITY, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have REST API", Enumerator.QUAL_FEATURE.API, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("REST");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have SOAP API", Enumerator.QUAL_FEATURE.API, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("SOAP");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have console", Enumerator.QUAL_FEATURE.CONSOLE, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		//add component to the components list
				comps.add(auxComp);
		
				//TEMPLATE6
				auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 15 requirements");
				auxComp.setWeight(4);

				auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
				auxReq.setMin(6204800);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
				auxReq.setPositive(true);
				auxReq.setQualValue("EU");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
				auxReq.setPositive(false);
				auxReq.setQualValue("MySQL");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
				auxReq.setMin((float)99.9);
				auxReq.setExclusivityMax(false);
				auxReq = new Requirement("min filesize > 5Gb", null, Enumerator.QUANT_FEATURE.FILESIZE);
				auxReq.setMin(5);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have security", Enumerator.QUAL_FEATURE.SECURITY, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have REST API", Enumerator.QUAL_FEATURE.API, null);
				auxReq.setPositive(true);
				auxReq.setQualValue("REST");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have SOAP API", Enumerator.QUAL_FEATURE.API, null);
				auxReq.setPositive(true);
				auxReq.setQualValue("SOAP");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have console", Enumerator.QUAL_FEATURE.CONSOLE, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have monitoring", Enumerator.QUAL_FEATURE.MONITORING, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have redundancy", Enumerator.QUAL_FEATURE.REDUNDANCY, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min ListRequests > 500", null, Enumerator.QUANT_FEATURE.LISTREQUESTS);
				auxReq.setMin(500);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min DeleteRequests > 500", null, Enumerator.QUANT_FEATURE.DELETEREQUESTS);
				auxReq.setMin(500);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				
				//add component to the components list
				comps.add(auxComp);
		
		//TEMPLATE7
				auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database with 20 requirements");
				auxComp.setWeight(4);

				auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
				auxReq.setMin(6204800);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
				auxReq.setPositive(true);
				auxReq.setQualValue("EU");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
				auxReq.setPositive(false);
				auxReq.setQualValue("MySQL");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
				auxReq.setMin((float)99.9);
				auxReq.setExclusivityMax(false);
				auxReq = new Requirement("min filesize > 5Gb", null, Enumerator.QUANT_FEATURE.FILESIZE);
				auxReq.setMin(5);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have security", Enumerator.QUAL_FEATURE.SECURITY, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have REST API", Enumerator.QUAL_FEATURE.API, null);
				auxReq.setPositive(true);
				auxReq.setQualValue("REST");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have SOAP API", Enumerator.QUAL_FEATURE.API, null);
				auxReq.setPositive(true);
				auxReq.setQualValue("SOAP");
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have console", Enumerator.QUAL_FEATURE.CONSOLE, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have monitoring", Enumerator.QUAL_FEATURE.MONITORING, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("must have redundancy", Enumerator.QUAL_FEATURE.REDUNDANCY, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min ListRequests > 500", null, Enumerator.QUANT_FEATURE.LISTREQUESTS);
				auxReq.setMin(500);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min DeleteRequests > 500", null, Enumerator.QUANT_FEATURE.DELETEREQUESTS);
				auxReq.setMin(500);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min GetRequests > 5000", null, Enumerator.QUANT_FEATURE.GETREQUESTS);
				auxReq.setMin(5000);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min PutRequests > 500", null, Enumerator.QUANT_FEATURE.PUTREQUESTS);
				auxReq.setMin(500);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min CopyRequests > 500", null, Enumerator.QUANT_FEATURE.COPYREQUESTS);
				auxReq.setMin(500);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("min PostRequests > 500", null, Enumerator.QUANT_FEATURE.POSTREQUESTS);
				auxReq.setMin(500);
				auxReq.setExclusivityMax(false);
				auxComp.addReq(auxReq);
				auxReq = new Requirement("Network Protocol", Enumerator.QUAL_FEATURE.PROTOCOL, null);
				auxReq.setPositive(true);
				auxComp.addReq(auxReq);
				
				//add component to the components list
				comps.add(auxComp);
				
				return new CSAData(comps, reqs, criteria);
	}
	
	private CSAData useCaseSimulation(){
		ArrayList<ServiceTemplate> comps = new ArrayList<ServiceTemplate>() ;
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		ArrayList<Criterion> criteria = new ArrayList<Criterion>() ;
		
		int componentPreffix = 1;
		//initiate the components and the requirements for each of them
		ServiceTemplate auxComp;
		Requirement auxReq;
		Criterion auxCrit;
		
		//GENERAL REQUIREMENTS & CRITERIA
		auxReq = new Requirement();
		auxReq.setDescription("The overall System price must be bellow 5000€");
		auxReq.setMax(1000);
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);
		auxReq = new Requirement("Availability of the system must be above 99.9%", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
		auxReq.setMin((float)99.9);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		
	
		
		//DATABASE
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "The database for energy monitoring data starage");
		auxComp.setWeight(4);
		
		//database requirements
		auxReq = new Requirement("min storage capacity > 200TB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(204800);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have encryption features", Enumerator.QUAL_FEATURE.ENCRYPTION, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(false);
		auxReq.setQualValue("MySQL");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min data transfer out capacity > 200TB", null, Enumerator.QUANT_FEATURE.DATAOUTEXTERNAL);
		auxReq.setMin(204800);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		
		//add component to the components list
		comps.add(auxComp);
		
		
		//DATA ANALYTICS
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "Data Analytics platform", "The platform that will process and analyse energy data");
		auxComp.setWeight(5);
		
		auxReq = new Requirement("must have 24X7 support", Enumerator.QUAL_FEATURE.SUPPORT_24_7, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Should have SSL, but is not mandatory", Enumerator.QUAL_FEATURE.SSL, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have backup service", Enumerator.QUAL_FEATURE.BACKUP_RECOVERY, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min API calls > 2000000", null, Enumerator.QUANT_FEATURE.APICALLS);
		auxReq.setMin(2000000);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);

		//add component to the components list
		comps.add(auxComp);
				
		
		//WEB SERVER
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "WebServer", "A machine to work as web server");
		auxComp.setWeight(3);
		
		auxReq = new Requirement("Must have the platform Apache", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("Apache");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be autoscalable", Enumerator.QUAL_FEATURE.SCALABILITY, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("autoscalling");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min CPU cores > 4", null, Enumerator.QUANT_FEATURE.CPUCORES);
		auxReq.setMin(4);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Some network performance metric", Enumerator.QUAL_FEATURE.PERFORMANCE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("Network");
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min storage capacity > 500Gb", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(500);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("max main memory < 16", null, Enumerator.QUANT_FEATURE.MEMORYSIZE);
		auxReq.setMax(16);
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must support php language", Enumerator.QUAL_FEATURE.LANGUAGE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("php");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be a UNIX machine", Enumerator.QUAL_FEATURE.UNIX, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		
		//add component to the components list
		comps.add(auxComp);
		
		//APP PLATFORM
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "App Platform", "App platform to hold the Applciation");
		auxComp.setWeight(2);
		
		auxReq = new Requirement("Must have the platform Apache", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("Apache");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be autoscalable", Enumerator.QUAL_FEATURE.SCALABILITY, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("autoscalling");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must be located in Europe", Enumerator.QUAL_FEATURE.LOCATION, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("EU");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min CPU cores > 8", null, Enumerator.QUANT_FEATURE.CPUCORES);
		auxReq.setMin(4);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Some network performance metric", Enumerator.QUAL_FEATURE.PERFORMANCE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("Network");
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must support Ruby language", Enumerator.QUAL_FEATURE.LANGUAGE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("ruby");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min storage capacity > 500Gb", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(500);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("max main memory < 8", null, Enumerator.QUANT_FEATURE.MEMORYSIZE);
		auxReq.setMax(8);
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		
		//add component to the components list
		comps.add(auxComp);
		
		//MAIL SERVICE
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "Mail Service", "a mail serviceto send all the reports");
		auxComp.setWeight(1);
		
		auxReq = new Requirement("Must send emails", Enumerator.QUAL_FEATURE.MESSAGETYPE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("email");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("Must use SMTP", Enumerator.QUAL_FEATURE.MESSAGEPROTOCOL, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("smtp");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min number of message > 5000", null, Enumerator.QUANT_FEATURE.MESSAGENUMBER);
		auxReq.setMin(5000);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min number of dedicated IP > 1", null, Enumerator.QUANT_FEATURE.DEDICATEDIP);
		auxReq.setMin(1);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("send files bigger than > 100Mb", null, Enumerator.QUANT_FEATURE.FILESIZE);
		auxReq.setMin((float)0.09765625);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		
		//add component to the components list
		comps.add(auxComp);
		
		//SMS SERVICE
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "sms Service", "a sms service that will interact with the app");
		auxComp.setWeight(1);
		
		auxReq = new Requirement("Must send SMS", Enumerator.QUAL_FEATURE.MESSAGETYPE, null);
		auxReq.setPositive(true);
		auxReq.setQualValue("sms");
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min number of message > 1000", null, Enumerator.QUANT_FEATURE.MESSAGENUMBER);
		auxReq.setMin(1000);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min number of users > 4", null, Enumerator.QUANT_FEATURE.USERS);
		auxReq.setMin(4);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		
		//add component to the components list
		comps.add(auxComp);
		
		return new CSAData(comps, reqs, criteria);
	}
	
	private CSAData herokuSimulation(){
		ArrayList<ServiceTemplate> comps = new ArrayList<ServiceTemplate>() ;
		ArrayList<Requirement> reqs = new ArrayList<Requirement>() ;
		
		int componentPreffix = 1;
		//initiate the components and the requirements for each of them
		ServiceTemplate auxComp;
		Requirement auxReq;
		
//database Component
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database", "a database component");
		auxComp.setWeight(2);
		
		//database requirements
		auxReq = new Requirement("min storage capacity > 500GB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(500);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must have backup features", Enumerator.QUAL_FEATURE.BACKUP_RECOVERY, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("must support PostgreSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setQualValue("PostgreSQL");
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("dont want platform MYSQL", Enumerator.QUAL_FEATURE.PLATFORM, null);
		auxReq.setPositive(false);
		auxReq.setQualValue("MySQL");
		auxComp.addReq(auxReq);
		
		//add criterion to the database
		/*Criterion c1 = new Criterion(Enumerator.QUAL_FEATURE.LOADBALANCING.getValue());
		c1.setWeight(1.5);
		c1.setPreferenceDirection("max");
		auxComp.addCrit(c1);
		Criterion c2 = new Criterion(Enumerator.QUAL_FEATURE.BACKUP_RECOVERY.getValue());
		c2.setWeight(4);
		c2.setPreferenceDirection("max");
		auxComp.addCrit(c2);*/
		
		//add component to the components list
		comps.add(auxComp);
		
//data component
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database2", "a data component");
		auxComp.setWeight(3);
		
		auxReq = new Requirement("min storage capacity > 500GB", null, Enumerator.QUANT_FEATURE.STORAGECAPACITY);
		auxReq.setMin(500);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("availability", null, Enumerator.QUANT_FEATURE.AVAILABILITY);
		auxReq.setMin(99);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("cache size", null, Enumerator.QUANT_FEATURE.CACHESIZE);
		auxReq.setMin(16);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		
		comps.add(auxComp);
		
//data component
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++, "database3", "a data component");
		auxComp.setWeight(1);
		
		auxReq = new Requirement("cache size", null, Enumerator.QUANT_FEATURE.CACHESIZE);
		auxReq.setMin(5);
		auxReq.setExclusivityMax(false);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		
		comps.add(auxComp);
		
//data processing component
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++,"dataProcessing", "a unit to process data");
		auxComp.setWeight((float)1.5);
		//data processing requirements
		auxReq = new Requirement("min of 4 cpu cores", null, Enumerator.QUANT_FEATURE.CPUCORES);
		auxReq.setMin(1);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("min disk size of 160GB", null, Enumerator.QUANT_FEATURE.DISKSIZE);
		auxReq.setMin(160);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		/*auxReq = new Requirement("needs load balancing", Enumerator.QUAL_FEATURE.LOADBALANCING, null);
		auxReq.setNeeded(true);
		auxReq.setCriterion(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("needs monitoring", Enumerator.QUAL_FEATURE.MONITORING, null);
		auxReq.setNeeded(true);
		auxComp.addReq(auxReq);*/
		
		//add a criterion to the data processing component
		//binary (Y/N)
		Criterion c3 = new Criterion(Enumerator.QUAL_FEATURE.LOADBALANCING.getValue());
		c3.setWeight(1.0);
		c3.setPreferenceDirection("max");
		auxComp.addCrit(c3);
		
		//add component to the components list
		//comps.add(auxComp);
		
		
		//web-server component
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++,"web-server", "a server for processing");
		
		//web-server requirements
		auxReq = new Requirement("needs to be linux", Enumerator.QUAL_FEATURE.UNIX, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("needs load balancing", Enumerator.QUAL_FEATURE.LOADBALANCING, null);
		auxReq.setPositive(true);
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);
		auxReq = new Requirement("needs monitoring", Enumerator.QUAL_FEATURE.MONITORING, null);
		auxReq.setPositive(true);
		auxComp.addReq(auxReq);
		
		//add a criterion to the web-server component
		//binary (Y/N)
		Criterion c7 = new Criterion(Enumerator.QUAL_FEATURE.LOADBALANCING.getValue());
		c7.setWeight(1.0);
		c7.setPreferenceDirection("max");
		auxComp.addCrit(c7);
				
		//add component to the components list
		//comps.add(auxComp);
		
		//mobile app platform
		auxComp = new ServiceTemplate(COMPONENT_PREFIX+componentPreffix++,"mobile-App engine", "a platform for building mobile apps");
		
		//mobile app requirements
		auxReq = new Requirement("needs access to 3 users", null, Enumerator.QUANT_FEATURE.USERS);
		auxReq.setMin(3);
		auxReq.setExclusivityMax(false);
		auxComp.addReq(auxReq);
		auxReq = new Requirement("supported language", Enumerator.QUAL_FEATURE.LANGUAGE, null);
		auxReq.setQualValue("JAVA");
		auxReq.setCriterion(true); //this requirement will be a criterion
		auxComp.addReq(auxReq);

		//add a criterion to the mobile app platform component
		//non-numerical
		Criterion c8 = new Criterion(Enumerator.QUAL_FEATURE.LANGUAGE.getValue());
		c8.setWeight(1.5);
		c8.setPreferenceDirection("max");
		c8.setPreference("JAVA");
		auxComp.addCrit(c8);
				
		//add component to the components list
		//comps.add(auxComp);
		
		//initiate the general requirements. These requirements will be used for reaching a decision, but no minimum or maximum values are assigned
		//Our EMES systems deals with a lot of data, so values for the data in and out of the cloud are needed.
		/*auxReq = new Requirement("data processed", null, Enumerator.QUANT_FEATURE.DATAPROCESSED);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);
		auxReq = new Requirement("data in", null, Enumerator.QUANT_FEATURE.DATAINEXTERNAL);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);
		auxReq = new Requirement("data out external", null, Enumerator.QUANT_FEATURE.DATAOUTEXTERNAL);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);
		auxReq = new Requirement("data out internal", null, Enumerator.QUANT_FEATURE.DATAOUTINTERNAL);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);*/
		auxReq = new Requirement();
		auxReq.setDescription("price must be bellow 4000");
		auxReq.setMax(4000);
		auxReq.setExclusivityMax(true);
		auxReq.setCriterion(true); //this requirement will be a criterion
		reqs.add(auxReq);
		
		
		//export the simulation data
		return new CSAData(comps, reqs, generateCriteria());
	}
	
	public ArrayList<Criterion> generateCriteria(){
		ArrayList<Criterion> criteria = new ArrayList<Criterion>();
		
		/*Criterion c1 = new Criterion("price");
		c1.setWeight(2);
		c1.setPreferenceDirection("min");
		c1.setType(0); //numerical criterion
		criteria.add(c1);
		Criterion c2 = new Criterion(Enumerator.QUANT_FEATURE.DATAPROCESSED.getValue());
		c2.setWeight(2);
		c2.setPreferenceDirection("max");
		c2.setType(0); //numerical criterion
		criteria.add(c2);
		Criterion c4 = new Criterion(Enumerator.QUANT_FEATURE.DATAOUTINTERNAL.getValue());
		c4.setWeight(1.5);
		c4.setPreferenceDirection("max");
		c4.setType(0); //numerical criterion
		criteria.add(c4);
		Criterion c5 = new Criterion(Enumerator.QUANT_FEATURE.DATAINEXTERNAL.getValue());
		c5.setWeight(1.5);
		c5.setPreferenceDirection("max");
		c5.setType(0); //numerical criterion
		criteria.add(c5);
		Criterion c6 = new Criterion(Enumerator.QUANT_FEATURE.DATAOUTEXTERNAL.getValue());
		c6.setWeight(1.5);
		c6.setPreferenceDirection("max");
		c6.setType(0); //numerical criterion
		criteria.add(c6);*/
		return criteria;
	}
	
	
	public CSAData getData() {
		return data;
	}

	public void setData(CSAData data) {
		this.data = data;
	}
	
}
