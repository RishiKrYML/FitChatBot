package com.sample.mainapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class SelectionActivity : AppCompatActivity() {
    var send_btn1: Button? = null
    var send_btn2: Button? = null
    var send_btn3: Button? = null
    var send_btn4: Button? = null
    var send_btn5: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
        send_btn1 = findViewById(R.id.textView)
        send_btn2 = findViewById(R.id.textView2)
        send_btn3 = findViewById(R.id.textView3)
        send_btn4 = findViewById(R.id.textView4)
        send_btn5 = findViewById(R.id.textView5)

        send_btn1?.setOnClickListener {
            val i = Intent(this@SelectionActivity, MainActivity::class.java)
            i.putExtra("persona", 0);
            startActivity(i)

        }
        send_btn2?.setOnClickListener {
            val i = Intent(this@SelectionActivity, MainActivity::class.java)
            i.putExtra("persona", 1);
            startActivity(i)
        }
        send_btn3?.setOnClickListener {
            val i = Intent(this@SelectionActivity, MainActivity::class.java)
            i.putExtra("persona", 2);
            startActivity(i)
        }
        send_btn4?.setOnClickListener {
            val i = Intent(this@SelectionActivity, MainActivity::class.java)
            i.putExtra("persona", 3);
            startActivity(i)
        }
        send_btn5?.setOnClickListener {
            val i = Intent(this@SelectionActivity, MainActivity::class.java)
            i.putExtra("persona", 4);
            startActivity(i)
        }
    }
}