package com.pedroza.finddeliveryman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_result_list.*

class ResultListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)
        val listRegistro = getExternalFilesDir(null)!!.list()
        ListViewRegistro.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listRegistro!!.toList())
    }
}