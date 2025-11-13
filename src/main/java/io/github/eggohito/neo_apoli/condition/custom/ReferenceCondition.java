package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceCondition(Identifier value) implements ReferenceMetaCondition<Condition> {

	public static final MapCodec<ReferenceCondition> CODEC = ReferenceMetaCondition.codec(ReferenceCondition::new);

	public static final PacketCodec<RegistryByteBuf, ReferenceCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceCondition::new);

	@Override
	public Pair<Class<Condition>, String> classAndName() {
		return Pair.of(Condition.class, "Condition");
	}

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.REFERENCE;
	}

}
