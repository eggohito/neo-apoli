package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Strings;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Iterator;
import java.util.Map;

public class JsonTextFormatter {

	private static final Formatting NAME_COLOR = Formatting.AQUA;
	private static final Formatting STRING_COLOR = Formatting.GREEN;
	private static final Formatting NUMBER_COLOR = Formatting.GOLD;
	private static final Formatting BOOLEAN_COLOR = Formatting.BLUE;
	private static final Formatting TYPE_SUFFIX_COLOR = Formatting.RED;

	private final String indent;
	private final boolean root;

	private final int offset;

	protected JsonTextFormatter(String indent, int offset, boolean root) {
		this.indent = indent;
		this.offset = Math.max(0, offset);
		this.root = root;
	}

	public JsonTextFormatter(char indent, int size) {
		this(Strings.repeat(String.valueOf(indent), size), 1, true);
	}

	public JsonTextFormatter(int size) {
		this(' ', size);
	}

	public Text apply(JsonElement jsonElement) {
		return this.applyInternal(jsonElement)
			.mapError(error -> "Error trying to format JSON element " + jsonElement + " into text: " + error)
			.resultOrPartial(NeoApoli.LOGGER::warn)
			.orElse(Text.empty());
	}

	protected final DataResult<Text> applyInternal(JsonElement jsonElement) {

		try {

			Text text = switch (jsonElement) {
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

	public Text visitArray(JsonArray jsonArray) {

		if (jsonArray.isEmpty()) {
			return Text.literal("[]");
		}

		MutableText result = Text.literal("[");
		if (!indent.isEmpty()) {
			result.append("\n");
		}

		Iterator<JsonElement> iterator = jsonArray.iterator();
		while (iterator.hasNext()) {

			JsonElement jsonElement = iterator.next();
			DataResult<Text> jsonText = new JsonTextFormatter(indent, offset + 1, false).applyInternal(jsonElement).ifSuccess(text -> result
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

	public Text visitObject(JsonObject jsonObject) {

		if (jsonObject.isEmpty()) {
			return Text.literal("{}");
		}

		MutableText result = Text.literal("{");
		if (!indent.isEmpty()) {
			result.append("\n");
		}

		Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
		while (iterator.hasNext()) {

			Map.Entry<String, JsonElement> entry = iterator.next();

			Text name = Text.literal(entry.getKey()).formatted(NAME_COLOR);
			DataResult<Text> value = new JsonTextFormatter(indent, offset + 1, false).applyInternal(entry.getValue()).ifSuccess(text -> result
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

	public Text visitPrimitive(JsonPrimitive jsonPrimitive) {

		if (jsonPrimitive.isBoolean()) {
			return Text.literal(String.valueOf(jsonPrimitive.getAsBoolean())).formatted(BOOLEAN_COLOR);
		}

		else if (jsonPrimitive.isString()) {
			return Text.literal("\"" + jsonPrimitive.getAsString() + "\"").formatted(STRING_COLOR);
		}

		else if (jsonPrimitive.isNumber()) {

			Number number = jsonPrimitive.getAsNumber();
			MutableText numberText = Text.empty().formatted(NUMBER_COLOR);

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

	private static Text numberAsText(Number number, String suffix) {
		return Text.literal(number.toString()).append(Text.literal(suffix).formatted(TYPE_SUFFIX_COLOR));
	}

}
