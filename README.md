# NeoPaintingSwitch-Remastered
A remastered fork of arcvvolf's neoPaintingSwitch Minecraft Bukkit plugin

**Note: This is not an original plugin by Henry Shires. The original source code has been adapted from arcvvolf's original plugin for Bukkit servers.**

## Features & Usage
Use NeoPaintingSwitch to more easily decorate your Minecraft server worlds with paintings. NeoPaintingSwitch allows players to switch between paintings far more easily than in vanilla Minecraft. Simply scroll between hotbar items to change the painting type after placing it on a surface, then right click to lock your choice. NeoPaintingSwitch will also remember your choice for future painting placement if the same painting is desired in multiple locations.

- Place a painting on the wall.
- Right click the painting, then scroll with the mousewheel up or down to transition through the available painting options
- When the desired painting has been found, simply right click again or walk away from the painting. The plugin will then lock your painting choice on the surface
- The plugin will automatically determine which paintings can fit the size of the surface, so some paintings might not be an option if the size of the surface is too small

## What has been updated?
- Henry's remastered version includes native support for Spigot servers up to the latest version of Minecraft and is now built with Maven.
- Several bugs have also been fixed such as an issue with NeoPaintingSwitch ignoring permission nodes
- While the original version used a config file for debug and permission options, the remastered version does not, as it is no longer a necessary feature

## Installation
- Download Henry's remastered plugin from [SpigotMC](https://hcshires.github.io) or [GitHub Releases](https://github.com/hcshires/NeoPaintingSwitch-Remastered/releases)
- Upload the .jar file into your server's plugin folder
- Congratulations, you have NeoPaintingSwitch on your server!

## Permissions
`neopaintingswitch.use` - Give this permission node to a group or player with whom you wish to have access to use NeoPaintingSwitch

## WorldGuard Support (experimental)
If you have WorldGuard building permission nodes enabled with your world, NeoPaintingSwitch will use `worldguard.build.*` or `worldguard.region.bypass.WORLD_NAME` if you wish to run permissions through WorldGuard. See WorldGuard's [permission documentation](https://worldguard.enginehub.org/en/latest/permissions/) for more information

## Support
Please report bugs and issues by creating an issue on [GitHub](https://github.com/hcshires/NeoPaintingSwitch-Remastered/issues)

Contact Henry: [https://hcshires.github.io/contact](https://hcshires.github.io/contact)

### Original Bukkit Plugin by arcvvolf:
[https://dev.bukkit.org/projects/paintingswitch](https://dev.bukkit.org/projects/paintingswitch)

Plugin updated by Henry Shires | Visit ProjectDSM Minecraft server - https://projectdsm.org | [View Developer Page](https://hcshires.github.io)
