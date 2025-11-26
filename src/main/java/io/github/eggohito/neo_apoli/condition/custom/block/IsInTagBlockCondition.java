package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public record IsInTagBlockCondition(TagKey<Block> tag) implements BlockCondition {

	public static final MapCodec<IsInTagBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.hashedCodec(Registries.BLOCK).fieldOf("tag").forGetter(IsInTagBlockCondition::tag)
	).apply(instance, IsInTagBlockCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagBlockCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.BLOCK), IsInTagBlockCondition::tag,
		IsInTagBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.BLOCK_STATE)
			.map(state -> state.is(this.tag()))
			.orElse(false);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		BlockCondition.super.validate(reporter);
		RegistryUtil.validateTag(reporter.forChild(".tag"), this.tag());
	}

}
