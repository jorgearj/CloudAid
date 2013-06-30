/* Copyright (C) 2013 Jorge Araújo. All rights reserved.
* 
* This program and the accompanying materials are made available under
* the terms of the Common Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/cpl-v10.html
* 
* Id: test.java, Project: CloudAid, 13 Apr 2013 Author: Jorge Araújo
*/
package AggregationEngine;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileChecker {
	
	private Path directory;
	WatchService watcher;
	
	public FileChecker(String source){
		this.directory = FileSystems.getDefault().getPath(source);
		
	}
	
	public String listen(){	
		try {
			watcher = FileSystems.getDefault().newWatchService();
			WatchKey key = directory.register(watcher,ENTRY_CREATE);
			System.out.println("Listening at source: "+ directory.toString());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return checker();
	}
	
	private String checker(){
	    // wait for key to be signaled
	    WatchKey key;
	    try {
	        key = watcher.take();
	    } catch (InterruptedException x) {
	        return null;
	    }

	    for (WatchEvent<?> event: key.pollEvents()) {
	        WatchEvent.Kind<?> kind = event.kind();

	        // This key is registered only
	        // for ENTRY_CREATE events,
	        // but an OVERFLOW event can
	        // occur regardless if events
	        // are lost or discarded.
	        if (kind == OVERFLOW) {
	            continue;
	        }

	        // The filename is the
	        // context of the event.
	        WatchEvent<Path> ev = (WatchEvent<Path>)event;
	        Path filename = ev.context();

			Path child = this.directory.resolve(filename);
			System.out.println(child.toString());
			return child.toString();
	    }
	    return null;
	}
	
	private void executeOperation(Path filename){
		System.out.println("Teste");
	}
	
}
