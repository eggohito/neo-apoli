package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;

public record IsInTagWorldCondition(TagKey<Level> tag) implements WorldCondition {

	public static final MapCodec<IsInTagWorldCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(CodecUtil.hashedTag(Registries.DIMENSION).fieldOf("tag").forGetter(IsInTagWorldCondition::tag))
		.apply(instance, IsInTagWorldCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagWorldCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.DIMENSION), IsInTagWorldCondition::tag,
		IsInTagWorldCondition::new
	);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {

		Level level = context.getWorld();
		Registry<Level> levelRegistry = level.registryAccess().lookupOrThrow(Registries.DIMENSION);

		return levelRegistry.wrapAsHolder(level).is(this.tag());

	}

	@Override
	public void validate(ProblemReporter reporter) {
		WorldCondition.super.validate(reporter);
		RegistryUtil.validateTag(reporter.forChild(".tag"), this.tag());
	}

}
