package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyEffectImmunityPower extends Power {

	public static final MapCodec<ModifyEffectImmunityPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance).apply(instance, ModifyEffectImmunityPower::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyEffectImmunityPower> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition, ModifyEffectImmunityPower::new);

	public ModifyEffectImmunityPower(Optional<Condition> activeCondition) {
		super(activeCondition);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_EFFECT_IMMUNITY;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<ModifyEffectImmunityPower> {

		protected Instance(@NotNull ModifyEffectImmunityPower power) {
			super(power);
		}

		public Context createContext(Entity holder, MobEffectInstance effectInstance, @Nullable Entity source) {
			return this.createHolderContextBuilder(holder)
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, source)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, holder)
				.withRequired(NeoApoliContextParams.EFFECT_INSTANCE, effectInstance)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_EFFECT_DURATION.keySet());
		}

	}

	public static boolean modify(@NotNull Entity holder, MobEffectInstance effectInstance, @Nullable Entity source) {

		for (var instance : Powers.getInstances(holder, Instance.class)) {

			Context context = instance.createContext(holder, effectInstance, source);

			if (instance.isActive(context)) {
				return true;
			}

		}

		return false;

	}

}
