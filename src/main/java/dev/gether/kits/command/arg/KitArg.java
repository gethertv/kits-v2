package dev.gether.kits.command.arg;

import dev.gether.kits.KitsPlugin;
import dev.gether.kits.core.Kit;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class KitArg extends ArgumentResolver<CommandSender, Kit> {

    private final KitsPlugin alchemyPlugin;

    public KitArg(KitsPlugin alchemyPlugin) {
        this.alchemyPlugin = alchemyPlugin;
    }

    @Override
    protected ParseResult<Kit> parse(Invocation<CommandSender> invocation, Argument<Kit> context, String argument) {
        Optional<Kit> caseByName = this.alchemyPlugin.getFileManager().findKit(argument);
        return caseByName.map(ParseResult::success).orElse(ParseResult.failure("&cThe specified kit does not exists!"));
    }
    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Kit> argument, SuggestionContext context) {
        return this.alchemyPlugin.getFileManager().getAllNameSuggestionOfKit();
    }

}
