package io.github.eggohito.neo_apoli.keybinding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record KeyBindingReference(String id, boolean continuous) {

	public static final MapCodec<KeyBindingReference> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("id").forGetter(KeyBindingReference::id),
		Codec.BOOL.optionalFieldOf("continuous", false).forGetter(KeyBindingReference::continuous)
	).apply(instance, KeyBindingReference::new));

	public static final Codec<KeyBindingReference> CODEC = new MultiAlternativeCodec<>(MAP_CODEC.codec(), Codec.STRING.xmap(str -> new KeyBindingReference(str, false), KeyBindingReference::id));

	public static final PacketCodec<ByteBuf, KeyBindingReference> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.STRING, KeyBindingReference::id,
		PacketCodecs.BOOLEAN, KeyBindingReference::continuous,
		KeyBindingReference::new
	);

}
