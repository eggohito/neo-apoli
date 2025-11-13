package io.github.eggohito.neo_apoli.action.custom.entity;

import com.google.common.collect.Streams;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public record AddExperienceEntityAction(Optional<NumberProvider> points, Optional<NumberProvider> levels) implements EntityAction {

	private static final MapCodec<Optional<NumberProvider>> POINTS_CODEC = NumberProvider.CODEC.optionalFieldOf("points");
	private static final MapCodec<Optional<NumberProvider>> LEVELS_CODEC = NumberProvider.CODEC.optionalFieldOf("levels");

	public static final MapCodec<AddExperienceEntityAction> CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Streams.concat(POINTS_CODEC.keys(ops), LEVELS_CODEC.keys(ops));
		}

		@Override
		public <T> DataResult<AddExperienceEntityAction> decode(DynamicOps<T> ops, MapLike<T> input) {
			return LEVELS_CODEC.decode(ops, input)
				.flatMap(levels -> POINTS_CODEC.decode(ops, input)
					.flatMap(points -> validate(points, levels, input)));
		}

		@Override
		public <T> RecordBuilder<T> encode(AddExperienceEntityAction input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			return LEVELS_CODEC
				.encode(input.levels(), ops, POINTS_CODEC
					.encode(input.points(), ops, prefix));
		}

		private <I> DataResult<AddExperienceEntityAction> validate(Optional<NumberProvider> points, Optional<NumberProvider> levels, MapLike<I> input) {

			if (points.isEmpty() && levels.isEmpty()) {
				return DataResult.error(() -> "Any of 'points' or 'levels' keys must be present in input: " + input);
			}

			else {
				return DataResult.success(new AddExperienceEntityAction(points, levels));
			}

		}

	};

	public static final PacketCodec<RegistryByteBuf, AddExperienceEntityAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(NumberProvider.PACKET_CODEC), AddExperienceEntityAction::points,
		PacketCodecs.optional(NumberProvider.PACKET_CODEC), AddExperienceEntityAction::levels,
		AddExperienceEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.ADD_EXPERIENCE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.nullable(ContextParameters.THIS_ENTITY) instanceof ServerPlayerEntity serverPlayer)) {
			return;
		}

		Context pointsContext = context.makeChild(".points");
		this.points()
			.map(provider -> provider.nextInt(pointsContext))
			.filter(Predicate.not(points -> pointsContext.hasErrors()))
			.ifPresent(serverPlayer::addExperience);

		Context levelsContext = context.makeChild(".levels");
		this.levels()
			.map(provider -> provider.nextInt(levelsContext))
			.filter(Predicate.not(levels -> levelsContext.hasErrors()))
			.ifPresent(serverPlayer::addExperienceLevels);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		EntityAction.super.validate(reporter);

		points().ifPresent(points -> points.validate(reporter.makeChild(".points")));
		levels().ifPresent(levels -> levels.validate(reporter.makeChild(".levels")));

	}

}
