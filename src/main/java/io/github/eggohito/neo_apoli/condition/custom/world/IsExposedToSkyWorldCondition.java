package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextHelper;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.ListIterator;

public record IsExposedToSkyWorldCondition(List<Vec3Provider> positions) implements WorldCondition {

	public static final MapCodec<IsExposedToSkyWorldCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Vec3Provider.CODEC.listOf().fieldOf("positions").forGetter(IsExposedToSkyWorldCondition::positions))
		.apply(instance, IsExposedToSkyWorldCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsExposedToSkyWorldCondition> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.collection(ObjectArrayList::new, Vec3Provider.STREAM_CODEC), IsExposedToSkyWorldCondition::positions,
		IsExposedToSkyWorldCondition::new
	);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.IS_EXPOSED_TO_SKY;
	}

	@Override
	public boolean test(Context context) {

		Level level = context.level();
		ListIterator<Vec3Provider> listIterator = positions().listIterator();

		while (listIterator.hasNext()) {

			Context positionContext = context.forChild(".positions[" + listIterator.nextIndex() + "]");
			BlockPos blockPos = BlockPos.containing(listIterator.next().nextVec3(positionContext));

			if (!positionContext.hasErrors() && level.hasChunkAt(blockPos) && level.canSeeSky(blockPos)) {
				return true;
			}

		}

		return false;

	}

	@Override
	public void validate(Context.Validator validator) {
		WorldCondition.super.validate(validator);
		ContextHelper.validateAll(positions(), validator, index -> ".positions[" + index + "]");
	}

}
