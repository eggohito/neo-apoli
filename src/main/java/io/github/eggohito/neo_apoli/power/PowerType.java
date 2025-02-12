package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.MapCodec;

public record PowerType<P extends Power>(MapCodec<P> mapCodec) {

}
