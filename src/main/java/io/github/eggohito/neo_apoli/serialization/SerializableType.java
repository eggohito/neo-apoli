package io.github.eggohito.neo_apoli.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public abstract class SerializableType<T> {

    public abstract Serializer<? extends T> serializer();

    public static <T extends SerializableType.Serializer<?>> Codec<T> registryCodec(Registry<T> registry, IdentifierAlias aliases) {

        RegistryKey<? extends Registry<T>> registryKey = registry.getKey();

        Codec<T> toFromIdCodec = Identifier.CODEC.flatXmap(
            id -> registry.getOrEmpty(id)
                .or(() -> registry.getOrEmpty(aliases.resolveAliasUntil(id, registry::containsId)))
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Type \"" + id + "\" is not registered in registry \"" + registryKey.getValue() + "\"")),
            value -> registry.getKey(value)
                .map(RegistryKey::getValue)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Element \"" + value + "\" is not registered in registry \"" + registryKey.getValue() + "\""))
        );
        Codec<T> rawIdCodec = Codecs.rawIdChecked(
            value -> registry.getKey(value).isPresent() ? registry.getRawId(value) : -1,
            registry::get,
            -1
        );

        return Codecs.withLifecycle(
            Codecs.orCompressed(toFromIdCodec, rawIdCodec),
            registry::getEntryLifecycle,
            registry::getEntryLifecycle
        );

    }

    public record Factory<T>(Identifier id, Serializer<? extends T> type) {

    }

    public interface Serializer<T> {
        Codec<? extends T> codec();
    }

    public interface Supplier<T> {
        Factory<? extends T> getFactory();
    }

}
