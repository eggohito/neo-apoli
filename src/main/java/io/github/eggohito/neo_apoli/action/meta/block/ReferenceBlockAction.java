package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ReferenceBlockAction extends BlockAction implements ReferenceMetaAction<BlockAction> {

	public static final MapCodec<ReferenceBlockAction> CODEC = ReferenceMetaAction.codec(ReferenceBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBlockAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceBlockAction::new);

	private final Identifier value;

	public ReferenceBlockAction(Identifier value) {
		this.value = value;
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.REFERENCE;
	}

	@Override
	public void impl(Context context) {
		ReferenceMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ReferenceMetaAction.super.validate(reporter);
	}

}
