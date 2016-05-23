# VoteBan [![Build Status](https://jenkins.carrade.eu/job/VoteBan/badge/icon)](https://jenkins.carrade.eu/job/VoteBan/)

Vote for bans if moderators are away. [**Download link**](https://jenkins.carrade.eu/job/VoteBan/).

For playes with the needed permission, `/voteban <pseudo> <reason>` launches a vote to ban the given player. Other players have a configurable amount of time to vote, and an action is taken following the results.

Players can be protected against bans.

This plugin is intended to be used with a permissions plugin.  
Typical permissions:

- `voteban.start` (allows a player to start a vote): given to members;
- `voteban.vote` (allows a player to vote): given to members (not to visitors);
- `voteban.exempt` (prevents a player from being vote-banned): given to members and staff.

This plugin is currently available in English and French.

*This plugin is a rewrite of a plugin by niquecraft, with a nicer UI and the use of new modern Minecraft features. [The old no-longer maintened plugin was here](http://dev.bukkit.org/bukkit-plugins/voteban/).*
