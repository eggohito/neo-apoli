package io.github.eggohito.neo_apoli.impl.power;

import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.api.power.PowersAttachment;
import io.github.eggohito.neo_apoli.attachment.NeoApoliEntityAttachments;
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
