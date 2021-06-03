package com.filedownloader.presentation.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.filedownloader.core.ViewModelFactory
import com.example.filesdownloader.R
import com.filedownloader.presentation.viewmodel.JsonFileViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject


class FilesActivity : AppCompatActivity() {

//    private var usersAdapter = UsersAdapter()

    @Inject
    lateinit var jsonFileViewModelFactory: ViewModelFactory<JsonFileViewModel>
    private val jsonFileViewModel by lazy {
        ViewModelProviders.of(this, jsonFileViewModelFactory)
            .get(JsonFileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_files)
        jsonFileViewModel.readFile("getListOfFilesResponse.json")
//        init()
    }
//    private fun init () {
//        setUpRecyclerView()
//        getUsers()
//        observeOnUsers()
//    }

//    private fun showLoading(show: Boolean) {
//        if (show) {
//            pbUsersLoading.visibility = View.VISIBLE
//            rvUsersList.visibility = View.GONE
//        }
//        else {
//            pbUsersLoading.visibility = View.GONE
//            rvUsersList.visibility = View.VISIBLE
//        }
//    }

//    private fun setUpRecyclerView() {
//        rvUsersList.adapter = usersAdapter
//        rvUsersList.layoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.VERTICAL,
//            false
//        )
//        swipeUsersList.setOnRefreshListener {
//            usersAdapter.items.clear()
//            getUsers()
//            swipeUsersList.isRefreshing = false
//        }
//        usersAdapter.onItemClicked.observe(this, Observer {
//            showConfirmationDialog(it)
//        })
//    }

//    private fun getUsers() {
//        showLoading(true)
//        emitterUsersViewModel.getAllUsers()
//    }

//    private fun observeOnUsers() {
//        emitterUsersViewModel.liveData.observe(this, Observer {
//            it?.let {
//                showLoading(false)
//                usersAdapter.items = it
//                usersAdapter.notifyDataSetChanged()
//            }
//        })
//    }

//    private fun showConfirmationDialog(user: User) {
//        val title = "Confirmation"
//        val message = "Are you sure you want to send the data of (${user.name}) to the Middle Man App?"
//        val alertDialog: AlertDialog = this.let {
//            val builder = AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog)
//            builder.apply {
//                setPositiveButton("YES"
//                ) { dialog, _ ->
//                    dialog.dismiss()
//                    sendDataBroadcast(user)
//                }
//                setNegativeButton("NO") { dialog, _ ->
//                    dialog.dismiss()
//                }
//            }
//            builder.setTitle(title)
//            builder.setMessage(message)
//            builder.create()
//        }
//        alertDialog.setOnShowListener {
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.teal_700))
//            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.teal_700))
//        }
//        alertDialog.show()
//    }

//    private fun sendDataBroadcast(user: User) {
//        val intent = Intent("com.example.emitterapp")
////        intent.action = "com.example.middlemanapp"
//        val bundle = Bundle()
//        bundle.putParcelable(USER_KEY, user)
//        intent.putExtras(bundle)
//        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//        intent.setPackage("com.example.middlemanapp")
////        intent.component = ComponentName("com.example.middlemanapp", "com.example.middlemanapp.MainActivity")
//        sendBroadcast(intent)
//        Log.i("EmitterUsersActivity", "sendBroadcast")
//    }

    companion object {
        private const val USER_KEY = "user_key"
    }

}