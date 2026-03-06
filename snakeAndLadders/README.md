# Snake and Ladders — Low Level Design

A turn-based Snake and Ladders board game with extensible obstacle types (snakes, ladders, portals).

---

## Class Entities

| Class / Interface                 | Role                                                                                 |
| --------------------------------- | ------------------------------------------------------------------------------------ |
| `Game`                            | Game controller — manages turn loop, dice rolls, win detection, and player switching |
| `Board`                           | N×N grid; resolves moves against the registered obstacles                            |
| `Player`                          | A game participant with a name and a current board position                          |
| `Dice`                            | Simulates a dice roll within a configurable range                                    |
| `Obstacle` _(abstract/interface)_ | Base type for all board hazards                                                      |
| `Snake`                           | Sends the player backward when landed on the snake's head                            |
| `Ladder`                          | Moves the player forward when landed on the ladder's base                            |
| `Portal`                          | Teleports the player to a fixed destination cell                                     |
| `ObstacleFactory`                 | Creates obstacles from configuration                                                 |
| `ObstacleType` _(enum)_           | `SNAKE`, `LADDER`, `PORTAL`                                                          |

---

## Functional Requirements

1. Players take turns rolling the dice; the result advances their position.
2. After each move, the board checks if the landing cell has an obstacle and applies its effect.
3. A player wins by reaching exactly cell N×N (last cell).
4. Snakes reduce position; ladders increase it; portals can teleport to an arbitrary cell.
5. Multiple players are supported; turns cycle round-robin.

---

## Non-Functional Requirements

- **Extensibility**: New obstacle types only require subclassing `Obstacle` and registering in `ObstacleFactory`.
- **Configurability**: Board size (N) and obstacle positions are passed at construction — no hard-coding.
- **Testability**: `Dice` range is injectable — use a fixed value for deterministic tests.

---

## Concurrency Requirements

- The current design is single-threaded (one game loop). For a multiplayer online game, turn management would need a message queue or lock-based synchronization.

---

## Class Diagram

```
Game
    ├── players: Player[]
    ├── board: Board
    ├── dice: Dice
    ├── startGame()          ← game loop
    ├── switchPlayer()
    └── hasPlayerWon() : boolean

Board
    ├── N: int  (board is N×N)
    ├── obstacles: Map<position, Obstacle>
    └── makeMove(player, diceValue) → applies obstacle if any

Obstacle (abstract)
    ├── start: int  (trigger position)
    ├── end: int    (destination position)
    └── apply(Player)
         ├── Snake   (end < start — move back)
         ├── Ladder  (end > start — move forward)
         └── Portal  (end = arbitrary)

Player
    ├── name: String
    └── currentPosition: int

Dice
    └── getDiceValue() : int  [min..max]

ObstacleType (enum): SNAKE, LADDER, PORTAL
```

---

## Design Patterns Used

| Pattern                  | Where                                                                        |
| ------------------------ | ---------------------------------------------------------------------------- |
| Factory                  | `ObstacleFactory` — creates obstacles by type                                |
| Template Method          | `Obstacle` — defines `apply()` contract; subclasses implement movement logic |
| Strategy (possible ext.) | `Dice` — could be swapped for a weighted dice or fixed-sequence dice         |
