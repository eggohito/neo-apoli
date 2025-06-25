package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@Data
public final class RandomChanceItemAction extends ItemAction implements RandomChanceMetaAction<ItemAction> {

	public static final MapCodec<RandomChanceItemAction> CODEC = NeoApoliMapCodecs.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(ItemAction.CODEC, RandomChanceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceItemAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(ItemAction.PACKET_CODEC, RandomChanceItemAction::new));

	private final ItemAction successAction;
	private final Optional<ItemAction> failAction;

	private final float chance;

	public RandomChanceItemAction(ItemAction successAction, Optional<ItemAction> failAction, float chance) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.chance = chance;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void impl(Context context) {
		RandomChanceMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		RandomChanceMetaAction.super.validate(reporter);
	}

}
