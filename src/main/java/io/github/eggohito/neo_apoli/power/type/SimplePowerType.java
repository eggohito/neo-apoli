package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.PowerType;
import net.minecraft.util.Identifier;

public class SimplePowerType extends PowerType {

    public static final Codec<SimplePowerType> CODEC = MapCodec
        .of(Encoder.empty(), Decoder.unit(SimplePowerType::new))
        .codec();

    @Override
    public Serializer<SimplePowerType> serializer() {
        return () -> CODEC;
    }

    public static Factory<SimplePowerType> getFactory() {
        return new Factory<>(Identifier.of("neo-apoli", "simple"), () -> CODEC);
    }

}
