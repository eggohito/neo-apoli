package io.github.eggohito.neo_apoli.resource.json;

import com.google.gson.JsonObject;
import org.quiltmc.parsers.json.JsonFormat;

public record JsonObjectWithSource(String source, JsonObject element, JsonFormat format) implements JsonElementWithSource {

}
