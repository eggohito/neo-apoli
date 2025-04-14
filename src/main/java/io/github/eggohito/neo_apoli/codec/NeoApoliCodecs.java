package io.github.eggohito.neo_apoli.codec;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.util.HandProperty;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.Set;

public class NeoApoliCodecs {

	public static final Codec<Set<Identifier>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = Identifier.CODEC.listOf(1, Integer.MAX_VALUE).xmap(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Codec<Hand> HAND = HandProperty.CODEC.xmap(HandProperty::get, HandProperty::fromHand);

}
