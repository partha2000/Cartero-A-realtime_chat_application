package com.example.realtime_chat_application.ui.home

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.realtime_chat_application.R
import com.example.realtime_chat_application.model.message
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class HomeFragment : Fragment(){

    private lateinit var homeViewModel: HomeViewModel
    private var mfragmentListener:onFragmentListener? = null

    // Firebase instance variables
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mMessageDatabaseReference: DatabaseReference
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mAuthStateListener:FirebaseAuth.AuthStateListener
    private var mChildEventListener:ChildEventListener? = null
    private lateinit var mFirebaseStorage:FirebaseStorage
    private lateinit var mChatPhotoStorageReference: StorageReference
    private lateinit var mFirebaseRemoteConfig:FirebaseRemoteConfig

    // UI components
    private lateinit var send_message:Button
    private lateinit var message_body:EditText
    private lateinit var image_picker:ImageButton
    private lateinit var mProgressBar: ProgressBar

//    private var display_picture:ImageView? = null
    private var profile_name:TextView? = null
    private var profile_email:TextView? = null



    // Array adapter
    private var mMessageListView: ListView? = null
    private var mMessageAdapter: MessageAdapter? = null

    private val RC_SIGN_IN:Int = 1
    private val RC_PHOTO_PICKER = 2
    val DEFAULT_MSG_LENGTH_LIMIT = 1000

    private lateinit var mUsername:String
    private var mProfilePicture:ImageView? = null


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
        mProgressBar = root.findViewById(R.id.progress_bar);

//        display_picture = root.findViewById(R.id.profile_photo)
        profile_name = root.findViewById(R.id.profile_name)
        profile_email=root.findViewById(R.id.profile_email)
//        mProfilePicture = root.findViewById(R.id.profile_pic)


        // Main access point for the database
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages") // Interested for the message portion
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()
        mChatPhotoStorageReference = mFirebaseStorage.getReference().child("chat_photos")
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Initialize message ListView and its adapter
        val list_of_messages: List<message?> =
            ArrayList<message?>()
        mMessageAdapter = MessageAdapter(this.requireContext(), R.layout.message_card, list_of_messages)
        mMessageListView?.setAdapter(mMessageAdapter)

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        // Enable Send button when there's text to send

        // Enable Send button when there's text to send
        message_body.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                send_message.isEnabled = false
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (charSequence.toString().trim { it <= ' ' }.length > 0) {
                    send_message.isEnabled = true
                } else {
                    send_message.isEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        message_body.setFilters(arrayOf<InputFilter>(LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)))

        //Send button
        send_message.setOnClickListener {
            val newMessage: message = message(message_body.text.toString(),mUsername,null)
            mMessageDatabaseReference.push().setValue(newMessage)
            message_body.text.clear()
        }

        // Image Picker button
        image_picker.setOnClickListener {
            println("Image picker activated")
            val intent:Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent,"Complete action using"),RC_PHOTO_PICKER)
        }

        mAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user: FirebaseUser? = p0.currentUser
                if(user != null){
                    // User signed in
                    onSignedInInitialize(user.displayName,user.email,user.photoUrl)
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
//        val configSettings:FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder


//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
//                Toast.makeText(this.requireContext(), "Signed in", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this.requireContext(), "Sign in Cancelled", Toast.LENGTH_SHORT)
                    .show()
                println("Back Presses")
                activity?.finish()
            } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
                println("Image selected: ${data?.extras?.getString("chat_photos")}")
                val selectedImageUri: Uri? = data?.data


                // Get reference to store file at chat_photos/<Filename>
                var photoRef: StorageReference? =
                    mChatPhotoStorageReference?.child(selectedImageUri?.lastPathSegment!!)


                //Upload file to firebase
                val photoPutRef = photoRef?.putFile(selectedImageUri!!)
                // TODO : add the photo picker functionality

                // on success listener
                photoPutRef?.addOnSuccessListener {
                    photoRef?.downloadUrl }?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("Uploaded and URL are sucessfull")
                        val downloadUri = task.result
                        val messageObject:message = message(null,mUsername,downloadUri.toString())
                        mMessageDatabaseReference.push().setValue(messageObject)
                    } else {
                            println("Phoot upload not successfull")
                        // Handle failures
                        // ...
                    }

                }
//                val urlTask = photoPutRef?.continueWithTask() { task ->
//                    if (!task.isSuccessful) {
//                        task.exception?.let {
//                            throw it
//                        }
//                    }
//                    photoRef?.downloadUrl
//                }?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val downloadUri = task.result
//                        val messageObject:message = message(null,mUsername,downloadUri)
//                        mMessageDatabaseReference.push().setValue(messageObject)
//                    } else {
//                        // Handle failures
//                        // ...
//                    }
//                }

                    // TODO: This can be used
//                photoRef?.putFile(selectedImageUri!!)?.addOnSuccessListener {
//                    val downloadUrl = photoRef.downloadUrl
//                    val messageImage: message = message(null, mUsername, downloadUrl.toString())
//                    mMessageDatabaseReference.push().setValue(messageImage)
//                }


//                photoRef?.putFile(selectedImageUri!!)?.addOnSuccessListener {
//                            // When the image has successfully uploaded, we get its download URL
//
//                    val downloadUrl = photoRef?.downloadUrl.
//
//                            // Set the download URL to the message box, so that the user can send it to the database
//                            val messageImage: message = message(
//                                null,
//                                mUsername,
//                                downloadUrl.toString()
//                            );
//                            mMessageDatabaseReference.push().setValue(messageImage);
//                        }
            }


        }
    }

    private fun onSignedInInitialize(displayName: String?,displayEmail:String?,photo:Uri?) {
        mUsername = displayName.toString()
        attachDatabaseReadListener()
        mfragmentListener?.profileInitializer(mUsername,displayEmail.toString(),photo)
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
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
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
            mProgressBar.setVisibility(ProgressBar.VISIBLE)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is onFragmentListener){
            mfragmentListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mfragmentListener = null
    }
    interface onFragmentListener{
        fun profileInitializer(name: String, email: String, photo: Uri?)
    }

}

