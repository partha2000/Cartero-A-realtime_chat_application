package com.example.realtime_chat_application.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.realtime_chat_application.R
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.util.*
import com.example.realtime_chat_application.model.message as message

class HomeFragment : Fragment(){

    private lateinit var homeViewModel: HomeViewModel

    // Firebase instance variables
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mMessageDatabaseReference: DatabaseReference

    // UI components
    private lateinit var send_message:Button
    private lateinit var message_body:EditText


    // Array adapter
    private var mMessageListView: ListView? = null
    private var mMessageAdapter: MessageAdapter? = null

    private lateinit var messageList: MutableList<message>


//    private lateinit var recyclerView : RecyclerView
////    private lateinit var recyclerViewAdapter: Adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        // UI Components
        send_message = root.findViewById(R.id.send_message)
        message_body = root.findViewById(R.id.new_message)
        mMessageListView = root.findViewById(R.id.message_list)


        // Main access point for the database
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages") // Interested for the message portion


        // Initialize message ListView and its adapter
        val list_of_messages: List<message?> =
            ArrayList<message?>()
        mMessageAdapter = MessageAdapter(this.requireContext(), R.layout.message_card, list_of_messages)
        mMessageListView?.setAdapter(mMessageAdapter)


        //Send button
        send_message.setOnClickListener { view ->
            val newMessage: message = message(message_body.text.toString(),"Partha",null)
            mMessageDatabaseReference.push().setValue(newMessage)
            message_body.text.clear()
        }

        val mChildEventListener = object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                var MessageBody: message? = p0.getValue<message>()
                mMessageAdapter!!.add(MessageBody)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        }
        mMessageDatabaseReference.addChildEventListener(mChildEventListener)



//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        return root
    }


}

//private fun ChildEventListener.onChildAdded(dataSnapshot: DataSnapshot,s: String) {
//    TODO("Not yet implemented")
//    val value:message = dataSnapshot.getValue() as message
//}
//        val fab: Button = findViewById(R.id.send_message)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }