package com.pokermon.android

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * About screen crediting contributors and providing app information.
 */
@Composable
fun AboutScreen(
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App header
        Text(
            text = "🃏 Poker Game",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Mobile Edition v1.0.0",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Description
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "A cross-platform educational poker game demonstrating modern software development practices with shared business logic across desktop and mobile platforms.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Contributors Section
        ContributorsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Technical Info Section
        TechnicalInfoSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // License Section
        LicenseSection()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Back button
        OutlinedButton(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Menu")
        }
    }
}

@Composable
fun ContributorsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "👥 Contributors",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Art Contributors
            ContributorCard(
                icon = Icons.Default.Palette,
                name = "Peter & Chris Vey",
                role = "Card Art Assets",
                description = "Beautiful playing card designs used throughout the game"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Development Team
            ContributorCard(
                icon = Icons.Default.Code,
                name = "Development Team",
                role = "Game Logic & UI",
                description = "Cross-platform game engine and user interface implementation"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Educational Framework
            ContributorCard(
                icon = Icons.Default.School,
                name = "Educational Framework",
                role = "Learning Platform",
                description = "Demonstrating object-oriented design and code improvement techniques"
            )
        }
    }
}

@Composable
fun ContributorCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    role: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TechnicalInfoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🔧 Technical Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            TechInfoItem(
                label = "Platform",
                value = "Android (API 26+)"
            )
            
            TechInfoItem(
                label = "Framework",
                value = "Jetpack Compose"
            )
            
            TechInfoItem(
                label = "Language",
                value = "Kotlin + Java"
            )
            
            TechInfoItem(
                label = "Architecture",
                value = "Cross-Platform Shared Logic"
            )
            
            TechInfoItem(
                label = "Game Modes",
                value = "Classic (Available), Adventure, Safari, Ironman (Coming Soon)"
            )
            
            TechInfoItem(
                label = "Target SDK",
                value = "API 34 (Android 14)"
            )
        }
    }
}

@Composable
fun TechInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun LicenseSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📄 License & Credits",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "This is an educational project demonstrating cross-platform development practices.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "• Card art assets by Peter & Chris Vey\n" +
                      "• Game logic and implementation by development team\n" +
                      "• Built with modern Android development tools\n" +
                      "• Designed for educational purposes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}