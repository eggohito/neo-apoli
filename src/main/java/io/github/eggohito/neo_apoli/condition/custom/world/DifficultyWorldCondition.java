package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Difficulty;

public record DifficultyWorldCondition(Difficulty difficulty) implements WorldCondition {

	public static final MapCodec<DifficultyWorldCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.DIFFICULTY.fieldOf("difficulty").forGetter(DifficultyWorldCondition::difficulty))
		.apply(instance, DifficultyWorldCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DifficultyWorldCondition> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.DIFFICULTY, DifficultyWorldCondition::difficulty,
		DifficultyWorldCondition::new
	);

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.DIFFICULTY;
	}

	@Override
	public boolean test(Context context) {
		return context.level().getDifficulty() == this.difficulty();
	}

}
