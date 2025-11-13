package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record LoopBiEntityAction(Optional<BiEntityAction> beforeAction, Optional<BiEntityAction> afterAction, NumberProvider iterations, BiEntityAction action) implements BiEntityAction, LoopMetaAction<BiEntityAction> {

	public static final MapCodec<LoopBiEntityAction> CODEC = MapCodecUtil.lazy(LoopBiEntityAction.class.getSimpleName(), () -> LoopMetaAction.codec(BiEntityAction.CODEC, LoopBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopBiEntityAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, LoopBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.LOOP;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}
