/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package searchEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import data.servicedata.Enumerator.QUANT_FEATURE;
import data.servicedata.Enumerator.QUAL_FEATURE;
import data.servicedata.Enumerator.UNIT_OF_MEASUREMENT;
import data.servicedata.Offering;
import data.servicedata.QualitativeFeature;
import data.servicedata.QuantitativeFeature;

public class ResourceConverter {
	
	private final static String FOAF = "http://xmlns.com/foaf/0.1/";
	private final static String PRICE = "http://www.linked-usdl.org/ns/usdl-price#";
	private final static String USDL = "http://www.linked-usdl.org/ns/usdl-core#";
	private final static String LEGAL= "http://www.linked-usdl.org/ns/usdl-legal#";
	private final static String SLA = "http://www.linked-usdl.org/ns/usdl-sla#";
	private final static String CLOUD = "http://rdfs.genssiz.org/CloudTaxonomy#";
	private final static String RDF  = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	private final static String GR   = "http://purl.org/goodrelations/v1#";
	
	private static Model model = ModelFactory.createDefaultModel();
	private static Property label = model.createProperty(RDFS+"label");
	private static Property comment = model.createProperty(RDFS+"comment");
	private static Property type = model.createProperty(RDF+"type");
	private static Property qualitativeProductOrServiceProperty = model.createProperty(GR+"qualitativeProductOrServiceProperty");
	private static Property quantitativeProductOrServiceProperty = model.createProperty(GR+"quantitativeProductOrServiceProperty");
	private static Property hasValue = model.createProperty(GR+"hasValue");
	private static Property hasUnitOfMeasurement = model.createProperty(GR+"hasUnitOfMeasurement");
	private static Property hasCurrency = model.createProperty(GR+"hasCurrency");
	private static Property name = model.createProperty(GR+"name");
	private static Property hasCurrencyValue = model.createProperty(GR+"hasCurrencyValue");
	private static Property includes = model.createProperty(USDL+"includes");
	private static Property hasServiceModel = model.createProperty(USDL+"hasServiceModel");
	private static Property hasProvider = model.createProperty(USDL+"hasProvider");
	private static Property hasPriceComponent = model.createProperty(PRICE+"hasPriceComponent");
	private static Property hasMetrics = model.createProperty(PRICE+"hasMetrics");
	private static Property hasPricePlan = model.createProperty(PRICE+"hasPricePlan");
	private static Property hasPrice = model.createProperty(PRICE+"hasPrice");
	private static Property isLinkedTo = model.createProperty(PRICE+"isLinkedTo");
	private static Property page = model.createProperty(FOAF+"page");

	public static ArrayList<Offering> convertOfferings(HashMap<Resource,Float> resources){
		ArrayList<Offering> services = new ArrayList<Offering>();
		
		Iterator it = resources.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        Resource r = (Resource) pairs.getKey();
	        Float f = (Float) pairs.getValue();	
			services.add(convertOffering(r, f));
		}
		
