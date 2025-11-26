package io.github.eggohito.neo_apoli.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.HolderLookup;

public record NeoApoliConfig(Command command) {

	public static final Codec<NeoApoliConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Command.CODEC.fieldOf("command").forGetter(NeoApoliConfig::command)
	).apply(instance, NeoApoliConfig::new));

	public NeoApoliConfig() {
		this(new Command());
	}

	public static void init() {

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {

			HolderLookup.Provider wrapperLookup = server.registryAccess();
			if (NeoApoli.loadConfig(wrapperLookup)) {
				return;
			}

			NeoApoli.LOGGER.info("Loading and saving config with default values...");
			NeoApoli.saveConfig(wrapperLookup, new NeoApoliConfig());

		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {

			if (success) {
				NeoApoli.loadConfig(server.registryAccess());
			}

		});

	}

	public record Command(int permissionLevel, boolean showOutput) {

		public static final Codec<Command> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.intRange(0, 4).fieldOf("permission_level").forGetter(Command::permissionLevel),
			Codec.BOOL.fieldOf("show_output").forGetter(Command::showOutput)
		).apply(instance, Command::new));

		public Command() {
			this(2, false);
		}

	}

}
