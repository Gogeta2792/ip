# Text UI testing for Spot

This folder uses **input/output redirection** to semi-automate testing of the Spot text UI. See [Automating the testing of text UIs](https://se-education.org/guides/tutorials/textUiTesting.html).

## How to run tests

- **Windows**: From this folder in Command Prompt, run `runtest.bat`
- **Linux/macOS**: From this folder, run `./runtest.sh`

Or manually:

1. Compile: `javac -cp ../src/main/java -Xlint:none -d ../bin ../src/main/java/*.java`
2. Run: `java -classpath ../bin Spot < input.txt > ACTUAL.TXT`
3. Compare: `FC ACTUAL.TXT EXPECTED.TXT` (Windows) or `diff ACTUAL.TXT EXPECTED.TXT` (Unix)

## Updating tests when the program changes

1. Run the test; it will fail if behavior changed.
2. Compare `ACTUAL.TXT` with `EXPECTED.TXT` (e.g. diff tool) and confirm the new behavior is correct.
3. Copy `ACTUAL.TXT` to `EXPECTED.TXT` to accept the new expected output.
4. Re-run the test to confirm it passes.

## What is tested

- **list**: Empty list and list with tasks
- **todo**, **deadline**, **event**: Adding tasks and confirmation messages
- **mark** / **unmark**: Marking tasks done and undone
- **bye**: Farewell message
- **Error cases**: `mark` without number, empty `todo`, invalid `deadline` (missing `/by`)

When you add new features, extend `input.txt` with more commands and refresh `EXPECTED.TXT` from the new `ACTUAL.TXT` so the tests stay up to date.
