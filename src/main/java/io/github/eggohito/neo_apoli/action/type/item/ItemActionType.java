package io.github.eggohito.neo_apoli.action.type.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ItemActionType<A extends ItemAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

}
