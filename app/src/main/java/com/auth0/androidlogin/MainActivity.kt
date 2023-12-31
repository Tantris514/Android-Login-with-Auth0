//
// MainActivity.kt
// Android Kotlin XML Login
// 24 June 2022
//
// From the companion project for the Auth0 blog article
// “Get Started with Android Authentication Using Kotlin”.
//
// See the end of the file for licensing information.
//


package com.auth0.androidlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar
import com.auth0.androidlogin.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    // Access to onscreen controls
    private lateinit var binding: ActivityMainBinding

    // App and user status
    private lateinit var account: Auth0
    private var appJustLaunch = true
    private var userIsAuthentificated = false


    // Auth0 data
    private var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        binding.buttonLogin.setOnClickListener { login() }
        binding.buttonLogout.setOnClickListener { logout() }
        binding.textviewTitle.text = getString(R.string.initial_title)

    }

    private fun login() {

        WebAuthProvider
            .login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .start(this,object : Callback<Credentials,AuthenticationException>{
                override fun onFailure(error: AuthenticationException) {
                    showSnackBar(getString(R.string.login_failure_message))
                }
                override fun onSuccess(result: Credentials) {
                    Log.d("AndroidLogin",result.idToken)


                    val idToken = result.idToken
                    user = User(idToken)
                    userIsAuthentificated = true
                    showSnackBar(getString(R.string.login_success_message, user.name))
                    updateUI()
                }
            }
            )

    }

    private fun logout() {
        WebAuthProvider
            .logout(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .start(this, object : Callback<Void?, AuthenticationException> {

                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar(getString(R.string.general_failure_with_exception_code,
                        exception.getCode()))
                }

                override fun onSuccess(payload: Void?) {
                    user = User()
                    userIsAuthentificated = false
                    updateUI()
                }

            })
    }

    private fun updateUI() {

        binding.textviewTitle.text = if (userIsAuthentificated) {
            getString(R.string.logged_in_title)
        } else {
            getString(R.string.logged_out_title)
        }

        binding.buttonLogin.isEnabled = !userIsAuthentificated
        binding.buttonLogout.isEnabled = userIsAuthentificated


        binding.textviewUserProfile.isVisible = userIsAuthentificated
        binding.textviewUserProfile.text = getString(
            R.string.user_profile,
            user.name,
            user.email,
        )
        binding.imageviewUser.isVisible = userIsAuthentificated
        binding.imageviewUser.loadImage(user.picture)
        binding.jwt.isVisible = userIsAuthentificated
        binding.jwt.text = user.idToken





    }


    // Utility functions
    // =================

    /**
     * This is a convenience method that simplifies the process
     * of displaying a SnackBar.
     *
     * @param text The text that the SnackBar should display.
     */
    private fun showSnackBar(text: String) {
        Snackbar
            .make(
                binding.root,
                text,
                Snackbar.LENGTH_LONG
            ).show()
    }

}


//
// License information
// ===================
//
// Copyright (c) 2022 Auth0 (http://auth0.com)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.