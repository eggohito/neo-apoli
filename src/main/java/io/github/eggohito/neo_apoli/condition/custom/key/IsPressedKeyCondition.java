package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingState;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingStateHolder;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsPressedKeyCondition(StringProvider id) implements KeyCondition {

	public static final MapCodec<IsPressedKeyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(IsPressedKeyCondition::id)
	).apply(instance, IsPressedKeyCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsPressedKeyCondition> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, IsPressedKeyCondition::id,
		IsPressedKeyCondition::new
	);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.IS_PRESSED;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Context idContext = context.makeChild(".id");
		String id = id().next(idContext);

		if (idContext.hasErrors()) {
			return false;
		}

		return context.optional(ContextParameters.THIS_ENTITY)
			.map(Entity::getUuid)
			.flatMap(uuid -> KeyBindingStateHolder.getState(uuid, id))
			.map(KeyBindingState::pressed)
			.orElse(false);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		KeyCondition.super.validate(reporter);
		id().validate(reporter.makeChild(".id"));
	}

}
