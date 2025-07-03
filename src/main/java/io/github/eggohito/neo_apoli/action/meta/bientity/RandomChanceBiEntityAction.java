package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class RandomChanceBiEntityAction extends BiEntityAction implements RandomChanceMetaAction<BiEntityAction> {

	public static final MapCodec<RandomChanceBiEntityAction> CODEC = NeoApoliMapCodecs.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(BiEntityAction.CODEC, RandomChanceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, RandomChanceBiEntityAction::new));

	private final BiEntityAction successAction;
	private final Optional<BiEntityAction> failAction;
	private final float chance;

	public RandomChanceBiEntityAction(BiEntityAction successAction, Optional<BiEntityAction> failAction, float chance) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.chance = chance;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void impl(Context context) {
		RandomChanceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		RandomChanceMetaAction.super.validate(reporter);
	}

}
