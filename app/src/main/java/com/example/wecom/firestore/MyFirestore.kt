package com.example.wecom.firestore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.wecom.others.AppUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MyFirestore {

    val exerciseCollectionRef= Firebase.firestore.collection("exercise")
    val auth = FirebaseAuth.getInstance()

        val winnerListByDistance = MutableLiveData<MutableList<ExerciseFstore>>()
        val userExerciseList = MutableLiveData<MutableList<ExerciseFstore>>()
        val userRank= MutableLiveData<Int>()

// save exercise to the firestrore
    fun saveExerciseToFstore(exerciseFbase: ExerciseFstore) = CoroutineScope(Dispatchers.IO).launch {
        exerciseCollectionRef.add(exerciseFbase).addOnSuccessListener {
            retrieveExerciseByDistance()
            retrieveUserExercise()

    }.addOnFailureListener {
        Log.d("xerror firestore upload","eerrrr")
    }
    }

    fun getUser(): FirebaseUser?{
        auth?.let {
            if (it.currentUser != null){
                return it.currentUser
            }
        }
        return null
    }
// get user id
    fun getUserId():String?{
        auth?.let {
            return it.currentUser!!.uid
        }
        return null
    }
    fun getUserWeight():String?{
        auth.let {
            if (it.currentUser!!.photoUrl != null)
            return it.currentUser!!.photoUrl.toString()
        }
        return null
    }





// get the user exercise
 fun retrieveUserExercise() {
        val id = getUserId()
        val list = mutableListOf<ExerciseFstore>()
        try {
            exerciseCollectionRef
                .whereEqualTo("idUser",id)
                .orderBy("distanceInMeters",Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnSuccessListener {document->
                if (document != null) {
                    for (exercise in document){
                        var exerciseFbase = exercise.toObject<ExerciseFstore>()
                        list.add(exerciseFbase)
                        Log.d("userex", exerciseFbase.toString()
                        )
                    }
                    userExerciseList.postValue(list)
                    Log.d("userex", "DocumentSnapshot data: " +
                            "}")
                } else {
                    Log.d("fireData", "No such document")
                }

            }.addOnFailureListener {
                Log.d("fireData", "No such document")
            }
        }catch (e:Exception){
        }
    }

//get 10 best of the day from firestore
    fun retrieveExerciseByDistance() {
        val date = AppUtility.getFormatedDay()

        val list = mutableListOf<ExerciseFstore>()
        try {
            exerciseCollectionRef
                .whereEqualTo("exerciseDate",date)
                .orderBy("distanceInMeters", Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnSuccessListener {document->
                    if (document != null) {
                        for (exercise in document){
                            var exerciseFbase = exercise.toObject<ExerciseFstore>()
                            list.add(exerciseFbase)
                            Log.d("mfireee", exerciseFbase.toString()
                            )
                        }
                        winnerListByDistance.postValue(list)
                    } else {
                        Log.d("fireData", "No such document")
                    }

                }.addOnFailureListener {
                    Log.d("fireData", "No such document")
                }
        }catch (e:Exception){

        }
    }

  suspend  fun getUserRank () = CoroutineScope(Dispatchers.IO).launch {
        val date = AppUtility.getFormatedDay()
        val list = mutableListOf<String>()
            exerciseCollectionRef
                .whereEqualTo("exerciseDate",date)
                .orderBy("distanceInMeters", Query.Direction.DESCENDING)
                .get().addOnSuccessListener {document->
                    if (document != null) {
                        for (exercise in document){
                            var exerciseFbase = exercise.toObject<ExerciseFstore>()
                            list.add(exerciseFbase.idUser)
                            Log.d("mfireee", exerciseFbase.toString()
                            )
                            userRank.postValue(list.indexOf(getUserId()))
                        }


                    }
    }

    }
}