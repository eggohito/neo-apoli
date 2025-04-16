package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.entity.Entity;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsSneakingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsSneakingEntityCondition> CODEC = MapCodec.unit(IsSneakingEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsSneakingEntityCondition> PACKET_CODEC = PacketCodec.unit(new IsSneakingEntityCondition());

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SNEAKING;
	}

	@Override
	public boolean test(ErrorReporter reporter, EntityConditionContext context) {
		return context.entity()
			.map(Entity::isSneaking)
			.orElse(false);
	}

}
