package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public abstract class EntityCondition extends Condition {

	public static final MapCodec<EntityCondition> MAP_CODEC = EntityConditionTypes.CODEC.dispatchMap("type", EntityCondition::getType, EntityConditionType::mapCodec);
	public static final Codec<EntityCondition> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, EntityCondition> PACKET_CODEC = EntityConditionTypes.PACKET_CODEC.dispatch(EntityCondition::getType, EntityConditionType::packetCodec);

	@Override
	public abstract EntityConditionType<?> getType();

	@Override
	public boolean test(Context context) {

		context = new ContextImpl.Builder(context)
			.add(ContextParameters.POSITION, context.required(ContextParameters.ENTITY_POS))
			.build(context.getWorld());

		return super.test(context);

	}

	@Override
	public ConditionCategory<EntityCondition> getCategory() {
		return ConditionCategories.ENTITY_CONDITION;
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return ContextTypes.ENTITY.getAllowed();
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.ENTITY_CONDITION_TYPE, this.getType()) + "\"";
	}

}
