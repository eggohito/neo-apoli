package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingState;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingStateHolder;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public record IsSimultaneouslyPressedKeyCondition(List<StringProvider> ids, NumberProvider buffer) implements KeyCondition {

	public static final MapCodec<IsSimultaneouslyPressedKeyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.listOf(2, Integer.MAX_VALUE).fieldOf("ids").forGetter(IsSimultaneouslyPressedKeyCondition::ids),
		NumberProvider.CODEC.optionalFieldOf("buffer", new ConstantNumberProvider(3)).forGetter(IsSimultaneouslyPressedKeyCondition::buffer)
	).apply(instance, IsSimultaneouslyPressedKeyCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsSimultaneouslyPressedKeyCondition> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectArrayList::new, StringProvider.PACKET_CODEC), IsSimultaneouslyPressedKeyCondition::ids,
		NumberProvider.PACKET_CODEC, IsSimultaneouslyPressedKeyCondition::buffer,
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

		UUID uuid = context.required(NeoApoliContextParameters.THIS_ENTITY).getUuid();
		ListIterator<StringProvider> iterator = ids().listIterator();

		long previousPressedTime = Long.MIN_VALUE;
		boolean result = false;

		while (iterator.hasNext()) {

			int index = iterator.nextIndex();
			StringProvider idProvider = iterator.next();

			Context idContext = context.makeChild(".ids[" + index + "]");
			String id = idProvider.next(idContext);

			if (!idContext.hasErrors() && KeyBindingStateHolder.getState(uuid, id).isPresent()) {

				KeyBindingState state = KeyBindingStateHolder.getState(uuid, id).orElseThrow();
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
	public void validate(ErrorReporter reporter) {

		KeyCondition.super.validate(reporter);
		ListIterator<StringProvider> iterator = ids().listIterator();

		while (iterator.hasNext()) {

			int index = iterator.nextIndex();
			StringProvider idProvider = iterator.next();

			idProvider.validate(reporter.makeChild(".ids[" + index + "]"));

		}

		buffer().validate(reporter.makeChild(".buffer"));

	}

}
