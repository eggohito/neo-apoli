package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.serialization.SerializableType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.ApiStatus;

public abstract class PowerType extends SerializableType<PowerType> {

    public static final MapCodec<PowerType> DISPATCH_CODEC = PowerType
        .registryCodec(NeoApoliRegistries.POWER_TYPES, PowerTypes.ALIASES)
        .dispatchMap(SerializableType::serializer, Serializer::codec);

    private LivingEntity holder;
    private Power power;

    @ApiStatus.Internal
    final void init(LivingEntity holder, Power power) {
        this.holder = holder;
        this.power = power;
    }

    public LivingEntity holder() {
        return holder;
    }

    public Power power() {
        return power;
    }

    public void commonTick() {

    }

    public void serverTick() {

    }

    public void clientTick() {

    }

    public NbtElement toNbt() {
        return new NbtCompound();
    }

    public void fromNbt(NbtElement nbt) {

    }

    public boolean shouldTick() {
        return false;
    }

    public boolean shouldTickWhenInActive() {
        return false;
    }

}
