package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class IdentifierAlias {

    public static final Function<Identifier, RuntimeException> NO_ALIAS_EXCEPTION = id -> new RuntimeException("Tried resolving a non-existing alias for identifier \"" + id + "\"");

    private final Map<Identifier, Identifier> identifierAliases;
    private final Map<String, String> namespaceAliases;
    private final Map<String, String> pathAliases;

    public IdentifierAlias() {
        this.identifierAliases = new HashMap<>();
        this.namespaceAliases = new HashMap<>();
        this.pathAliases = new HashMap<>();
    }

    public void addAlias(Identifier from, Identifier to) {

        if (identifierAliases.containsKey(from)) {
            NeoApoli.LOGGER.error("Identifier \"{}\" already has a defined alias!", from);
        }

        else {
            identifierAliases.put(from, to);
        }

    }

    public void addNamespaceAlias(String from, String to) {

        if (namespaceAliases.containsKey(from)) {
            NeoApoli.LOGGER.error("Namespace \"{}\" already has a defined alias!", from);
        }

        else {
            namespaceAliases.put(from, to);
        }

    }

    public void addPathAlias(String from, String to) {

        if (pathAliases.containsKey(from)) {
            NeoApoli.LOGGER.error("Path \"{}\" already has a defined alias!", from);
        }

        else {
            pathAliases.put(from, to);
        }

    }

    public boolean hasAlias(Identifier id) {
        return identifierAliases.containsKey(id)
            || hasNamespaceAlias(id)
            || hasPathAlias(id);
    }

    public boolean hasIdentifierAlias(Identifier id) {
        return identifierAliases.containsKey(id);
    }

    public boolean hasNamespaceAlias(Identifier id) {
        return namespaceAliases.containsKey(id.getNamespace());
    }

    public boolean hasPathAlias(Identifier id) {
        return pathAliases.containsKey(id.getPath());
    }

    public Identifier resolveAliasUntil(Identifier id, Predicate<Identifier> resolvedPredicate, Order... orders) {

        Identifier aliasedId = id;
        orders = orders.length == 0 ? Order.values() : orders;

        for (Order order : orders) {

            aliasedId = order.apply(this, id);

            if (resolvedPredicate.test(aliasedId)) {
                break;
            }

        }

        return aliasedId;

    }

    public Identifier resolveIdentifierAlias(Identifier id) {

        if (!this.hasIdentifierAlias(id)) {
            throw NO_ALIAS_EXCEPTION.apply(id);
        }

        return identifierAliases.get(id);

    }

    public Identifier resolveNamespaceAlias(Identifier id) {

        if (!this.hasNamespaceAlias(id)) {
            throw NO_ALIAS_EXCEPTION.apply(id);
        }

        String aliasedNamespace = namespaceAliases.get(id.getNamespace());
        String path = id.getPath();

        return Identifier.of(aliasedNamespace, path);

    }

    public Identifier resolvePathAlias(Identifier id) {

        if (!this.hasPathAlias(id)) {
            throw NO_ALIAS_EXCEPTION.apply(id);
        }

        String namespace = id.getNamespace();
        String aliasedPath = pathAliases.get(id.getPath());

        return Identifier.of(namespace, aliasedPath);

    }

    public static abstract class Order implements BiFunction<IdentifierAlias, Identifier, Identifier>, Comparable<Order> {

        private static final Set<Order> VALUES = new HashSet<>();

        public static final Order NAMESPACE_AND_PATH;
        public static final Order IDENTIFIER;
        public static final Order NAMESPACE;
        public static final Order PATH;

        public static Order register(Order order) {
            VALUES.add(order);
            return order;
        }

        private final int ordinal;
        public Order(int ordinal) {
            this.ordinal = ordinal;
        }

        @Override
        public int compareTo(@NotNull IdentifierAlias.Order o) {
            return Integer.compare(this.ordinal(), o.ordinal());
        }

        public int ordinal() {
            return ordinal;
        }

        public static Order[] values() {
            return VALUES
                .stream()
                .sorted()
                .toArray(Order[]::new);
        }

        static {

            IDENTIFIER = register(new Order(0) {

                @Override
                public Identifier apply(IdentifierAlias aliases, Identifier id) {
                    return aliases.hasIdentifierAlias(id) ? aliases.resolveIdentifierAlias(id) : id;
                }

            });
            NAMESPACE = register(new Order(1) {

                @Override
                public Identifier apply(IdentifierAlias aliases, Identifier id) {
                    return aliases.hasNamespaceAlias(id) ? aliases.resolveNamespaceAlias(id) : id;
                }

            });
            PATH = register(new Order(2) {

                @Override
                public Identifier apply(IdentifierAlias aliases, Identifier id) {
                    return aliases.hasPathAlias(id) ? aliases.resolvePathAlias(id) : id;
                }

            });
            NAMESPACE_AND_PATH = register(new Order(3) {

                @Override
                public Identifier apply(IdentifierAlias aliases, Identifier id) {

                    String aliasedNamespace = aliases.hasNamespaceAlias(id)
                        ? aliases.resolveNamespaceAlias(id).getNamespace()
                        : id.getNamespace();
                    String aliasedPath = aliases.hasPathAlias(id)
                        ? aliases.resolvePathAlias(id).getPath()
                        : id.getPath();

                    return Identifier.of(aliasedNamespace, aliasedPath);

                }

            });


        }

    }

}
