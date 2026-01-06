package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public record ApplyEffectsEntityAction(List<MobEffectInstance> effects) implements EntityAction {

	public static final MapCodec<ApplyEffectsEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(ExtraCodecs.nonEmptyList(MobEffectInstance.CODEC.listOf()).fieldOf("effects").forGetter(ApplyEffectsEntityAction::effects))
		.apply(instance, ApplyEffectsEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ApplyEffectsEntityAction> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.collection(ObjectArrayList::new, MobEffectInstance.STREAM_CODEC), ApplyEffectsEntityAction::effects,
		ApplyEffectsEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.APPLY_EFFECTS;
	}

	@Override
	public void execute(Context context) {

		if (context.nullable(NeoApoliContextKeys.THIS_ENTITY) instanceof LivingEntity livingEntity && !context.getLevel().isClientSide()) {
			effects().forEach(livingEntity::addEffect);
		}

	}

}
