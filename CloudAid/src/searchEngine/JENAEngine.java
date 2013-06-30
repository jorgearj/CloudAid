/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package searchEngine;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Controller.CloudAid;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import data.csadata.Requirement;
import data.servicedata.Offering;

public class JENAEngine {
	
	//testing variables
	long queryExecutionTime = 0;
	long convertionExecutionTime = 0;
	
	//prefixes
	private String ns;
	private final static String FOAF = "http://xmlns.com/foaf/0.1/";
	private final static String PRICE = "http://www.linked-usdl.org/ns/usdl-price#";
	private final static String USDL = "http://www.linked-usdl.org/ns/usdl-core#";
	private final static String LEGAL= "http://www.linked-usdl.org/ns/usdl-legal#";
	private final static String SLA = "http://www.linked-usdl.org/ns/usdl-sla#";
	private final static String CLOUD = "http://rdfs.genssiz.org/CloudTaxonomy#";
	private final static String RDF  = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	private final static String GR   = "http://purl.org/goodrelations/v1#";
	
	public final static String USDL_Core_Schema_File = "./USDL/usdl-core.ttl";
	public final static String USDL_Price_Schema_File = "./USDL/usdl-price.ttl";
	
	//properties
	private Property label;

	
	private static ServiceSet serviceSet;
	
	//only loads the serviceSet once
	static{
		serviceSet = new ServiceSet();
		int serviceCount = serviceSet.load();
		System.out.println("SYSTEM: Successfully loaded "+ serviceCount + " Service Descriptions.");
	}
	
