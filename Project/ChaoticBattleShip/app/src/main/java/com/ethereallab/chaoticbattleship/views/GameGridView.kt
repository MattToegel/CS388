package com.ethereallab.chaoticbattleship.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.ethereallab.chaoticbattleship.Mode

class GameGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val gridSize = 10
    private val cellPaint = Paint()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val cellSize: Float
        get() = width / gridSize.toFloat()

    private var gridData: MutableList<MutableList<MutableMap<String, Any>>> = MutableList(gridSize) {
        MutableList(gridSize) { mutableMapOf("ships" to 0, "status" to 0) }
    }

    private var selectedAttackCell: Pair<Int, Int>? = null
    private var longPressHandler: Runnable? = null
    private var longPressTriggered = false
    private var pressedRow = -1
    private var pressedCol = -1
    private var mode: Mode = Mode.PLACEMENT

    init {
        cellPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val left = col * cellSize
                val top = row * cellSize
                val right = left + cellSize
                val bottom = top + cellSize

                // Get cell data
                val cell = gridData[row][col]
                val ships = cell["ships"] as Int
                val status = cell["status"] as Int

                // Determine cell background color based on status
                cellPaint.color = when (status) {
                    1 -> Color.YELLOW // Miss
                    2 -> Color.RED // Hit
                    else -> if (ships > 0) Color.BLUE else Color.LTGRAY // Ships or unused
                }

                // Draw cell background
                canvas.drawRect(left, top, right, bottom, cellPaint)

                // Draw ship count
                if (ships > 0 && mode == Mode.PLACEMENT) {
                    val textX = left + cellSize / 2
                    val textY = top + cellSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2
                    canvas.drawText(ships.toString(), textX, textY, textPaint)
                }

                // Draw border for selected cell in ATTACK mode
                if (mode == Mode.ATTACK && selectedAttackCell == Pair(row, col)) {
                    canvas.drawRect(left, top, right, bottom, borderPaint)
                }
            }
        }
    }

    fun setMode(newMode: Mode) {
        Log.d("GameGridView", "Setting mode to $newMode")
        mode = newMode
        selectedAttackCell = null // Clear any selected cell in ATTACK mode
        invalidate() // Redraw the grid
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mode == Mode.NONE) return false // Ignore all touch events when mode is NONE

        val row = (event.y / cellSize).toInt()
        val col = (event.x / cellSize).toInt()

        if (row !in 0 until gridSize || col !in 0 until gridSize) return false // Ignore out-of-bounds touches

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressedRow = row
                pressedCol = col
                longPressTriggered = false

                if (mode == Mode.PLACEMENT) {
                    // Schedule a long press handler
                    longPressHandler = Runnable {
                        longPressTriggered = true
                        resetCell(row, col)
                    }
                    postDelayed(longPressHandler!!, 500) // 500ms for long press
                }
            }

            MotionEvent.ACTION_UP -> {
                removeCallbacks(longPressHandler)
                if (!longPressTriggered) {
                    when (mode) {
                        Mode.PLACEMENT -> incrementCell(row, col)
                        Mode.ATTACK -> selectAttackCell(row, col)
                        else -> Unit // No action in NONE mode
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(longPressHandler)
            }
        }
        return true // Indicate that the touch event was handled
    }


    private fun incrementCell(row: Int, col: Int) {
        val cell = gridData[row][col]
        val currentShips = cell["ships"] as Int
        if (currentShips < 5) { // Increment up to a maximum of 5 ships
            cell["ships"] = currentShips + 1
        } else {
            cell["ships"] = 0 // Reset to 0 if it exceeds the limit
        }
        cell["status"] = 0 // Reset status for placement mode
        invalidate()
    }

    private fun resetCell(row: Int, col: Int) {
        val cell = gridData[row][col]
        cell["ships"] = 0
        cell["status"] = 0
        invalidate()
    }

    private fun selectAttackCell(row: Int, col: Int) {
        // Clear previous selection
        selectedAttackCell?.let { (prevRow, prevCol) ->
            gridData[prevRow][prevCol]["status"] = 0
        }

        // Select new cell
        selectedAttackCell = Pair(row, col)
        invalidate()
    }

    fun setPlacementData(data: List<List<Map<String, Any>>>) {
        gridData = MutableList(gridSize) { row ->
            MutableList(gridSize) { col ->
                data.getOrNull(row)?.getOrNull(col)?.toMutableMap()
                    ?: mutableMapOf("ships" to 0, "status" to 0)
            }
        }
        invalidate()
    }


    fun getPlacementData(): List<List<Map<String, Any>>> {
        return gridData
    }

}
