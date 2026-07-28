package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.command.ActionCommand;
import io.github.eggohito.neo_apoli.command.ConditionCommand;
import io.github.eggohito.neo_apoli.command.PowerCommand;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import io.github.eggohito.neo_apoli.config.NeoApoliCommonConfig;
import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import io.github.eggohito.neo_apoli.impl.misc.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.integration.CommonConfigIntegrations;
import io.github.eggohito.neo_apoli.integration.PowerIntegrations;
import io.github.eggohito.neo_apoli.key.manager.KeyStateManager;
import io.github.eggohito.neo_apoli.network.packet.NeoApoliPackets;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundClearCachedLogsPacket;
import io.github.eggohito.neo_apoli.power.global.manager.GlobalPowerSetManager;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import io.github.eggohito.neo_apoli.registry.*;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParamSets;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.*;
import io.github.eggohito.neo_apoli.registry.recipe.NeoApoliRecipeBookCategories;
import io.github.eggohito.neo_apoli.registry.recipe.NeoApoliRecipeSerializers;
import io.github.eggohito.neo_apoli.tag.manager.NestedTagManager;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class NeoApoli implements ModInitializer {

	public static final String MOD_NAMESPACE = "neo-apoli";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	static final IntSet CACHED_LOGS = new IntOpenHashSet();
	static MinecraftServer server;

	@Override
	public void onInitialize() {

		NeoApoliPackets.registerAll();

		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> {

			var rootNode = dispatcher.getRoot();
			var baseNode = Commands.literal("neo-apoli").build();

			PowerCommand.register(rootNode);
			PowerCommand.register(baseNode);

			ActionCommand.register(buildContext, rootNode);
			ActionCommand.register(buildContext, baseNode);

			ConditionCommand.register(buildContext, rootNode);
			ConditionCommand.register(buildContext, baseNode);

			rootNode.addChild(baseNode);

		});

		NeoApoliBlockProviderTypes.registerAll();
		NeoApoliBooleanProviderTypes.registerAll();
		NeoApoliBoxProviderTypes.registerAll();
		NeoApoliCommandSourceProviderTypes.registerAll();
		NeoApoliDirectionProviderTypes.registerAll();
		NeoApoliEffectProviderTypes.registerAll();
		NeoApoliEntityProviderTypes.registerAll();
		NeoApoliItemProviderTypes.registerAll();
		NeoApoliNbtProviderTypes.registerAll();
		NeoApoliNumberProviderTypes.registerAll();
		NeoApoliSlotProviderTypes.registerAll();
		NeoApoliStringProviderTypes.registerAll();
		NeoApoliVec3ProviderTypes.registerAll();

		NeoApoliArguments.registerAll();
		NeoApoliComponentContents.registerAll();
		NeoApoliContainerMenuTypes.registerAll();
		NeoApoliParticleTypes.registerAll();
		NeoApoliDataProviders.registerAll();

		NeoApoliRecipeSerializers.registerAll();
		NeoApoliRecipeBookCategories.registerAll();

		NeoApoliEntityAttachments.registerAll();

		NeoApoliComparisonTypes.registerAll();
		NeoApoliColorTypes.registerAll();
		NeoApoliModifierTypes.registerAll();
		NeoApoliHudElementTypes.registerAll();

		NeoApoliActionTypes.registerAll();
		NeoApoliConditionTypes.registerAll();
		NeoApoliPowerTypes.registerAll();

		ActionManager.INSTANCE.init();
		ConditionManager.INSTANCE.init();
		GlobalPowerSetManager.INSTANCE.init();
		KeyStateManager.INSTANCE.init();
		NestedTagManager.INSTANCE.init();
		PowerManager.INSTANCE.init();

		PowerIntegrations.init();
		CommonConfigIntegrations.init();

		NeoApoliContextParams.registerAll();
		NeoApoliContextParamSets.registerAll();

		NeoApoliNestedTags.registerAll();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> NeoApoli.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> NeoApoli.server = null);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ((CommandStorageHolder) server).neo_apoli$sendAll(handler.getPlayer()));
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ((PowerRecipeDisplayHolder) player.server.getRecipeManager()).neo_apoli$sendAll(player));

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> {
			CACHED_LOGS.clear();
			server.getPlayerList().getPlayers().forEach(player -> ServerPlayNetworking.send(player, ClientboundClearCachedLogsPacket.INSTANCE));
		});

	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_NAMESPACE, path);
	}

	public static NeoApoliCommonConfig getConfig() {
		return NeoApoliCommonConfig.INSTANCE;
	}

	public static boolean onServerThread() {
		return server != null
			&& server.isSameThread();
	}

	public static void logOnce(Level level, String message) {

		if (CACHED_LOGS.add(message.hashCode())) {
			LOGGER.atLevel(level).log(message);
		}

	}

}
