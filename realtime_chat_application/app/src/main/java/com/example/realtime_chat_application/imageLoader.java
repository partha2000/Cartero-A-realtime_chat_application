//package com.example.realtime_chat_application;
//
//import android.content.Intent;
//import android.net.Uri;
//
//import com.example.realtime_chat_application.model.message;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//class imageLoader {
//    public void loadImageToFireBase(Intent data, StorageReference ChatPhotoStorageReference, String Username, DatabaseReference mMessageDatabaseReference){
//        Uri selectedImageUri = data.getData();
//        StorageReference photoRef = ChatPhotoStorageReference.child(selectedImageUri.getLastPathSegment());
//        photoRef.putFile(selectedImageUri).addOnSuccessListener(thi(){
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> downloadUrl = photoRef.getDownloadUrl();
//                // Set the download URL to the message box, so that the user can send it to the database
//                message friendlyMessage = new message(null, Username, downloadUrl.toString());
//                mMessageDatabaseReference.push().setValue(friendlyMessage);
//            }
//    }
//
//}
//
//
//public void loadImageToFireBase(Intent data, StorageReference ChatPhotoStorageReference, String Username, DatabaseReference mMessageDatabaseReference){
//    Uri selectedImageUri = data.getData();
//
//    // Get a reference to store file at chat_photos/<FILENAME>
//    StorageReference photoRef = ChatPhotoStorageReference.child(selectedImageUri.getLastPathSegment());
//    // Upload file to Firebase Storage
//    photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // When the image has successfully uploaded, we get its download URL
//                    Task<Uri> downloadUrl = photoRef.getDownloadUrl();
//
//
//                    // Set the download URL to the message box, so that the user can send it to the database
//                    message friendlyMessage = new message(null, Username, downloadUrl.toString());
//                    mMessageDatabaseReference.push().setValue(friendlyMessage);})}
//}
//}
//
//
