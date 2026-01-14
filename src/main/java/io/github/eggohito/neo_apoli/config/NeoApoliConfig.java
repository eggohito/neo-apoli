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

	@SerialEntry
	public final Identifier identifier = new Identifier();

	@SerialEntry
	public final ModifyPlayerSpawn modifyPlayerSpawn = new ModifyPlayerSpawn();

	public static class Command {

		@SerialEntry
		public boolean showOutput = false;

		@SerialEntry
		public int permissionLevel = 2;

	}

	public static class Identifier {

		@SerialEntry
		public char placeholder = '*';

		@SerialEntry
		public boolean enabled = true;

	}

	public static class ModifyPlayerSpawn {

		@SerialEntry
		public int horizontalStep = 64;

		@SerialEntry
		public int verticalStep = 64;

		@SerialEntry
		public int radius = 6400;

	}

}
