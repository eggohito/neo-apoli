package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.action.custom.SequenceAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface Action extends ContextAware, StringDisplayable {

	Codec<Action> BASE_CODEC = Codec.recursive(Action.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(ActionType.CODEC.dispatch(Action::getType, ActionType::mapCodec), codec.listOf().xmap(SequenceAction::new, SequenceAction::actions), NothingAction.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, Action> BASE_PACKET_CODEC = ActionType.PACKET_CODEC.dispatch(Action::getType, ActionType::packetCodec);

	@Override
	default String asDisplayString() {
		return "Action with type \"%s\"".formatted(RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()));
	}

	ActionType<?> getType();

	void execute(Context context);

}
