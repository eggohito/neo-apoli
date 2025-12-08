package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.DamageModifyingPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Getter
public class ModifyDamageTakenPower extends DamageModifyingPower {

	public static final MapCodec<ModifyDamageTakenPower> CODEC = DamageModifyingPower.createDamageModifyingCodec(ModifyDamageTakenPower::new);
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

	}

	public static Context createContext(Entity actor, Entity target, DamageSource damageSource, float damageAmount) {
		return PowerTypes.MODIFY_DAMAGE_TAKEN.contextBuilder()
			.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, actor)
			.add(NeoApoliContextKeys.TARGET_ENTITY, target)
			.add(NeoApoliContextKeys.DAMAGE_SOURCE, damageSource)
			.add(NeoApoliContextKeys.DAMAGE_AMOUNT, damageAmount)
			.addNullable(NeoApoliContextKeys.DAMAGING_ENTITY, damageSource.getEntity())
			.addNullable(NeoApoliContextKeys.DIRECT_DAMAGING_ENTITY, damageSource.getDirectEntity())
			.add(NeoApoliContextKeys.THIS_ENTITY, target)
			.add(NeoApoliContextKeys.THIS_POS, target.position())
			.build(actor.level());
	}

}
