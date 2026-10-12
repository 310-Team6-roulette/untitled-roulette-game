# Contributing to URG

## Code of Conduct

Thank you for your interest in contributing!
Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md) to help keep our community safe and friendly.

## How to file a bug report
Before you submit an issue, make sure to search through existing issues to see if your question was already answered.

If it has not and it looks like a bug, create a new issue following the [bug report template](.github/ISSUE_TEMPLATE/bug_report.md). Try your best to answer the following:

- **Overview:** a one or two sentence summary
- **Steps to reproduce:** numbered steps, starting from launching the game
- **Expected behaviour:** what you thought should happen
- **Observed behaviour:** what actually happened
- **Environment:** Java version and operating system
- **Logs / screenshots:** anything relevant, e.g. console output or a screenshot of the table

Any bug report should be approved by team members before beginning work. If any details are unclear, the report submitter will be expected to provide clarification.

## How to submit a new issue
Before you create an issue, make sure that it does not already exist. Use the [feature request template](.github/ISSUE_TEMPLATE/feature-request.md) when creating an issue. It should include the following:
- **What:** a clear description of the feature
- **Motivation:** what inspired the idea and how it improves URG
- **Proposed behaviour:** how it should work from the player's point of view
- **Dependencies:** Clearly note any dependencies between other issues if any (e.g., `Depends on #3` or `Blocks #12`). Closely related issues can also be created as sub-issues under one generalised parent issue. 

Similar to bug reports, you must have the approval of team members before beginning work.

## How to submit a pull request
- Check GitHub for existing pull requests to prevent duplicates.
- Work out of your own fork of the main repository (fork-and-pull workflow). **Never work directly on `dev` or `main`.**
- Keep your fork synced with the upstream repository, and rebase often rather than waiting until all changes are done.

### Setting up your remote
Fork this repository to create your own copy, either under your GitHub account or your team's organisation.
```
git clone https://github.com/<OWNER>/untitled-roulette-game.git
cd untitled-roulette-game
```
Add this central repository as the upstream repository.
```
git remote add upstream https://github.com/310-Team6-roulette/untitled-roulette-game.git
```
Verify you have done everything correctly:
```
git remote -v
```
You should see something like this:
```
origin  https://github.com/<OWNER>/untitled-roulette-game.git (fetch)
origin  https://github.com/<OWNER>/untitled-roulette-game.git (push)
upstream        https://github.com/310-Team6-roulette/untitled-roulette-game.git (fetch)
upstream        https://github.com/310-Team6-roulette/untitled-roulette-game.git (push)
```

### Keeping your work up to date
Use rebase to keep your fork updated. This is so the development history is kept linear.
```
git fetch upstream
git rebase upstream/dev
```

### Our branch naming conventions
Start with the category prefix, then your GitHub username, then a short description separated by hyphens: `<prefix>/<username>/<short-description>`. 
- `feat/`: new features or functionality
- `fix/`: bug fixes
- `refactor/`: improving code structure without changing behaviour
- `test/`: writing or improving automated tests
- `docs/`: documentation updates
- `chore/`: maintenance and tooling

Example: `feat/SimonB-Engineer/infinite-quota`

Commit messages should also include the relevant prefix with a short description (`prefix: short-description`). 

Example: `feat: created the base layout for the tutorial menu`

### Standard development workflow
1. Create a new branch from `dev`
2. Make your changes, while including tests alongside your code (if relevant)
3. Run all the tests and play the game to check that your implementation is working as intended, without breaking old behaviour.
4. Commit your changes with a descriptive message using a conventional prefix
5. Rebase onto the latest `dev` branch and resolve any conflicts. Afterwards, re-run the tests and play the game to check everything is okay. **Make sure to do this often.**
6. Push your changes to your fork (`origin`)
7. On your fork, click the "Compare & pull request" banner. Set the base repository to the `upstream` and the base branch to `dev`. Pull requests to `main` will **not** be accepted.
8. Add a clear title describing the change itself (e.g. "Add bouncy charm upgrade"), not just an issue number. **Never leave the description blank.** Include:
   - A clear description of the change
   - The linked issue, (e.g., `Closes #17`, `Contributes to #19`)
   - Screenshots for any visual change
   - Any dependencies on other issues or PRs

If an issue is worked on by more than one team member, record that in both the issue and the PR comments.

### Review process
- At least one member must review and approve the PR before merging (**no self-merging**).
- Reviewers should test the new code, play the game to check it behaves as expected, and thoroughly check the code for readability, maintainability, and correctness.
- Approve using GitHub's formal **Approve** option under *Files changed*. 
- If changes are requested, make them on the same branch and re-test. Do not create a new issue for fixes found during code review.
- Once approved, the PR is **squash merged** into `dev`. Fix any merge conflicts first. Then you may delete your branch (locally and on your fork) and rebase onto the latest `upstream/dev`.

## How to set up your environment and run tests
**Required:**
- JDK 17 or higher
- Git

Further details about the prerequisites can be found in the wiki's [Onboarding Guide](https://github.com/310-Team6-roulette/untitled-roulette-game/wiki/Onboarding-Guide).

