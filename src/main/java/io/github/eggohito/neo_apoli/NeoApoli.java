package io.github.eggohito.neo_apoli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.command.PowerCommand;
import io.github.eggohito.neo_apoli.command.argument.NeoApoliArgumentTypes;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.config.NeoApoliConfig;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.networking.packet.NeoApoliPackets;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class NeoApoli implements ModInitializer {

	public static final String MOD_NAMESPACE = "neo-apoli";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	private static NeoApoliConfig config;

	@Override
	public void onInitialize() {

		loadConfig();

		CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> PowerCommand.register(commandDispatcher.getRoot()));
		NeoApoliArgumentTypes.registerAll();

		ValueProviderTypes.registerAll();

		ConditionTypes.registerAll();
		ActionTypes.registerAll();

		PowerTypes.registerAll();
		PowerManager.init();

		NeoApoliPackets.registerAll();

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(power -> power.onAdded(entity)));
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(power -> power.onRemoved(entity)));

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_NAMESPACE, path);
	}

	public static NeoApoliConfig getConfig() {

		if (config == null) {
			config = new NeoApoliConfig();
		}

		return config;

	}

	private static void saveConfig() {

		LOGGER.info("Saving neo-apoli's config...");

		try {

			Path configPath = FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/common.json5");
			Files.createDirectories(configPath);

			File configFile = Files.createFile(configPath).toFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

			GsonWriter gsonWriter = new GsonWriter(JsonWriter.json5(writer));
			JsonElement jsonElement = NeoApoliConfig.CODEC.encodeStart(JsonOps.INSTANCE, getConfig()).getOrThrow(JsonParseException::new);

			GSON.toJson(jsonElement, gsonWriter);

		}

		catch (Exception e) {
			LOGGER.warn("Error trying to save neo-apoli config file: ", e);
		}

	}

	private static void loadConfig() {

		LOGGER.info("Loading neo-apoli's config...");

		try {

			File configFile = FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/common.json5").toFile();
			BufferedReader reader = new BufferedReader(new FileReader(configFile));

			GsonReader gsonReader = new GsonReader(JsonReader.json5(reader));
			config = NeoApoliConfig.CODEC.parse(JsonOps.INSTANCE, GSON.fromJson(gsonReader, JsonElement.class)).getOrThrow(JsonParseException::new);

		}

		catch (Exception e) {

			LOGGER.error("Error trying to load neo-apoli config file (loading config with default values...): ", e);
			config = new NeoApoliConfig();

			saveConfig();

		}

		LOGGER.info("Loaded neo-apoli's config!");

	}

}
