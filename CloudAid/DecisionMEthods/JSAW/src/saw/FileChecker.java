package saw;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;

import xmcda.XMCDAConverter;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileChecker {
	
	private Path directory;
	private Path SaveDirectory;
	WatchService watcher;
	
	public FileChecker(String source, String dest){
		this.directory = FileSystems.getDefault().getPath(source);
		this.SaveDirectory = FileSystems.getDefault().getPath(dest);
		
	}
	
	public void init(){
		System.out.println("Listening at source: "+ this.directory.toString());
		System.out.println("Posting results at destination: "+ this.SaveDirectory.toString());
		try {
			watcher = FileSystems.getDefault().newWatchService();
			WatchKey key = this.directory.register(watcher,ENTRY_CREATE);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		checker();
	}
	
	private void checker(){
		for (;;) {
		    // wait for key to be signaled
		    WatchKey key;
		    try {
		        key = watcher.take();
		    } catch (InterruptedException x) {
		        return;
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

		        // Resolve the filename against the directory.
				// If the filename is "test" and the directory is "foo",
				// the resolved name is "test/foo".
				Path child = this.directory.resolve(filename);
				System.out.println(child.toString());
				executeOperation(child);
		    }

		    // Reset the key -- this step is critical if you want to
		    // receive further watch events.  If the key is no longer valid,
		    // the directory is inaccessible so exit the loop.
		    boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }
		}
	}
	
	private void executeOperation(Path filename){
		ArrayList<XMCDA> files = new ArrayList<XMCDA>();
		HashMap<String, String> params;
		XMCDAConverter converter = new XMCDAConverter();
		XMCDA xmcda = converter.getFromFile(filename.toString());
		
		
		SAW saw = new SAW(converter.getAlternatives(xmcda), converter.getCriteria(xmcda));
		
		params = converter.getMethodParameters(xmcda);
		String time = params.get("FileTimestamp");
		String compId = params.get("ComponentID");
		System.out.println(time);
		System.out.println(compId);
		
		files.add(converter.attachCompTimestamp(time, compId));
		
		ArrayList<Double> results = saw.decide();
		System.out.println(results);
		
		files.add(converter.createPerformance(saw.getAlternatives()));

		converter.export(converter.append(files), generateFileName(time));
	}
	
	private String generateFileName(String time){
		
		return this.SaveDirectory + "/XMCDA_Decision_" + time + ".xml"; 
		
	}
	
}
