package io.github.eggohito.neo_apoli.condition;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.Validatable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Condition<CTX extends ConditionContext, CT extends ConditionType<?>> implements Predicate<CTX>, Validatable {

	public static final String TYPE_KEY = "type";
	private final boolean inverted;

	public Condition(boolean inverted) {
		this.inverted = inverted;
	}

	@Override
	public boolean test(CTX context) {
		return isInverted() != check(context);
	}

	public final boolean isInverted() {
		return inverted;
	}

	public abstract CT getType();

	protected abstract boolean check(CTX context);

	protected static <C extends Condition<?, ?>> MapCodec<C> createSimpleCodec(Function<Boolean, C> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).apply(instance, constructor));
	}

	protected static <C extends Condition<?, ?>> PacketCodec<RegistryByteBuf, C> createSimplePacketCodec(Function<Boolean, C> constructor) {
		return createCommonPacketCodec((buf, c) -> {}, (buf, inverted) -> constructor.apply(inverted));
	}

	protected static <C extends Condition<?, ?>> Products.P1<RecordCodecBuilder.Mu<C>, Boolean> addCommonFields(RecordCodecBuilder.Instance<C> instance) {
		return instance.group(PrimitiveCodec.BOOL.optionalFieldOf("inverted", false).forGetter(Condition::isInverted));
	}

	protected static <C extends Condition<?, ?>> PacketCodec<RegistryByteBuf, C> createCommonPacketCodec(BiConsumer<RegistryByteBuf, C> encoder, BiFunction<RegistryByteBuf, Boolean, C> decoder) {
		return PacketCodec.ofStatic(
			(buf, value) -> {
				buf.writeBoolean(value.isInverted());
				encoder.accept(buf, value);
			},
			buf -> {
				boolean inverted = buf.readBoolean();
				return decoder.apply(buf, inverted);
			}
		);
	}

}
