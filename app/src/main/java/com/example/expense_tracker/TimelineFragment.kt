package com.example.expense_tracker

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expense_tracker.databinding.FragmentTimelineBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: ExpenseDatabase
    private lateinit var adapter: ExpenseAdapter
    private var filterJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = ExpenseDatabase.getDatabase(requireContext())

        setupRecyclerView()
        observeData()

        binding.btnadd.setOnClickListener {
            val intent = Intent(requireContext(), AddExpenseActivity::class.java)
            startActivity(intent)
        }

        binding.btnTimeFilter.setOnClickListener { v ->
            showFilterMenu(v)
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expense ->

        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun showFilterMenu(v: View) {
        val popup = PopupMenu(requireContext(), v)
        val s = SpannableString("Sort by")
        s.setSpan(ForegroundColorSpan(Color.GRAY), 0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(StyleSpan(Typeface.BOLD), 0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val header = popup.menu.add(s)
        header.isEnabled = false

        popup.menu.add("All history")
        popup.menu.add("Yesterday")
        popup.menu.add("Week")
        popup.menu.add("Month")
        popup.menu.add("Year")

        popup.setOnMenuItemClickListener { item ->
            binding.btnTimeFilter.text = item.title
            val calendar = Calendar.getInstance()
            val now = System.currentTimeMillis()
            var startTime = 0L
            var endTime = now

            when (item.title) {
                "Yesterday" -> {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    startTime = calendar.timeInMillis
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    endTime = calendar.timeInMillis
                }
                "Week" -> {
                    calendar.add(Calendar.DAY_OF_YEAR, -7)
                    startTime = calendar.timeInMillis
                }
                "Month" -> {
                    calendar.add(Calendar.MONTH, -1)
                    startTime = calendar.timeInMillis
                }
                "Year" -> {
                    calendar.add(Calendar.YEAR, -1)
                    startTime = calendar.timeInMillis
                }
                else -> {
                    startTime = 0L
                    endTime = Long.MAX_VALUE
                }
            }
            applyNewFilter(startTime, endTime)
            true
        }
        popup.show()
    }

    private fun observeData() {
        filterJob?.cancel()
        filterJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.expenseDao().getAllExpenses().collect { expenseList ->
                    updateUI(expenseList)
                }
            }
        }
    }

    private fun applyNewFilter(start: Long, end: Long) {
        filterJob?.cancel()
        filterJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.expenseDao().getExpensesBetween(start, end).collect { expenseList ->
                    updateUI(expenseList)
                }
            }
        }
    }

    private fun updateUI(expenseList: List<ExpenseEntity>) {
        adapter.submitList(expenseList.sortedByDescending { it.date })
        val total = expenseList.sumOf { if (it.isDebit) -it.amount else it.amount }
        binding.tvTotalBalance.text = "₹$total"
        binding.tvTotalBalance.setTextColor(if (total < 0) Color.RED else Color.parseColor("#26BA92"))
        updateChart(expenseList)
    }

    private fun updateChart(expenses: List<ExpenseEntity>) {
        if (expenses.isEmpty()) {
            binding.expenseChart.clear()
            return
        }
        val entries = ArrayList<Entry>()
        expenses.reversed().takeLast(10).forEachIndexed { index, expense ->
            entries.add(Entry(index.toFloat(), expense.amount.toFloat()))
        }
        val dataSet = LineDataSet(entries, "Spending").apply {
            color = Color.parseColor("#26BA92")
            setCircleColor(Color.parseColor("#26BA92"))
            lineWidth = 2.5f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#26BA92")
            fillAlpha = 40
            setDrawValues(false)
        }
        binding.expenseChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            animateX(800)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}