		return services;
	}
	
	public static Offering convertOffering(Resource resource, Float price){
		Offering offering = new Offering();
		
		offering.setId(resource.getLocalName());
		if( resource.hasProperty(label)){
			offering.setName(resource.getProperty(label).getString());
		}else{
			offering.setName(resource.getLocalName());
		}
		
		if( resource.hasProperty(comment)){
			offering.setDescription(resource.getProperty(comment).getString());
		}
		
		//get included services
		StmtIterator iter = resource.listProperties(includes);
		//System.out.println(iter.toList().size());
		while (iter.hasNext()) {
			Resource service = iter.next().getResource();
			offering = addFeatures(service, offering);
		}
		
		//get offering price
		offering = getOfferingPrice(price, offering);
		
		return offering;
		
	}
	
	public static Offering addFeatures(Resource service, Offering offering){
		ArrayList<Resource> services = new ArrayList<Resource>();
		
		services.add(service);
		
		//get the parent features
		StmtIterator iter = service.listProperties(hasServiceModel);
		while (iter.hasNext()) {
			//get the Service Parent
			services.add(iter.next().getResource()); 
		}
		
		for(Resource r : services){
			offering = addServiceFeatures(r, offering);
		}
		
		return offering;
	}
	
	public static Offering addServiceFeatures(Resource service, Offering offering){
		ArrayList<QualitativeFeature> qualFeatures = offering.getQualitativeFeatures();
		ArrayList<QuantitativeFeature> quantFeatures = offering.getQuantitativeFeatures();
		
		/*//adding the offering provider
		if(service.hasProperty(hasProvider)){
			Resource provider = service.getPropertyResourceValue(hasProvider);
			if(provider.hasProperty(page))
				offering.setURL(provider.getProperty(page).getString());
		}*/
		
		//get qualitativeFeatures
		StmtIterator iter = service.listProperties(qualitativeProductOrServiceProperty);
		//System.out.println(iter.toList().size());
		while (iter.hasNext()) {
			Resource feature = iter.next().getObject().asResource();
			qualFeatures.addAll(convertQualitativeFeatures(feature)) ;
		}
		//get quantitative
		StmtIterator iter2 = service.listProperties(quantitativeProductOrServiceProperty);
		//System.out.println(iter.toList().size());
		while (iter2.hasNext()) {
			Resource feature = iter2.next().getObject().asResource();
			quantFeatures.addAll(convertQuantitativeFeatures(feature)) ;
		}
		
		
		return offering;
	}
	
	private static ArrayList<QuantitativeFeature> convertQuantitativeFeatures(Resource r){
		ArrayList<QuantitativeFeature> features = new ArrayList<QuantitativeFeature>();
		
		//a feature can have multiple types
		StmtIterator iter = r.listProperties(type);
		//iterate through types
		while (iter.hasNext()) {
			Resource rType = iter.next().getResource();
			String type = rType.getLocalName();
			if(QUANT_FEATURE.get(type) != null){
				features.add(convertQuantitativeFeature(r, type));
			}			
		}
		
		return features;
	}
	
	private static QuantitativeFeature convertQuantitativeFeature(Resource r, String attributeType){		
		QuantitativeFeature qf = new QuantitativeFeature();
		qf.setName(r.getLocalName());
		qf.setType(QUANT_FEATURE.get(attributeType));
		if(r.hasProperty(comment))
			qf.setDescription(r.getProperty(comment).getString());
		if(r.hasProperty(hasValue))
			qf.setValueFloat(r.getProperty(hasValue).getFloat());
		if(r.hasProperty(hasUnitOfMeasurement))
			qf.setUnitOfMeasurement(UNIT_OF_MEASUREMENT.get(r.getProperty(hasUnitOfMeasurement).getString()));
		
		return qf;
	}
	
	private static ArrayList<QualitativeFeature> convertQualitativeFeatures(Resource r){
		ArrayList<QualitativeFeature> features = new ArrayList<QualitativeFeature>();
		
		//a feature can have multiple types
		StmtIterator iter = r.listProperties(type);
		//iterate through types
		while (iter.hasNext()) {
			Resource rType = iter.next().getResource();
			String type = rType.getLocalName();
			if(QUAL_FEATURE.get(type) != null){
				features.add(convertQualitativeFeature(r, type));
			}			
		}
				
		return features;
	}
	
	private static QualitativeFeature convertQualitativeFeature(Resource r, String attributeType){
		QualitativeFeature qf = new QualitativeFeature();
		qf.setType(QUAL_FEATURE.get(attributeType));
		if(r.hasProperty(comment))
			qf.setDescription(r.getProperty(comment).getString());
		if(r.hasProperty(name))
			qf.setName(r.getProperty(name).getString());
		
		return qf;
	}
	
	//does not use priceCaps that may exist
	private static Offering getOfferingPrice(Float price, Offering offering){
		HashMap<String, String> attributes = new HashMap<String, String>();
			
		attributes.put("price", Float.toString(price));
		offering.setAttributes(attributes);
		
		return offering;
	}
	
}
