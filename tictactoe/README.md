# Tic-Tac-Toe — Low Level Design

A two-player (or human-vs-AI) Tic-Tac-Toe game with pluggable input and output strategies.

---

## Class Entities

| Class / Interface              | Role                                                                            |
| ------------------------------ | ------------------------------------------------------------------------------- |
| `TicTacToe`                    | Bootstrap — creates players and starts the game                                 |
| `Game`                         | Game controller — manages the game loop, turn switching, win/draw detection     |
| `Board`                        | N×N grid — tracks cell ownership, validates moves, checks win/draw conditions   |
| `Player`                       | A participant with a name, symbol, and input strategy                           |
| `InputStrategy` _(interface)_  | How a player provides their move — `ScannerStrategy` (keyboard) or `AIStrategy` |
| `AIStrategy`                   | Computes the best move programmatically (random pick or minimax)                |
| `ScannerStrategy`              | Reads (row, col) from stdin                                                     |
| `InputProcessor`               | Decorates `InputStrategy` — validates bounds and occupied cells                 |
| `OutputStrategy` _(interface)_ | How the game renders output — `ConsoleStrategy`                                 |
| `OutputProcessor`              | Wraps `OutputStrategy` to display grid and messages                             |
| `Symbol` _(enum)_              | `X`, `O`                                                                        |
| `GameStatus` _(enum)_          | `IN_PROGRESS`, `WON`, `DRAW`                                                    |

---

## Functional Requirements

1. Two players alternate turns; each selects an empty cell on the N×N board.
2. After each valid move, the board checks for a win (row, column, diagonal) or a draw (board full).
3. Invalid inputs (out-of-bounds, already occupied) prompt the current player to try again.
4. Game ends when a player wins or the board is full (draw).
5. Either player can be AI-controlled — AI computes the move using its strategy.

---

## Non-Functional Requirements

- **Extensibility**: Swap input (keyboard → AI) or output (console → GUI) strategies without touching game logic.
- **Configurability**: Board size N is passed at construction — supports 3×3, 4×4, etc.
- **Testability**: Pass a deterministic `AIStrategy` for reproducible game sequences.

---

## Concurrency Requirements

- Current design is single-threaded (sequential turns). A network multiplayer extension would require async input handling and state synchronization.

---

## Class Diagram

```
TicTacToe
    └── Game
            ├── board: Board (N×N grid)
            ├── p1, p2: Player
            ├── currentPlayer: Player
            ├── gameStatus: GameStatus
            └── startGame()     ← game loop

Player
    ├── name: String
    ├── symbol: Symbol (X / O)
    └── inputStrategy: InputStrategy

InputStrategy (interface)
    └── getInput(grid) : int[2]
         ├── ScannerStrategy   (reads from stdin)
         └── AIStrategy        (computes best move)

OutputStrategy (interface)
    └── display(grid)
         └── ConsoleStrategy

Board
    ├── grid: int[][]
    ├── makeMove(x, y, symbol, value) : boolean
    ├── hasPlayerWon(x, y) : boolean
    └── isDraw() : boolean

Symbol (enum): X, O
GameStatus (enum): IN_PROGRESS, WON, DRAW
```

---

## Design Patterns Used

| Pattern           | Where                                                                   |
| ----------------- | ----------------------------------------------------------------------- |
| Strategy          | `InputStrategy`, `OutputStrategy` — decouple player input and rendering |
| Decorator         | `InputProcessor` wraps `InputStrategy` adding validation                |
| Factory (implied) | `TicTacToe` assembles players with their strategies                     |
