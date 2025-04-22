package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public interface MultiMetaCondition<CX extends ConditionContext, CC extends Condition<CX, CT>, CT extends ConditionType<?>> extends Condition<CX, CT> {

	List<CC> conditions();

	@Override
	default void validate(ErrorReporter reporter) {

		ListIterator<CC> conditionIterator = conditions().listIterator();

		while (conditionIterator.hasNext())	{
			ErrorReporter conditionReporter = reporter.makeChild("conditions[" + conditionIterator.nextIndex() + "]");
			conditionIterator.next().validate(conditionReporter);
		}

	}

	static <CC extends Condition<?, ?>, MC extends MultiMetaCondition<?, CC, ?>> MapCodec<MC> createCodec(Codec<CC> elementCodec, Function<List<CC>, MC> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.listOf().fieldOf("conditions").forGetter(MultiMetaCondition::conditions)
		).apply(instance, constructor));
	}

	static <BB extends ByteBuf, CC extends Condition<?, ?>, MC extends MultiMetaCondition<?, CC, ?>> PacketCodec<BB, MC> createPacketCodec(PacketCodec<BB, CC> elementCodec, Function<List<CC>, MC> constructor) {
		return PacketCodecs.collection(ObjectArrayList::new, elementCodec).xmap(constructor, multiMetaCondition -> new ObjectArrayList<>(multiMetaCondition.conditions()));
	}

}
