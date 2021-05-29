package com.example.firestorekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private val ref = Firebase.firestore.collection("persons")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUploadData.setOnClickListener {
            val firstname = etFirstName.text.toString()
            val lastname = etLastName.text.toString()
            val age = etAge.text.toString().toInt()

            val person = Person(firstname,lastname,age)
            savePerson(person)
        }
        btnRetrieveData.setOnClickListener {
            retrieveData()
        }
    }

    private fun retrieveData()=CoroutineScope(IO).launch {
        try {
            val querySnapshot = ref.get().await()
            val sb = StringBuilder()
            for(document in querySnapshot.documents){
                val person = document.toObject(Person::class.java)
                sb.append("$person\n")
            }
            withContext(Dispatchers.Main){
                data_display.text = sb.toString()
            }
        }catch(e: Exception){
            withContext(Dispatchers.Main){
                Log.e("info",e.toString())
                Toast.makeText(this@MainActivity,e.toString(),Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun savePerson(person: Person) = CoroutineScope(IO).launch {
        try {
            ref.add(person).await()
            /** await(): the line of code after this will only
             execute if the data is properly entered otherwise will continue to catch block
             available in'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1'*/
            withContext(Dispatchers.Main){   // to switch context as
                Toast.makeText(this@MainActivity,"Saved",Toast.LENGTH_LONG).show()
            }

        }catch (e: Exception){
            withContext(Dispatchers.Main){   // to switch context as
                Toast.makeText(this@MainActivity,e.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }
}