package com.rs132studio.multiplerecycler.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hbb20.CountryCodePicker
import com.rs132studio.multiplerecycler.R
import com.rs132studio.multiplerecycler.util.Constant.Companion.CHOOSE_IMG
import com.rs132studio.multiplerecycler.util.HideKeyBoard
import com.rs132studio.multiplerecycler.util.ToastMessage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_display_detail.*
import java.io.IOException

class DisplayDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var customerImg : ImageView
    private lateinit var customerName : EditText
    private lateinit var customerAddress : EditText
    private lateinit var customerPin : EditText
    private lateinit var customerCCP : CountryCodePicker
    private lateinit var customerPhone : EditText
    private lateinit var submitButton : Button
    private lateinit var progressBar: ProgressBar

    private lateinit var storageReference : StorageReference

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference

    var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_detail)
        setSupportActionBar(display_toolBar)

        customerImg = findViewById(R.id.customer_img)
        customerName = findViewById(R.id.customer_name)
        customerAddress = findViewById(R.id.customer_address)
        customerPin = findViewById(R.id.customer_pin)
        customerCCP = findViewById(R.id.customer_ccp)
        customerPhone = findViewById(R.id.customer_phonenumber)
        submitButton = findViewById(R.id.submitBtn)
        progressBar = findViewById(R.id.imageUploadProgressBar)

        customerCCP.registerCarrierNumberEditText(customerPhone)


        customerImg.setOnClickListener {
            choseCustomerImage()
        }

        submitButton.setOnClickListener { view->
            uploadImageInFB(view)
        }


        //hide key board
        parent_scrollView.setOnClickListener {
            HideKeyBoard.hideSoftKeyBoard(this@DisplayDetailActivity, it)
        }
        display_toolBar.setOnClickListener{
            HideKeyBoard.hideSoftKeyBoard(this@DisplayDetailActivity, it)
        }
        //hide key board
    }

    //choose image from gallery
    private fun choseCustomerImage() {
       val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select profile image"), CHOOSE_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            CHOOSE_IMG -> {
                if (resultCode == RESULT_OK && data != null){
                    imageUri = data.data!!
                    imageUri.let {
                        launchImageCrop(imageUri!!)
                    }
                } else {
                    ToastMessage.displayToast(this, "Image selection error: Couldn't select that image from memory.")
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK){
                    imageUri = result.uri
                    try {
                        setImage(imageUri!!)

                    }catch (e : IOException){
                        e.printStackTrace()
                    }
                }
                else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    ToastMessage.displayToast(this, "Crop error: ${result.error}")
                }
            }
        }

    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(customerImg)
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private fun uploadImageInFB(view: View) {

        val name = customerName.text.toString()
        val address = customerAddress.text.toString()
        val pin = customerPin.text.toString()
        val phone = customerCCP.fullNumberWithPlus.toString()

        when {
            TextUtils.isEmpty(name) -> {
                customerName.error = "Name should not blank"
                customerName.requestFocus()
                return
            } else -> {
            customerName.error = null
        }
        }

        when {
            TextUtils.isEmpty(address) -> {
                customerAddress.error = "Address should not blank"
                customerAddress.requestFocus()
                return
            }
            address.length < 10 -> {
                customerAddress.error = "Please provide correct address"
                customerAddress.requestFocus()
                return
            }
            else -> {
                customerAddress.error = null
            }
        }

        when {
            TextUtils.isEmpty(pin) -> {
                customerPin.error = "Pin code should not blank"
                customerPin.requestFocus()
                return
            }
            pin.length != 6 -> {
                customerPin.error = "Your pin code is invalid"
                customerPin.requestFocus()
                return
            }
            else -> {
                customerPin.error = null
            }
        }

        when {
            TextUtils.isEmpty(phone) -> {
                customerPhone.error = "Phone number should not blank"
                customerPhone.requestFocus()
                return
            }
            phone.length != 10 && !Patterns.PHONE.matcher(phone).matches() -> {
                customerPhone.error = "Please enter the valid phone number"
                customerPhone.requestFocus()
                return
            }
            else -> {
                customerPhone.error = null
            }
        }

        when {
            imageUri == null -> {
                ToastMessage.displayToast(this, "Please upload your image")
                return
            }
        }

        progressBar.visibility = View.VISIBLE
        storageReference = FirebaseStorage.getInstance().reference.child("user_image").child("${auth.currentUser?.uid.toString()}.jpg")
        storageReference.putFile(imageUri!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageReference.downloadUrl.addOnSuccessListener {uri ->
                    storeDataToFirebase(uri, name, address, pin, phone)
                }

            } else {
                ToastMessage.displayToast(this, "${task.exception?.message} occurred please check")
                progressBar.visibility = View.GONE
            }
            progressBar.visibility = View.GONE
        }


}

    private fun storeDataToFirebase(uri: Uri, name: String, address: String, pin: String, phone: String) {

        val userId  = auth.currentUser?.uid.toString()
        documentReference = firebaseFirestore.collection("users").document(userId)
        val user : MutableMap<String, String> = HashMap()
        user["user_image"] = uri.toString()
        user["name"] = name
        user["address"] = address
        user["pin"] = pin
        user["phone"] = phone

        documentReference.set(user).addOnCompleteListener {task ->
            if (task.isSuccessful){
                progressBar.visibility = View.GONE
                ToastMessage.displayToast(this, "Your information updated successfully")
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                progressBar.visibility = View.GONE
                ToastMessage.displayToast(this, "${task.exception?.message} occurred")
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        if (auth.currentUser != null){
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
}