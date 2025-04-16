package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.function.Function;

public interface SequenceMetaAction<AX extends ActionContext<?>, AA extends Action<AX, AT>, AT extends ActionType<?>> extends Action<AX, AT> {

	List<AA> actions();

	@Override
	default void execute(ErrorReporter reporter, AX context) {

		for (AA action : actions()) {
			action.execute(reporter, context);
		}

	}

	@Override
	default void validate(ErrorReporter reporter) {

		for (int i = 0; i < actions().size(); i++) {
			actions().get(i).validate(reporter.makeChild("actions[" + i + "]"));
		}

	}

	static <AA extends Action<?, ?>, MA extends SequenceMetaAction<?, AA, ?>> MapCodec<MA> createCodec(Codec<AA> elementCodec, Function<List<AA>, MA> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.listOf().fieldOf("actions").forGetter(SequenceMetaAction::actions)
		).apply(instance, constructor));
	}

	static <BB extends ByteBuf, AA extends Action<?, ?>, MA extends SequenceMetaAction<?, AA, ?>> PacketCodec<BB, MA> createPacketCodec(PacketCodec<BB, AA> elementCodec, Function<List<AA>, MA> constructor) {
		return PacketCodecs.collection(ObjectArrayList::new, elementCodec).xmap(constructor, sequenceMetaAction -> new ObjectArrayList<>(sequenceMetaAction.actions()));
	}

}
