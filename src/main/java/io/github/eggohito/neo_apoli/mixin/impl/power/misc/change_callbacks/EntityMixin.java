package io.github.eggohito.neo_apoli.mixin.impl.power.misc.change_callbacks;

import io.github.eggohito.neo_apoli.attachment.entity.PowersAttachment;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	void registerChangedCallbackEvent(EntityType<?> entityType, Level level, CallbackInfo ci) {
		Entity thisAsEntity = (Entity) (Object) this;
		//noinspection UnstableApiUsage
		thisAsEntity.onAttachedSet(NeoApoliEntityAttachments.POWERS).register(Powers.ID, (oldValue, newValue) -> PowersAttachment.onChanged(thisAsEntity, oldValue, newValue));
	}

}
