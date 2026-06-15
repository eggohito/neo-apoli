package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.misc.DamageModifyingPower;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record ModifyDamageTakenPower(Optional<Condition> activeCondition, List<Modifier> modifiers, Action onModifyAction) implements DamageModifyingPower {

	public static final MapCodec<ModifyDamageTakenPower> CODEC = DamageModifyingPower.codec(ModifyDamageTakenPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyDamageTakenPower> STREAM_CODEC = DamageModifyingPower.streamCodec(ModifyDamageTakenPower::new);

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_DAMAGE_TAKEN;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends DamageModifyingPower.Instance<ModifyDamageTakenPower> {

		protected Instance(@NotNull ModifyDamageTakenPower power) {
			super(power);
		}

		@Override
		public Context createDamageContext(@Nullable Entity actor, @NotNull Entity target, DamageSource source, float amount) {
			return this.createHolderContextBuilder(target)
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, actor)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, target)
				.withRequired(NeoApoliContextParams.TAKEN_DAMAGE_SOURCE, source)
				.withRequired(NeoApoliContextParams.TAKEN_DAMAGE_AMOUNT, amount)
				.withNullable(NeoApoliContextParams.DAMAGING_ENTITY, source.getEntity())
				.withNullable(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY, source.getDirectEntity())
				.buildWithRequirements(target.level(), NeoApoliPowerTypes.MODIFY_DAMAGE_TAKEN.requirements());
		}

	}

	public static float modify(@NotNull Entity target, DamageSource source, float amount) {
		return DamageModifyingPower.modify(NeoApoliPowerTypes.MODIFY_DAMAGE_TAKEN, Instance.class, target, source.getEntity(), target, source, amount);
	}

}
