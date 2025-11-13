package io.github.eggohito.neo_apoli.action.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.MetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record MetaActionType<A extends MetaAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

}
