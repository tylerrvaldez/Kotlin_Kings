/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.amphibians.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amphibians.network.Amphibian
import com.example.amphibians.network.AmphibianApi
//import com.example.amphibians.network.DetailApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
enum class AmphibianApiStatus {LOADING, ERROR, DONE}

class AmphibianViewModel : ViewModel() {

    // TODO: Create properties to represent MutableLiveData and LiveData for the API status
    private val _status = MutableLiveData<AmphibianApiStatus>()
    val status: LiveData<AmphibianApiStatus> = _status
    // TODO: Create properties to represent MutableLiveData and LiveData for a list of amphibian objects
    private val _amphibians: MutableLiveData<List<Amphibian>> = MutableLiveData()
    val amphibians: LiveData<List<Amphibian>> = _amphibians


    private val _locations: MutableLiveData<List<Amphibian>> = MutableLiveData()
    val locations: LiveData<List<Amphibian>> = _locations
    // TODO: Create properties to represent MutableLiveData and LiveData for a single amphibian object.
    //  This will be used to display the details of an amphibian when a list item is clicked
    private val _amphibian: MutableLiveData<Amphibian> = MutableLiveData()
    val amphibian: LiveData<Amphibian> = _amphibian

    // TODO: Create a function that gets a list of amphibians from the api service and sets the
    //  status via a Coroutine

     fun  getAmphibianList(){
        viewModelScope.launch {
            _status.value = AmphibianApiStatus.LOADING
            try {
//                Log.d("no",api_county)
                _locations.value = AmphibianApi.retrofitService.getData()
                var res: MutableList<Amphibian> = mutableListOf()
//                var res2: MutableList<Amphibian> = mutableListOf()
                for (i in _locations.value!!){
                    if (i.province == "Texas") res += i
//                    if (i.county == api_county && i.province == api_state) res2 += i
                }
//                Log.d("no2",res2.toString())
                _amphibians.value = res

                _status.value = AmphibianApiStatus.DONE
            } catch (e: Exception) {
                _amphibians.value = listOf()
                _status.value = AmphibianApiStatus.ERROR
                _locations.value = listOf()
            }
        }
    }



    fun onAmphibianClicked(amphibian: Amphibian) {
        // TODO: Set the amphibian object
            _amphibian.value = amphibian

    }
}
