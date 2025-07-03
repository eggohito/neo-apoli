package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class IfElseBiEntityAction extends BiEntityAction implements IfElseMetaAction<BiEntityAction, BiEntityCondition> {

	public static final MapCodec<IfElseBiEntityAction> CODEC = NeoApoliMapCodecs.lazy(IfElseBiEntityAction.class.getSimpleName(), () -> IfElseMetaAction.codec(BiEntityCondition.CODEC, BiEntityAction.CODEC, IfElseBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseBiEntityAction.class.getSimpleName(), () -> IfElseMetaAction.packetCodec(BiEntityCondition.PACKET_CODEC, BiEntityAction.PACKET_CODEC, IfElseBiEntityAction::new));

	private final BiEntityCondition condition;
	private final BiEntityAction ifAction;
	private final Optional<BiEntityAction> elseAction;

	public IfElseBiEntityAction(BiEntityCondition condition, BiEntityAction ifAction, Optional<BiEntityAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.IF_ELSE;
	}

	@Override
	public void impl(Context context) {
		IfElseMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaAction.super.validate(reporter);
	}

}
