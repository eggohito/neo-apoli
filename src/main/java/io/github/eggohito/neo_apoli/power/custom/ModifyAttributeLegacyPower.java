package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.power.misc.AttributeModifying;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.util.AttributedAttributeModifier;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EqualsAndHashCode
@Getter
public class ModifyAttributeLegacyPower extends AttributeModifying {

	public static final MapCodec<ModifyAttributeLegacyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addAttributeModifyingFields(instance)
		.apply(instance, ModifyAttributeLegacyPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyAttributeLegacyPower> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.ATTRIBUTE_MODIFIERS, AttributeModifying::getModifiers,
		BooleanProvider.STREAM_CODEC, AttributeModifying::getSendUpdate,
		ModifyAttributeLegacyPower::new
	);

	public ModifyAttributeLegacyPower(List<AttributedAttributeModifier> modifiers, BooleanProvider sendUpdate) {
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
