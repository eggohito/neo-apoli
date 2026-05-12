package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;

public record IsInTagWorldCondition(TagKey<Level> tag) implements WorldCondition {

	public static final MapCodec<IsInTagWorldCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(TagKey.hashedCodec(Registries.DIMENSION).fieldOf("tag").forGetter(IsInTagWorldCondition::tag))
		.apply(instance, IsInTagWorldCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagWorldCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.DIMENSION), IsInTagWorldCondition::tag,
		IsInTagWorldCondition::new
	);

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {

		Level level = context.level();
		Registry<Level> levelRegistry = level
			.registryAccess()
			.lookupOrThrow(Registries.DIMENSION);

		return levelRegistry.wrapAsHolder(level).is(this.tag());

	}

	@Override
	public void validate(Context.Validator validator) {
		WorldCondition.super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".tag"), this.tag());
	}

}
