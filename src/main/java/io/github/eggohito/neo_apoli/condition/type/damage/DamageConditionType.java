package io.github.eggohito.neo_apoli.condition.type.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DamageConditionType<C extends DamageCondition>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) implements ConditionType<C> {

}
