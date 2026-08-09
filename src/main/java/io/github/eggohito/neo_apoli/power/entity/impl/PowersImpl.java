package io.github.eggohito.neo_apoli.power.entity.impl;

import io.github.eggohito.neo_apoli.attachment.entity.PowersAttachment;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class PowersImpl extends AbstractPowers {

	PowersImpl(Entity holder, PowersAttachment attachment) {
		super(holder, attachment.instances(), attachment.sources());
	}

	public static Powers of(@NotNull Entity holder) {
		return new PowersImpl(holder, holder.getAttachedOrCreate(NeoApoliEntityAttachments.POWERS));
	}

}
