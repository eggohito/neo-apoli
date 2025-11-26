package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.keybinding.KeyState;
import io.github.eggohito.neo_apoli.keybinding.KeyStateManager;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record IsPressedKeyCondition(StringProvider id) implements KeyCondition {

	public static final MapCodec<IsPressedKeyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("id").forGetter(IsPressedKeyCondition::id)
	).apply(instance, IsPressedKeyCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsPressedKeyCondition> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, IsPressedKeyCondition::id,
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

		return context.optional(NeoApoliContextKeys.THIS_ENTITY)
			.map(Entity::getUUID)
			.flatMap(uuid -> KeyStateManager.getState(uuid, id))
			.map(KeyState::pressed)
			.orElse(false);

	}

	@Override
	public void validate(ProblemReporter reporter) {
		KeyCondition.super.validate(reporter);
		id().validate(reporter.forChild(".id"));
	}

}
