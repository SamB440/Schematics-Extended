<br/>
<p align="center">
  <img src="https://www.convallyria.com/images/schematicsgif.gif">
  <h3 align="center">Schematics Extended</h3>

  <p align="center">
    Allows the pasting and retrieval of Sponge format schematics block-by-block
    <br/>
    <br/>
    <a href="https://github.com/SamB440/Schematics-Extended/issues">Report Bug / Feature request</a>
  </p>

  <center>
    <a href="https://discord.gg/fh62mxU">
      <img alt="Discord" src="https://img.shields.io/discord/282242806695591938">
    </a>
  </center>
</p>

[![CodeFactor](https://www.codefactor.io/repository/github/samb440/schematics-extended/badge/master)](https://www.codefactor.io/repository/github/samb440/schematics-extended/overview/master) ![Contributors](https://img.shields.io/github/contributors/SamB440/Schematics-Extended?color=dark-green) ![Issues](https://img.shields.io/github/issues/SamB440/Schematics-Extended) ![License](https://img.shields.io/github/license/SamB440/Schematics-Extended)
![Forks](https://img.shields.io/github/forks/SamB440/Schematics-Extended?style=social) ![Stargazers](https://img.shields.io/github/stars/SamB440/Schematics-Extended?style=social)

## Table Of Contents

* [About the Project](#about-the-project)
* [Branches](#branches)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Setting up](#setting-up)
* [What to do](#what-to-do)
* [Contributing](#contributing)
* [License](#license)
* [Authors & Contributors](#authors--contributors)

## About The Project

Schematics Extended allows you to paste schematics block-by-block. It also allows fetching the palette and materials of a schematic before even pasting it.

You can find the documentation on the Spigot thread: https://www.spigotmc.org/threads/previewing-and-pasting-schematics-block-by-block.324817/

## Branches

Schematics Extended has branches for the WorldEdit API and previous versions.
* [WorldEdit API](https://github.com/SamB440/Schematics-Extended/tree/worldedit-api)
* [1.17.1](https://github.com/SamB440/Schematics-Extended/tree/ver/1.17.1)

## Getting Started

To contribute, follow the steps below. Contributions are very welcome.

### Prerequisites

* [JDK 16](https://adoptium.net/)
* [Gradle](https://gradle.org/)
* [Git](https://gitforwindows.org/), if on windows.

Running BuildTools is not required as we use the CodeMC NMS repository.

### Setting up

Clone the repository
Via IntelliJ:
```File > New > Project from Version Control > URL: https://github.com/SamB440/Schematics-Extended.git > Clone```

Or, via git bash:
```sh
git clone https://github.com/SamB440/Schematics-Extended.git
```

To build, run the `build` task from IntelliJ, gradle, or gradlew.

## What to do

See the [open issues](https://github.com/SamB440/Schematics-Extended/issues) for a list of proposed features (and known issues).

## Contributing

Contributions are greatly welcomed.
* If you have suggestions for new features or changes, feel free to [open an issue](https://github.com/SamB440/Schematics-Extended/issues/new) to discuss it, or directly create a pull request.
* Make sure to add javadoc comments.
* Create an individual PR for each suggestion.

### Creating A Pull Request

Please be mindful that we may ask you to make changes to your pull requests.

Also, if possible, please use `feature` or `fix` branch prefixes.

## License

Distributed under the GNU GPL v3 License. See [LICENSE](https://github.com/SamB440/Schematics-Extended/blob/master/LICENSE) for more information.
Please provide credit to the authors/contributors below if used.

## Authors & contributors

* [SamB440](https://github.com/SamB440) - Schematic previews, centering and pasting block-by-block, class itself
* [brainsynder](https://github.com/brainsynder-Dev) - 1.13+ Palette Schematic Reader
* [Math0424](https://github.com/Math0424) - Rotation calculations
* [Jojodmo](https://github.com/jojodmo) - Legacy (< 1.12) Schematic Reader