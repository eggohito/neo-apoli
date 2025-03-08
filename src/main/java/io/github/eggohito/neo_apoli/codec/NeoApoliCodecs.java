package io.github.eggohito.neo_apoli.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;

import java.util.Set;

public class NeoApoliCodecs {

	public static final Codec<NbtElement> NBT_ELEMENT = Codec.PASSTHROUGH.xmap(
		dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(),
		nbtElement -> new Dynamic<>(NbtOps.INSTANCE, nbtElement)
	);

	public static final Codec<Set<Identifier>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = Identifier.CODEC.listOf(1, Integer.MAX_VALUE).xmap(
		ObjectOpenHashSet::new,
		ObjectArrayList::new
	);

}
