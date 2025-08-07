package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.power.misc.AbstractModifyAttributeLegacyPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.util.AttributeModifier;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class ModifyAttributeLegacyPower extends AbstractModifyAttributeLegacyPower {

	public static final MapCodec<ModifyAttributeLegacyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addAttributeModifyingFields(instance).apply(instance, ModifyAttributeLegacyPower::new));
	public static final PacketCodec<RegistryByteBuf, ModifyAttributeLegacyPower> PACKET_CODEC = createAttributeModifyingPacketCodec((buf, power) -> {}, (buf, properties, modifiers, sendUpdate) -> new ModifyAttributeLegacyPower(properties, modifiers, sendUpdate));

	public ModifyAttributeLegacyPower(Properties properties, List<AttributeModifier> modifiers, BooleanProvider sendUpdate) {
		super(properties, modifiers, sendUpdate);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ATTRIBUTE_LEGACY;
	}

	@Override
	public AbstractModifyAttributeLegacyPower.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends AbstractModifyAttributeLegacyPower.Impl<ModifyAttributeLegacyPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyAttributeLegacyPower power) {
			super(holder, power);
		}

		@Override
		public void onGranted() {

			super.onGranted();

			Context context = this.createGenericContext();
			this.addModifiersPersistently(context);

		}

		//	Re-apply persistent attribute modifiers because they do not persist through respawn
		@Override
		public void onRespawned() {

			super.onRespawned();

			Context context = this.createGenericContext();
			this.addModifiersPersistently(context);

		}

		@Override
		public void onRevoked() {

			super.onRevoked();

			Context context = this.createGenericContext();
			this.removeModifiers(context);

		}

	}

}
