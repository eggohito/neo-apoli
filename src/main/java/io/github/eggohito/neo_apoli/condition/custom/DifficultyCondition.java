package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Difficulty;

public record DifficultyCondition(Difficulty difficulty) implements Condition {

	public static final MapCodec<DifficultyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.DIFFICULTY.fieldOf("difficulty").forGetter(DifficultyCondition::difficulty))
		.apply(instance, DifficultyCondition::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, DifficultyCondition> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.DIFFICULTY, DifficultyCondition::difficulty,
		DifficultyCondition::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.DIFFICULTY;
	}

	@Override
	public boolean test(Context context) {
		return context.level().getDifficulty() == difficulty();
	}

}
