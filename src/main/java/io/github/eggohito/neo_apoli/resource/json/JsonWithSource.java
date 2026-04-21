package io.github.eggohito.neo_apoli.resource.json;

import com.google.gson.JsonElement;

public record JsonWithSource(JsonElement json, String source) {

}
