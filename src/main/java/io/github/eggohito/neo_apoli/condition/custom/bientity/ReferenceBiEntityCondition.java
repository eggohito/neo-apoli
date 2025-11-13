package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBiEntityCondition(Identifier value) implements BiEntityCondition, ReferenceMetaCondition<BiEntityCondition> {

	public static final MapCodec<ReferenceBiEntityCondition> CODEC = ReferenceMetaCondition.codec(ReferenceBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBiEntityCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceBiEntityCondition::new);

	@Override
	public Pair<Class<BiEntityCondition>, String> classAndName() {
		return Pair.of(BiEntityCondition.class, "Bi-entity condition");
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
