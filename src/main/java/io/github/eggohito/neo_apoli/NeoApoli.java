package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.command.ActionCommand;
import io.github.eggohito.neo_apoli.command.ConditionCommand;
import io.github.eggohito.neo_apoli.command.PowerCommand;
import io.github.eggohito.neo_apoli.command.argument.NeoApoliArgumentTypes;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.config.NeoApoliConfig;
import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import io.github.eggohito.neo_apoli.hud.type.HudElementTypes;
import io.github.eggohito.neo_apoli.integration.PowerIntegrations;
import io.github.eggohito.neo_apoli.key.KeyStateManager;
import io.github.eggohito.neo_apoli.network.NeoApoliC2SNetworkHandler;
import io.github.eggohito.neo_apoli.network.packet.NeoApoliPackets;
import io.github.eggohito.neo_apoli.network.packet.s2c.ClearLogsS2CPacket;
import io.github.eggohito.neo_apoli.particle.type.NeoApoliParticleTypes;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderTypes;
import io.github.eggohito.neo_apoli.recipe.NeoApoliRecipeSerializers;
import io.github.eggohito.neo_apoli.recipe.book.NeoApoliRecipeBookCategories;
import io.github.eggohito.neo_apoli.util.color.type.ColorTypes;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.container_type.NeoApoliContainerTypes;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.Set;

public class NeoApoli implements ModInitializer {

	public static final String MOD_NAMESPACE = "neo-apoli";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	@ApiStatus.Internal
	public static final Set<String> LOGS = new ObjectOpenHashSet<>();
	private static MinecraftServer server;

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

			var rootNode = dispatcher.getRoot();
			var baseNode = Commands.literal("neo-apoli").build();

			ActionCommand.register(registryAccess, rootNode);
			ActionCommand.register(registryAccess, baseNode);

			ConditionCommand.register(registryAccess, rootNode);
			ConditionCommand.register(registryAccess, baseNode);

			PowerCommand.register(rootNode);
			PowerCommand.register(baseNode);

			rootNode.addChild(baseNode);

		});

		NeoApoliArgumentTypes.registerAll();
		ValueProviderTypes.registerAll();

		ConditionManager.init();
		ConditionTypes.registerAll();

		ActionManager.init();
		ActionTypes.registerAll();

		NeoApoliContainerTypes.registerAll();
		NeoApoliParticleTypes.registerAll();

		NeoApoliRecipeSerializers.registerAll();
		NeoApoliRecipeBookCategories.registerAll();

		ComparisonTypes.registerAll();
		ColorTypes.registerAll();
		ModifierTypes.registerAll();
		HudElementTypes.registerAll();

		PowerTypes.registerAll();
		PowerManager.init();

		NeoApoliPackets.registerAll();
		NeoApoliC2SNetworkHandler.init();

		PowerIntegrations.registerAll();
		NeoApoliConfig.HANDLER.load();

		NeoApoliContextKeys.init();
		NeoApoliContextKeySets.init();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> NeoApoli.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> NeoApoli.server = null);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ((CommandStorageHolder) server).neo_apoli$sendAll(handler.getPlayer()));

		ServerTickEvents.END_SERVER_TICK.register(KeyStateManager::startTrackingServer);
		ServerPlayConnectionEvents.DISCONNECT.register(KeyStateManager::stopTrackingServer);

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> {
			LOGS.clear();
			server.getPlayerList().getPlayers().forEach(serverPlayer -> ServerPlayNetworking.send(serverPlayer, ClearLogsS2CPacket.INSTANCE));
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> NeoApoliConfig.HANDLER.load());

	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_NAMESPACE, path);
	}

	public static NeoApoliConfig getConfig() {
		return NeoApoliConfig.HANDLER.instance();
	}

	public static boolean serverSide() {
		return server != null
			&& server.isSameThread();
	}

	public static CommandSource validateCommandOutput(CommandSource commandOutput) {

		if (getConfig().command.showOutput) {
			return commandOutput;
		}

		else {
			return CommandSource.NULL;
		}

	}

	public static void logOnce(Level level, String message) {

		if (LOGS.add(message)) {
			LOGGER.atLevel(level).log(message);
		}

	}

}
