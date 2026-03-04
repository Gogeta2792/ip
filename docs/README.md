# Spot — User Guide

**Spot the Dog** is a friendly task-management chatbot that helps you keep track of todos, deadlines, and events. Type commands in plain English, and Spot responds in a chat-style window. If you can type fast, Spot can get your task management done quickly.

---

## Table of Contents

- [Getting Started](#getting-started)
- [Core Features](#core-features)
  - [Viewing help](#viewing-help-help)
  - [Listing all tasks](#listing-all-tasks-list)
  - [Adding tasks](#adding-tasks)
  - [Marking tasks done or undone](#marking-tasks-done-or-undone-mark--unmark)
  - [Deleting tasks](#deleting-tasks-delete)
  - [Finding tasks by keyword](#finding-tasks-by-keyword-find)
  - [Viewing deadlines on a date](#viewing-deadlines-on-a-date-on)
  - [Getting a motivational quote](#getting-a-motivational-quote-cheer)
  - [Exiting Spot](#exiting-spot-bye)
- [Saving the data](#saving-the-data)
- [Editing the data file](#editing-the-data-file)
- [FAQ / Troubleshooting](#faq--troubleshooting)
- [Privacy / Data notes](#privacy--data-notes)
- [Command summary](#command-summary)

---

## Getting Started

### Prerequisites

- **Java 17 or above** installed on your computer.  
  - The project is built with JDK 21. If you use a different version, ensure it is compatible.

### Running Spot

**Option 1: Run from source (recommended for development)**

1. Open a terminal in the project folder.
2. Run:
   - **Windows:** `.\gradlew.bat run`
   - **Mac/Linux:** `./gradlew run`

**Option 2: Run the JAR file**

1. Build the JAR:
   - **Windows:** `.\gradlew.bat clean shadowJar`
   - **Mac/Linux:** `./gradlew clean shadowJar`
2. The JAR is created at `build/libs/spot.jar`.
3. Copy `spot.jar` to a folder of your choice.
4. Open a terminal in that folder and run: `java -jar spot.jar`

### First launch

- A window titled **Spot** opens with a header showing **Spot the Dog** and a text box at the bottom.
- Spot greets you and asks: *"What tasks do you have today?"*
- Type a command (e.g. `help`) and press **Enter** or click **Send** to get started.

![Spot UI](Ui.png)

---

## Core Features

### Notes about the command format

- Words in `UPPER_CASE` are parameters you supply.  
  Example: in `todo DESCRIPTION`, `DESCRIPTION` is what you type, e.g. `todo buy milk`.
- The **index** for `mark`, `unmark`, and `delete` is the number shown in the task list (1, 2, 3, etc.).
- Commands are **case-insensitive** (e.g. `LIST` and `list` both work).
- Additional text after simple commands (like `help`, `list`, `bye`) is ignored.

---

### Viewing help: `help`

**What it does**  
Shows a list of all commands Spot understands.

**How to use it**

1. Type `help` in the input box.
2. Press **Enter** or click **Send**.

**Tips**

- Use this whenever you forget a command or its format.

---

### Listing all tasks: `list`

**What it does**  
Shows all your tasks in order, with their type, status, and details.

**How to use it**

1. Type `list`.
2. Press **Enter** or click **Send**.

**What to expect**

- If the list is empty: *"Your list is empty. Add a task to get started!"*
- Otherwise: tasks are shown numbered, e.g. `1. [T][ ] buy milk`, `2. [D][X] submit final version (by: Mar 8 2026)`.

**Task icons**

- `[T]` = Todo  
- `[D]` = Deadline  
- `[E]` = Event  
- `[X]` = done  
- `[ ]` = not done  

---

### Adding tasks

Spot supports three task types: **todo**, **deadline**, and **event**.

#### Todo: `todo DESCRIPTION`

**What it does**  
Adds a simple task with no date.

**Format**  
`todo DESCRIPTION`

**Examples**

- `todo buy milk`
- `todo call mom`

**Tips**

- The description cannot be empty. `todo` by itself will show an error.

---

#### Deadline: `deadline DESCRIPTION /by DATE`

**What it does**  
Adds a task with a due date (and optional time).

**Format**  
`deadline DESCRIPTION /by DATE`

**Date formats**

- `yyyy-mm-dd` (e.g. `2026-03-08`)
- `d/M/yyyy` (e.g. `8/3/2026`)
- `d/M/yyyy HHmm` for date and time (e.g. `8/3/2026 2359`)

**Examples**

- `deadline submit report /by 2026-03-08`
- `deadline pay bills /by 9/3/2026`

**Tips**

- You must include both a description and `/by <date>`.  
- If the format is wrong, Spot will ask you to use a valid format.

---

#### Event: `event DESCRIPTION /from START /to END`

**What it does**  
Adds an event with a start and end time (or date range).

**Format**  
`event DESCRIPTION /from START /to END`

**Examples**

- `event team meeting /from Mon 2pm /to 3pm`
- `event conference /from 2026-03-08 /to 2026-03-09`

**Tips**

- All three parts (description, `/from`, `/to`) are required.  
- Start and end can be flexible text (e.g. "Mon 2pm", "3pm").

---

### Marking tasks done or undone: `mark` / `unmark`

**What it does**  
- `mark NUMBER` — marks the task as done.  
- `unmark NUMBER` — marks the task as not done.

**How to use it**

1. Use `list` (or `find`) to see the task numbers.
2. Type `mark 2` to mark task 2 as done, or `unmark 2` to mark it not done.
3. Press **Enter** or click **Send**.

**Examples**

- `mark 1` — marks the first task as done  
- `unmark 3` — marks the third task as not done  

**Tips**

- The number must match a task in the current list.  
- If you use `find` first, the numbers refer to the search results, not the full list.

---

### Deleting tasks: `delete`

**What it does**  
Removes a task from your list permanently.

**Format**  
`delete INDEX`

**How to use it**

1. Use `list` (or `find`) to see the task numbers.
2. Type `delete 2` (for example) to remove the second task.
3. Press **Enter** or click **Send**.

**Examples**

- `list` followed by `delete 2` — deletes the 2nd task in the list  
- `find report` followed by `delete 1` — deletes the 1st matching task  

**Tips**

- The index must be a positive integer (1, 2, 3, …).  
- Spot will say *"That task doesn't exist!"* if the number is invalid.

---

### Finding tasks by keyword: `find`

**What it does**  
Searches your tasks for a keyword and shows only the matching ones.

**Format**  
`find KEYWORD`

**How to use it**

1. Type `find` followed by your keyword, e.g. `find report`.
2. Press **Enter** or click **Send**.

**Examples**

- `find report` — finds tasks whose description contains "report"  
- `find meeting` — finds tasks containing "meeting"  

**Tips**

- Search is **case-insensitive** (e.g. `find REPORT` matches "report").  
- The keyword is matched anywhere in the task description (e.g. "report" matches "submit report").

---

### Viewing deadlines on a date: `on`

**What it does**  
Shows only deadlines whose due date falls on the given date.

**Format**  
`on DATE`

**Date formats**

- `yyyy-mm-dd` (e.g. `2026-03-08`)
- `d/M/yyyy` (e.g. `8/3/2026`)

**Examples**

- `on 2026-03-08`  
- `on 8/3/2026`  

**Tips**

- If Spot says *"I couldn't understand that date"*, use `yyyy-mm-dd` or `d/M/yyyy`.

---

### Getting a motivational quote: `cheer`

**What it does**  
Shows a random motivational quote to keep you going.

**Format**  
`cheer`

**How to use it**

1. Type `cheer`.
2. Press **Enter** or click **Send**.

**Tips**

- If `data/cheer.txt` exists and has quotes (one per line), Spot picks one at random.  
- If the file is missing, Spot shows a default quote.

---

### Exiting Spot: `bye`

**What it does**  
Closes Spot and says goodbye.

**Format**  
`bye`

**How to use it**

1. Type `bye`.
2. Press **Enter** or click **Send**.

**What to expect**

- Spot replies: *"Bye. Hope to see you again soon!"*  
- The window closes automatically.

---

## Saving the data

- Spot saves your tasks **automatically** after every change (add, mark, unmark, delete).
- There is no need to save manually.
- Data is stored in `data/spot.txt` in the folder where you run Spot (or where the JAR is located).

---

## Editing the data file

- Advanced users can edit `data/spot.txt` directly.
- The format is pipe-delimited: `T | 0 | description` for todos, `D | 0 | description | date` for deadlines, etc.
- **Caution:** Invalid edits can cause Spot to discard data or behave unexpectedly. Back up the file before editing.

---

## FAQ / Troubleshooting

**Q: Spot says "I don't know what you mean"**  
A: You may have typed an unknown command. Type `help` to see the list of valid commands.

**Q: "That task doesn't exist!" when I use mark/delete**  
A: The number must match a task in the current list. Use `list` first to see the correct numbers. After `find`, numbers refer to the search results only.

**Q: "You can't todo nothing"**  
A: The `todo` command needs a description. Example: `todo buy milk`.

**Q: "Deadline must have a description and /by &lt;date&gt;"**  
A: Use the format: `deadline DESCRIPTION /by DATE`. Example: `deadline submit report /by 2026-03-08`.

**Q: "Event must have a description, /from &lt;start&gt;, and /to &lt;end&gt;"**  
A: Use the format: `event DESCRIPTION /from START /to END`. Example: `event team meeting /from Mon 2pm /to 3pm`.

**Q: How do I transfer my data to another computer?**  
A: Copy the `data` folder (containing `spot.txt` and optionally `cheer.txt`) to the same location relative to where you run Spot on the new computer.

**Q: The Send button doesn't appear**  
A: The Send button appears only when you type something. Type at least one character to see it.

**Q: How do I send a message?**  
A: Press **Enter** (without Shift) or click **Send**. The input box supports multiple lines. Enter sends the message.

---

## Privacy / Data notes

- All data is stored **locally** on your computer.
- Tasks are saved in `data/spot.txt` in the folder where Spot runs.
- Motivational quotes are loaded from `data/cheer.txt` (optional).
- No data is sent to the internet or any external server.

---

## Command summary

| Action | Format | Example |
|--------|--------|---------|
| **Help** | `help` | `help` |
| **List** | `list` | `list` |
| **Todo** | `todo DESCRIPTION` | `todo buy milk` |
| **Deadline** | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-03-08` |
| **Event** | `event DESCRIPTION /from START /to END` | `event meeting /from Mon 2pm /to 3pm` |
| **Mark** | `mark INDEX` | `mark 1` |
| **Unmark** | `unmark INDEX` | `unmark 1` |
| **Delete** | `delete INDEX` | `delete 2` |
| **Find** | `find KEYWORD` | `find report` |
| **On** | `on DATE` | `on 2026-03-08` |
| **Cheer** | `cheer` | `cheer` |
| **Bye** | `bye` | `bye` |