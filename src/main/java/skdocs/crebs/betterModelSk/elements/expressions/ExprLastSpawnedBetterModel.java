package skdocs.crebs.betterModelSk.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import skdocs.crebs.betterModelSk.api.BetterModelEntity;
import skdocs.crebs.betterModelSk.elements.effects.EffBetterModelSpawn;

@Name("Last Spawned Better Model Entity")
@Description("Returns the most recently spawned better model entity.")
@Example("""
        better model spawn {_mob} at location of player
        better model animate last spawned better model entity with "idle\"""")
@Since("1.0")
public class ExprLastSpawnedBetterModel extends SimpleExpression<BetterModelEntity> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(ExprLastSpawnedBetterModel.class, BetterModelEntity.class)
                        .supplier(ExprLastSpawnedBetterModel::new)
                        .priority(SyntaxInfo.SIMPLE)
                        .addPattern("[the] last spawned better model entity")
                        .build());
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected BetterModelEntity @Nullable [] get(Event event) {
        BetterModelEntity last = EffBetterModelSpawn.lastSpawned();
        return last == null ? new BetterModelEntity[0] : new BetterModelEntity[]{last};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends BetterModelEntity> getReturnType() {
        return BetterModelEntity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "last spawned better model entity";
    }
}
