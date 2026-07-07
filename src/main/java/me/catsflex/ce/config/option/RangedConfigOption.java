package me.catsflex.ce.config.option;

public abstract class RangedConfigOption<T extends Comparable<T>> extends ConfigOption<T> {
	private final T min, max;
	
	public RangedConfigOption(String key, T defaultValue, T min, T max) {
		super(key, defaultValue);
		
		this.min = min;
		this.max = max;
	}
	
	public T getMin() {
		return min;
	}
	
	public T getMax() {
		return max;
	}
	
	@Override
	public void set(T value) {
		if (value.compareTo(min) < 0) {
			super.set(min);
		} else if (value.compareTo(max) > 0) {
			super.set(max);
		} else {
			super.set(value);
		}
	}
}
