package me.catsflex.ce.config;

public enum KeyType {
	CATEGORY("category"),
	GROUP("group"),
	OPTION("option");
	
	private final String type;
	
	KeyType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return type;
	}
}
