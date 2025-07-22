package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class AddExperienceEntityAction extends EntityAction {

	public static final MapCodec<AddExperienceEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("points").forGetter(AddExperienceEntityAction::points),
		NumberProvider.CODEC.optionalFieldOf("levels").forGetter(AddExperienceEntityAction::levels)
	).apply(instance, AddExperienceEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, AddExperienceEntityAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(NumberProvider.PACKET_CODEC), AddExperienceEntityAction::points,
		PacketCodecs.optional(NumberProvider.PACKET_CODEC), AddExperienceEntityAction::levels,
		AddExperienceEntityAction::new
	);

	private final Optional<NumberProvider> points;
	private final Optional<NumberProvider> levels;

	public AddExperienceEntityAction(Optional<NumberProvider> points, Optional<NumberProvider> levels) {
		this.points = points;
		this.levels = levels;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.ADD_EXPERIENCE;
	}

	@Override
	protected void impl(Context context) {

		if (!(context.required(ContextParameters.ENTITY) instanceof ServerPlayerEntity serverPlayer)) {
			return;
		}

		Context pointsContext = context.makeChild(".points");
		Context levelsContext = context.makeChild(".levels");

		this.points()
			.map(pointsProvider -> pointsProvider.nextInt(pointsContext))
			.filter(points -> !pointsContext.hasErrors())
			.ifPresent(serverPlayer::addExperience);

		this.levels()
			.map(levelsProvider -> levelsProvider.nextInt(levelsContext))
			.filter(levels -> !levelsContext.hasErrors())
			.ifPresent(serverPlayer::addExperienceLevels);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		points().ifPresent(pointsProvider -> pointsProvider.validate(reporter.makeChild(".points")));
		levels().ifPresent(levelsProvider -> levelsProvider.validate(reporter.makeChild(".levels")));

	}

}
