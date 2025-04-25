package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.function.Function;

public interface MultiMetaCondition<C extends Condition<T>, T extends ConditionType<?>> extends Condition<T> {

	List<C> conditions();

	@Override
	default void validate(ErrorReporter reporter) {

		for (int i = 0; i < conditions().size(); i++) {
			conditions().get(i).validate(reporter.makeChild("conditions[" + i + "]"));
		}

	}

	static <C extends Condition<?>, M extends MultiMetaCondition<C, ?>> MapCodec<M> createCodec(Codec<C> elementCodec, Function<List<C>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.listOf().fieldOf("conditions").forGetter(MultiMetaCondition::conditions)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, C extends Condition<?>, M extends MultiMetaCondition<C, ?>> PacketCodec<B, M> createPacketCodec(PacketCodec<B, C> elementCodec, Function<List<C>, M> constructor) {
		return PacketCodecs.collection(ObjectArrayList::new, elementCodec).xmap(constructor, multiMetaCondition -> new ObjectArrayList<>(multiMetaCondition.conditions()));
	}

}
