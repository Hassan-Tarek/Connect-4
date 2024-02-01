# Connect-4
This project is a Java-based Connect-4 game featuring both single-player (human vs. AI) and multiplayer (client-server) modes.

## Table of Contents
1. [**Features**](#features)
2. [**Requirements**](#requirements)
3. [**Project Structure**](#project-structure)
4. [**Game Modes**](#game-modes)
5. [**Minimax AI**](#minimax-ai)
6. [**Contributing**](#contributing)
7. [**License**](#license)

## Features
- **Single-player mode**: Play against an AI opponent with configurable difficulty levels.
- **Multiplayer mode**: Connects clients over a network to play in real-time games.
- **Minimax AI**: Provides a challenging AI opponent with multiple difficulty levels.
- **Interactive GUI**: Intuitive game interface built with JavaFX.
- **Chat functionality**: Allows players to chat with each other in multiplayer mode.

## Requirements
- **Java 17** or higher
- **JavaFX SDK** for GUI components
- **Maven** (for dependency management)

## Project Structure
The project is organized into multiple modules for better separation of concerns:

- **Client**: Manages the player’s interface and communicates with the server for multiplayer.
- **Server**: Handles multiple clients, game sessions, and message relays.
- **Game**: Contains the game logic, including Minimax AI.

## Game Modes
The game offers two primary modes:

1. **Single-Player Mode (Human vs. AI)**
   - There are different difficulty level for the AI:
       - Easy: AI plays random moves.
       - Medium: Minimax without alpha-beta pruning.
       - Hard: Minimax with alpha-beta pruning.
2. **Multiplayer Mode (Player vs. Player)**
   - The client will connect to the server, awaiting a matching player.
   - Once connected, the game begins automatically. Chat with your opponent using the in-game chat box.

## Minimax AI
The AI in single-player mode is powered by the Minimax algorithm with alpha-beta pruning, allowing the AI to efficiently evaluate moves and select the optimal path. The depth of the Minimax search is configurable, providing three difficulty levels:

- **Easy**: AI plays random moves, making it easy for beginners.
- **Medium**: AI uses a limited depth search in Minimax, balancing between challenge and accessibility.
- **Hard**: AI searches the full depth using Minimax with alpha-beta pruning for optimal moves.

## Contributing
1. **Fork the repository** and create your branch.
2. Make your changes and **open a pull request** with a detailed explanation of the updates.

# License
This project is licensed under the **MIT License**. See the [LICENSE](./LICENSE) file for more details.