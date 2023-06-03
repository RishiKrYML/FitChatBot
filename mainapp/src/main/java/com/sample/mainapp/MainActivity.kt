package com.sample.mainapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    var recyclerView: RecyclerView? = null
    var message_text_text: EditText? = null
    var send_btn: ImageView? = null
    var messageList: MutableList<Message> = ArrayList()
    var messageAdapter: MessageAdapter? = null
    @OptIn(DelicateCoroutinesApi::class)
    private val scope = CoroutineScope(newSingleThreadContext("name"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val persona =    intent.getIntExtra("persona" , 0)
        message_text_text = findViewById(R.id.message_text_text)
        send_btn = findViewById(R.id.send_btn)
        recyclerView = findViewById(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView?.layoutManager = linearLayoutManager
        messageAdapter = MessageAdapter(messageList)
        recyclerView?.adapter = messageAdapter
        if (!isConnected(this@MainActivity)) {
            buildDialog(this@MainActivity).show()
        }

        val question1 = when(persona){
            0 ->  getString(R.string.persona0)
            1 ->  getString(R.string.persona1)
            2 ->  getString(R.string.persona2)
            3 ->  getString(R.string.persona3)
            4 ->  getString(R.string.persona4)
            else ->  getString(R.string.persona0)
        }
        addToChat(question1, Message.SEND_BY_ME)
        message_text_text?.setText("")
        messageList.add(Message("Typing...", Message.SEND_BY_BOT))
        scope.launch {
            withContext(Dispatchers.IO) {
                API().callAPI(question1).collectLatest {
                    addResponse(it)
                }
            }
        }
        message_text_text?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    send_btn?.isEnabled = false
                    // Toast.makeText(MainActivity.this, "Type your message", Toast.LENGTH_SHORT).show();
                } else {
                    send_btn?.isEnabled = true
                    send_btn?.setOnClickListener(View.OnClickListener { view: View? ->
                        val question =
                            message_text_text?.text.toString().trim { it <= ' ' }
                        addToChat(question, Message.SEND_BY_ME)
                        message_text_text?.setText("")
                        messageList.add(Message("Typing...", Message.SEND_BY_BOT))

                        scope.launch {
                            withContext(Dispatchers.IO) {
                                API().callAPI(question).collectLatest {
                                    addResponse(it)
                                }
                            }
                        }
                    })
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
    }

    fun addToChat(message: String?, sendBy: String?) {
        runOnUiThread {
            messageList.add(Message(message, sendBy))
            messageAdapter!!.notifyDataSetChanged()
            recyclerView!!.smoothScrollToPosition(messageAdapter!!.itemCount)
        }
    }

    fun addResponse(response: String?) {
        if (messageList.size > 0) messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SEND_BY_BOT)
    }

    fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return if (info != null && info.isConnectedOrConnecting) {
            val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
        } else false
    }

    fun buildDialog(context: Context?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please check your internet connection.")
        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> finishAffinity() }
        return builder
    }
}