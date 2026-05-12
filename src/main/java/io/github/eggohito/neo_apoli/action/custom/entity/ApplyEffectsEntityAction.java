package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public record ApplyEffectsEntityAction(List<MobEffectInstance> effects) implements EntityAction {

	public static final MapCodec<ApplyEffectsEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(ExtraCodecs.nonEmptyList(MobEffectInstance.CODEC.listOf()).fieldOf("effects").forGetter(ApplyEffectsEntityAction::effects))
		.apply(instance, ApplyEffectsEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ApplyEffectsEntityAction> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.collection(ObjectArrayList::new, MobEffectInstance.STREAM_CODEC), ApplyEffectsEntityAction::effects,
		ApplyEffectsEntityAction::new
	);

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.APPLY_EFFECTS;
	}

	@Override
	public void execute(Context context) {

		if (context.getNullable(NeoApoliContextParams.THIS_ENTITY) instanceof LivingEntity livingEntity && !context.level().isClientSide()) {
			effects().forEach(livingEntity::addEffect);
		}

	}

}
