package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class LoopBiEntityAction extends BiEntityAction implements LoopMetaAction<BiEntityAction> {

	public static final MapCodec<LoopBiEntityAction> CODEC = MapCodecUtil.lazy(LoopBiEntityAction.class.getSimpleName(), () -> LoopMetaAction.codec(BiEntityAction.CODEC, LoopBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopBiEntityAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, LoopBiEntityAction::new));

	private final Optional<BiEntityAction> beforeAction;
	private final Optional<BiEntityAction> afterAction;

	private final NumberProvider iterations;
	private final BiEntityAction action;

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.LOOP;
	}

	@Override
	protected void impl(Context context) {
		LoopMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		LoopMetaAction.super.validate(reporter);
	}

}
