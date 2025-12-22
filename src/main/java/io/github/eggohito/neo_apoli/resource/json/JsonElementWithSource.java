package io.github.eggohito.neo_apoli.resource.json;

import com.google.gson.JsonElement;
import org.quiltmc.parsers.json.JsonFormat;

public interface JsonElementWithSource {

	String source();

	JsonElement element();

	JsonFormat format();

	static JsonElementWithSource of(String source, JsonElement element, JsonFormat format) {
		return new JsonElementWithSource() {

			@Override
			public String source() {
				return source;
			}

			@Override
			public JsonElement element() {
				return element;
			}

			@Override
			public JsonFormat format() {
				return format;
			}

		};
	}

}
