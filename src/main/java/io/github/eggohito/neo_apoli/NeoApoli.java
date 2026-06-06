package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.action.manager.ServerActionManager;
import io.github.eggohito.neo_apoli.command.ActionCommand;
import io.github.eggohito.neo_apoli.command.ConditionCommand;
import io.github.eggohito.neo_apoli.command.PowerCommand;
import io.github.eggohito.neo_apoli.condition.manager.ServerConditionManager;
import io.github.eggohito.neo_apoli.config.NeoApoliCommonConfig;
import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import io.github.eggohito.neo_apoli.impl.log.NeoApoliLoggerImpl;
import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import io.github.eggohito.neo_apoli.impl.misc.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.impl.tag.NestedTagCacheImpl;
import io.github.eggohito.neo_apoli.integration.PowerIntegrations;
import io.github.eggohito.neo_apoli.network.NeoApoliServerboundPacketListener;
import io.github.eggohito.neo_apoli.network.packet.NeoApoliPackets;
import io.github.eggohito.neo_apoli.power.global.GlobalPowerSetManager;
import io.github.eggohito.neo_apoli.power.manager.ServerPowerManager;
import io.github.eggohito.neo_apoli.registry.*;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParamSets;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.*;
import io.github.eggohito.neo_apoli.registry.recipe.NeoApoliRecipeBookCategories;
import io.github.eggohito.neo_apoli.registry.recipe.NeoApoliRecipeSerializers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class NeoApoli implements ModInitializer {

	public static final String MOD_NAMESPACE = "neo-apoli";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	private static MinecraftServer server;

	@Override
	public void onInitialize() {

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

		ServerActionManager.init();
		ServerConditionManager.init();
		ServerPowerManager.init();
		GlobalPowerSetManager.init();

		NeoApoliPackets.registerAll();
		NeoApoliServerboundPacketListener.init();

		PowerIntegrations.registerAll();

		getConfig().loadFromFile();
		NeoApoliConfigs.registerAll();

		NeoApoliContextParams.registerAll();
		NeoApoliContextParamSets.registerAll();

		KeyStateManagerImpl.init();
		NeoApoliLoggerImpl.init();

		NeoApoliNestedTagCaches.registerAll();
		NestedTagCacheImpl.init();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> NeoApoli.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> NeoApoli.server = null);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ((CommandStorageHolder) server).neo_apoli$sendAll(handler.getPlayer()));
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ((PowerRecipeDisplayHolder) player.server.getRecipeManager()).neo_apoli$sendAll(player));

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {

			if (success) {
				getConfig().loadFromFile();
			}

		});

	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_NAMESPACE, path);
	}

	public static NeoApoliCommonConfig getConfig() {
		return NeoApoliCommonConfig.INSTANCE;
	}

	public static boolean serverSide() {
		return server != null
			&& server.isSameThread();
	}

	public static void logOnce(Level level, String message) {

		if (NeoApoliLoggerImpl.CACHE.add(message)) {
			LOGGER.atLevel(level).log(message);
		}

	}

}
