package com.app.kotlin_crypto_monnaie_app.ui

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.kotlin_crypto_monnaie_app.KEY_COIN
import com.app.kotlin_crypto_monnaie_app.R
import com.app.kotlin_crypto_monnaie_app.adapters.TeamAdapter
import com.app.kotlin_crypto_monnaie_app.utils.Resource
import com.app.kotlin_crypto_monnaie_app.view_model.CoinViewModel

class DetailCoinActivity : AppCompatActivity() {
    private val coinViewModel: CoinViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)

        val rv_team_membres = findViewById<RecyclerView>(R.id.rv_team_membres)
        val teamMembersAdapter = TeamAdapter()
        val layoutManager = LinearLayoutManager(this)

        rv_team_membres.adapter = teamMembersAdapter
        rv_team_membres.layoutManager = layoutManager

        val coins_name = findViewById<TextView>(R.id.coins_name)
        val coins_status = findViewById<TextView>(R.id.coins_status)
        val coins_describe = findViewById<TextView>(R.id.coins_describe)

        val textErrorRvTeamMembersEmpty = findViewById<TextView>(R.id.textErrorRvTeamMembersEmpty)
        val text_error_describe_empty = findViewById<TextView>(R.id.text_error_describe_empty)

        val progressBarDetailCoins = findViewById<ProgressBar>(R.id.progressBarDetailCoins)
        val textErrorDetailCoins = findViewById<TextView>(R.id.textErrorDetailCoins)
        val text_team_membres = findViewById<TextView>(R.id.text_team_membres)

        val coin = intent.getStringExtra(KEY_COIN)

        val swipeRefreshLayoutDetailCoins = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayoutDetailCoins)

        swipeRefreshLayoutDetailCoins.setOnRefreshListener {
            if (coin != null) {
                coinViewModel.getCoinById(coin).observe(this) { state ->
                    when (state) {
                        is Resource.Loading -> {
                            progressBarDetailCoins.visibility = View.GONE
                            textErrorDetailCoins.visibility = View.GONE
                            coins_name.visibility = View.GONE
                            coins_status.visibility = View.GONE
                            coins_describe.visibility = View.GONE
                            text_team_membres.visibility = View.GONE
                            rv_team_membres.visibility = View.GONE
                            text_error_describe_empty.visibility = View.GONE
                            textErrorDetailCoins.visibility = View.GONE
                            textErrorRvTeamMembersEmpty.visibility = View.GONE
                        }
                        is Resource.Success -> {
                            coins_name.visibility = View.VISIBLE
                            coins_status.visibility = View.VISIBLE
                            coins_name.text = "${state.data?.rank}. ${state.data?.name} (${state.data?.symbol})"
                            coins_status.text = if (state.data?.is_active == true) "active" else "inactive"
                            coins_status.setTextColor(if (state.data?.is_active == true) Color.GREEN else Color.RED)

                            text_team_membres.visibility = View.VISIBLE
                            text_team_membres.text = "Team members"

                            if (state.data?.description?.isNotEmpty() == true) {
                                coins_describe.visibility = View.VISIBLE
                                coins_describe.text = state.data.description
                            } else {
                                text_error_describe_empty.visibility = View.VISIBLE
                                text_error_describe_empty.text = "Pas de description pour cette monnaie"
                                text_error_describe_empty.setTextColor(Color.RED)
                            }

                            if (state.data?.team?.isEmpty() == true) {
                                textErrorRvTeamMembersEmpty.visibility = View.VISIBLE
                                textErrorRvTeamMembersEmpty.text = "Pas d'echange pour le moment"
                                textErrorRvTeamMembersEmpty.setTextColor(Color.RED)

                            } else {
                                textErrorRvTeamMembersEmpty.visibility = View.GONE
                                rv_team_membres.visibility = View.VISIBLE
                                teamMembersAdapter.submitList(state.data?.team)
                            }
                            progressBarDetailCoins.visibility = View.GONE
                            swipeRefreshLayoutDetailCoins.isRefreshing = false
                        }

                        is Resource.Error -> {
                            progressBarDetailCoins.visibility = View.GONE
                            textErrorDetailCoins.visibility = View.GONE
                            coins_name.visibility = View.GONE
                            coins_status.visibility = View.GONE
                            coins_describe.visibility = View.GONE
                            text_team_membres.visibility = View.GONE
                            rv_team_membres.visibility = View.GONE
                            text_error_describe_empty.visibility = View.GONE
                            textErrorRvTeamMembersEmpty.visibility = View.GONE
                            textErrorDetailCoins.visibility = View.VISIBLE
                            textErrorDetailCoins.text = state.message
                            swipeRefreshLayoutDetailCoins.isRefreshing = false
                        }
                    }
                }
            }
        }

        if (coin != null) {
            coinViewModel.getCoinById(coin).observe(this) { state ->
                when (state) {
                    is Resource.Loading -> {
                        progressBarDetailCoins.visibility = View.VISIBLE
                    }
                    //Si la resource est à l'etat Success (les donnés ont bien été chargé)
                    is Resource.Success -> {
                        progressBarDetailCoins.visibility = View.GONE
                        text_team_membres.visibility = View.VISIBLE
                        text_team_membres.text = "Team members"
                        coins_name.text =
                            "${state.data?.rank}. ${state.data?.name} (${state.data?.symbol})"

                        coins_status.text =
                            if (state.data?.is_active == true) "active" else "inactive"
                        coins_status.setTextColor(if (state.data?.is_active == true) Color.GREEN else Color.RED)

                        if (state.data?.description?.isNotEmpty() == true) {
                            coins_describe.visibility = View.VISIBLE
                            coins_describe.text = state.data.description
                        } else {
                            text_error_describe_empty.visibility = View.VISIBLE
                            text_error_describe_empty.text = "Pas de description pour cette monnaie"
                            text_error_describe_empty.setTextColor(Color.RED)
                        }

                        if (state.data?.team?.isEmpty() == true) {
                            textErrorRvTeamMembersEmpty.visibility = View.VISIBLE
                            textErrorRvTeamMembersEmpty.text = "Pas d'echange pour le moment"
                            textErrorRvTeamMembersEmpty.setTextColor(Color.RED)

                        } else {
                            textErrorRvTeamMembersEmpty.visibility = View.GONE
                            teamMembersAdapter.submitList(state.data?.team)
                        }

                    }

                    is Resource.Error -> {
                        textErrorDetailCoins.visibility = View.VISIBLE
                        textErrorDetailCoins.text = state.message
                        progressBarDetailCoins.visibility = View.GONE
                    }
                }
            }
        }

        val topAppBarDetailCoin = findViewById<Toolbar>(R.id.topAppBarDetailCoin)
        setSupportActionBar(topAppBarDetailCoin)
        topAppBarDetailCoin.setNavigationOnClickListener {
            finish()
        }
    }
}