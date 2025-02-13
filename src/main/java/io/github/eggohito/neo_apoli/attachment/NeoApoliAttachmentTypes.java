package io.github.eggohito.neo_apoli.attachment;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class NeoApoliAttachmentTypes {

	public static final AttachmentType<PowerHolderAttachment> POWER_HOLDER = AttachmentRegistry.create(NeoApoli.id("powers"), builder -> builder
		.initializer(PowerHolderAttachment::new)
		.persistent(PowerHolderAttachment.CODEC)
		.syncWith(PowerHolderAttachment.PACKET_CODEC, AttachmentSyncPredicate.all())
		.copyOnDeath()
	);

	public static void registerAll() {

	}

}
