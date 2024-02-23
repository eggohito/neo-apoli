package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SubPower extends Power {

    public static final Codec<SubPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("id").forGetter(SubPower::id),
        PowerType.DISPATCH_CODEC.forGetter(Power::type)
    ).apply(instance, SubPower::new));

    private final Identifier id;

    public SubPower(Identifier id, PowerType type) {
        super(type, Optional.empty(), Optional.empty(), true);
        this.id = id;
    }

    public Identifier id() {
        return id;
    }

}
