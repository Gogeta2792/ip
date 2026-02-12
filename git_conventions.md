# Git conventions (condensed)

Source: SE-EDU Git conventions guide: https://se-education.org/guides/conventions/git.html

## Commit message — subject line

- **Always write a clear subject line.**
- **Keep it short:** aim for **≤ 50 chars** (hard limit **72 chars**).
- **Use imperative mood** (as if completing the sentence “This commit will …”):
  - ✅ `Add README.md`
  - ❌ `Added README.md` / `Adding README.md`
- **Capitalize the first letter**:
  - ✅ `Move index.html file to root`
  - ❌ `move index.html file to root`
- **No period at the end**:
  - ✅ `Update sample data`
  - ❌ `Update sample data.`
- **Optional prefix** when helpful: `<scope>:` or `<category>:`  
  Examples:
  - `Person class: Remove static imports`
  - `Main.java: Remove blank lines`
  - `bug fix: Add space after name`
  - `chore: Update release date`

## Commit message — body (for non-trivial commits)

- Add a body when the change is not obvious from the subject alone.
- **Format**
  - Put a **blank line** between subject and body.
  - **Wrap lines at 72 characters**.
  - Use **blank lines** to separate paragraphs.
  - Use **bullet points** when it improves clarity.

- **Content principle:** explain **WHAT** and **WHY**, not **HOW**
  - WHAT: what the change achieves
  - WHY: why it was needed / why this approach was chosen
  - HOW: leave implementation details to the diff

- **If it gets long, split the work**
  - A very long explanation is often a sign the commit should be broken into smaller commits.

- **Avoid redundancy**
  - Don’t repeat what is already clearly stated in code comments added in the same commit.

### Suggested body structure (template)

- **Current situation** (present tense)
- **Why it needs to change**
- **What is being done** (imperative mood; “Let’s …” is acceptable)
- **Why it is done that way**
- **Any other relevant info** (links, references, follow-ups)

Notes:
- Avoid words like “currently” / “originally” when describing the current situation (it’s implied).

## Branch names

- Use **meaningful keywords** in **kebab-case**:
  - `refactor-ui-tests`
- If the branch relates to an issue, use:
  - `issueNumber-some-keywords-from-issue-title`
  - Example: `1234-ui-freeze-error`
