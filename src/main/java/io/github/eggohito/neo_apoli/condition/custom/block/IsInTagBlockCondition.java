package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public record IsInTagBlockCondition(TagKey<Block> tag) implements BlockCondition {

	public static final MapCodec<IsInTagBlockCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.hashedCodec(Registries.BLOCK).fieldOf("tag").forGetter(IsInTagBlockCondition::tag)
	).apply(instance, IsInTagBlockCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagBlockCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.BLOCK), IsInTagBlockCondition::tag,
		IsInTagBlockCondition::new
	);

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.BLOCK_STATE)
			.map(state -> state.is(this.tag()))
			.orElse(false);
	}

	@Override
	public void validate(Context.Validator validator) {
		BlockCondition.super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".tag"), this.tag());
	}

}
