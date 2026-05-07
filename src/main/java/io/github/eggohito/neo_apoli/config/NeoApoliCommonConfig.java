package io.github.eggohito.neo_apoli.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import io.github.eggohito.neo_apoli.api.config.ConfigCategoryRegistrant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.fabricmc.loader.api.FabricLoader;
import org.quiltmc.parsers.json.JsonFormat;

@SuppressWarnings("UnstableApiUsage")
public final class NeoApoliCommonConfig extends AbstractJsonCodecConfig<NeoApoliCommonConfig> {

	public static final NeoApoliCommonConfig INSTANCE = new NeoApoliCommonConfig();
	public static final int VERSION = 1;

	public final ConfigEntry<Integer> version = register("version", VERSION, Codec.INT);
	public final ConfigEntry<Command> command = register("command", Command.DEFAULT, Command.CODEC);
	public final ConfigEntry<PlaceholderIdentifier> placeholderIdentifier = register("placeholder_identifier", PlaceholderIdentifier.DEFAULT, PlaceholderIdentifier.CODEC);

	private NeoApoliCommonConfig() {
		super(FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/common.json5"), JsonFormat.JSON5);
	}

	@Override
	public void saveToFile() {

		super.saveToFile();

		ConfigCategoryRegistrant.HUD_ELEMENT_TYPE.invoker().save();
		ConfigCategoryRegistrant.POWER_TYPE.invoker().save();

	}

	@Override
	public boolean loadFromFile() {
		return super.loadFromFile()
			&& ConfigCategoryRegistrant.HUD_ELEMENT_TYPE.invoker().load()
			&& ConfigCategoryRegistrant.POWER_TYPE.invoker().load();
	}

	@AllArgsConstructor
	@Accessors(fluent = true)
	@Data
	public static final class Command {

		public static final Command DEFAULT = new Command(false, 2);

		public static final Codec<Command> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("show_output").forGetter(Command::showOutput),
			Codec.intRange(0, 4).fieldOf("permission_level").forGetter(Command::permissionLevel)
		).apply(instance, Command::new));

		private boolean showOutput;
		private int permissionLevel;

	}

	@AllArgsConstructor
	@Accessors(fluent = true)
	@Data
	public static final class PlaceholderIdentifier {

		public static final PlaceholderIdentifier DEFAULT = new PlaceholderIdentifier(true);

		public static final Codec<PlaceholderIdentifier> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.BOOL.fieldOf("enabled").forGetter(PlaceholderIdentifier::enabled))
			.apply(instance, PlaceholderIdentifier::new));

		private boolean enabled;

	}

}
