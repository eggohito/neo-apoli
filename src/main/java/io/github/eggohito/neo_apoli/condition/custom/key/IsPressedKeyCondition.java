package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.api.key.KeyStateManager;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record IsPressedKeyCondition(StringProvider id) implements KeyCondition {

	public static final MapCodec<IsPressedKeyCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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

		Context idContext = context.forChild(".id");
		String id = id().next(idContext);

		if (idContext.hasErrors()) {
			return false;
		}

		return context.getOptional(NeoApoliContextParams.THIS_ENTITY)
			.map(Entity::getUUID)
			.flatMap(uuid -> KeyStateManager.getState(uuid, id))
			.map(KeyState::pressed)
			.orElse(false);

	}

	@Override
	public void validate(Context.Validator validator) {
		KeyCondition.super.validate(validator);
		id().validate(validator.forChild(".id"));
	}

}
