package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
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

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public final class IfElseListBiEntityAction extends BiEntityAction implements IfElseListMetaAction<BiEntityAction, BiEntityCondition> {

	public static final MapCodec<IfElseListBiEntityAction> CODEC = NeoApoliMapCodecs.lazy(IfElseListBiEntityAction.class.getSimpleName(), () -> IfElseListMetaAction.codec(BiEntityCondition.CODEC, BiEntityAction.CODEC, IfElseListBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseListBiEntityAction.class.getSimpleName(), () -> IfElseListMetaAction.packetCodec(BiEntityCondition.PACKET_CODEC, BiEntityAction.PACKET_CODEC, IfElseListBiEntityAction::new));

	private final List<Entry<BiEntityCondition, BiEntityAction>> entries;

	public IfElseListBiEntityAction(List<Entry<BiEntityCondition, BiEntityAction>> entries) {
		this.entries = entries;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.IF_ELSE_LIST;
	}

	@Override
	public void impl(Context context) {
		IfElseListMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaAction.super.validate(reporter);
	}

}
