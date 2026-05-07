package io.github.eggohito.neo_apoli.config;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public final class NeoApoliConfig extends AbstractJsonCodecConfig<NeoApoliConfig> {

	public static final NeoApoliConfig INSTANCE = new NeoApoliConfig();
	public static final int VERSION = 1;

	public final ConfigEntry<Integer> version = register("version", VERSION, Codec.INT);
	public final ConfigEntry<Command> command = register("command", Command.DEFAULT, Command.CODEC);
	public final ConfigEntry<PlaceholderIdentifier> placeholderIdentifier = register("placeholder_identifier", PlaceholderIdentifier.DEFAULT, PlaceholderIdentifier.CODEC);
	public final ConfigEntry<ModifyPlayerSpawn> modifyPlayerSpawn = register("modify_player_spawn", ModifyPlayerSpawn.DEFAULT, ModifyPlayerSpawn.CODEC);
	public final ConfigEntry<Multiples> multiples = register("multiples", Multiples.DEFAULT, Multiples.CODEC);

	private NeoApoliConfig() {
		super(FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/common.json5"), JsonFormat.JSON5);
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

	@AllArgsConstructor
	@Accessors(fluent = true)
	@Data
	public static final class ModifyPlayerSpawn {

		public static final ModifyPlayerSpawn DEFAULT = new ModifyPlayerSpawn(64, 64, 6400);

		public static final Codec<ModifyPlayerSpawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CodecUtil.nonNegativeInt().fieldOf("horizontal_step").forGetter(ModifyPlayerSpawn::horizontalStep),
			CodecUtil.nonNegativeInt().fieldOf("vertical_step").forGetter(ModifyPlayerSpawn::verticalStep),
			CodecUtil.nonNegativeInt().fieldOf("radius").forGetter(ModifyPlayerSpawn::radius)
		).apply(instance, ModifyPlayerSpawn::new));

		private int horizontalStep;
		private int verticalStep;
		private int radius;

	}

	@Accessors(fluent = true)
	@Data
	public static final class Multiples {

		public static final ImmutableList<Pattern> DEFAULT_IGNORED_FIELDS = Util.make(ImmutableList.builder(), Multiples::makeDefault).build();
		public static final Multiples DEFAULT = new Multiples(DEFAULT_IGNORED_FIELDS);

		public static final Codec<Multiples> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(ExtraCodecs.PATTERN.listOf().fieldOf("ignored_fields").forGetter(Multiples::ignoredFields))
			.apply(instance, Multiples::new));

		private List<Pattern> ignoredFields;

		public Multiples(List<Pattern> ignoredFields) {
			this.ignoredFields = ignoredFields;
		}

		private static void makeDefault(ImmutableList.Builder<Pattern> builder) {

			PowerHolder.MAP_CODEC.keys(JavaOps.INSTANCE)
				.map(Object::toString)
				.filter(Predicate.not("value"::equals))
				.distinct()
				.map(Pattern::compile)
				.forEach(builder::add);

			builder.add(Pattern.compile("^\\$"));
			builder.add(Pattern.compile(ResourceConditions.CONDITIONS_KEY));

		}

	}

}
