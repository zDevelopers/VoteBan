/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 * 
 * http://amaury.carrade.eu
 * 
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.zcraft.VoteBan.commands.votebans;

import fr.zcraft.VoteBan.Permissions;
import fr.zcraft.VoteBan.VoteBan;
import fr.zcraft.VoteBan.votes.Vote;
import fr.zcraft.quartzlib.components.commands.Command;
import fr.zcraft.quartzlib.components.commands.CommandException;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.components.rawtext.RawText;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public abstract class VoteBansVoteCommand extends Command
{
    private boolean choice = false;

    protected void setChoice(boolean choice)
    {
        this.choice = choice;
    }

    @Override
    protected void run() throws CommandException
    {
        final Map<UUID, Vote> votes = VoteBan.get().getVotes();
        final Vote vote;

        if (votes.isEmpty())
        {
            warning(I.t("There is no running vote-ban."));

            if (choice)
                send(new RawText(I.t("To start one, use the /voteban command (or click here)."))
                        .color(ChatColor.GRAY)
                        .suggest("/voteban " + (args.length >= 1 ? args[0] + " " : ""))
                );

            return;
        }

        else if (args.length >= 1)
        {
            final Player target = getPlayerParameter(0);
            vote = votes.get(target.getUniqueId());

            if (vote == null)
            {
                warning(I.t("There is no running vote-ban for {0}.", target.getName()));

                if (choice)
                    send(new RawText(I.t("To start one, use the /voteban command (or click here)."))
                            .color(ChatColor.GRAY)
                            .suggest("/voteban " + target.getName())
                    );

                return;
            }
        }

        else
        {
            if (votes.size() > 1)
                error(I.t("There is more than one running vote-ban, please add the name of your target to the command."));

            vote = votes.values().iterator().next();
        }

        if (!vote.vote(playerSender(), choice))
        {
            error(I.t("You can only vote once."));
        }
    }

    @Override
    protected List<String> complete() throws CommandException
    {
        if (args.length == 1)
        {
            final List<String> currentVotedPlayers = new ArrayList<>();

            for (final Vote vote : VoteBan.get().getVotes().values())
                currentVotedPlayers.add(vote.getTargetPlayer().getName());

            return getMatchingSubset(currentVotedPlayers, args[0]);
        }

        else return null;
    }

    @Override
    public boolean canExecute(final CommandSender sender)
    {
        return Permissions.VOTE.grantedTo(sender);
    }
}
