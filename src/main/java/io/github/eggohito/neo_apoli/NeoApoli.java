package io.github.eggohito.neo_apoli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.command.PowerCommand;
import io.github.eggohito.neo_apoli.command.argument.NeoApoliArgumentTypes;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.config.NeoApoliConfig;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.networking.packet.NeoApoliPackets;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
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
import java.util.Objects;

public class NeoApoli implements ModInitializer {

	public static final String MOD_NAMESPACE = "neo-apoli";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/common.json5");

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	private static MinecraftServer server;
	private static NeoApoliConfig config;

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> PowerCommand.register(commandDispatcher.getRoot()));
		NeoApoliArgumentTypes.registerAll();

		NumberProviderTypes.registerAll();
		StringProviderTypes.registerAll();

		ConditionTypes.registerAll();
		ConditionCategories.registerAll();
		ConditionManager.init();

		ActionTypes.registerAll();
		ActionCategories.registerAll();
		ActionManager.init();

		ComparisonTypes.registerAll();

		PowerTypes.registerAll();
		PowerManager.init();

		NeoApoliPackets.registerAll();

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(Power.Impl::onAdded));
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(Power.Impl::onRemoved));

		ServerLifecycleEvents.SERVER_STARTING.register(server -> NeoApoli.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> NeoApoli.server = null);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ((DataCommandStorageHolder) server).neo_apoli$sendAll(handler.getPlayer()));
		NeoApoliConfig.init();

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_NAMESPACE, path);
	}

	public static NeoApoliConfig getConfig() {
		return Objects.requireNonNull(config, MOD_NAMESPACE + "'s config wasn't initialized properly!");
	}

	public static boolean serverSide() {
		return server != null
			&& server.isOnThread();
	}

	public static void saveConfig(RegistryWrapper.WrapperLookup wrapperLookup, NeoApoliConfig config) {

		try {

			LOGGER.info("Saving {}'s config...", MOD_NAMESPACE);
			Files.createDirectories(CONFIG_PATH.getParent());

			File configFile = CONFIG_PATH.toFile();
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {

				GsonWriter gsonWriter = new GsonWriter(JsonWriter.json5(writer));
				JsonElement jsonElement = NeoApoliConfig.CODEC.encodeStart(wrapperLookup.getOps(JsonOps.INSTANCE), config).getOrThrow(JsonParseException::new);

				GSON.toJson(jsonElement, gsonWriter);

			}

			LOGGER.info("Saved {}'s config!", MOD_NAMESPACE);

		}

		catch (Exception e) {
			LOGGER.warn("Error trying to save {} config file: ", MOD_NAMESPACE, e);
		}

	}

	public static boolean loadConfig(RegistryWrapper.WrapperLookup wrapperLookup) {

		try {

			LOGGER.info("Loading {}'s config...", MOD_NAMESPACE);

			File configFile = CONFIG_PATH.toFile();
			BufferedReader reader = new BufferedReader(new FileReader(configFile));

			GsonReader gsonReader = new GsonReader(JsonReader.json5(reader));
			config = NeoApoliConfig.CODEC.parse(wrapperLookup.getOps(JsonOps.INSTANCE), GSON.fromJson(gsonReader, JsonElement.class)).getOrThrow(JsonParseException::new);

			LOGGER.info("Loaded {}'s config!", MOD_NAMESPACE);
			return true;

		}

		catch (Exception e) {
			LOGGER.error("Error trying to load {} config file: ", MOD_NAMESPACE, e);
			return false;
		}

	}

	public static CommandOutput validateCommandOutput(CommandOutput commandOutput) {

		if (getConfig().command().showOutput()) {
			return commandOutput;
		}

		else {
			return CommandOutput.DUMMY;
		}

	}

}
