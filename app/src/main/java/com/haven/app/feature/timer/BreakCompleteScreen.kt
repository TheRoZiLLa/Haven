package com.haven.app.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haven.app.ui.theme.*

/**
 * Screen showing success completion of the Break session.
 * Provides a button to return to the Home Screen.
 */
@Composable
fun BreakCompleteScreen(
    onReturnHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF9FBF7),
                        Color(0xFFEBF3E6)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = 48.dp, horizontal = 24.dp)
        ) {
            // Spacer to balance layout
            Spacer(modifier = Modifier.height(24.dp))

            // Completion Message Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large leaf icon representing organic growth
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFFF2F7F0),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Eco,
                            contentDescription = "Success",
                            tint = ForestGreen,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "✨ Great Job!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGreenDark
                )

                Text(
                    text = "Your tree is ready to grow.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MediumGray
                )
            }

            // Return Home CTA Button
            Button(
                onClick = onReturnHome,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = ForestGreen)
            ) {
                Text(
                    text = "Return Home",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
