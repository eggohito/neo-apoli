package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.api.key.KeyStateManager;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextHelper;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.UUID;

public record HasPressedKeysSimultaneouslyEntityCondition(List<StringProvider> keys, NumberProvider timeWindow) implements EntityCondition {

	public static final MapCodec<HasPressedKeysSimultaneouslyEntityCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.listOf(2, Integer.MAX_VALUE).fieldOf("keys").forGetter(HasPressedKeysSimultaneouslyEntityCondition::keys),
		NumberProvider.CODEC.optionalFieldOf("time_window", new ConstantNumberProvider(3)).forGetter(HasPressedKeysSimultaneouslyEntityCondition::timeWindow)
	).apply(instance, HasPressedKeysSimultaneouslyEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HasPressedKeysSimultaneouslyEntityCondition> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC.apply(ByteBufCodecs.list()), HasPressedKeysSimultaneouslyEntityCondition::keys,
		NumberProvider.STREAM_CODEC, HasPressedKeysSimultaneouslyEntityCondition::timeWindow,
		HasPressedKeysSimultaneouslyEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.HAS_PRESSED_KEYS_SIMULTANEOUSLY;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Context timeWindowContext = context.forChild(".time_window");
		long timeWindow = timeWindow().nextLong(timeWindowContext);

		if (timeWindowContext.hasErrors()) {
			return false;
		}

		UUID uuid = context.getRequired(NeoApoliContextParams.THIS_ENTITY).getUUID();
		ListIterator<StringProvider> listIterator = keys().listIterator();

		long previousPressedTime = Long.MIN_VALUE;
		boolean result = false;

		while (listIterator.hasNext()) {

			Context keyContext = context.forChild(".keys[" + listIterator.nextIndex() + "]");
			StringProvider key = listIterator.next();

			String id = key.nextString(keyContext);
			Optional<KeyState> optState = KeyStateManager.getState(uuid, id);

			if (keyContext.hasErrors() || optState.isEmpty()) {
				continue;
			}

			KeyState state = optState.get();
			long currentPressedTime = state.pressedTime();

			if (state.pressed()) {

				if (previousPressedTime == Long.MIN_VALUE) {
					previousPressedTime = currentPressedTime;
				}

				else if (currentPressedTime < (previousPressedTime - timeWindow) || currentPressedTime > (previousPressedTime + timeWindow)) {
					return false;
				}

				else {
					result = true;
				}

			}

		}

		return result;

	}

	@Override
	public void validate(Context.Validator validator) {

		EntityCondition.super.validate(validator);

		ContextHelper.validateAll(keys(), validator, index -> ".keys[" + index + "]");
		timeWindow().validate(validator.forChild(".time_window"));

	}

}