	public JENAEngine(){
		//this.usdlCore = this.loadUSDL(USDL_Core_Schema_File);
		//this.usdlPrice = this.loadUSDL(USDL_Price_Schema_File);
		
		label = this.serviceSet.getServiceSet().createProperty(RDFS+"label");
	}

	
	private void updatePrefix(){
		Map prefixes;
		Model model = this.serviceSet.getServiceSet();
		prefixes = model.getNsPrefixMap();
		
		this.ns = (String) prefixes.get("");
		System.out.println("prefix map size: " + prefixes.size());
		System.out.println("NS prefix: " + this.ns);
		
		Iterator it = prefixes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println("- "+ pairs.getKey() + " = " + pairs.getValue());
	    }
	    System.out.println();
	}

	private Model loadUSDL(String File){
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
		
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(USDL_Core_Schema_File);
		if (in == null) {
		    throw new IllegalArgumentException("ERROR: File: " + USDL_Core_Schema_File + " not found");
		}
		
		// read the RDF/XML file
		model.read(in, "USDL", "TTL");
		
		return model;
	}
	
	private String queryBuilder(ArrayList<Requirement> reqs){
		boolean hasPrice = false;
		
		//adds the preffixes to the query
		String query = " PREFIX core: <"+ this.USDL+"> " +
		        " PREFIX price: <"+ this.PRICE+"> " +		
				" PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> " +
		        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
				" PREFIX gr: <http://purl.org/goodrelations/v1#> " +
				" PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#> "+
				" PREFIX CloudTaxonomy: <"+ this.CLOUD+">";
		
		//adds the query header
		query = query + 
				" SELECT REDUCED ?offering ?finalPrice " + 
				" WHERE { ";
		int count = 0;
		for(Requirement req : reqs){
			//build the requirement query
			if(count > 0)
				query = query + ".";
		
			if(req.getQualType() != null){
				query = query + 
						" { " +
						this.qualitativeReqQueryBuilder(req) +
						" } ";
			}else if(req.getQuantType() != null){
				query = query + 
						" { " +
						this.quantitativeReqQueryBuilder(req) +
						" } ";
			}else{
				query = query + 
						" { " +
						this.priceQueryBuilder(req) +
						" } ";
				hasPrice = true;
			}
			
			count++;
		}
		
		//if now requirement for the price has been specified we still want to compute the offering price
		if(!hasPrice){
			if(count > 0)
				query = query + ".";
			
			query = query + 
					" { " +
					this.priceQueryBuilder(null) +
					" } ";
		}
		
		//close the query
		query = query +
				" }";
		
		return query;
	}
	
	private String qualitativeReqQueryBuilder(Requirement req){
		String reqSearch = 
				" SELECT REDUCED ?offering " +
				" WHERE { " +
					" ?offering rdf:type core:ServiceOffering . " +
					" ?offering core:includes ?a . " ;
		if(!req.isPositive()){
			reqSearch = 
					reqSearch +
					" MINUS{ ";
		}
		String value = req.getQualValue();
		if(value != null){
			//there is a value to this requirement that must be included in the search
			String filter = " FILTER regex(?value, '"+ value +"', 'i') ";
			
			reqSearch = 
				reqSearch +
				" { " +
					" ?a gr:qualitativeProductOrServiceProperty ?f . " +
					" ?f rdf:type CloudTaxonomy:"+ req.getQualType().getValue() +" . " +
					" ?f gr:name ?value " +
					filter +
				" }UNION{ " +
					" ?a core:hasServiceModel ?model . " +
					" ?model gr:qualitativeProductOrServiceProperty ?f. " +
					" ?f rdf:type CloudTaxonomy:"+ req.getQualType().getValue() +" . " +
					" ?f gr:name ?value " +
					filter +
				" } ";
		}else{
			reqSearch = 
				reqSearch +
					" { " +
						" ?a gr:qualitativeProductOrServiceProperty CloudTaxonomy:"+ req.getQualType().getValue() +" . " +
					" }UNION{ " +
						" ?a gr:qualitativeProductOrServiceProperty ?f . " +
						" ?f rdf:type CloudTaxonomy:"+ req.getQualType().getValue() +" " +
					" }UNION{ " +
						" ?a core:hasServiceModel ?model . " +
						" ?model gr:qualitativeProductOrServiceProperty CloudTaxonomy:"+ req.getQualType().getValue() +" . " +
					" }UNION{ " +
						" ?a core:hasServiceModel ?model . " +
						" ?model gr:qualitativeProductOrServiceProperty ?f. " +
						" ?f rdf:type CloudTaxonomy:"+ req.getQualType().getValue() +" " +
					" } ";
		}
		
		if(!req.isPositive()){
			//close the MINUS statement
			reqSearch = 
					reqSearch +
					" } ";
		}
		
		return reqSearch + " } ";
	}
	
	private String quantitativeReqQueryBuilder(Requirement req){
			//this is a quantitative feature requirement
			String reqSearch;
			String filter = "";
			
			if(req.isExclusivityMax()){
				//we want values smaller than the max field
				filter = " FILTER(?value <= "+ req.getMax()+") ";
			}else{
				//we want values bigger than the min field
				filter = " FILTER(?value >= "+ req.getMin()+") ";
			}
			
			reqSearch = 
					" SELECT REDUCED ?offering " +
					" WHERE { " +
						" ?offering rdf:type core:ServiceOffering . " +
						" ?offering core:includes ?a . " +
						" { " + 
							" ?a gr:quantitativeProductOrServiceProperty CloudTaxonomy:"+ req.getQuantType().getValue() +" . " +
							" ?f gr:hasValue ?value " +
							filter +
						" }UNION{ " +
							" ?a gr:quantitativeProductOrServiceProperty ?f . " +
							" ?f rdf:type CloudTaxonomy:"+ req.getQuantType().getValue() +" . " +
							" ?f gr:hasValue ?value " + 
							filter +
						" }UNION{ " +
							" ?a core:hasServiceModel ?model . " +
							" ?model gr:quantitativeProductOrServiceProperty CloudTaxonomy:"+ req.getQuantType().getValue() +" . " +
							" ?f gr:hasValue ?value " +
							filter +
						" }UNION{ " +
							" ?a core:hasServiceModel ?model . " +
							" ?model gr:quantitativeProductOrServiceProperty ?f . " +
							" ?f rdf:type CloudTaxonomy:"+ req.getQuantType().getValue() +" . " +
							" ?f gr:hasValue ?value " +
							filter +
						" } " +
					" } ";
				
		return reqSearch;
	}
	
	private String priceQueryBuilder(Requirement req){
		
		String filter = "";
		
		if(req != null){
			if(req.isExclusivityMax()){
				//we want values smaller than the max field
				filter = " FILTER((?offeringPrice-?deduction) <= "+req.getMax()+") ";
			}else{
				//we want values bigger than the min field
				filter = " FILTER((?offeringPrice-?deduction) >= "+req.getMin()+") ";
			}
		}
		
		String reqSearch = 
				" SELECT ?offering ?offeringPrice ?deduction ((?offeringPrice-?deduction)as ?finalPrice) " +
				" WHERE{ " +
					 " { " +
						" SELECT ?offering ?offeringPrice (COALESCE(?finalDeductPrice, 0) AS ?deduction) " +
						" WHERE{ " +
							" { " +
								" SELECT ?offering (SUM(?price) AS ?offeringPrice) " +
								" WHERE{ " +
									" ?offering rdf:type core:ServiceOffering . " +
									" ?offering price:hasPricePlan ?plan . " +
									" ?plan price:hasPriceComponent ?comp . " +
									" ?comp rdf:type price:PriceComponent . " +
									" ?comp price:hasPrice ?priceSpec . " +
									" ?priceSpec gr:hasCurrencyValue ?price " +
								" } " +
								" GROUP BY ?offering " +
							" }OPTIONAL{ " +
								" SELECT ?offering (SUM(?deductPrice) AS ?finalDeductPrice) " +
								" WHERE{ " +
									" ?offering rdf:type core:ServiceOffering . " +
									" ?offering price:hasPricePlan ?plan . " +
									" ?plan price:hasPriceComponent ?comp . " +
									" ?comp rdf:type price:Deduction . " +
									" ?comp price:hasPrice ?priceSpec . " +
									" ?priceSpec gr:hasCurrencyValue ?deductPrice " +
								" } " +
								" GROUP BY ?offering " +
							" } " +
						" } " +
					" } " +
					filter +
				" } " ;
		
		return reqSearch;
	}
	
	public ArrayList<Offering> getOfferingByExcludedReqs(ArrayList<Requirement> reqs){
		ArrayList<Offering> offerings = new ArrayList<Offering>();
		HashMap<Resource,Float> resources = new HashMap<Resource,Float>();

		long start = System.nanoTime();
		String queryString = this.queryBuilder(reqs);
		//System.out.println("SYSTEM: "+ queryString);
				
		Query query = QueryFactory.create(queryString);
        QueryExecution exec = QueryExecutionFactory.create(query, this.serviceSet.getServiceSet());
		
		ResultSet results = exec.execSelect();

		while(results.hasNext()){
			QuerySolution row = results.next();
			resources.put(row.getResource("offering"), row.getLiteral("finalPrice").getFloat());
		}		
		
		exec.close();
		long end = System.nanoTime();
		this.queryExecutionTime = end - start;
		
		
		String[] msg = {"Number of alternatives found:" +resources.size()};
		CloudAid.askData(CloudAid.PROMPT, msg, null);
		
		Iterator it = resources.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        Resource r = (Resource) pairs.getKey();
	        Float f = (Float) pairs.getValue();
	        System.out.println("SYSTEM - "+ r.getLocalName() + " = " + f);
	    }
	    long startconvert = System.nanoTime();
		offerings = ResourceConverter.convertOfferings(resources);
		long endconvert = System.nanoTime();
		this.convertionExecutionTime = endconvert - startconvert;
		
		System.out.println("SYSTEM: Query Execution Time: "+ this.queryExecutionTime);
		System.out.println("SYSTEM: Convertion Execution Time: "+ this.convertionExecutionTime);
		
		return offerings;
	}
	
	public ArrayList<Offering> getOfferingByName(String name){
		ArrayList<Offering> offerings = new ArrayList<Offering>();
		HashMap<Resource,Float> resources = new HashMap<Resource,Float>();

		String queryString =
				" PREFIX core: <"+ this.USDL+"> " +
		        " PREFIX price: <"+ this.PRICE+"> " +		
				" PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> " +
		        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
				" PREFIX gr: <http://purl.org/goodrelations/v1#> " +
				" PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#> "+
				" PREFIX CloudTaxonomy: <"+ this.CLOUD+">" +
				" SELECT REDUCED ?offering ?finalPrice " +
		        " WHERE { " +
					" { " +
						" ?offering rdf:type core:ServiceOffering . "  +
						" ?offering rdfs:label ?a . " +
						" FILTER regex(?a, \"" + name + "\", \"i\") " +
					" }.{ " +
						this.priceQueryBuilder(null)+
					" }" +
				" } ";
				
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString);
        QueryExecution exec = QueryExecutionFactory.create(query, this.serviceSet.getServiceSet());
		
		ResultSet results = exec.execSelect();

		while(results.hasNext()){
			QuerySolution row = results.next();
			resources.put(row.getResource("offering"), row.getLiteral("finalPrice").getFloat());
		}		
		
		exec.close();
		System.out.println(resources.size());
		Iterator it = resources.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        Resource r = (Resource) pairs.getKey();
	        Float f = (Float) pairs.getValue();
	        System.out.println(" - "+ r.getLocalName() + " = " + f);
	    }
		
		offerings = ResourceConverter.convertOfferings(resources);
		
		return offerings;
	}
	
	public ArrayList<String> getOfferingNames() {
		ArrayList<String> names = new ArrayList<String>();
		
	    //System.out.println(" PREFIX ns: <"+ this.ns +"> ");
		String queryString =
		        " PREFIX core: <"+ this.USDL+">" +
				" PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> " +
		        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
		        " SELECT ?x " +
		        " WHERE { " +
				" ?x rdf:type core:ServiceOffering . "  +
				" }";
				
		Query query = QueryFactory.create(queryString);
        QueryExecution exec = QueryExecutionFactory.create(query, this.serviceSet.getServiceSet());
		
		ResultSet results = exec.execSelect();

		while(results.hasNext()){
			QuerySolution row = results.next();
			Resource r = row.getResource("x");
			if(r.hasProperty(label))
				names.add(r.getProperty(label).getString());
			else
				names.add(r.getLocalName());
		}		
		
		exec.close();
		
	    return names;
	}
		
}
