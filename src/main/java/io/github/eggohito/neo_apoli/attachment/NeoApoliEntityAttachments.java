package io.github.eggohito.neo_apoli.attachment;

import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.api.power.PowersAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("UnstableApiUsage")
public final class NeoApoliEntityAttachments {

	/**
	 *  It's <b>highly recommended</b> to use any of the helper methods in {@link Powers} instead of using this data
	 *  attachment type directly.
	 */
	@ApiStatus.Internal
	public static final AttachmentType<PowersAttachment> POWERS = AttachmentRegistry.create(Powers.ID, builder -> builder
		.persistent(PowersAttachment.CODEC)
		.syncWith(PowersAttachment.STREAM_CODEC, AttachmentSyncPredicate.all())
		.initializer(PowersAttachment::new)
		.copyOnDeath());

	public static void registerAll() {

	}

}
