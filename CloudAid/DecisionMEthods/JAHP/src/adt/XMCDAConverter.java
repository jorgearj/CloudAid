package adt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternativeOnCriteriaPerformances;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XAlternatives;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriteria;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XCriterion;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMethodParameters;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XParameter;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPerformanceTable;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XPreferenceDirection;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XQuantitative;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XScale;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XValue;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAReadUtils;
import org.decisiondeck.jmcda.persist.xmcda2.utils.XMCDAWriteUtils;

import com.google.common.io.Files;

public class XMCDAConverter {
	
	private final String ALTERNATIVE_PREFIX = "alt";
	private final String CRITERION_PREFIX = "cri";
	
	private String dest = "./Decision/AHP";
	public XMCDAConverter(){
		
	}
	
	public XMCDA getFromFile(String source){
		File f = new File(source);
		XMCDAReadUtils reader = new XMCDAReadUtils();
		XMCDADoc doc = null;
		try {
			doc = reader.getXMCDADoc(Files.newInputStreamSupplier(f));
		} catch (IOException | XmlException e) {
			System.err.println("Unable to retrieve XMCDA data from file!!!");
			e.printStackTrace();
		}
		return doc.getXMCDA();
	}
	
	public HashMap<String, String> getMethodParameters(XMCDA xmcda){
		HashMap<String, String> param = new HashMap<String, String>(); 
		
		List<XMethodParameters> methodParameters = xmcda.getMethodParametersList();
		List<XParameter> parametersList = methodParameters.get(0).getParameterList();
		
		for(XParameter parameter : parametersList){
			param.put(parameter.getName(), parameter.getValue().getLabel());
		}
		
		return param;
	}
	
	public XMCDA attachCompTimestamp(String time, String compId){
		XMCDA XMCDATime = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMethodParameters parameters = XMCDATime.addNewMethodParameters();
		XParameter p1 = parameters.addNewParameter();
		p1.setName("FileTimestamp");
		XValue val1 = p1.addNewValue();
		val1.setLabel(time);
		XParameter p2 = parameters.addNewParameter();
		p2.setName("ComponentID");
		XValue val2 = p2.addNewValue();
		val2.setLabel(compId);
		
		return XMCDATime;
		
	}
	
	public XMCDA createPerformance(Hierarchy h){
		//create the XMCDA file
		XMCDA XMCDAPerformance = XMCDADoc.XMCDA.Factory.newInstance();
		
		XPerformanceTable performanceTable = XMCDAPerformance.addNewPerformanceTable();
		for(int i=0; i<h.getNb_alternatives(); i++){
			XAlternativeOnCriteriaPerformances performances = performanceTable.addNewAlternativePerformances();
			Alternative alt=(Alternative)(h.getAlternatives()).get(i);
			performances.setAlternativeID(alt.getId());
			XAlternativeOnCriteriaPerformances.Performance p = performances.addNewPerformance();
			p.setCriterionID(h.getGoal().getId());
			XValue v = p.addNewValue();
			Double res = new Double((h.getGoal()).Jstar(i));
			v.setReal(res.floatValue());
			System.out.print(alt.getName()+" -- ");
			System.out.println(res);
		}
	    
		//System.out.println(XMCDAPerformance.toString());
		
		return XMCDAPerformance;
	}
	
	
	public XMCDA append(ArrayList<XMCDA> list){
		XMCDA xmcda = XMCDADoc.XMCDA.Factory.newInstance();
		
		XMCDAWriteUtils writer = new XMCDAWriteUtils();
		
		writer.appendTo(list, xmcda);
		
		//print data
		//System.out.println(xmcda.toString());
		
		return xmcda;
		
	}
	
	public void export(XMCDA xmcda, String name){
		XMCDAWriteUtils writer = new XMCDAWriteUtils();
		XMCDADoc file = XMCDADoc.Factory.newInstance();
		
		File result = new File(dest+name);
		
		file.setXMCDA(xmcda);
		try {
			writer.write(file, Files.newOutputStreamSupplier(result));
		} catch (IOException e) {
			System.out.println("Unable to create file!!!");
			e.printStackTrace();
		}
	}

}
