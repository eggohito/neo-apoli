package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public abstract class DamageCondition extends Condition {

	public static final MapCodec<DamageCondition> MAP_CODEC = DamageConditionTypes.CODEC.dispatchMap("type", DamageCondition::getType, DamageConditionType::mapCodec);
	public static final Codec<DamageCondition> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, DamageCondition> PACKET_CODEC = DamageConditionTypes.PACKET_CODEC.dispatch(DamageCondition::getType, DamageConditionType::packetCodec);

	@Override
	public abstract DamageConditionType<?> getType();

	@Override
	public boolean test(Context context) {

		DamageSource damageSource = context.required(ContextParameters.DAMAGE_SOURCE);
		context = context.copy(builder -> builder
			.addNullable(ContextParameters.DAMAGING_ENTITY, damageSource.getAttacker())
			.addNullable(ContextParameters.DIRECT_DAMAGING_ENTITY, damageSource.getSource()));

		return super.test(context);

	}

	@Override
	public ConditionCategory<DamageCondition> getCategory() {
		return ConditionCategories.DAMAGE_CONDITION;
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return ContextTypes.DAMAGE.getRequired();
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, this.getType()) + "\"";
	}

}
