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
public class ModifyDamageDealtPower extends DamageModifyingPower {

	public static final MapCodec<ModifyDamageDealtPower> CODEC = DamageModifyingPower.createDamageModifyingCodec(ModifyDamageDealtPower::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyDamageDealtPower> STREAM_CODEC = DamageModifyingPower.createDamageModifyingStreamCodec(ModifyDamageDealtPower::new);

	public ModifyDamageDealtPower(Optional<Condition> activeCondition, List<Modifier> modifiers, Action onModifyAction) {
		super(activeCondition, modifiers, onModifyAction);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_DAMAGE_DEALT;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends DamageModifyingPower.Instance<ModifyDamageDealtPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyDamageDealtPower power) {
			super(holder, power);
		}

	}

	public static Context createContext(Entity actor, Entity target, DamageSource damageSource, float damageAmount) {
		return PowerTypes.MODIFY_DAMAGE_DEALT.contextBuilder()
			.add(NeoApoliContextKeys.ACTOR_ENTITY, actor)
			.add(NeoApoliContextKeys.TARGET_ENTITY, target)
			.add(NeoApoliContextKeys.DAMAGE_SOURCE, damageSource)
			.add(NeoApoliContextKeys.DAMAGE_AMOUNT, damageAmount)
			.addNullable(NeoApoliContextKeys.DAMAGING_ENTITY, damageSource.getEntity())
			.addNullable(NeoApoliContextKeys.DIRECT_DAMAGING_ENTITY, damageSource.getDirectEntity())
			.add(NeoApoliContextKeys.THIS_ENTITY, actor)
			.add(NeoApoliContextKeys.THIS_POS, actor.position())
			.build(actor.level());
	}

}
