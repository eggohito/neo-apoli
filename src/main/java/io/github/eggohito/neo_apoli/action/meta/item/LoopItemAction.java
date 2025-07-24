package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class LoopItemAction extends ItemAction implements LoopMetaAction<ItemAction> {

	public static final MapCodec<LoopItemAction> CODEC = MapCodecUtil.lazy(LoopItemAction.class.getSimpleName(), () -> LoopMetaAction.codec(ItemAction.CODEC, LoopItemAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopItemAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopItemAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(ItemAction.PACKET_CODEC, LoopItemAction::new));

	private final Optional<ItemAction> beforeAction;
	private final Optional<ItemAction> afterAction;

	private final NumberProvider iterations;
	private final ItemAction action;

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.LOOP;
	}

	@Override
	protected void impl(ServerContext context) {
		LoopMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		LoopMetaAction.super.validate(reporter);
	}

}
