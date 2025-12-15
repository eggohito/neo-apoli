package io.github.eggohito.neo_apoli.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.loader.api.FabricLoader;

public class NeoApoliConfig {

	public static final ConfigClassHandler<NeoApoliConfig> HANDLER = ConfigClassHandler.createBuilder(NeoApoliConfig.class)
		.id(NeoApoli.id("config"))
		.serializer(handler -> GsonConfigSerializerBuilder.create(handler)
			.setPath(FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/common.json5"))
			.setJson5(true).build()).build();

	@SerialEntry
	public final Command command = new Command();

	public static class Command {

		@SerialEntry
		public boolean showOutput = false;

		@SerialEntry
		public int permissionLevel = 2;

	}

}
