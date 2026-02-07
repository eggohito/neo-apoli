package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public enum EqualsBiEntityCondition implements BiEntityCondition {

	INSTANCE;

	public static final MapCodec<EqualsBiEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, EqualsBiEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.EQUALS;
	}

	@Override
	public boolean test(Context context) {

		Entity actor = context.getNullable(NeoApoliContextParams.ACTOR_ENTITY);
		Entity target = context.getNullable(NeoApoliContextParams.TARGET_ENTITY);

		return Objects.equals(actor, target);

	}

}
