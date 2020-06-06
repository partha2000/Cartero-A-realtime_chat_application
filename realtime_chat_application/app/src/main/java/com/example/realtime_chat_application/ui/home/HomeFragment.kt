package com.example.realtime_chat_application.ui.home

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.realtime_chat_application.R
import com.example.realtime_chat_application.model.message
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class HomeFragment : Fragment(){

    private lateinit var homeViewModel: HomeViewModel


    // Firebase instance variables
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mMessageDatabaseReference: DatabaseReference
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mAuthStateListener:FirebaseAuth.AuthStateListener
    private var mChildEventListener:ChildEventListener? = null
    private lateinit var mFirebaseStorage:FirebaseStorage
    private var mChatPhotoStorageReference: StorageReference? = null


    // UI components
    private lateinit var send_message:Button
    private lateinit var message_body:EditText
    private lateinit var image_picker:ImageButton


    // Array adapter
    private var mMessageListView: ListView? = null
    private var mMessageAdapter: MessageAdapter? = null

    private val RC_SIGN_IN:Int = 1
    private val RC_PHOTO_PICKER = 2

    private lateinit var mUsername:String
//    private lateinit var mProfilePicture:ImageView


//    private lateinit var recyclerView : RecyclerView
////    private lateinit var recyclerViewAdapter: Adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        // UI Components
        send_message = root.findViewById(R.id.send_message)
        message_body = root.findViewById(R.id.new_message)
        mMessageListView = root.findViewById(R.id.message_list)
        image_picker = root.findViewById(R.id.image_picker)
//        mProfilePicture = root.findViewById(R.id.profile_pic)


        // Main access point for the database
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages") // Interested for the message portion
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()
        mChatPhotoStorageReference = mFirebaseStorage.getReference().child("chat_photos")

        // Initialize message ListView and its adapter
        val list_of_messages: List<message?> =
            ArrayList<message?>()
        mMessageAdapter = MessageAdapter(this.requireContext(), R.layout.message_card, list_of_messages)
        mMessageListView?.setAdapter(mMessageAdapter)


        //Send button
        send_message.setOnClickListener {
            val newMessage: message = message(message_body.text.toString(),mUsername,null)
            mMessageDatabaseReference.push().setValue(newMessage)
            message_body.text.clear()
        }

        // Image Picker button
        image_picker.setOnClickListener {
            val intent:Intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg")
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
        }

        mAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user: FirebaseUser? = p0.currentUser
                if(user != null){
                    // User signed in
                    onSignedInInitialize(user.displayName)
                    Toast.makeText(requireContext(),"You are signed in !",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    onSignedOutCleanup()
                    // User is Signed out
                    // Choose authentication providers
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build())

                  // Create and launch sign-in intent
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(providers)
                            .build(),
                            RC_SIGN_IN)  // RC is request code
                }
            }

        }



//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(this.requireContext(), "Signed in", Toast.LENGTH_SHORT).show()
            }
            else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this.requireContext(), "Sign in Cancelled", Toast.LENGTH_SHORT).show()
                println("Back Presses")
                super.getActivity()?.finish()
            }
            else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
                println("Image selected")
                val selectedImageUri: Uri? = data?.data

                // Get reference to store file at chat_photos/<Filename>
                var photoRef: StorageReference? = mChatPhotoStorageReference?.child(selectedImageUri?.lastPathSegment!!)

                //Upload file to firebase

            }

        }
    }

    private fun onSignedInInitialize(displayName: String?) {
        mUsername = displayName.toString()
        attachDatabaseReadListener()

    }


    private fun attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                    var MessageBody: message? = p0.getValue<message>()
                    mMessageAdapter!!.add(MessageBody)
                }
            }
            // TODO : Check this casting
            mMessageDatabaseReference.addChildEventListener(mChildEventListener!!)

        }
    }

    private fun onSignedOutCleanup(){
        mUsername = "Anonymous"
        mMessageAdapter?.clear()
        detachDatabaseReadListener()

    }

    private fun detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessageDatabaseReference.removeEventListener(mChildEventListener!!)
            mChildEventListener = null
        }

    }

    override fun onPause() {
        super.onPause()
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
        }
        detachDatabaseReadListener()
        mMessageAdapter?.clear()
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

}

