package io.github.eggohito.neo_apoli.mixin.impl.event.modifier_order_callback;

import io.github.eggohito.neo_apoli.event.ModifierOrderCallback;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {

	@Shadow
	public abstract ResourceKey<? extends Registry<T>> key();

	@Inject(method = "freeze", at = @At(value = "FIELD", target = "Lnet/minecraft/core/MappedRegistry;frozen:Z", opcode = Opcodes.PUTFIELD))
	void orderModifiersOnFreeze(CallbackInfoReturnable<Registry<T>> cir) {

		if (!this.key().equals(NeoApoliRegistryKeys.MODIFIER_TYPE)) {
			return;
		}

		try (Modifier.Orderer orderer = Modifier.Orderer.INSTANCE) {
			ModifierOrderCallback.EVENT.invoker().order(orderer);
		}

	}

}
