package me.catsflex.ce.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;

public class ColorAdapter extends TypeAdapter<Color> {
	
	@Override
	public void write(JsonWriter out, Color value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		
		out.value(colorToHexString(value));
	}
	
	@Override
	public Color read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}
		
		var hex = in.nextString().substring(1);
		
		try {
			int argb = Integer.parseUnsignedInt(hex, 16);
			return new Color(argb, true);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	// ARGB format.
	private String colorToHexString(Color color) {
		return String.format("#%08X", color.getRGB());
	}
}
