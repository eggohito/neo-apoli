package io.github.eggohito.neo_apoli.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.modifier.custom.AddModifier;
import io.github.eggohito.neo_apoli.modifier.custom.MultiplyAdditiveModifier;
import io.github.eggohito.neo_apoli.modifier.custom.MultiplyMultiplicativeModifier;
import io.github.eggohito.neo_apoli.provider.custom.number.floats.ConstantFloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.IntConsumer;

public interface Modifier extends ContextUser, Comparable<Modifier> {

	Codec<Modifier> CODEC = Type.CODEC.dispatch(Modifier::getType, Type::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = Type.STREAM_CODEC.dispatch(Modifier::getType, Type::streamCodec);

	@Override
	default int compareTo(@NotNull Modifier that) {

		if (this.phase() == that.phase()) {
			return Orderer.INSTANCE.compare(this, that);
		}

		else {
			return this.phase().compareTo(that.phase());
		}

	}

	Type<?> getType();

	Phase phase();

	List<Modifier> modifiers();

	List<Operation> collectNestedOps(Operation parent);

	double apply(Context context, double base, double total);

	default Operation asOperation(Context context) {
		return new Operation(this, context);
	}

	static <M extends Modifier> Products.P1<RecordCodecBuilder.Mu<M>, Phase> addPhaseField(RecordCodecBuilder.Instance<M> instance) {
		return instance.group(Phase.CODEC.fieldOf("phase").forGetter(Modifier::phase));
	}

	static Modifier fromVanilla(AttributeModifier vanillaModifier) {

		AttributeModifier.Operation operation = vanillaModifier.operation();
		float amount = (float) vanillaModifier.amount();

		return switch (operation) {
			case ADD_VALUE ->
				new AddModifier(Modifier.Phase.BASE, new ConstantFloatProvider(amount), List.of());
			case ADD_MULTIPLIED_BASE ->
				new MultiplyAdditiveModifier(Modifier.Phase.BASE, new ConstantFloatProvider(amount), List.of());
			case ADD_MULTIPLIED_TOTAL ->
				new MultiplyMultiplicativeModifier(Modifier.Phase.TOTAL, new ConstantFloatProvider(amount), List.of());
		};

	}

	static double applyAll(List<Operation> operations, double baseValue) {

		if (operations.isEmpty()) {
			return baseValue;
		}

		List<Operation> sorted = new ObjectArrayList<>(operations);
		sorted.sort(Operation::compareTo);

		double currentBase = baseValue;
		double currentTotal = baseValue;

		Phase previousPhase = null;
		for (var operation : sorted) {

			Modifier modifier = operation.modifier();
			Context context = operation.context();

			Phase currentPhase = modifier.phase();

			if (currentPhase != previousPhase) {
				previousPhase = currentPhase;
				currentBase = currentTotal;
			}

			try {

				if (context.visitor().push(modifier)) {

					List<Operation> nestedOps = modifier.collectNestedOps(operation);
					double value = applyAll(nestedOps, modifier.apply(context, currentBase, currentTotal));

					if (!context.hasProblems()) {
						currentTotal = value;
					}

				}

			}

			finally {
				context.visitor().pop(modifier);
			}

		}

		return currentTotal;

	}

	static Operation operation(Modifier modifier, Context context) {
		return new Operation(modifier, context);
	}

	record Operation(Modifier modifier, Context context) implements Comparable<Operation> {

		@Override
		public int compareTo(@NotNull Modifier.Operation that) {
			return this.modifier().compareTo(that.modifier());
		}

	}

	enum Phase {

		BASE,
		TOTAL;

		public static final Codec<Phase> CODEC = CodecUtil.enumType(Phase.class);
		public static final StreamCodec<ByteBuf, Phase> STREAM_CODEC = StreamCodecUtil.enumType(Phase.class);

	}

	record Type<M extends Modifier>(MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> streamCodec) {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.MODIFIER_TYPE);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.MODIFIER_TYPE);

		@Override
		public @NotNull String toString() {
			return Util.getRegisteredName(NeoApoliRegistries.MODIFIER_TYPE, this);
		}

	}

	final class Orderer implements AutoCloseable {

		public static final Orderer INSTANCE = new Orderer();

		private final List<Modifier.Type<?>> order = new ObjectArrayList<>();
		private boolean frozen = false;

		private Orderer() {

		}

		@Override
		public void close() {

			List<Modifier.Type<?>> unordered = new ObjectArrayList<>();

			for (var registered : NeoApoliRegistries.MODIFIER_TYPE) {

				if (!order.contains(registered)) {
					unordered.add(registered);
				}

			}

			if (!unordered.isEmpty()) {
				throw new IllegalStateException("The following modifier types weren't added to the orderer: " + unordered);
			}

			this.frozen = true;

		}

		public Orderer addBefore(Modifier.Type<?> target, Modifier.Type<?> type) {

			validateChange();
			this.findIndex(target, index -> {

				order.remove(type);

				if (index <= 0) {
					order.addFirst(type);
				}

				else {
					order.add(index - 1, type);
				}

			});

			return this;

		}

		public Orderer addAfter(Modifier.Type<?> target, Modifier.Type<?> type) {

			validateChange();
			this.findIndex(target, index -> {

				order.remove(type);

				if (index >= order.size()) {
					order.addLast(type);
				}

				else {
					order.add(index + 1, type);
				}

			});

			return this;

		}

		public Orderer addFirst(Modifier.Type<?> type) {

			validateChange();

			order.remove(type);
			order.addFirst(type);

			return this;

		}

		public Orderer addLast(Modifier.Type<?> type) {

			validateChange();

			order.remove(type);
			order.addLast(type);

			return this;

		}

		public int compare(Modifier first, Modifier second) {

			int firstIndex = this.getIndex(first.getType());
			int secondIndex = this.getIndex(second.getType());

			return Integer.compare(firstIndex, secondIndex);

		}

		private int getIndex(Modifier.Type<?> type) {

			MutableInt index = new MutableInt(0);
			this.findIndex(type, index::setValue);

			return index.intValue();

		}

		private void findIndex(Modifier.Type<?> target, IntConsumer visitor) {

			for (int i = 0; i < order.size(); i++) {

				var type = order.get(i);

				if (type == target) {
					visitor.accept(i);
					break;
				}

			}

		}

		private void validateChange() {

			if (this.frozen) {
				throw new IllegalStateException("Modifier orderer is already frozen!");
			}

		}

	}

}
