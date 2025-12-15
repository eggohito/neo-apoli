package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record IsOfWorldCondition(ResourceKey<Level> dimension) implements WorldCondition {

	public static final MapCodec<IsOfWorldCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(IsOfWorldCondition::dimension))
		.apply(instance, IsOfWorldCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOfWorldCondition> STREAM_CODEC = StreamCodec.composite(
		ResourceKey.streamCodec(Registries.DIMENSION), IsOfWorldCondition::dimension,
		IsOfWorldCondition::new
	);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.getLevel().dimension().equals(this.dimension());
	}

}
