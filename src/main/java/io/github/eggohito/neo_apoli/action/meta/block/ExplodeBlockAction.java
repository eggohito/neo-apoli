package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.ExplodeMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record ExplodeBlockAction(BiEntityCondition damageableBiEntityCondition, BlockCondition destructibleBlockCondition, ExplosionProperty property, ExplosionDisplay display) implements BlockAction, ExplodeMetaAction<BlockActionType<?>> {

	public static final MapCodec<ExplodeBlockAction> CODEC = ExplodeMetaAction.codec(ExplodeBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, ExplodeBlockAction> PACKET_CODEC = ExplodeMetaAction.packetCodec(ExplodeBlockAction::new);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.EXPLODE;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return BlockAction.super.getAllowedParameters();
	}

}
