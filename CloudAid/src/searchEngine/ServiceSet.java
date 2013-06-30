/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package searchEngine;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

public class ServiceSet{
	
	//prefixes
	private String PRICE = "http://www.linked-usdl.org/ns/usdl-price#";
	private String CORE = "http://www.linked-usdl.org/ns/usdl-core#";
	private String LEGAL= "http://www.linked-usdl.org/ns/usdl-legal#";
	private String RDF  = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private String OWL  = "http://www.w3.org/2002/07/owl#";
	private String DC   = "http://purl.org/dc/elements/1.1/";
	private String XSD  = "http://www.w3.org/2001/XMLSchema#";
	private String VANN = "http://purl.org/vocab/vann/";
	private String FOAF = "http://xmlns.com/foaf/0.1/";
	private String USDK = "http://www.linked-usdl.org/ns/usdl#";
	private String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	private String GR   = "http://purl.org/goodrelations/v1#";
	private String SKOS = "http://www.w3.org/2004/02/skos/core#";
	private String ORG  = "http://www.w3.org/ns/org#";
	private String CLOUD= "http://rdfs.genssiz.org/CloudTaxonomy#";
	private String BASE  = "http://rdfs.genssiz.org/serviceSet#";
	private String SP	= "http://spinrdf.org/sp#";
	private String SPIN =    "http://spinrdf.org/spin#";
	private String SPL =     "http://spinrdf.org/spl#>";
	
	private final String serviceSetFolder = "./Services/";

	private Model serviceSet;
	private int nServices;
	
	public ServiceSet(){
		serviceSet = ModelFactory.createDefaultModel();
		this.setPrefixes();
		nServices = 0;
		
		//this.printPrefixes();
	}
	
	public int load(){
		int count = 0;
		
		ArrayList<String> fileNames = new ArrayList<String>();
		
		fileNames = this.getFileNames();
		
		System.out.println("SYSTEM: Loading ServiceSet...");
		for(String file : fileNames){
			Model temp;
			//System.out.println(test.getFileExtension(file));
			String ext = this.getFileExtension(file);
			if(ext.equalsIgnoreCase("ttl")){
				temp = this.readFile(file, "TTL" );
				if(temp != null){
					this.addPrefix(temp);
					this.serviceSet.add(temp);
					//checks if the file is a Linked-USDL
					if(this.validateUSDLFile(temp))
						count++;
				}
			}else if(ext.equalsIgnoreCase("rdf")){
				temp = this.readFile(file, "RDF/XML" );
				if(temp != null){
					this.addPrefix(temp);
					this.serviceSet.add(temp);
					//checks if the file is a Linked-USDL
					if(this.validateUSDLFile(temp))
						count++;
				}
			}
		}
		
		this.nServices = count;
		
		this.evaluateServiceSet();
		//writeUSDLModeltoFile();
		
		return this.nServices;
	}
	
	public void evaluateServiceSet(){
		int count = 0; 
		String queryString =
				" PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> " +
		        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
		        " SELECT (COUNT(*) as ?count)" +
		        " WHERE { " +
				" ?x ?y ?z . "  +
				" }";
				
		Query query = QueryFactory.create(queryString);
        QueryExecution exec = QueryExecutionFactory.create(query, this.serviceSet);
		
		ResultSet results = exec.execSelect();
		
		while(results.hasNext()){
			QuerySolution row = results.next();
			count = row.getLiteral("count").getInt();
		}		
		
		exec.close();
		System.out.println("SYSTEM: Total triples in ServiceSet: "+count);
	}
	
