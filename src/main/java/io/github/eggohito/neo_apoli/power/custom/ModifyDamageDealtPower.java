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

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyDamageDealtPower extends DamageModifyingPower {

	public static final MapCodec<ModifyDamageDealtPower> MAP_CODEC = DamageModifyingPower.createDamageModifyingCodec(ModifyDamageDealtPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyDamageDealtPower> STREAM_CODEC = DamageModifyingPower.createDamageModifyingStreamCodec(ModifyDamageDealtPower::new);

	public ModifyDamageDealtPower(Optional<Condition> activeCondition, List<Modifier> modifiers, Action onModifyAction) {
		super(activeCondition, modifiers, onModifyAction);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_DAMAGE_DEALT;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends DamageModifyingPower.Instance<ModifyDamageDealtPower> {

		protected Instance(@NotNull ModifyDamageDealtPower power) {
			super(power);
		}

		@Override
		public Context createDamageContext(Entity actor, Entity target, DamageSource source, float amount) {
			return this.createHolderContextBuilder(actor)
				.withRequired(NeoApoliContextParams.ACTOR_ENTITY, actor)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, target)
				.withRequired(NeoApoliContextParams.DAMAGE_SOURCE, source)
				.withRequired(NeoApoliContextParams.DAMAGE_AMOUNT, amount)
				.withNullable(NeoApoliContextParams.DAMAGING_ENTITY, source.getEntity())
				.withNullable(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY, source.getDirectEntity())
				.build(actor.level());
		}

	}

	public static float modify(@NotNull Entity actor, @NotNull Entity target, DamageSource source, float amount) {
		return DamageModifyingPower.modify(PowerTypes.MODIFY_DAMAGE_DEALT, Instance.class, actor, actor, target, source, amount);
	}

}
