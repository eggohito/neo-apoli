package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.power.misc.AttributeModifying;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.util.AttributeModifier;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

import java.util.List;

@Getter
public class ModifyAttributeLegacyPower extends AttributeModifying {

	public static final MapCodec<ModifyAttributeLegacyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addAttributeModifyingFields(instance)
		.apply(instance, ModifyAttributeLegacyPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyAttributeLegacyPower> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.ATTRIBUTE_MODIFIERS, AttributeModifying::getModifiers,
		BooleanProvider.PACKET_CODEC, AttributeModifying::getSendUpdate,
		ModifyAttributeLegacyPower::new
	);

	public ModifyAttributeLegacyPower(List<AttributeModifier> modifiers, BooleanProvider sendUpdate) {
		super(modifiers, sendUpdate);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ATTRIBUTE_LEGACY;
	}

	@Override
	public AttributeModifying.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends AttributeModifying.Instance<ModifyAttributeLegacyPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyAttributeLegacyPower power) {
			super(holder, power);
		}

		@Override
		public void onGranted() {

			super.onGranted();

			Context context = this.createHolderContext();
			this.addModifiersPersistently(context);

		}

		//	Re-apply persistent attribute modifiers because they do not persist through respawn
		@Override
		public void onRespawned() {

			super.onRespawned();

			Context context = this.createHolderContext();
			this.addModifiersPersistently(context);

		}

		@Override
		public void onRevoked() {

			super.onRevoked();

			Context context = this.createHolderContext();
			this.removeModifiers(context);

		}

	}

}
