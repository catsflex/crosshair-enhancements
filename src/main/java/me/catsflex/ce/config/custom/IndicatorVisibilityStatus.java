package me.catsflex.ce.config.custom;

public enum IndicatorVisibilityStatus {
	ALWAYS_ON("alwaysOn"),
	TARGETED("targeted"),
	NEVER("never");
	
	private final String _status;
	
	IndicatorVisibilityStatus(String status) {
		_status = status;
	}
	
	public String getStatus() {
		return _status;
	}
}
