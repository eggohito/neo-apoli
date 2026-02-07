package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.DamageModifyingPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyDamageTakenPower extends DamageModifyingPower {

	public static final MapCodec<ModifyDamageTakenPower> MAP_CODEC = DamageModifyingPower.createDamageModifyingCodec(ModifyDamageTakenPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyDamageTakenPower> STREAM_CODEC = DamageModifyingPower.createDamageModifyingStreamCodec(ModifyDamageTakenPower::new);

	public ModifyDamageTakenPower(Optional<Condition> activeCondition, List<Modifier> modifiers, Action onModifyAction) {
		super(activeCondition, modifiers, onModifyAction);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_DAMAGE_TAKEN;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends DamageModifyingPower.Instance<ModifyDamageTakenPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyDamageTakenPower power) {
			super(holder, power);
		}

		@Override
		public Context createDamageContext(@Nullable Entity actor, @NotNull Entity target, DamageSource source, float amount) {
			return this.createHolderContextBuilder()
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, actor)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, target)
				.withRequired(NeoApoliContextParams.DAMAGE_SOURCE, source)
				.withRequired(NeoApoliContextParams.DAMAGE_AMOUNT, amount)
				.withNullable(NeoApoliContextParams.DAMAGING_ENTITY, source.getEntity())
				.withNullable(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY, source.getDirectEntity())
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_DAMAGE_TAKEN.keySet());
		}

	}

	public static float modify(@NotNull Entity target, DamageSource source, float amount) {
		return DamageModifyingPower.modify(PowerTypes.MODIFY_DAMAGE_TAKEN, Instance.class, target, source.getEntity(), target, source, amount);
	}

}
