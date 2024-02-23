package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.power.PowerType;
import io.github.eggohito.neo_apoli.power.SubPower;
import net.minecraft.util.Identifier;

import java.util.List;

public class MultiplePowerType extends PowerType {

    public static final Codec<MultiplePowerType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        SubPower.CODEC.listOf().fieldOf("subpowers").forGetter(MultiplePowerType::subPowers)
    ).apply(instance, MultiplePowerType::new));

    private final List<SubPower> subPowers;

    public MultiplePowerType(List<SubPower> subPowers) {
        this.subPowers = subPowers;
    }

    public List<SubPower> subPowers() {
        return subPowers;
    }

    @Override
    public Serializer<MultiplePowerType> serializer() {
        return () -> CODEC;
    }

    public static Factory<MultiplePowerType> getFactory() {
        return new Factory<>(Identifier.of("neo-apoli", "multiple"), () -> CODEC);
    }

}
