/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Run.java, Project: CloudGen, 13 Apr 2013 Author: Jorge Araújo
*/

package main;

import generator.GeneralGenerator;

import java.util.Scanner;

public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		//ServicePopulator populator = new ServicePopulator();
		
		/*EnumSet<Enumerator.INTERFACE_TYPES> s = EnumSet.of(Enumerator.INTERFACE_TYPES.API, Enumerator.INTERFACE_TYPES.COMMAND_LINE, Enumerator.INTERFACE_TYPES.API);
		s.add(Enumerator.INTERFACE_TYPES.GUI);
		for(Enumerator.INTERFACE_TYPES aType : s) {
			System.out.println(aType);
		}*/
		
		int nrServices = 0;
		
		while(true){
			System.out.println("How many services you want to generate?");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			try{
				nrServices = Integer.parseInt(s);
				break;
			}catch(Exception ex){
				System.out.println("Please insert a numerical value");
			}
		}
		
		System.out.println("Starting Service Dataset Generator...");
		GeneralGenerator gen = new GeneralGenerator();
		gen.start(nrServices);
		System.out.println("Service Set generated. Exiting...");
		//tests
		/*PriceComponent comp = new PriceComponent(false);

		QuantitativeProperty test = new QuantitativeProperty("test", 12.0, "testType");
		System.out.println(test.getType());
		comp.addisLinkedTo(test);
		System.out.println(comp.getIsLinkedTo().get(0).getType());*/
		
		
		//populator.populate(serv);

	}

}
