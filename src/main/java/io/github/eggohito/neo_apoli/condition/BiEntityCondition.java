package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public abstract class BiEntityCondition extends Condition {

	public static final Codec<BiEntityCondition> CODEC = BiEntityConditionTypes.CODEC.dispatch("type", BiEntityCondition::getType, BiEntityConditionType::mapCodec);
	public static final PacketCodec<RegistryByteBuf, BiEntityCondition> PACKET_CODEC = BiEntityConditionTypes.PACKET_CODEC.dispatch(BiEntityCondition::getType, BiEntityConditionType::packetCodec);

	@Override
	public abstract BiEntityConditionType<?> getType();

	@Override
	public ConditionCategory<BiEntityCondition> getCategory() {
		return ConditionCategories.BIENTITY_CONDITION;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return ContextTypes.BIENTITY.getAllowed();
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, this.getType()) + "\"";
	}

}
