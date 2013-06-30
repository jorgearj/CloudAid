package saw;

import java.util.HashMap;

public class Alternative {
	
	private String name;
	private String description;
	private String id;
	private HashMap attributes;
	private double value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public HashMap getAttributes() {
		return attributes;
	}
	public void setAttributes(HashMap attributes) {
		this.attributes = attributes;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Alternative [name=" + name + ", description=" + description
				+ ", id=" + id + ", attributes=" + attributes + ", value="
				+ value + "]";
	}

	
	
	
	

}
