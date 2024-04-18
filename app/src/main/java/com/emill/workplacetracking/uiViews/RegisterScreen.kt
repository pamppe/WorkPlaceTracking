import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.emill.workplacetracking.uiViews.PickImageFromGallery
import com.emill.workplacetracking.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, navController: NavController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }  // Ensure the type Uri? is explicitly declared

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PickImageFromGallery { uri ->
            imageUri = uri  // uri is now correctly handled as Uri?
        }
        imageUri?.let {
            Image(painter = rememberImagePainter(it), contentDescription = "Selected Image", modifier = Modifier.size(125.dp).padding(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = number, onValueChange = { number = it }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword), visualTransformation = VisualTransformation.None, label = { Text("Phone number") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Confirm Password") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = salary, onValueChange = { salary = it }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword), visualTransformation = VisualTransformation.None, label = { Text("Monthly salary") })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (imageUri != null) {
                viewModel.registerUser(name, email, number, password, salary, imageUri)
            } else {
                Log.e("RegisterScreen", "No image selected")
            }
            Log.i("Credential", "Email: $email, Password: $password, name: $name, number: $number")
        }, modifier = Modifier.width(170.dp).height(45.dp)) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
