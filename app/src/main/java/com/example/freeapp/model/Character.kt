package com.example.freeapp.model

class Character(var maze: Maze, private val soundPlayer: CharacterSoundPlayer) {
    interface CharacterSoundPlayer {
        fun playWalk()
        fun playKey()
        fun playDeath()
        fun playLevelCompleted()
        fun playPushEnemy()
        fun playDestroyEnemy()
        fun playPushRock()
    }

    //private val speed: Float = 2f
    var position = Position(maze.origin)
    var coorX = position.col + 0.5f
    var coorY = position.row + 0.5f
    /*var direction = Direction.UP
    var nextDirection = Direction.UP*/

    private var hasKey: Boolean = false
    var passLevel: Boolean = false
    var gameOver: Boolean = false
    var currentMoves = 10
    var animBool: Boolean = false

    fun update(deltaTime: Float) {

    }

    fun move(direction: Direction) {
        animBool = false

        val nextPos = position.translate(direction)     //Para chekear que la posicion a la que vas a moverse tiene algo
        if (maze[nextPos].type == CellType.CHEST && hasKey) {
            passLevel = true
            soundPlayer.playLevelCompleted()
        }
        else if (maze[nextPos].type == CellType.OBSTACLES) {
            soundPlayer.playPushRock()
            animBool = true
        }
        else if (maze[nextPos].type == CellType.ENEMIES) {
            if (maze[nextPos.translate(direction)].type != CellType.EMPTY){ //Si despues del esqueleto hay una pared donde se vaya a romper
                soundPlayer.playDestroyEnemy()
            }
            else{
                soundPlayer.playPushEnemy()
            }
            animBool = true
        }

        if(maze.canMove(position, direction)){
            position.move(direction)
            toCenter()
            soundPlayer.playWalk()

            val newPos = position
            if (maze[newPos].type == CellType.KEY) {
                maze[newPos].type = CellType.EMPTY
                hasKey = true
                soundPlayer.playKey()
            }
            else if (maze[newPos].type == CellType.TRAPS) {
                currentMoves--
            }

            currentMoves--
            if(currentMoves <= 0){
                currentMoves = 10
                hasKey = false
                gameOver = true
                soundPlayer.playDeath()
                position = Position(maze.origin)
                maze.reset()
                toCenter()
            }
        }
    }

    private fun toCenter() {
        coorX = position.col + 0.5f
        coorY = position.row + 0.5f
    }
}