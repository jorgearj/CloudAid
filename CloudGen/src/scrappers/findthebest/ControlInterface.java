/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: ControlInterface.java, Project: CloudGen, 13 Apr 2013 Author: Jorge Araújo
*/

package scrappers.findthebest;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.*;


@XStreamAlias("control_Interface")
public class ControlInterface {
	
	@XStreamImplicit
    private List<String> value = new ArrayList<String>();
	
	

	public List<String> getValue() {
		return value;
	}



	public void setValue(List<String> value) {
		this.value = value;
	}



	@Override
	public String toString() {
		return "ControlInterface [values=" + value + "]";
	}
	
	

}
