package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Strings;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Iterator;
import java.util.Map;

public class JsonTextFormatter {

	private static final ChatFormatting NAME_COLOR = ChatFormatting.AQUA;
	private static final ChatFormatting STRING_COLOR = ChatFormatting.GREEN;
	private static final ChatFormatting NUMBER_COLOR = ChatFormatting.GOLD;
	private static final ChatFormatting BOOLEAN_COLOR = ChatFormatting.BLUE;
	private static final ChatFormatting TYPE_SUFFIX_COLOR = ChatFormatting.RED;

	private final String indent;
	private final int depth;

	protected JsonTextFormatter(String indent, int depth) {
		this.indent = indent;
		this.depth = Math.max(1, depth);
	}

	protected JsonTextFormatter(char indent, int size) {
		this(Strings.repeat(String.valueOf(indent), size), 1);
	}

	protected JsonTextFormatter(int size) {
		this(' ', size);
	}

	public Component apply(JsonElement jsonElement) {
		return this.applyInternal(jsonElement)
			.mapError(error -> "Error trying to format JSON element " + jsonElement + " into text: " + error)
			.resultOrPartial(NeoApoli.LOGGER::warn)
			.orElse(Component.empty());
	}

	protected final DataResult<Component> applyInternal(JsonElement jsonElement) {

		try {

			Component text = switch (jsonElement) {
				case JsonArray jsonArray ->
					this.visitArray(jsonArray);
				case JsonObject jsonObject ->
					this.visitObject(jsonObject);
				case JsonPrimitive jsonPrimitive ->
					this.visitPrimitive(jsonPrimitive);
				case JsonNull ignored ->
					throw new JsonSyntaxException("JSON element cannot be null!");
				case null ->
					throw new JsonSyntaxException("JSON element cannot be null!");
				default ->
					throw new JsonParseException("JSON element " + jsonElement + " is not supported!");
			};

			return DataResult.success(text);

		}

		catch (Exception e) {
			return DataResult.error(e::getMessage);
		}

	}

	public Component visitArray(JsonArray jsonArray) {

		if (jsonArray.isEmpty()) {
			return Component.literal("[]");
		}

		MutableComponent result = Component.literal("[");
		if (!indent.isEmpty()) {
			result.append("\n");
		}

		Iterator<JsonElement> iterator = jsonArray.iterator();
		while (iterator.hasNext()) {

			JsonElement jsonElement = iterator.next();
			DataResult<Component> jsonText = new JsonTextFormatter(indent, depth + 1).applyInternal(jsonElement).ifSuccess(text -> result
				.append(Strings.repeat(indent, depth))
				.append(text));

			if (iterator.hasNext() && jsonText.isSuccess()) {
				result.append(!indent.isEmpty() ? ",\n" : ", ");
			}

		}

		if (!indent.isEmpty()) {
			result.append("\n");
		}

		if (!this.isRoot()) {
			result.append(Strings.repeat(indent, depth - 1));
		}

		return result.append("]");

	}

	public Component visitObject(JsonObject jsonObject) {

		if (jsonObject.isEmpty()) {
			return Component.literal("{}");
		}

		MutableComponent result = Component.literal("{");
		if (!indent.isEmpty()) {
			result.append("\n");
		}

		Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
		while (iterator.hasNext()) {

			Map.Entry<String, JsonElement> entry = iterator.next();

			Component name = Component.literal(entry.getKey()).withStyle(NAME_COLOR);
			DataResult<Component> value = new JsonTextFormatter(indent, depth + 1).applyInternal(entry.getValue()).ifSuccess(text -> result
				.append(Strings.repeat(indent, depth))
				.append(name).append(": ").append(text));

			if (iterator.hasNext() && value.isSuccess()) {
				result.append(!indent.isEmpty() ? ",\n" : ", ");
			}

		}

		if (!indent.isEmpty()) {
			result.append("\n");
		}

		if (!this.isRoot()) {
			result.append(Strings.repeat(indent, depth - 1));
		}

		return result.append("}");

	}

	public Component visitPrimitive(JsonPrimitive jsonPrimitive) {

		if (jsonPrimitive.isBoolean()) {
			return Component.literal(String.valueOf(jsonPrimitive.getAsBoolean())).withStyle(BOOLEAN_COLOR);
		}

		else if (jsonPrimitive.isString()) {
			return Component.literal("\"" + jsonPrimitive.getAsString() + "\"").withStyle(STRING_COLOR);
		}

		else if (jsonPrimitive.isNumber()) {

			Component numberText = switch (jsonPrimitive.getAsNumber()) {
				case Long l ->
					numberAsText(l, "L");
				case Float f ->
					numberAsText(f, "F");
				case Double d ->
					numberAsText(d, "D");
				case Byte b ->
					numberAsText(b, "B");
				case Short s ->
					numberAsText(s, "S");
				case Number n ->
					numberAsText(n, "");
			};

			return Component.empty().withStyle(NUMBER_COLOR).append(numberText);

		}

		else {
			throw new JsonParseException("JSON primitive " + jsonPrimitive + " is not supported!");
		}

	}

	public boolean isRoot() {
		return depth <= 1;
	}

	public static Component format(JsonElement jsonElement, char ch, int indent) {
		return new JsonTextFormatter(ch, indent).apply(jsonElement);
	}

	public static Component format(JsonElement jsonElement, int indent) {
		return new JsonTextFormatter(indent).apply(jsonElement);
	}

	private static Component numberAsText(Number number, String suffix) {
		return Component.literal(number.toString()).append(Component.literal(suffix).withStyle(TYPE_SUFFIX_COLOR));
	}

}
