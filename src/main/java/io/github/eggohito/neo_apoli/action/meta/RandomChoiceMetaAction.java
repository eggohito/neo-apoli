package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.mixin.access.WeightedListAccessor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public interface RandomChoiceMetaAction<AX extends ActionContext<?>, AA extends Action<AX, AT>, AT extends ActionType<?>> extends Action<AX, AT> {

	WeightedList<AA> actions();

	@Override
	default void accept(AX context) {

		actions().shuffle();
		Iterator<AA> iterator = actions().iterator();

		if (iterator.hasNext())	{
			iterator.next().accept(context);
		}

	}

	static <AA extends Action<?, ?>, CMA extends RandomChoiceMetaAction<?, AA, ?>> MapCodec<CMA> createCodec(Codec<AA> elementCodec, Function<WeightedList<AA>, CMA> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			WeightedList.createCodec(elementCodec).fieldOf("actions").forGetter(RandomChoiceMetaAction::actions)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, AA extends Action<?, ?>, CMA extends RandomChoiceMetaAction<?, AA, ?>> PacketCodec<B, CMA> createPacketCodec(PacketCodec<B, AA> elementCodec, Function<WeightedList<AA>, CMA> constructor) {
		return new PacketCodec<>() {

			@Override
			public CMA decode(B buf) {

				WeightedList<AA> actions = new WeightedList<>();
				int size = buf.readInt();

				for (int i = 0; i < size; i++) {

					AA element = elementCodec.decode(buf);
					int weight = buf.readInt();

					actions.add(element, weight);

				}

				return constructor.apply(actions);

			}

			@Override
			public void encode(B buf, CMA value) {

				//noinspection unchecked
				List<WeightedList.Entry<AA>> entries = ((WeightedListAccessor<AA>) value.actions()).getEntries();
				buf.writeInt(entries.size());

				for (WeightedList.Entry<AA> entry : entries) {
					elementCodec.encode(buf, entry.getElement());
					buf.writeInt(entry.getWeight());
				}

			}

		};
	}

}
