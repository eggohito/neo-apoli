package io.github.eggohito.neo_apoli.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractJsonCodecConfig<T extends AbstractJsonCodecConfig<T>> extends JsonFileCodecConfig<T> {

	private final Path configPath;
	private final JsonFormat format;

	private final Gson gson;

	protected AbstractJsonCodecConfig(Path configPath, JsonFormat format, Gson gson) {
		super(configPath);
		this.configPath = configPath;
		this.format = format;
		this.gson = gson;
	}

	protected AbstractJsonCodecConfig(Path configPath, JsonFormat format) {
		this(configPath, format, MiscUtil.GSON);
	}

	@Override
	public void saveToFile() {

		try {

			Files.createDirectories(configPath.getParent());
			NeoApoli.LOGGER.info("Saving config file '{}'...", configPath);

			try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

				JsonElement jsonElement = this.encodeStart(JsonOps.INSTANCE).getPartialOrThrow();
				GsonWriter gsonWriter = new GsonWriter(JsonWriter.create(writer, format));

				gson.toJson(jsonElement, gsonWriter);
				NeoApoli.LOGGER.info("Saved config file '{}'!", configPath);

			}

			catch (Exception e) {
				NeoApoli.LOGGER.error("Couldn't save config file '{}'", configPath, e);
			}

		}

		catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}

	@Override
	public boolean loadFromFile() {

		if (Files.notExists(configPath)) {

			NeoApoli.LOGGER.info("Config file '{}' doesn't exist! Initializing...", configPath);
			this.saveToFile();

			return true;

		}

		else {

			NeoApoli.LOGGER.info("Loading config file '{}'...", configPath);
			boolean result = false;

			try (BufferedReader reader = Files.newBufferedReader(configPath)) {

				GsonReader gsonReader = new GsonReader(JsonReader.create(reader, format));
				JsonElement jsonElement = gson.fromJson(gsonReader, JsonElement.class);

				result = this.decode(jsonElement, JsonOps.INSTANCE);
				NeoApoli.LOGGER.info("Loaded config file '{}'!", configPath);

			}

			catch (Exception e) {
				NeoApoli.LOGGER.error("Couldn't load config file '{}'", configPath, e);
			}

			return result;

		}

	}

}
