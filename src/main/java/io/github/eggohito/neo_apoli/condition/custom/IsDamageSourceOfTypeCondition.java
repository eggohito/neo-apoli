package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

import java.util.Set;

public record IsDamageSourceOfTypeCondition(ResourceKey<DamageType> damageType, Context.Parameter<DamageSource> damageSource) implements Condition {

	public static final MapCodec<IsDamageSourceOfTypeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("damage_type").forGetter(IsDamageSourceOfTypeCondition::damageType),
		NeoApoliContextParams.Codecs.DAMAGE_SOURCE.fieldOf("damage_source").forGetter(IsDamageSourceOfTypeCondition::damageSource)
	).apply(instance, IsDamageSourceOfTypeCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsDamageSourceOfTypeCondition> STREAM_CODEC = StreamCodec.composite(
		ResourceKey.streamCodec(Registries.DAMAGE_TYPE), IsDamageSourceOfTypeCondition::damageType,
		NeoApoliContextParams.StreamCodecs.DAMAGE_SOURCE, IsDamageSourceOfTypeCondition::damageSource,
		IsDamageSourceOfTypeCondition::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.IS_DAMAGE_SOURCE_OF_TYPE;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(damageSource())
			.map(source -> source.is(this.damageType()))
			.orElse(false);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(damageSource());
	}

	@Override
	public void validate(Context.Validator validator) {
		Condition.super.validate(validator);
		RegistryUtil.validateKey(validator.forChild(".damage_type"), this.damageType());
	}

}
