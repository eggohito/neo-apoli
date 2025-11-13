package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceDamageCondition(Identifier value) implements DamageCondition, ReferenceMetaCondition<DamageCondition> {

	public static final MapCodec<ReferenceDamageCondition> CODEC = ReferenceMetaCondition.codec(ReferenceDamageCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceDamageCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceDamageCondition::new);

	@Override
	public Pair<Class<DamageCondition>, String> classAndName() {
		return Pair.of(DamageCondition.class, "Damage condition");
	}

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