	public boolean clean(){
		try{
			this.serviceSet = serviceSet.removeAll();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public void addPrefix(Model service){
		
		try {
			String name = this.getModelName(service);
			this.serviceSet.setNsPrefix(name, service.getNsPrefixURI(""));
		} catch (NullPointerException e) {
			//e.printStackTrace();
			System.out.println("ERROR: Model without baseURI!!!");
		}
	}
	
	
	private void printPrefixes(){
		Iterator it = this.serviceSet.getNsPrefixMap().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println("        - "+ pairs.getKey() + " = " + pairs.getValue());
	    }
	}
	
	private String getModelName(Model model){
		String prefix = model.getNsPrefixURI("");
		String[] tokens = prefix.split("/");
		String name = tokens[tokens.length-1];
		name = name.replace("#", "");
		//System.out.println(name);
		
		return name;
	}
	
	private ArrayList<String> getFileNames(){
		final ArrayList<String> fileNames = new ArrayList<String>();
		
		try {
		    Path startPath = Paths.get(this.serviceSetFolder);
		    Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
		        @Override
		        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		            //System.out.println("Dir: " + dir.toString());
		            return FileVisitResult.CONTINUE;
		        }

		        @Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		            //System.out.println("File: " + file.toString());
		            if (file.getFileName().toString().endsWith(".ttl") || file.getFileName().toString().endsWith(".rdf")){
		            	fileNames.add(file.toString());
		            }		            
		            return FileVisitResult.CONTINUE;
		        }

		        @Override
		        public FileVisitResult visitFileFailed(Path file, IOException e) {
		            return FileVisitResult.CONTINUE;
		        }
		    });
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return fileNames;
	}
	
	private String getFileExtension(String file){
		String ext = null;
	    int i = file.lastIndexOf('.');

	    if (i > 0 &&  i < file.length() - 1) {
	        ext = file.substring(i+1).toLowerCase();
	    }
	    return ext;
	}
	
	private Model readFile(String file, String lang){
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open( file );
		if (in == null) {
		    throw new IllegalArgumentException("ERROR: File: " + file + " not found");
		}

		System.out.println("SYSTEM: Reading file: "+ file);
		// read the RDF/XML file
		model.read(in, "", lang);
		
		//USDL Validation - does not make sense, some files can be auxiliar to the service descriptions (vocabularies)
		/*if(this.validateUSDLFile(model)){
			System.out.println("The file: "+ file +" is a validated Linked-USDL Service Instance.");
			return model;
		}else{
			System.out.println("The file: "+ file +" is not a validated Linked-USDL Service Instance.");
			return null;
		}*/
		
		return model;
	}
	
	private boolean validateUSDLFile(Model model) {
		
		String queryString =
		        " PREFIX core: <"+ this.CORE+">" +
				" PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> " +
		        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
		        " SELECT ?a " +
		        " WHERE { " +
				" ?x rdf:type core:ServiceOffering . "  +
				" }";
		try{		
			Query query = QueryFactory.create(queryString);
	        QueryExecution exec = QueryExecutionFactory.create(query, model);
			
			ResultSet results = exec.execSelect();
			if(results.hasNext()){
				exec.close();
				return true;
			}			
		}catch(Exception ex){
			return false;
		}
		
		return false;
		
    }
	
	private void setPrefixes(){
		this.serviceSet.setNsPrefix("usdl",  CORE);
		this.serviceSet.setNsPrefix("rdf",   RDF);
		this.serviceSet.setNsPrefix("owl",   OWL);
		this.serviceSet.setNsPrefix("dc",    DC );
		this.serviceSet.setNsPrefix("xsd",   XSD);
		this.serviceSet.setNsPrefix("vann",  VANN);
		this.serviceSet.setNsPrefix("foaf",  FOAF);
		this.serviceSet.setNsPrefix("rdfs",  RDFS);
		this.serviceSet.setNsPrefix("gr",    GR  );
		this.serviceSet.setNsPrefix("skos",  SKOS);
		this.serviceSet.setNsPrefix("org",   ORG );
		this.serviceSet.setNsPrefix("price", PRICE );
		this.serviceSet.setNsPrefix("legal", LEGAL );
		this.serviceSet.setNsPrefix("cloud", CLOUD);
		this.serviceSet.setNsPrefix("sp", SP);
		this.serviceSet.setNsPrefix("spl", SPL);
		this.serviceSet.setNsPrefix("spin", SPIN);
		
		this.serviceSet.setNsPrefix("",   BASE );
	}

	public Model getServiceSet() {
		return serviceSet;
	}

	public void setServiceSet(Model serviceSet) {
		this.serviceSet = serviceSet;
	}

	public int getnServices() {
		return nServices;
	}

	public void setnServices(int nServices) {
		this.nServices = nServices;
	}
	
	private void writeUSDLModeltoFile() {
		Model m = this.serviceSet;
		
		try {
			File outputFile = new File("./ServiceSet.ttl");
			if (!outputFile.exists()) {
	        	outputFile.createNewFile();        	 
	        }
			
			FileOutputStream out = new FileOutputStream(outputFile);
			m.write(out, "TTL");
			out.close();
		}
		catch (IOException e) { System.out.println(e.toString()); }
	}
	
	
	private void test() {
		
		ArrayList<String> data = new ArrayList<String>();
		
	    //System.out.println(" PREFIX ns: <"+ this.ns +"> ");
		String queryString =
		        " PREFIX core: <"+ this.CORE+">" +
				" PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#> " +
		        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
				" PREFIX gr: <http://purl.org/goodrelations/v1#> " +
				" PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#> "+
				" PREFIX CloudTaxonomy: <"+ this.CLOUD+">"+
		        " SELECT REDUCED ?offering " +
				" WHERE { " +
					" ?offering rdf:type core:ServiceOffering . " +
					" ?offering core:includes ?a . " +
					" { " +
						" ?a gr:qualitativeProductOrServiceProperty CloudTaxonomy:Backup_Recovery . " +
					" }UNION{ " +
						" ?a gr:qualitativeProductOrServiceProperty ?f . " +
						" ?f rdf:type CloudTaxonomy:Backup_Recovery " +
					" }UNION{ " +
						" ?a core:hasServiceModel ?model . " +
						" ?model gr:qualitativeProductOrServiceProperty CloudTaxonomy:Backup_Recovery . " +
					" }UNION{ " +
						" ?a core:hasServiceModel ?model . " +
						" ?model gr:qualitativeProductOrServiceProperty ?f. " +
						" ?f rdf:type CloudTaxonomy:Backup_Recovery " +
					" } "+
				" } " ;
				
		Query query = QueryFactory.create(queryString);
        QueryExecution exec = QueryExecutionFactory.create(query, this.serviceSet);
		
		ResultSet results = exec.execSelect();

		System.out.println("SYSTEM: Test Results:");
		while(results.hasNext()){
			QuerySolution row = results.next();
			//System.out.println(row.getLiteral("type").getString());
			System.out.println(row.getResource("offering").getLocalName());
		}		
		
		exec.close();
		
    }
	
	public static void main(String[] args) {
		ServiceSet test = new ServiceSet();
		test.load();
		test.writeUSDLModeltoFile();
		test.test();
		
	}
	
}
