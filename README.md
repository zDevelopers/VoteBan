# VoteBan ![Build & Test](https://github.com/zDevelopers/VoteBan/workflows/Build%20&%20Test/badge.svg)

Vote for bans if moderators are away. [**Download link**](https://github.com/zDevelopers/VoteBan/releases).

For players with the needed permission, `/voteban <pseudo> <reason>` launches a vote to ban the given player (as example, a visitor flooding the chat without moderator available). Other players have a configurable amount of time to vote, and an action is taken following the results.

Players can be protected against bans.

This plugin is intended to be used with a permissions manager.

### Permissions and suggestions

- `voteban.start` allows to start a vote (default: no-one). Given to members, not visitors.
- `voteban.vote` allows to vote (default: no-one). Given to members, not visitors.
- `voteban.exempt` prevents players with it to be vote-banned (default: operators). Given to members (avoiding mutual vote-bans) and staff.

 

This plugin is currently available in English and French.

*This plugin is a rewrite of a plugin by niquecraft, with a nicer UI and the use of new modern Minecraft features. [The old no-longer maintened plugin was here.](http://dev.bukkit.org/bukkit-plugins/voteban/)*
