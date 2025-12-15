package io.github.eggohito.neo_apoli.action.custom.entity;

import com.google.common.collect.Streams;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

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

	public static final StreamCodec<RegistryFriendlyByteBuf, AddExperienceEntityAction> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), AddExperienceEntityAction::points,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), AddExperienceEntityAction::levels,
		AddExperienceEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.ADD_EXPERIENCE;
	}

	@Override
	public void execute(Context context) {

		if (!(context.nullable(NeoApoliContextKeys.THIS_ENTITY) instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Context pointsContext = context.forChild(".points");
		this.points()
			.map(provider -> provider.nextInt(pointsContext))
			.filter(Predicate.not(points -> pointsContext.hasErrors()))
			.ifPresent(serverPlayer::giveExperiencePoints);

		Context levelsContext = context.forChild(".levels");
		this.levels()
			.map(provider -> provider.nextInt(levelsContext))
			.filter(Predicate.not(levels -> levelsContext.hasErrors()))
			.ifPresent(serverPlayer::giveExperienceLevels);

	}

	@Override
	public void validate(ProblemReporter reporter) {

		EntityAction.super.validate(reporter);

		points().ifPresent(points -> points.validate(reporter.forChild(".points")));
		levels().ifPresent(levels -> levels.validate(reporter.forChild(".levels")));

	}

}
