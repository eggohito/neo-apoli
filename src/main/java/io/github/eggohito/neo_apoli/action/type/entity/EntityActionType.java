package io.github.eggohito.neo_apoli.action.type.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record EntityActionType<A extends EntityAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

}
