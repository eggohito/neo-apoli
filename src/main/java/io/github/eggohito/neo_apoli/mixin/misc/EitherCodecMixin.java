package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.EitherCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(EitherCodec.class)
public abstract class EitherCodecMixin<F, S> {

	@Redirect(method = "decode", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;error(Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;"), remap = false)
	private <T> DataResult<Pair<Either<F, S>, T>> mapErrorsInAReadableWay(Supplier<String> message, @Local(ordinal = 0) DataResult<Pair<Either<F, S>, T>> first, @Local(ordinal = 1) DataResult<Pair<Either<F, S>, T>> second) {
		return DataResult.error(() ->	"\n\t - " + first.error().orElseThrow().message() +
										"\n\t - " + second.error().orElseThrow().message());
	}

}
