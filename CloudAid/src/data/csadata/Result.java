/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: Result.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package data.csadata;

import data.servicedata.Offering;

public class Result implements Comparable<Result>{
	
	private Offering service;
	private double performance;
	
	public Result(Offering serv, double perf){
		this.service = serv;
		this.performance = perf;
	}

	public Offering getService() {
		return service;
	}

	public void setService(Offering service) {
		this.service = service;
	}

	public double getPerformance() {
		return performance;
	}

	public void setPerformance(double performance) {
		this.performance = performance;
	}

	@Override
	public String toString() {
		return "Result [service=" + service + ", performance=" + performance
				+ "]";
	}

	@Override
	public int compareTo(Result r) {
		return new Double(this.performance).compareTo(new Double(r.performance));
	}

	
}
