package com.example.expense_tracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.expense_tracker.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private lateinit var database: ExpenseDatabase
    private lateinit var historyAdapter: ExpenseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = ExpenseDatabase.getDatabase(requireContext())

        // FIX: onItemClick pass kiya gaya hai (same logic)
        historyAdapter = ExpenseAdapter { expense ->
            val intent = Intent(requireContext(), TransactionDetailActivity::class.java)
            intent.putExtra("CATEGORY_NAME", expense.category)
            startActivity(intent)
        }

        binding.rvProfileHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfileHistory.adapter = historyAdapter

        val user = auth.currentUser
        if (user != null) {
            binding.tvUserName.text = user.displayName
            binding.tvUserEmail.text = user.email
            Glide.with(this).load(user.photoUrl).circleCrop().into(binding.ivUserProfile)
        }

        observeStats()

        binding.cardHistory.setOnClickListener {
            binding.rvProfileHistory.visibility = if (binding.rvProfileHistory.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun observeStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            database.expenseDao().getAllExpenses().collect { list ->
                val total = list.sumOf { if (it.isDebit) -it.amount else it.amount }
                binding.tvTotalSavings.text = "₹$total"
                binding.tvTransactionCount.text = "${list.size} Items"
                historyAdapter.submitList(list.sortedByDescending { it.date })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}