### Run the game
Once you have cloned your fork, run either one of these commands from the project root:
```
# macOS / Linux
./gradlew lwjgl3:run

# Windows
./gradlew.bat lwjgl3:run
```

**Troubleshooting (macOS / Linux):** Git doesn't reliably preserve execute permissions across clones and zip downloads. If you get `Permission denied`, run this once before your first Gradle command:
```
chmod +x gradlew
```

### Tests
This project uses JUnit tests via Gradle. You can run them with either one of the following commands:
```
# macOS / Linux
./gradlew :core:test

# Windows
./gradlew.bat :core:test
```
The individual test file results can be found under `core/build/test-results/test`.

## Types of contributions
**Examples of contributions URG is looking for:**
- New bet types, cards, charms and game modes
- Bug fixes and gameplay or usability improvements
- Refactors that improve maintainability
- Tests, especially for game logic
- Visuals, sound and documentation improvements

**Examples of contributions we do not want:**
- Uncoordinated or abrupt code dumps
- Duplicate work
- Changes outside the scope of the issue being addressed
- Work that does not address an active issue
- PRs opened against `main`

## How contributors can get started if they are newcomers
1. Look on the GitHub repository's Issues tab.
2. Look for issues labelled `good first issue`.
3. Leave a comment asking to be assigned before starting work.
4. Have a maximum of **one claimed open issue** at a time.

Unsure about something? Open an issue with the `question` label.

## Our custom issue labels
| Label | Meaning |
|---|---|
| `chore` | Trivial tasks that do not need a PR |
| `major feature` | A significant, large feature to be added to URG |
| `minor feature` | A feature that isn't too large and doesn't have lots of components |
| `refactor` | Changes to internal code structure but not behaviour |
| `required feature` | Features needed for a `major feature` to be considered done |
| `security` | Involves security risks or fixes |
| `sound` | Feature requires sound effects or music |
| `Visuals` | A component requires visuals, created internally or externally |

## Technical requirements for contributions
- **Include tests with every addition or modification where relevant**, written alongside the code. Game logic (bet resolution, payouts, card and charm effects) must have unit tests. 
- UI and rendering code that needs a graphics context (e.g. `Tile`) is difficult to test, so for those changes, extract testable logic where practical.
- All tests must pass and the project must build before opening a PR. Avoid introducing new compiler warnings.
- Follow the [Style and Organization Guide](https://github.com/310-Team6-roulette/untitled-roulette-game/wiki/Style-and-Organization-Guide) on the wiki.
- Use meaningful names and keep methods focused on a single responsibility.
- Add comments and Javadoc where they improve understanding, not to restate the code.

## Our roadmap/vision for URG
URG is a roguelike game built on the classic game of roulette. The focus is on randomness, high replayability and enough content for players to have multiple unique playthroughs.

### Milestone 1 (A1): 
- Core gameplay loop and functionality:
  - Roulette wheel and ball
  - Round system that scales difficulty as the player makes it further in the game
  - Progression system where the player is able to encounter bosses at the end of each round
  - Shop where the player may purchase upgrades
- Persistent high-score.
- Suitable assets including pixel art and background music.

### Milestone 2 (A2):
- Implement a tutorial
- Implement standard menu interface (settings, quit, title page).
- Different difficulties that unlock as you beat previous difficulties
- More upgrade variance
- Alternative bosses to each boss
- Saving/loading game states
- Meta progression
- Achievements (tied to both meta progression and in-game events)

This can also be found in the wiki's [Home](https://github.com/310-Team6-roulette/untitled-roulette-game/wiki) page.

## High level design / architecture design information
Base package: `io.wasabi.urg`

- **Game state:** `state.RunState` is the single source of truth for chip balance, tiles and active bets, and it persists across screens.
- **Betting system:** `elements.betting` is built around `BetZone`, a universal abstraction used for every bet type. Bets are reserved, not deducted, until `RunState.resolveActiveBets()` settles them all at once.
- **Table and wheel:** `elements.game` (`BettingTable`, which handles bet placement and rendering, plus `Tile`, `Wheel` and `Ball`) and `elements.tiles` (`TileType` subclasses, which control tile colour and texture).
- **Cards and charms:** `elements.card` (base class `Card`) and `elements.charms` (base class `Charm`). New ones are registered in `managers/CardPool.java` and `managers/CharmPool.java`, and each one's logic stays in its own class.

Systems like cards and charms build on the core game state, so check dependencies before starting. Detailed information about this section can be found under the wiki's [Component Architecture Guide](https://github.com/310-Team6-roulette/untitled-roulette-game/wiki/Component-Architecture-Guide).

## License
By contributing to URG, you acknowledge that your contributions will be licensed under the [MIT license](LICENSE).

## How contributors should get in touch with us
- **GitHub Issues:** open an issue with the `question` label for help or discussion.
- **Discord:** join our [discord community](https://discord.gg/5WnxdX8R8m) to ask us questions and discuss ideas.
