package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public record IsInTagDamageCondition(TagKey<DamageType> tag) implements DamageCondition {

	public static final MapCodec<IsInTagDamageCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.hashedCodec(Registries.DAMAGE_TYPE).fieldOf("tag").forGetter(IsInTagDamageCondition::tag)
	).apply(instance, IsInTagDamageCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagDamageCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.DAMAGE_TYPE), IsInTagDamageCondition::tag,
		IsInTagDamageCondition::new
	);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.DAMAGE_SOURCE)
			.map(source -> source.is(this.tag()))
			.orElse(false);
	}

	@Override
	public void validate(Context.Validator validator) {
		DamageCondition.super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".tag"), this.tag());
	}

}
