package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;

import java.util.Objects;

public enum IsOwnerBiEntityCondition implements BiEntityCondition {

	INSTANCE;

	public static final MapCodec<IsOwnerBiEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsOwnerBiEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.IS_OWNER;
	}

	@Override
	public boolean test(Context context) {

		Entity actor = context.getNullable(NeoApoliContextParams.ACTOR_ENTITY);
		Entity target = context.getNullable(NeoApoliContextParams.TARGET_ENTITY);

		return actor != null
			&& this.isOwnedBy(target, actor);

	}

	private boolean isOwnedBy(Entity target, Entity actor) {
		return (target instanceof OwnableEntity tameable && Objects.equals(actor, tameable.getOwner()))
			|| (target instanceof TraceableEntity ownable && Objects.equals(actor, ownable.getOwner()));
	}

}
