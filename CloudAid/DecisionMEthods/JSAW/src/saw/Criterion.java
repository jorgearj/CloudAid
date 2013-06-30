package saw;

public class Criterion {
	
	public static final int NUMERICAL = 0;
	public static final int BINARY = 1;
	public static final int NON_NUMERICAL = 2;
	
	private String id;
	private String name;
	private double wheight;
	private String preferenceDirection;
	private String preference;
	private int type = -1;
	
	public Criterion(String name){
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getWheight() {
		return wheight;
	}

	public void setWheight(double wheight) {
		this.wheight = wheight;
	}
	

	public String getPreferenceDirection() {
		return preferenceDirection;
	}

	public void setPreferenceDirection(String preferenceDirection) {
		this.preferenceDirection = preferenceDirection;
	}

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Criterion [id=" + id + ", name=" + name + ", wheight="
				+ wheight + ", preferenceDirection=" + preferenceDirection
				+ ", preference=" + preference + ", type=" + type + "]";
	}

	public boolean isNumerical(){
		if(this.type == Criterion.NUMERICAL)
			return true;
		
		return false;
	}
	
	public boolean hasPreference(){
		if(this.preference != null)
			return true;
		
		return false;
	}

}
