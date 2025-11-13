package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(KeyDispatchCodec.class)
public abstract class KeyDispatchCodecMixin {

	@Shadow(remap = false)
	@Final
	private String typeKey;

	@ModifyExpressionValue(method = "keys", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;of([Ljava/lang/Object;)Ljava/util/stream/Stream;"), remap = false)
	private <T> Stream<String> onlyAddCompressedValueKeyWhenNecessary(Stream<String> original, DynamicOps<T> ops) {

		//	If the passed ops compresses maps, return the original stream, which contains the compressed value key
		if (ops.compressMaps()) {
			return original;
		}

		else {
			return Stream.of(this.typeKey);
		}

	}

}
