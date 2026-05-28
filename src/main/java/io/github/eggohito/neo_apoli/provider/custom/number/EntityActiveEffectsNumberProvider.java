package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.ConstantCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record EntityActiveEffectsNumberProvider(Condition condition, EntityProvider entity) implements NumberProvider {

	public static final Context.Parameter<MobEffectInstance> ACTIVE_EFFECT = NeoApoliContextParams.registerSimpleInternal("active_effect", MobEffectInstance.class);
	public static final ContextKeySet CONDITION_PARAMETER_SET = new ContextKeySet.Builder().required(ACTIVE_EFFECT).build();

	public static final MapCodec<EntityActiveEffectsNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.optionalFieldOf("condition", new ConstantCondition(true)).forGetter(EntityActiveEffectsNumberProvider::condition),
		EntityProvider.CODEC.fieldOf("entity").forGetter(EntityActiveEffectsNumberProvider::entity)
	).apply(instance, EntityActiveEffectsNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityActiveEffectsNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Condition.STREAM_CODEC, EntityActiveEffectsNumberProvider::condition,
		EntityProvider.STREAM_CODEC, EntityActiveEffectsNumberProvider::entity,
		EntityActiveEffectsNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ENTITY_ACTIVE_EFFECTS;
	}

	@Override
	public double getDouble(Context context) {

		Entity entity = entity().getEntity(context.forChild(".entity")).orElse(null);
		int count = 0;

		if (!(entity instanceof LivingEntity livingEntity)) {
			return count;
		}

		for (var activeEffect : livingEntity.getActiveEffects()) {

			Context effectContext = new Context.Builder(context)
				.withRequired(ACTIVE_EFFECT, activeEffect)
				.build(context.level());

			if (condition().test(effectContext.forChild(".condition"))) {
				count++;
			}

		}

		return count;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMETER_SET).forChild(".condition"));
		entity().validate(validator.forChild(".entity"));
	}

}
