package saw;

import java.util.ArrayList;
import java.util.HashMap;

public class SAW {
	
	private ArrayList<Alternative> alternatives;
	private ArrayList<Criterion> criteria;
	
	public SAW(ArrayList<Alternative> alt, ArrayList<Criterion> crit){
		this.alternatives = alt;
		this.criteria = crit; 
	}

	public ArrayList<Alternative> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(ArrayList<Alternative> alternatives) {
		this.alternatives = alternatives;
	}

	public ArrayList<Criterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(ArrayList<Criterion> criteria) {
		this.criteria = criteria;
	}
	
	public ArrayList<Double> decide(){
		ArrayList<Double> results = new ArrayList<Double>();
		HashMap<String, String> attr = new HashMap<String, String>(); 
		
		for(Alternative alternative : this.alternatives){
			System.out.print(alternative.getName() + ":  ");
			attr = alternative.getAttributes();
			Double sum = 0.0;
			for(Criterion criterion : this.criteria){
				//System.out.print(" "+criterion.getWheight() + " * " + attr.get(criterion.getName()));
				Double cellValue = criterion.getWheight() * Double.parseDouble(attr.get(criterion.getName()));
				System.out.print(" "+cellValue);
				sum = sum +cellValue; 
			}
			System.out.print(" ----- total: "+ sum);
			System.out.println();
			alternative.setValue(sum);
			results.add(sum);
		}
		
		return results;
	}

	@Override
	public String toString() {
		return "SAW [alternatives=" + alternatives + ", criteria=" + criteria
				+ "]";
	}
	
	
	

}
