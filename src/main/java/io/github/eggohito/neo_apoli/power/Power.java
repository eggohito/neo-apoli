package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Power {

    public static final Codec<Power> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PowerType.DISPATCH_CODEC.forGetter(Power::type),
        Codecs.createStrictOptionalFieldCodec(TextCodecs.STRINGIFIED_CODEC, "name").forGetter(Power::name),
        Codecs.createStrictOptionalFieldCodec(TextCodecs.STRINGIFIED_CODEC, "description").forGetter(Power::description),
        Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Power::hidden)
    ).apply(instance, Power::new));

    private final PowerType type;

    private Optional<Text> name;
    private Optional<Text> description;

    private final boolean hidden;

    public Power(PowerType type, Optional<Text> name, Optional<Text> description, boolean hidden) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.hidden = hidden;
    }

    public Power init(LivingEntity holder) {

        Identifier id = holder.getWorld().getRegistryManager().get(NeoApoliRegistryKeys.POWERS).getId(this);
        if (id != null) {

            String namespace = id.getNamespace();
            String path = id.getPath();

            this.name = name.or(() -> Optional.of(Text.translatable("power." + namespace + "." + path + ".name")));
            this.description = description.or(() -> Optional.of(Text.translatable("power." + namespace + "." + path + ".description")));

        }

        this.type.init(holder, this);
        return this;

    }

    public PowerType type() {
        return type;
    }

    public Optional<Text> name() {
        return name;
    }

    public Optional<Text> description() {
        return description;
    }

    public boolean hidden() {
        return hidden;
    }

}
