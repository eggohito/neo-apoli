package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.key.KeyState;
import io.github.eggohito.neo_apoli.key.KeyStateManager;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public record IsSimultaneouslyPressedKeyCondition(List<StringProvider> ids, NumberProvider buffer) implements KeyCondition {

	public static final MapCodec<IsSimultaneouslyPressedKeyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.listOf(2, Integer.MAX_VALUE).fieldOf("ids").forGetter(IsSimultaneouslyPressedKeyCondition::ids),
		NumberProvider.CODEC.optionalFieldOf("buffer", new ConstantNumberProvider(3)).forGetter(IsSimultaneouslyPressedKeyCondition::buffer)
	).apply(instance, IsSimultaneouslyPressedKeyCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsSimultaneouslyPressedKeyCondition> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.collection(ObjectArrayList::new, StringProvider.STREAM_CODEC), IsSimultaneouslyPressedKeyCondition::ids,
		NumberProvider.STREAM_CODEC, IsSimultaneouslyPressedKeyCondition::buffer,
		IsSimultaneouslyPressedKeyCondition::new
	);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.IS_SIMULTANEOUSLY_PRESSED;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Context bufferContext = context.makeChild(".buffer");
		long buffer = buffer().nextLong(bufferContext);

		if (bufferContext.hasErrors()) {
			return false;
		}

		UUID uuid = context.required(NeoApoliContextKeys.THIS_ENTITY).getUUID();
		ListIterator<StringProvider> iterator = ids().listIterator();

		long previousPressedTime = Long.MIN_VALUE;
		boolean result = false;

		while (iterator.hasNext()) {

			int index = iterator.nextIndex();
			StringProvider idProvider = iterator.next();

			Context idContext = context.makeChild(".ids[" + index + "]");
			String id = idProvider.next(idContext);

			if (!idContext.hasErrors() && KeyStateManager.getState(uuid, id).isPresent()) {

				KeyState state = KeyStateManager.getState(uuid, id).orElseThrow();
				long currentPressedTime = state.pressedTime();

				if (state.pressed()) {

					if (previousPressedTime == Long.MIN_VALUE) {
						previousPressedTime = currentPressedTime;
					}

					else if (currentPressedTime < (previousPressedTime - buffer) || currentPressedTime > (previousPressedTime + buffer)) {
						return false;
					}

					else {
						result = true;
					}

				}

			}

		}

		return result;

	}

	@Override
	public void validate(ProblemReporter reporter) {

		KeyCondition.super.validate(reporter);
		ListIterator<StringProvider> iterator = ids().listIterator();

		while (iterator.hasNext()) {

			int index = iterator.nextIndex();
			StringProvider idProvider = iterator.next();

			idProvider.validate(reporter.forChild(".ids[" + index + "]"));

		}

		buffer().validate(reporter.forChild(".buffer"));

	}

}
