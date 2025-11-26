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
	private final boolean root;

	private final int offset;

	protected JsonTextFormatter(String indent, int offset, boolean root) {
		this.indent = indent;
		this.offset = Math.max(0, offset);
		this.root = root;
	}

	protected JsonTextFormatter(char indent, int size) {
		this(Strings.repeat(String.valueOf(indent), size), 1, true);
	}

	protected JsonTextFormatter(int size) {
		this(' ', size);
	}

	public static Component format(JsonElement jsonElement, char ch, int indent) {
		return new JsonTextFormatter(ch, indent).apply(jsonElement);
	}

	public static Component format(JsonElement jsonElement, int indent) {
		return new JsonTextFormatter(indent).apply(jsonElement);
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
			DataResult<Component> jsonText = new JsonTextFormatter(indent, offset + 1, false).applyInternal(jsonElement).ifSuccess(text -> result
				.append(Strings.repeat(indent, offset))
				.append(text));

			if (iterator.hasNext() && jsonText.isSuccess()) {
				result.append(!indent.isEmpty() ? ",\n" : ", ");
			}

		}

		if (!indent.isEmpty()) {
			result.append("\n");
		}

		if (!root) {
			result.append(Strings.repeat(indent, offset - 1));
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
			DataResult<Component> value = new JsonTextFormatter(indent, offset + 1, false).applyInternal(entry.getValue()).ifSuccess(text -> result
				.append(Strings.repeat(indent, offset))
				.append(name).append(": ").append(text));

			if (iterator.hasNext() && value.isSuccess()) {
				result.append(!indent.isEmpty() ? ",\n" : ", ");
			}

		}

		if (!indent.isEmpty()) {
			result.append("\n");
		}

		if (!root) {
			result.append(Strings.repeat(indent, offset - 1));
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

			Number number = jsonPrimitive.getAsNumber();
			MutableComponent numberText = Component.empty().withStyle(NUMBER_COLOR);

			return numberText.append(switch (number) {
				case Long ignored ->
					numberAsText(number, "L");
				case Float ignored ->
					numberAsText(number, "F");
				case Double ignored ->
					numberAsText(number, "D");
				case Byte ignored ->
					numberAsText(number, "B");
				case Short ignored ->
					numberAsText(number, "S");
				default ->
					numberAsText(number, "");
			});

		}

		else {
			throw new JsonParseException("JSON primitive " + jsonPrimitive + " is not supported!");
		}

	}

	private static Component numberAsText(Number number, String suffix) {
		return Component.literal(number.toString()).append(Component.literal(suffix).withStyle(TYPE_SUFFIX_COLOR));
	}

}
