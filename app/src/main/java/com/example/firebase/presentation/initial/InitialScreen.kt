package com.example.firebase.presentation.initial

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebase.R
import com.example.firebase.ui.theme.BackgroundButton
import com.example.firebase.ui.theme.Black
import com.example.firebase.ui.theme.Gray
import com.example.firebase.ui.theme.ShapeButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun InitialScreen(
    navigateToLogin: ()->Unit={},
    navigateToSignUp:()->Unit={},
    navigateToHome: ()->Unit={}
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val token = stringResource(R.string.default_web_client_id)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            navigateToHome()
                        } else {
                            Toast.makeText(context, "Error: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.e("GoogleAuth", "Error de Google: ${e.message}")
                Toast.makeText(context, "Operación cancelada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier= Modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(listOf(Gray, Black), startY = 0f, endY = 600f)),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Spacer(modifier = Modifier.weight(1f))
        Image(painter = painterResource(R.drawable.spotify),
            contentDescription = "",
            modifier = Modifier.clip(
                CircleShape
            ))
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.millions_songs),
            color = Color.White,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.free_spotify),
            color = Color.White,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {navigateToSignUp()},
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 32.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)){
            Text(text = stringResource(R.string.sign_up_free), color = Color.Black,fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))

        CustomButton(modificer = Modifier.fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp)
            .background(BackgroundButton)
            .border(2.dp, ShapeButton, CircleShape)
            .clickable {
                googleSignInClient.signOut().addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            }
            , painter = painterResource(R.drawable.google), title = stringResource(R.string.continue_google))

        Spacer(modifier = Modifier.height(8.dp))
        CustomButton(modificer = Modifier.fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp)
            .background(BackgroundButton)
            .border(2.dp, ShapeButton, CircleShape)
            , painter = painterResource(R.drawable.facebook), title = stringResource(R.string.continue_facebook))

        Text(text= stringResource(R.string.log_in), color = Color.White, modifier = Modifier
            .padding(24.dp)
            .clickable{navigateToLogin()}, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun CustomButton(modificer: Modifier, painter: Painter, title: String){
    Box(modifier = modificer, contentAlignment = Alignment.CenterStart){
        Image(painter = painter,
            contentDescription = "",
            modifier = Modifier.padding(start=16.dp).size(16.dp))
        Text(text=title,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold)
    }
}