package fr.zcraft.VoteBan;

import fr.zcraft.VoteBan.commands.votebans.VoteBansNoCommand;
import fr.zcraft.VoteBan.commands.votebans.VoteBansStartCommand;
import fr.zcraft.VoteBan.commands.votebans.VoteBansYesCommand;
import fr.zcraft.VoteBan.votes.CannotRegisterVoteException;
import fr.zcraft.VoteBan.votes.Vote;
import fr.zcraft.quartzlib.components.commands.Commands;
import fr.zcraft.quartzlib.components.i18n.I18n;
import fr.zcraft.quartzlib.core.QuartzPlugin;
import fr.zcraft.quartzlib.tools.runners.RunTask;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public final class VoteBan extends QuartzPlugin
{
    private static VoteBan instance;

    private Map<UUID, Vote> votes = new HashMap<>();
    private Set<UUID> immunePlayers = new HashSet<>();


    public static VoteBan get()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        instance = this;

        saveDefaultConfig();
        loadComponents(Commands.class, Config.class, I18n.class);

        I18n.setPrimaryLocale(Config.LOCALE.get());

        Commands.register("votebans", VoteBansStartCommand.class, VoteBansYesCommand.class, VoteBansNoCommand.class);
        Commands.registerShortcut("votebans", VoteBansStartCommand.class, "voteban");
        Commands.registerShortcut("votebans", VoteBansYesCommand.class, "voteyes");
        Commands.registerShortcut("votebans", VoteBansNoCommand.class, "voteno");
    }


    public Vote getVote(UUID against)
    {
        return votes.get(against);
    }

    public Vote getVote(Player against)
    {
        return getVote(against.getUniqueId());
    }

    public Vote registerVote(Player against, Player launcher, String reason)
    {
        if (immunePlayers.contains(against.getUniqueId()))
            throw new CannotRegisterVoteException(CannotRegisterVoteException.Reason.IMMUNIZED_COOLDOWN);

        else if (Permissions.EXEMPTED.grantedTo(against))
            throw new CannotRegisterVoteException(CannotRegisterVoteException.Reason.IMMUNIZED_PERMISSION);

        else if (votes.containsKey(against.getUniqueId()))
            throw new CannotRegisterVoteException(CannotRegisterVoteException.Reason.ALREADY_RUNNING);

        else
        {
            Vote vote = new Vote(against, launcher, reason);
            votes.put(against.getUniqueId(), vote);
            return vote;
        }
    }

    public void unregisterVote(Vote vote)
    {
        votes.remove(vote.getTargetUUID());
    }

    public void immunize(final UUID player)
    {
        immunePlayers.add(player);

        RunTask.later(new Runnable() {
            @Override
            public void run()
            {
                immunePlayers.remove(player);
            }
        }, Config.VOTES.COOLDOWN.get() * 20);
    }

    public Map<UUID, Vote> getVotes()
    {
        return Collections.unmodifiableMap(votes);
    }
}
