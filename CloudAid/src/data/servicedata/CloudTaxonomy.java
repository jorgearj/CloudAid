/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.servicedata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class CloudTaxonomy {
	private final String taxonomyPath = "./Taxonomy/CloudTaxonomy_v2.ttl";
	
	//PREFIXES
	private final String TAXONOMY = "http://rdfs.genssiz.org/CloudTaxonomy#";
	private final String DCTEARMS = "http://purl.org/dc/terms/";
	private final String FOAF = "http://xmlns.com/foaf/0.1/";
	private final String GN = "http://www.geonames.org/ontology#";
	private final String OWL = "http://www.w3.org/2002/07/owl#";
	private final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	private final String XSD = "http://www.w3.org/2001/XMLSchema#";

	private Model taxonomy;
	private ArrayList<Resource> concepts;
	
	public CloudTaxonomy(){
		this.taxonomy = loadTaxonomy();
		this.concepts = getConcepts();
		this.printConcepts();
	}
	
	private Model loadTaxonomy(){
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
		
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(this.taxonomyPath);
		if (in == null) {
		    throw new IllegalArgumentException("File: " + this.taxonomyPath + " not found");
		}
		
		// read the RDF/XML file
		model.read(in, "USDL", "TTL");
		
		return model;
	}
	
	private void printConcepts(){
		Property label = this.taxonomy.getProperty(RDFS, "label");
		System.out.println("TAXONOMY CONCEPTS IMPORTED:");
		for(Resource r: this.concepts){
			System.out.println("    - " + r.getProperty(label).getLiteral().getString());
		}
	}
	
	private ArrayList<Resource> getConcepts(){
		ArrayList<Resource> concepts = new ArrayList<Resource>();
		
		String queryString =
		        " PREFIX taxonomy: <"+ this.TAXONOMY+">" +
				" PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> " +
		        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
		        " SELECT ?x " +
		        " WHERE { " +
				" ?x rdf:type rdfs:Class . "  +
				" ?x rdfs:label ?a . " +
				" }";
				
		Query query = QueryFactory.create(queryString);
        QueryExecution exec = QueryExecutionFactory.create(query, this.taxonomy);
		
		ResultSet results = exec.execSelect();

		while(results.hasNext()){
			QuerySolution row = results.next();
			concepts.add(row.getResource("x"));
		}		
		
		exec.close();
		
		return concepts;
	}
	
	public static void main(String[] args) {
		CloudTaxonomy test = new CloudTaxonomy();
		
	}
}
