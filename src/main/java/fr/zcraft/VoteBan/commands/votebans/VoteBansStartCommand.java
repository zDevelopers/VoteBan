package fr.zcraft.VoteBan.commands.votebans;

import fr.zcraft.VoteBan.Permissions;
import fr.zcraft.VoteBan.votes.Vote;
import fr.zcraft.VoteBan.VoteBan;
import fr.zcraft.VoteBan.votes.CannotRegisterVoteException;
import fr.zcraft.zlib.components.commands.Command;
import fr.zcraft.zlib.components.commands.CommandException;
import fr.zcraft.zlib.components.commands.CommandInfo;
import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zlib.components.rawtext.RawText;
import fr.zcraft.zlib.tools.text.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


@CommandInfo (name = "start", usageParameters = "<player> <reason>")
public final class VoteBansStartCommand extends Command
{
    @Override
    protected void run() throws CommandException
    {
        if (args.length < 2)
            throwInvalidArgument(I.t("A player name and a reason are required."));

        Player target = getPlayerParameter(0);

        if (target.equals(playerSender()))
            error(I.t("You cannot vote-ban yourself"));

        String reason = "";
        for (int i = 1; i < args.length; i++)
            reason += " " + args[i];

        try
        {
            Vote vote = VoteBan.get().registerVote(target, playerSender(), reason.trim());
            vote.start();
        }
        catch (CannotRegisterVoteException e)
        {
            switch (e.getReason())
            {
                case IMMUNIZED_COOLDOWN:
                    error(I.t("{0} is currently immunized due to a previously failed vote.", target.getName()));
                    break;

                case IMMUNIZED_PERMISSION:
                    error(I.t("You cannot vote-ban {0}.", target.getName()));
                    break;

                case ALREADY_RUNNING:
                    Vote vote = VoteBan.get().getVote(target);

                    warning(I.t("A vote against {0} is already running (started by {1}).", target.getName(), vote.getLauncherPlayer().getName()));
                    warning(I.t("The reason is: {0}", vote.getReason()));

                    RawMessage.send(playerSender(), new RawText("")
                            .then("» ")
                                .style(ChatColor.BOLD, ChatColor.DARK_RED)
                            .then(I.t("Click here to vote for this ban"))
                                .color(ChatColor.RED)
                                .hover(new RawText(I.t("Vote the ban of {0}", target.getName())).color(ChatColor.WHITE))
                                .command(VoteBansYesCommand.class, target.getName())
                            .then(" «")
                                .style(ChatColor.BOLD, ChatColor.DARK_RED)
                            .build()
                    );
                    break;
            }
        }
    }

    @Override
    protected List<String> complete() throws CommandException
    {
        if (args.length == 1)
        {
            List<String> playersNames = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers())
                if (!Permissions.EXEMPTED.grantedTo(player))
                    playersNames.add(player.getName());

            return getMatchingSubset(playersNames, args[0]);
        }

        else return null;
    }

    @Override
    public boolean canExecute(CommandSender sender)
    {
        return Permissions.START_VOTE.grantedTo(sender);
    }
}
