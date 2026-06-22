package skdocs.crebs.betterModelSk.elements.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import skdocs.crebs.betterModelSk.api.BetterModelEntity;

public final class BetterModelEntityType {

    private BetterModelEntityType() {
    }

    public static void register() {
        if (Classes.getExactClassInfo(BetterModelEntity.class) != null) return;

        Classes.registerClass(new ClassInfo<>(BetterModelEntity.class, "bettermodelentity")
                .user("better ?model ?entit(y|ies)")
                .name("Better Model Entity")
                .description("A model + carrier-mob template created with 'a new better model entity', "
                        + "which can then be spawned, animated and removed.")
                .since("1.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(BetterModelEntity o, int flags) {
                        return o.toString();
                    }

                    @Override
                    public String toVariableNameString(BetterModelEntity o) {
                        return "bettermodelentity:" + System.identityHashCode(o);
                    }
                }));
    }
}