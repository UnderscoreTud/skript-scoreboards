# skript-scoreboards
skript-scoreboards is an addon that allows you to create and manage packet-based scoreboards easily.

## Features
- Packet-based scoreboards
- Per player scoreboards
- Scoreboards with shared viewers
- No character limit on lines
- Support for custom line scores (Minecraft 1.20.3+)

## Documentation
[![Get on skUnity](https://docs.skunity.com/skunity/library/Docs/Assets/assets/images/buttons/v2/get-the-syntax-square.png)](https://docs.skunity.com/syntax/search/addon:skript-scoreboards)

## Usage/Examples
#### Global Scoreboards
```applescript
on load:
    set {-global-scoreboard} to a new scoreboard
    set title of {-global-scoreboard} to "&cExample Scoreboard"
    set line 1 of {-global-scoreboard} to "Online Players"
    # Custom scores are only available on Minecraft 1.20.3+!
    set score of line 1 of {-global-scoreboard} to "&e%size of all players%"

    set line 2 of {-global-scoreboard} to "IP"
    set score of line 2 of {-global-scoreboard} to "&emyserver.com"

    add all players to {-global-scoreboard}

on join:
    set player's scoreboard to {-global-scoreboard}
```

#### Per Player Scoreboards
```applescript
on join:
    set {_board} to player's scoreboard
    set title of {_board} "My Scoreboard"
    set line 1 of {_board} to "Rank"
    # Custom scores are only available on Minecraft 1.20.3+!
    set score of line 1 of {_board} to {rank::%player%}

    set line 2 of {_board} to "Coins"
    set score of line 2 of {_board} to "&e%{coins::%player%}%"
```

## License
[MIT](https://choosealicense.com/licenses/mit/)
