package io.github.eggohito.neo_apoli.resource.json;

import com.google.gson.JsonElement;
import org.quiltmc.parsers.json.JsonFormat;

public record JsonWithSource(String source, JsonElement json, JsonFormat format) {

	public boolean matches(String source, JsonFormat format) {
		return source().equals(source)
			&& format().equals(format);
	}

}
