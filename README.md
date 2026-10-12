# untitled roulette game

A roguelike game based on the classic game of roulette made in [libGDX](https://libgdx.com/).

## What is this project about?
untitled roulette game is a roguelike spin on the classic game of roulette, with pixel graphics, various bosses, cards, charms and more! 

## Why this project is useful?
Provides an interactive and randomised roulette gaming experience. 

## Getting Started
### Pre-requisites 
- Java JDK 17 or newer, Gradle 9.6.1 requires a 17+ runtime.
- No separate Gradle install needed as the project has an included wrapper (./gradlew or gradlew.bat).
- A GPU with OpenGL 2.0+ and current graphics drivers to run the desktop build.
- Supported OS: Windows x64, Linux x64, macOS (Intel & Apple Silicon)

### Installation 
**Initial Step:** Clone the repository into your desired path with `git clone https://github.com/310-Team6-roulette/untitled-roulette-game.git`.

### Running the game
- Windows (PowerShell/CMD):  .\gradlew.bat lwjgl3:run
- macOS/Linux:               ./gradlew lwjgl3:run

## Testing
- Tests are written with JUnit 5 and are in core/src/test/java

### Run all tests
- Windows:              .\gradlew.bat :core:test
- macOS/Linux:          ./gradlew :core:test

### Test results
After testing, if all tests pass, no details will be written to terminal, more detailed results are stored in core/build/test-results/test folder.

## Building with Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

## License Details
This project is licensed under the **MIT License**. More details can be found in the [LICENSE](LICENSE) file.

## Versions
- v1.0.0 Milestone 1

## Contributing

We welcome contributions!

Please read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting changes.

To get started with contributing to the project, follow the [Onboarding Guide](https://github.com/310-Team6-roulette/untitled-roulette-game/wiki/Onboarding-Guide) on the untitled roulette game wiki.

Contributions should be made through **GitHub forks**. Pull requests submitted directly from personal branches may be rejected.

## Tech Stack & Acknowledgements
### Technology Stack
- **Backend:** Java 9, libGDX 1.14.2, Box2D physics, Gradle 9.6.1
- **Frontend:** libGDX rendering (LWJGL 3.4.1/OpenGL), custom GLSL shaders, Terminus bitmap fonts
- **Testing:** JUnit 5
- **Code Quality:** SonarCloud, Snyk
### Major Contributors
- **Initial project developers (A1):** Tony Dang Tran, Raymond Wang, Hanxiang Wang, Jonathan Wu, Susan Shi, Taiki Yorifuji.
- **Incoming project developers (A2):** Bryan Quach, Jeffrey Song, Mansher Bhullar, Takahiro Kanaizumi, Tevita 'Akau'ola, Wayne Tian.
