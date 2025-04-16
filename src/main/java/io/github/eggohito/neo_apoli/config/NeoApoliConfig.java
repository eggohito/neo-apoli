package io.github.eggohito.neo_apoli.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NeoApoliConfig(Command command) {

	public static final Codec<NeoApoliConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Command.CODEC.optionalFieldOf("command", new Command()).forGetter(NeoApoliConfig::command)
	).apply(instance, NeoApoliConfig::new));

	public NeoApoliConfig() {
		this(new Command());
	}

	public record Command(int permissionLevel, boolean showOutput) {

		public static final Codec<Command> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.intRange(0, 4).optionalFieldOf("permission_level", 2).forGetter(Command::permissionLevel),
			Codec.BOOL.optionalFieldOf("show_output", false).forGetter(Command::showOutput)
		).apply(instance, Command::new));

		public Command() {
			this(2, false);
		}

	}

}
