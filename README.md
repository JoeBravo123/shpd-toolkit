# SHPD Toolkit

This repository is a fork of [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) and contains code from [SHPD Seedfinder](https://github.com/Elektrochecker/shpd-seed-finder) which is a fork of [Alessiomarotta's SHPD Seedfinder](https://github.com/alessiomarotta/shpd-seed-finder).

This fork also provides an unsigned arm64 iOS build intended for personal use with [LiveContainer](https://github.com/LiveContainer/LiveContainer). It includes a touch-friendly seed result screen and expanded Simplified Chinese localization.

## Current iOS release

`v3.3.8-1.7-ios.1` is the first packaged iOS release. It adds LiveContainer-compatible IPA output, compact floor navigation, a clean result overlay with an explicit Back action, persistent language selection, and translated seed-analysis output.

# Installation

### iOS / LiveContainer

1. Download the unsigned `.ipa` from this fork's [Releases](https://github.com/wenja678-boop/shpd-toolkit/releases).
2. Import the IPA into LiveContainer.
3. Launch it from LiveContainer. No additional signing is required by this build workflow.

The IPA is built for arm64 iOS devices. It is not an App Store package and is not intended for normal signed installation outside LiveContainer.

### Android and desktop

Android and desktop users can use the packages published by the [upstream SHPD Toolkit releases](https://github.com/Elektrochecker/shpd-toolkit/releases). Desktop builds require Java. For intensive desktop searches, consider the [original seedfinder CLI tool](https://github.com/Elektrochecker/shpd-seed-finder), which is faster and more powerful.

As different versions of Shattered Pixel Dungeon (SHPD) generate different dungeons, a release of SHPD Toolkit will only work with certain versions of SHPD. Versions that only differ by the last digit in the version code (for example v2.3.0 and v2.3.1) are likely, but not guaranteed to share the same level generation.

# Usage

### scouting mode
Scouting mode can be used to gain information about a given dungeon seed. The maximum depth of the search can be changed in the seedfinder settings. By changing the logging options, the item categories to be shown can be changed. The result window uses previous/current/next floor controls so that long searches remain touch-friendly. Use the visible Back tab, the system back action, or Escape on desktop to return to the title screen. Daily run scouting works similarly to regular seed scouting.

### seedfinding
Seedfinding mode is used to generate seeds to fit user specified criteria. When pressing on "Find Seed", the user will be prompted to enter a list of items for the seed to contain. This list needs to fulfill the same conditions as the item list from the original seedfinder:

- item names are the same as read in-game, including upgrade level
- all items must be written in lowercase letters
- each item must go on a new line

The following two examples are valid inputs and functionally equivalent:

```
ring of sharpshooting +2
alchemist's toolkit
```
```
sharpshooting +2
toolkit
```
The seedfinder can run in two different modes which can be changed in the settings:
- ANY mode: find seeds that contain any one of the specified items
- ALL mode: find seeds that contain all of the specified items

The max. depth slider in the settings controls until which floor the condition has to be met. Setting the slider to 4 and the mode to ALL will generate seeds that contain all of the specified items before floor 5.

Upon starting the seedfinder the app will loop through different seeds until it finds a fitting one. The app will appear frozen until a seed is found. When entering an invalid, impossible or sufficiently unlikeley combination of items, the app will lock up and has to be forcefully closed.

### Configuration
The number of floors and which categories of items are searched are adjustable in the "Settings" Tab.
One can also change the currently active challenges, the mode of the Seedfinder and the font size of the results window in the settings.

### Challenges
Some challenges such as "forbidden runes" change level generation. The challenges the seedfinder uses can be changed in the settings.

### Item catalog
The item catalog can be used to check/confirm the names of different items. After finding or scouting a seed, the consumables in the catalog will have the types of the ones in this seed.

# Building
SHPD Toolkit can be compiled exactly like [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon).

The unsigned iOS IPA is produced by the `Build iOS IPA for LiveContainer` GitHub Actions workflow. Run it manually from the Actions tab on the branch or tag to build. The workflow uses a macOS runner, RoboVM, arm64-only output, and skips signing; the resulting IPA is uploaded as a workflow artifact.

Local Java compilation can be checked with:

```sh
./gradlew :core:compileJava :ios:compileJava
```
