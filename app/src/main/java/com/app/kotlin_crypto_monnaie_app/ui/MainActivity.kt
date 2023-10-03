package com.app.kotlin_crypto_monnaie_app

import android.annotation.SuppressLint
import android.content.Intent
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
import com.app.kotlin_crypto_monnaie_app.adapters.CoinAdapter
import com.app.kotlin_crypto_monnaie_app.adapters.onCoinListner
import com.app.kotlin_crypto_monnaie_app.data.model.Coin
import com.app.kotlin_crypto_monnaie_app.ui.DetailCoinActivity
import com.app.kotlin_crypto_monnaie_app.utils.Resource
import com.app.kotlin_crypto_monnaie_app.view_model.CoinViewModel

const val KEY_COIN = "key_coin"

class MainActivity : AppCompatActivity(), onCoinListner {
    private val coinViewModel: CoinViewModel by viewModels()

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    val coinAdapter2 = CoinAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_crypto)
        val coinAdapter = CoinAdapter(this)
        val layoutManager = LinearLayoutManager(this)

        val textViewError = findViewById<TextView>(R.id.textViewError)
        val progresseBar = findViewById<ProgressBar>(R.id.progressBar)

        val topAppBar1 = findViewById<Toolbar>(R.id.topAppBar1)
        setSupportActionBar(topAppBar1)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            coinViewModel.getCoinData().observe(this) { state ->
                when (state) {
                    //Si la resource est à l'etat Loading
                    is Resource.Loading -> {
                        progresseBar.visibility = View.GONE
                        textViewError.visibility = View.GONE
                    }
                    //Si la resource est à l'etat Success (les donnés ont bien été chargé)
                    is Resource.Success -> {
                        coinAdapter.submitList(state.data)
                        recyclerView.visibility = View.VISIBLE
                        progresseBar.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    }

                    is Resource.Error -> {
                        textViewError.visibility = View.VISIBLE
                        textViewError.text = state.message
                        progresseBar.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }

        coinViewModel.getCoinData().observe(this) { state ->
            when (state) {
                //Si la resource est à l'etat Loading
                is Resource.Loading -> {
                    progresseBar.visibility = View.VISIBLE
                }
                //Si la resource est à l'etat Success (les donnés ont bien été chargé)
                is Resource.Success -> {
                    coinAdapter.submitList(state.data)
                    progresseBar.visibility = View.GONE
                }

                is Resource.Error -> {
                    textViewError.visibility = View.VISIBLE
                    textViewError.text = state.message
                    progresseBar.visibility = View.GONE
                }
            }

        }

        recyclerView.adapter = coinAdapter
        recyclerView.layoutManager = layoutManager
    }

    override fun onClickCoin(coin: Coin) {
        val intentInfoCoin = Intent(this, DetailCoinActivity::class.java)
        intentInfoCoin.putExtra(KEY_COIN, coin.id)
        startActivity(intentInfoCoin)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val searchView = findViewById<SearchView>(R.id.app_bar_search)
        if (item.itemId.equals(searchView)) {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //Supprimer tout les element de la liste
                    val searchText = newText!!.toLowerCase(Locale.getDefault())
                    if (searchText.isNotEmpty()) {
                        coinViewModel.getCoinData().observe(this) { state ->
                            when (state) {
                                //Si la resource est à l'etat Loading
                                is Resource.Loading -> {

                                }
                                //Si la resource est à l'etat Success (les donnés ont bien été chargé)
                                is Resource.Success -> {
                                    val coins = coinAdapter2.submitList(state.data) as List<Coin>
                                    coins.forEach{
                                        if(it.name.toLowerCase(Locale.getDefault())){

                                        }
                                    }
                                }

                                is Resource.Error -> {

                                }
                            }
                        }
                    }

                }
            })
        }
        return super.onOptionsItemSelected(item)
    }*/
}