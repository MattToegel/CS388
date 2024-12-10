package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ethereallab.chaoticbattleship.Mode
import com.ethereallab.chaoticbattleship.databinding.FragmentGameGridBinding
import com.ethereallab.chaoticbattleship.views.GameGridView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class GameGridFragment : Fragment() {

    private var _binding: FragmentGameGridBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var lobbyId: String? = null
    private var playerId: String? = null
    private var mode: Mode = Mode.PLACEMENT
    private val maxShips = 5
    private val gridSize = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameGridBinding.inflate(inflater, container, false)
        lobbyId = arguments?.getString("lobbyId")
        playerId = auth.currentUser?.uid

        if (lobbyId == null || playerId == null) {
            showToast("Lobby ID and Player ID are required")
            return binding.root
        }
        fetchLobbyDetails()
        setupListeners()

        return binding.root
    }

    private fun fetchLobbyDetails() {
        db.collection("lobbies").document(lobbyId!!)
            .get()
            .addOnSuccessListener { document ->
                val lobbyName = document.getString("name") ?: "Unknown Lobby"
                val status = document.getString("status") ?: "unknown"
                val currentPlayers = (document.get("currentPlayers") as? List<String>)?.size ?: 0
                val maxPlayers = document.getLong("maxPlayers")?.toInt() ?: 0
                val currentTurnPlayer = document.getString("currentTurn")
                val placedShips = document.get("placedShips") as? List<String> ?: emptyList()

                updateLobbyUI(lobbyName, status, currentPlayers, maxPlayers, currentTurnPlayer, placedShips)
                loadPlayerGrid()
            }
            .addOnFailureListener { e ->
                showToast("Failed to fetch lobby details: ${e.message}")
            }
    }

    private fun updateLobbyUI(
        lobbyName: String, status: String, currentPlayers: Int, maxPlayers: Int,
        currentTurnPlayer: String?, placedShips: List<String>
    ) {
        binding.lobbyNameStatusText.text = "$lobbyName - Status: ${status.capitalize()}"
        binding.playerCountText.text = "Players: $currentPlayers/$maxPlayers"

        mode = when (status.lowercase()) {
            "place" -> Mode.PLACEMENT
            "attack" -> Mode.ATTACK
            else -> Mode.NONE
        }

        if (mode == Mode.ATTACK) {
            binding.currentPlayerTurnText.text = "Current Turn: $currentTurnPlayer"
            binding.currentPlayerTurnText.visibility = View.VISIBLE
            binding.gameGridView.setMode(Mode.ATTACK)
        } else {
            binding.currentPlayerTurnText.visibility = View.GONE
        }

        if (mode == Mode.PLACEMENT) {
            if (playerId in placedShips) {
                binding.actionButton.visibility = View.GONE
                binding.waitingMessageText.visibility = View.VISIBLE
                mode = Mode.NONE
                binding.gameGridView.setMode(mode)
            } else {
                binding.actionButton.visibility = View.VISIBLE
                binding.waitingMessageText.visibility = View.GONE
            }
        }
    }

    private fun loadPlayerGrid() {
        db.collection("shipPlacements").document("$lobbyId-$playerId")
            .get()
            .addOnSuccessListener { document ->
                val flatGridData = document.get("grid") as? List<Map<String, Any>>
                val gridData = flatGridData?.let { reconstructGrid(it) } ?: initializeGrid()
                binding.gameGridView.setPlacementData(gridData)
                if (mode != Mode.NONE) {
                    binding.gameGridView.setMode(mode)
                }
            }
            .addOnFailureListener { e ->
                showToast("Failed to load grid: ${e.message}")
            }
    }

    private fun reconstructGrid(flatGridData: List<Map<String, Any>>): List<List<Map<String, Any>>> {
        val gridSize = 10
        val grid = MutableList(gridSize) { MutableList(gridSize) { mutableMapOf("ships" to 0, "status" to 0) } }
        for (cell in flatGridData) {
            val row = (cell["row"] as? Number)?.toInt() ?: continue
            val col = (cell["col"] as? Number)?.toInt() ?: continue
            val ships = (cell["ships"] as? Number)?.toInt() ?: 0
            val status = (cell["status"] as? Number)?.toInt() ?: 0
            grid[row][col] = mutableMapOf("ships" to ships, "status" to status)
        }
        return grid
    }




    private fun setupListeners() {
        binding.actionButton.setOnClickListener {
            when (mode) {
                Mode.PLACEMENT -> confirmPlacement()
                Mode.ATTACK -> {
                    val selectedCell = binding.gameGridView.getSelectedAttackCell()
                    if (selectedCell != null) {
                        val (row, col) = selectedCell
                        handleAttack(row, col)
                    } else {
                        showToast("Please select a cell to attack!")
                    }
                }
                else -> showToast("Invalid mode")
            }
        }
    }

    private fun checkAndStartAttackPhase() {
        db.collection("lobbies").document(lobbyId!!)
            .get()
            .addOnSuccessListener { document ->
                val currentPlayers = document.get("currentPlayers") as? List<String> ?: return@addOnSuccessListener
                val placedShips = document.get("placedShips") as? List<String> ?: emptyList()

                if (placedShips.containsAll(currentPlayers)) {
                    db.collection("lobbies").document(lobbyId!!)
                        .update(
                            mapOf(
                                "status" to "attack",
                                "currentTurn" to currentPlayers.first()
                            )
                        )
                        .addOnSuccessListener {
                            showToast("All players are ready! Attack phase started.")
                        }
                        .addOnFailureListener { e ->
                            showToast("Failed to update lobby status: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                showToast("Failed to fetch lobby details: ${e.message}")
            }
    }

    private fun confirmPlacement() {
        val gridView = binding.gameGridView
        val shipPlacementData = gridView.getPlacementData()
        val totalShips = shipPlacementData.flatten().sumOf { it["ships"] as Int }

        if (totalShips > maxShips) {
            showToast("You can place up to $maxShips ships only!")
            return
        }

        val flattenedGridData = shipPlacementData.flatMapIndexed { row, rowData ->
            rowData.mapIndexed { col, cellData ->
                cellData.toMutableMap().apply { put("row", row); put("col", col) }
            }
        }

        // Start a batch write for efficient updates
        val batch = db.batch()

        // Step 1: Update the `shipPlacements` document
        val placementRef = db.collection("shipPlacements").document("$lobbyId-$playerId")
        batch.set(
            placementRef,
            mapOf("lobbyId" to lobbyId, "playerId" to playerId, "grid" to flattenedGridData)
        )
        Log.d("GameGridFragment", "Queued shipPlacement for $playerId")

        // Step 2: Update each cell in the `gameBoard` collection
        shipPlacementData.forEachIndexed { row, rowData ->
            rowData.forEachIndexed { col, cellData ->
                val ships = cellData["ships"] as? Int ?: 0
                if (ships > 0) {
                    val cellId = "$lobbyId-$row-$col"
                    val cellRef = db.collection("gameBoard").document(cellId)

                    // Create or update the cell document
                    batch.set(
                        cellRef,
                        mapOf(
                            "playerShips.$playerId" to ships // Add/update player's ship count in this cell
                        ),
                        SetOptions.merge() // Merge with existing data
                    )
                    Log.d("GameGridFragment", "Queued gameBoard update for $cellId")
                }
            }
        }

        // Step 3: Update the `lobbies` document
        val lobbyRef = db.collection("lobbies").document(lobbyId!!)
        batch.update(
            lobbyRef,
            mapOf(
                "placedShips" to FieldValue.arrayUnion(playerId),
                "remainingShips.$playerId" to totalShips // Update the player's total ships in lobby
            )
        )
        Log.d("GameGridFragment", "Queued lobby update for $lobbyId")

        // Commit the batch
        batch.commit()
            .addOnSuccessListener {
                binding.actionButton.visibility = View.GONE
                binding.waitingMessageText.visibility = View.VISIBLE
                binding.gameGridView.setMode(Mode.NONE)
                showToast("Ship placement confirmed!")
                checkAndStartAttackPhase()
            }
            .addOnFailureListener { e ->
                Log.e("GameGridFragment", "Batch commit failed: ${e.message}")
                showToast("Failed to confirm placement: ${e.message}")
            }
    }






    private fun handleAttack(row: Int, col: Int) {
        val cellId = "$lobbyId-$row-$col"
        db.collection("gameBoard").document(cellId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // If the document doesn't exist, it's a miss
                    showToast("Miss!")
                    return@addOnSuccessListener
                }

                val cellData = document.data ?: return@addOnSuccessListener
                val playerShips = cellData["playerShips"] as? Map<String, Long> ?: emptyMap()

                // Update the lobby's remainingShips based on the cell data
                val updates = mutableMapOf<String, Any>()
                playerShips.forEach { (playerId, shipCount) ->
                    if (shipCount > 0) {
                        updates["remainingShips.$playerId"] = FieldValue.increment(-1)
                    }
                }

                // Check if this cell has already been hit
                if (cellData["firstHitBy"] == null) {
                    updates["firstHitBy"] = auth.currentUser!!.uid
                    db.collection("gameBoard").document(cellId)
                        .update(updates)
                        .addOnSuccessListener {
                            showToast("Hit!")
                        }
                        .addOnFailureListener { e ->
                            showToast("Failed to record hit: ${e.message}")
                        }
                } else {
                    showToast("Miss!")
                }
            }
            .addOnFailureListener { e ->
                showToast("Failed to fetch cell data: ${e.message}")
            }
    }

    private fun initializeGrid(): List<List<Map<String, Any>>> {
        return List(10) { List(10) { mapOf("ships" to 0, "status" to 0) } }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
